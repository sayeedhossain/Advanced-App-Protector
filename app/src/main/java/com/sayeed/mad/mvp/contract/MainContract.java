package com.sayeed.mad.mvp.contract;

import android.content.Context;

import com.sayeed.mad.base.BasePresenter;
import com.sayeed.mad.base.BaseView;
import com.sayeed.mad.model.CommLockInfo;

import java.util.List;



public interface MainContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void onDestroy();
    }
}
