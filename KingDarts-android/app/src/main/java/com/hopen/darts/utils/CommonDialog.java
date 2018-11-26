package com.hopen.darts.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hopen.darts.base.BaseDialog;

/**
 * Created by zhangyanxue on 2018/6/7.
 */

public class CommonDialog extends BaseDialog {

    private View view = null;

    public static CommonDialog create(View view){
        CommonDialog commonDialog = new CommonDialog();
        commonDialog.view = view;
        return commonDialog;
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

}
