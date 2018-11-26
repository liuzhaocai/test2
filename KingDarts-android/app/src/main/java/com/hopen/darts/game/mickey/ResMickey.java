package com.hopen.darts.game.mickey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.GameRes;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Round;
import com.hopen.darts.game.mickey.type.TypeRuleMickey;
import com.hopen.darts.views.CustomFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 米老鼠游戏资源类
 */

public class ResMickey extends GameRes<GameMickey> {
    protected ResMickey(GameMickey game) {
        super(game);
    }

    @Override
    public int getPageBg() {
        return R.mipmap.mickey_page_bg;
    }

    @Override
    public int getTopLogo() {
        return R.mipmap.mickey_top_logo;
    }

    @Override
    public int getPlayerBg() {
        return R.mipmap.mickey_player_bg;
    }

    @Override
    public int getPlayerInfoItemBg() {
        return R.mipmap.mickey_player_info_item_bg;
    }

    @Override
    public int getPlayerHeadBg() {
        return R.mipmap.mickey_player_head_bg;
    }

    @Override
    public int getRoundBg() {
        return R.mipmap.mickey_round_bg;
    }

    @Override
    public int getRoundItemBg() {
        return R.mipmap.mickey_round_item_bg;
    }

    @Override
    public View getRoundCustomItemView(Context context, Group group, Round round, int round_position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_round_mickey, null);
            mHolder = new ViewHolder(convertView);
            mHolder.itemRoundLl.setBackgroundResource(getRoundItemBg());
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.itemRoundPositionTv.setText("回 合 " + (round_position + 1));
        mHolder.itemRoundScoreRvm.setInfo(mGame.getGroupMickey(group.getId()).rounds[round_position]);
        return convertView;
    }

    @Override
    public int getLiveBg() {
        if (mGame.typeRuleMickey == TypeRuleMickey.BONUS)
            return R.mipmap.mickey_live_bg_bonus;
        else
            return R.mipmap.mickey_live_bg_standard;
    }

    @Override
    public int getLiveItemBg() {
        return 0;
    }

    @Override
    public int getDartsBg() {
        return R.mipmap.mickey_darts_bg;
    }

//    @Override
//    public View inflateHitDartView(Context context, LinearLayout parent) {
//        ImageView view = new ImageView(context);
//        view.setAlpha(0f);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//        params.weight = 1;
//        parent.addView(view, params);
//        return view;
//    }
//
//    @Override
//    public void setHitDartViewData(Group group, View view, int position) {
//        ImageView image_view = (ImageView) view;
//        switch (mGame.getGroupMickey(group.getId()).rounds[group.getCurrentRoundPosition()].timesArray[position]) {
//            case -1:
//                image_view.setImageResource(R.mipmap.mickey_round_item_invalid_times);
//                break;
//            case 1:
//                image_view.setImageResource(R.mipmap.mickey_round_item_one_times);
//                break;
//            case 2:
//                image_view.setImageResource(R.mipmap.mickey_round_item_two_times);
//                break;
//            case 3:
//                image_view.setImageResource(R.mipmap.mickey_round_item_three_times);
//                break;
//            default:
//                image_view.setImageResource(R.color.transparent);
//                break;
//        }
//
//    }

    @Override
    public int getBottomBg() {
        return R.mipmap.mickey_bottom_bg;
    }

    @Override
    public boolean isBottomItemSingleDynamicBg() {
        return true;
    }

    @Override
    public int getBottomItemSingleBg(int position) {
        if (mGame.getGroupMickeyByPosition(position).isCrazy())
            return R.mipmap.game_play_bottom_player_single_crazy_mickey_bg;
        else
            return super.getBottomItemSingleBg(position);
    }

    @Override
    public int getBottomItemGroupBg(int position) {
        switch (mGame.getGameMode().getPlayerNumInGroup()) {
            case 2:
                if (position == 0)
                    return R.mipmap.mickey_bottom_item_group_2v2_left_bg;
                else
                    return R.mipmap.mickey_bottom_item_group_2v2_right_bg;
            case 3:
            default:
                if (position == 0)
                    return R.mipmap.mickey_bottom_item_group_3v3_left_bg;
                else
                    return R.mipmap.mickey_bottom_item_group_3v3_right_bg;
        }
    }

    @Override
    protected GameCustomArea<GameMickey> createCustomArea(FrameLayout parent) {
        return new CustomAreaMickey(parent, mGame);
    }

    static class ViewHolder {
        @BindView(R.id.item_round_position_tv)
        CustomFontTextView itemRoundPositionTv;
        @BindView(R.id.item_round_score_rvm)
        RoundViewMickey itemRoundScoreRvm;
        @BindView(R.id.item_round_ll)
        LinearLayout itemRoundLl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
