package com.hopen.darts.networks.dialog;

import android.app.Activity;

import com.hopen.darts.networks.interfaces.NetDialog;

/**
 * Created by weitong on 16/7/29.
 */
public class NetDialogUtil implements NetDialog {

    private static NetDialogUtil instance;
    private NetDialogFragment dialogFragment;
    public boolean isRunning;

    public static NetDialogUtil getInstance() {
        if (instance == null) {
            instance = new NetDialogUtil();
        }
        return instance;
    }

    private NetDialogUtil() {
        isRunning = false;
    }

    @Override
    public synchronized void show(Activity activity) {
        if (!isRunning) {
            isRunning = true;
            try {
                android.app.FragmentManager fragmentManager = activity.getFragmentManager();
                dialogFragment = new NetDialogFragment();
                dialogFragment.show(fragmentManager, "NetDialogFragment");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void hide() {
        try {
            if (dialogFragment != null)
                dialogFragment.dismiss();
            isRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
