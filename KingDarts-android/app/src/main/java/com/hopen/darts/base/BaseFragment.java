package com.hopen.darts.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bjlx on 2017/7/6.
 */

public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return bindView(inflater);
    }

    protected abstract View bindView(LayoutInflater inflater);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initControl();
        initListener();
    }

    protected abstract void initView();

    public abstract void initData();

    protected abstract void initControl();

    protected abstract void initListener();
}
