package com.sayeed.mad.mvp.contract;

import android.content.Context;

import com.sayeed.mad.base.BasePresenter;
import com.sayeed.mad.base.BaseView;
import com.sayeed.mad.model.CommLockInfo;
import com.sayeed.mad.mvp.p.LockMainPresenter;

import java.util.List;



public interface LockMainContract {
    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
