package io.sayeed.hossain.crashreporter.utils;

import android.support.v4.view.ViewPager;

public abstract class SimplePageChangeListener implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public abstract void onPageSelected(int position);

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
