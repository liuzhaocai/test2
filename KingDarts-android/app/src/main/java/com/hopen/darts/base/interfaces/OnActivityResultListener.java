package com.hopen.darts.base.interfaces;

import android.content.Intent;

/**
 * Created by thomas on 2018/1/26.
 */

public interface OnActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
