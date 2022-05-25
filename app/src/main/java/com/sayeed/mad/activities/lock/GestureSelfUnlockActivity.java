package com.sayeed.mad.activities.lock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.sayeed.mad.R;
import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.base.BaseActivity;
import com.sayeed.mad.db.CommLockInfoManager;
import com.sayeed.mad.activities.main.MainActivity;
import com.sayeed.mad.activities.setting.LockSettingActivity;
import com.sayeed.mad.utils.LockPatternUtils;
import com.sayeed.mad.utils.SpUtil;
import com.sayeed.mad.utils.SystemBarHelper;
import com.sayeed.mad.widget.LockPatternView;
import com.sayeed.mad.widget.LockPatternViewPattern;

import java.util.List;



public class GestureSelfUnlockActivity extends BaseActivity {

    private LockPatternView mLockPatternView;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private String actionFrom;
    private String pkgName;
    private CommLockInfoManager mManager;
    private RelativeLayout mTopLayout;

    private TextureView mTextureView;
    @NonNull
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture_self_unlock;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mLockPatternView = findViewById(R.id.unlock_lock_view);
        mTopLayout = findViewById(R.id.top_layout);
        mTextureView = findViewById(R.id.texture_view);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void initData() {
        mManager = new CommLockInfoManager(this);
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        initLockPatternView();


    }

    private void initLockPatternView() {
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                if (mLockPatternUtils.checkPattern(pattern)) {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
                        Intent intent = new Intent(GestureSelfUnlockActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
                        mManager.unlockCommApplication(pkgName);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_SETTING)) {
                        startActivity(new Intent(GestureSelfUnlockActivity.this, LockSettingActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_UNLOCK)) {
                        mManager.setIsUnLockThisApp(pkgName, true);
                        mManager.unlockCommApplication(pkgName);
                        sendBroadcast(new Intent(GestureUnlockActivity.FINISH_UNLOCK_THIS_APP));
                        finish();
                    }
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                        }
                    } else {
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= 3) {
                        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_RECORD_PIC, false)) {

                        }
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) { //The number of failures is greater than the maximum number of incorrect attempts before blocking the use

                    } else {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                }
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    protected void initAction() {

    }


}
