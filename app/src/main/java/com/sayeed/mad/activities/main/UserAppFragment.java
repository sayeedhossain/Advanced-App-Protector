package com.sayeed.mad.activities.main;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sayeed.mad.R;
import com.sayeed.mad.adapters.MainAdapter;
import com.sayeed.mad.base.BaseFragment;
import com.sayeed.mad.model.CommLockInfo;

import java.util.ArrayList;
import java.util.List;



public class UserAppFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    @Nullable
    private List<CommLockInfo> data, list;
    @Nullable
    private MainAdapter mMainAdapter;

    @NonNull
    public static UserAppFragment newInstance(List<CommLockInfo> list) {
        UserAppFragment userAppFragment = new UserAppFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        userAppFragment.setArguments(bundle);
        return userAppFragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View rootView) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = getArguments().getParcelableArrayList("data");
        mMainAdapter = new MainAdapter(getContext());
        mRecyclerView.setAdapter(mMainAdapter);
        list = new ArrayList<>();
        for (CommLockInfo info : data) {
            if (!info.isSysApp()) {
                list.add(info);
            }
        }
        mMainAdapter.setLockInfos(list);
    }
}
