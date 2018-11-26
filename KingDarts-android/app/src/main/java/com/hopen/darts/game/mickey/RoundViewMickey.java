package com.hopen.darts.game.mickey;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hopen.darts.R;

/**
 * Created by thomas on 2018/6/27.
 */

public class RoundViewMickey extends LinearLayout {
    public RoundViewMickey(Context context) {
        super(context);
        init();
    }

    public RoundViewMickey(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundViewMickey(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    void setInfo(RoundMickey info) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        if (info == null) return;
        for (int i = 0; i < info.timesArray.length; i++) {
            ImageView item = new ImageView(getContext());
            LayoutParams params = new LayoutParams(23, 23);
            switch (info.timesArray[i]) {
                case -1:
                    item.setImageResource(R.mipmap.mickey_round_item_invalid_times_white);
                    break;
                case 1:
                    item.setImageResource(R.mipmap.mickey_round_item_one_times_white);
                    break;
                case 2:
                    item.setImageResource(R.mipmap.mickey_round_item_two_times_white);
                    break;
                case 3:
                    item.setImageResource(R.mipmap.mickey_round_item_three_times_white);
                    break;
                default:
                    item.setImageResource(R.color.transparent);
                    break;
            }
            addView(item, params);
        }
    }
}
