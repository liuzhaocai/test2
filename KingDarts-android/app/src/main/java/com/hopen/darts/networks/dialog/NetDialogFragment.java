package com.hopen.darts.networks.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseDialog;

import java.util.Timer;
import java.util.TimerTask;


public class NetDialogFragment extends BaseDialog {
    TextView loadingTv;

    private int dot_num = 0;
    private int show_times;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    try {
                        if (show_times == Integer.MAX_VALUE){
                            NetDialogUtil.getInstance().hide();
                            dismiss();
                            return;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    show_times++;
                    if (loadingTv != null) {
                        switch (dot_num) {
                            case 0:
                                loadingTv.setText("正在加载");
                                dot_num = 1;
                                break;
                            case 1:
                                loadingTv.setText("正在加载.");
                                dot_num = 2;
                                break;
                            case 2:
                                loadingTv.setText("正在加载..");
                                dot_num = 3;
                                break;
                            case 3:
                            default:
                                loadingTv.setText("正在加载...");
                                dot_num = 0;
                                break;
                        }
                    }
                }
            });
        }
    };

    @Override
    protected View createDialogView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setClickOutToDismiss(false);
        show_times = 0;
        timer.schedule(task, 0, 350);
        View view = inflater.inflate(R.layout.network_dialog, container, false);
        loadingTv = (TextView) view.findViewById(R.id.loading_tv);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        NetDialogUtil.getInstance().isRunning = false;
        super.onDestroyView();
    }
}
