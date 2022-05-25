package com.sayeed.mad.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sayeed.mad.R;
import com.sayeed.mad.adapters.SelectTimeAdapter;
import com.sayeed.mad.model.LockAutoTime;
import com.sayeed.mad.activities.setting.LockSettingActivity;

import java.util.ArrayList;
import java.util.List;



public class SelectLockTimeDialog extends BaseDialog {

    private RecyclerView mRecyclerView;
    private List<LockAutoTime> mTimeList;
    private SelectTimeAdapter mSelectTimeAdapter;
    private Context context;
    private String title;


    public SelectLockTimeDialog(@NonNull Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_lock_select_time;
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Nullable
    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Nullable
    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        setCanceledOnTouchOutside(false);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        String titleArray[] = context.getResources().getStringArray(R.array.lock_time_array);
        Long timeArray[] = {15000L, 30000L, 60000L, 180000L, 300000L, 600000L, 1800000L, 0L};
        mTimeList = new ArrayList<>();
        for (int i = 0; i < titleArray.length; i++) {
            LockAutoTime time = new LockAutoTime();
            time.setTitle(titleArray[i]);
            time.setTime(timeArray[i]);
            mTimeList.add(time);
        }
        mSelectTimeAdapter = new SelectTimeAdapter(mTimeList, context);
        mRecyclerView.setAdapter(mSelectTimeAdapter);
        mSelectTimeAdapter.setTitle(title);
        mSelectTimeAdapter.setListener(new SelectTimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LockAutoTime info, boolean isLast) {
                Intent intent = new Intent();
                intent.putExtra("info", info);
                intent.putExtra("isLast", isLast);
                intent.setAction(LockSettingActivity.ON_ITEM_CLICK_ACTION);
                context.sendBroadcast(intent);
            }
        });
    }

    @Override
    public void show() {

        super.show();
    }
}
