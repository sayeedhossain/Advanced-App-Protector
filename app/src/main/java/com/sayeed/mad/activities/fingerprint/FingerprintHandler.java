package com.sayeed.mad.activities.fingerprint;


import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private final String KEY_NAME = "unlock_key";

    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private CancellationSignal cancellationSignal;
    private Callback callback;

    public boolean checkFingerprint(Context context) {
        keyguardManager = context.getSystemService(KeyguardManager.class);
        fingerprintManager = context.getSystemService(FingerprintManager.class);
        return keyguardManager.isKeyguardSecure() && fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
    }

    public void init() {

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        createKey();
    }

    private void createKey() {

        try {
            keyStore.load(null);
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new RuntimeException("Failed to load KeyStore", e);
        }

        try {
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init KeyGenerator", e);
        }

        keyGenerator.generateKey();
    }

    private boolean initCipher() {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void startListening(Callback callback) {
        stopListening();
        if (initCipher()) {
            this.callback = callback;
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), cancellationSignal, 0, this, null);
        }
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
            this.callback = null;
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        if (callback != null) {
            callback.onAuthenticated();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        if (callback != null) {
            callback.onFailed();
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (callback != null) {
            callback.onError();
        }
    }

    public interface Callback {

        void onAuthenticated();

        void onFailed();

        void onError();
    }
}
