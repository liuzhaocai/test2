package com.hopen.darts.ui.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseAdaptor;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 2018/6/11.
 */

public class GamePlayRoundAdapter extends BaseAdaptor<Round> {
    private BaseGame mGame;
    private Group currentGroup;

    public GamePlayRoundAdapter(Context context, BaseGame game) {
        super(context);
        mGame = game;
    }

    public void refresh(Group group) {
        currentGroup = group;
        refresh(currentGroup.getAllRound());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Round item = getItem(position);
        View custom_view = mGame.getGameRes().getRoundCustomItemView(mContext, currentGroup, item,
                position, convertView, parent);
        if (custom_view != null) {
            convertView = custom_view;
        } else {
            final ViewHolder mHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_round, null);
                mHolder = new ViewHolder(convertView);
                mHolder.itemRoundLl.setBackgroundResource(mGame.getGameRes().getRoundItemBg());
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.itemRoundPositionTv.setText("回 合 " + (position + 1));
            if (item != null)
                mHolder.itemRoundScoreTv.setText(item.getScore() + "");
            else
                mHolder.itemRoundScoreTv.setText("");
        }
        return convertView;
    }

    static class ViewHolder {
        View convertView;
        @BindView(R.id.item_round_position_tv)
        CustomFontTextView itemRoundPositionTv;
        @BindView(R.id.item_round_score_tv)
        CustomFontTextView itemRoundScoreTv;
        @BindView(R.id.item_round_ll)
        LinearLayout itemRoundLl;

        ViewHolder(View view) {
            convertView = view;
            ButterKnife.bind(this, view);
        }
    }
}
