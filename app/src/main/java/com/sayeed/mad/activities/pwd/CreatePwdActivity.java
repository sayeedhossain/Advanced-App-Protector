package com.sayeed.mad.activities.pwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sayeed.mad.R;
import com.sayeed.mad.base.AppConstants;
import com.sayeed.mad.base.BaseActivity;
import com.sayeed.mad.model.LockStage;
import com.sayeed.mad.activities.main.MainActivity;
import com.sayeed.mad.mvp.contract.GestureCreateContract;
import com.sayeed.mad.mvp.p.GestureCreatePresenter;
import com.sayeed.mad.services.BackgroundManager;
import com.sayeed.mad.services.LockService;
import com.sayeed.mad.utils.LockPatternUtils;
import com.sayeed.mad.utils.SpUtil;
import com.sayeed.mad.utils.SystemBarHelper;
import com.sayeed.mad.widget.LockPatternView;
import com.sayeed.mad.widget.LockPatternViewPattern;

import java.util.List;



public class CreatePwdActivity extends BaseActivity implements View.OnClickListener,
        GestureCreateContract.View {

    @Nullable
    private List<LockPatternView.Cell> mChosenPattern = null;

    private TextView mLockTip;
    private LockPatternView mLockPatternView;
    @NonNull
    private final Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };
    private TextView mBtnReset;
    private LockStage mUiStage = LockStage.Introduction;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureCreatePresenter mGestureCreatePresenter;
    private RelativeLayout mTopLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_pwd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mLockPatternView = findViewById(R.id.lock_pattern_view);
        mLockTip = findViewById(R.id.lock_tip);
        mBtnReset = findViewById(R.id.btn_reset);
        mTopLayout = findViewById(R.id.top_layout);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void initData() {
        mGestureCreatePresenter = new GestureCreatePresenter(this, this);
        initLockPatternView();
    }

    private void initLockPatternView() {
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                mGestureCreatePresenter.onPatternDetected(pattern, mChosenPattern, mUiStage);
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    protected void initAction() {
        mBtnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_reset:
                setStepOne();
                break;
        }
    }

    private void setStepOne() {
        mGestureCreatePresenter.updateStage(LockStage.Introduction);
        mLockTip.setText(getString(R.string.lock_recording_intro_header));
    }

    private void gotoLockMainActivity() {
        SpUtil.getInstance().putBoolean(AppConstants.LOCK_STATE, true);
        BackgroundManager.getInstance().init(this).startService(LockService.class);
        SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_FIRST_LOCK, false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void updateUiStage(LockStage stage) {
        mUiStage = stage;
    }

    @Override
    public void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern) {
        this.mChosenPattern = mChosenPattern;
    }

    @Override
    public void updateLockTip(String text, boolean isToast) {
        mLockTip.setText(text);
    }

    @Override
    public void setHeaderMessage(int headerMessage) {
        mLockTip.setText(headerMessage);
    }

    @Override
    public void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode) {
        if (patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }
        mLockPatternView.setDisplayMode(displayMode);
    }

    @Override
    public void Introduction() {
        clearPattern();
    }

    @Override
    public void HelpScreen() {

    }

    @Override
    public void ChoiceTooShort() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }

    @Override
    public void moveToStatusTwo() {

    }


    @Override
    public void clearPattern() {
        mLockPatternView.clearPattern();
    }


    @Override
    public void ConfirmWrong() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }


    @Override
    public void ChoiceConfirmed() {
        mLockPatternUtils.saveLockPattern(mChosenPattern);
        clearPattern();
        gotoLockMainActivity();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGestureCreatePresenter.onDestroy();
    }
}
