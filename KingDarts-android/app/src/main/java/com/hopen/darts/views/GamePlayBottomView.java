package com.hopen.darts.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.game.base.BaseGame;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;

import java.util.List;

/**
 * 游戏页面底部玩家信息显示view
 */

public class GamePlayBottomView extends LinearLayout {
    private BaseGame mGame;
    private TextView[] scoreViews;
    private RelativeLayout[] singleParentViews;
    private GroupHead[] groupHeadViews;

    public GamePlayBottomView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public GamePlayBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public GamePlayBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    /**
     * 将正在操作的阵营(队伍)中正在操作的玩家头像显示到最顶部，其他队伍都正常来显示
     * 多人组队模式专用，单人队伍模式下，该方法直接return
     *
     * @param current 正在操作的阵营(队伍)
     */
    public void showCurrentPlayerToTop(Group current) {
        if (groupHeadViews == null) return;
        for (GroupHead item : groupHeadViews) {
            item.refresh(item.group.getId() == current.getId());
        }
    }

    /**
     * 刷新分数显示
     */
    public void refreshScore() {
        List<Group> groups = mGame.getGroupList();
        if (singleParentViews != null && mGame.getGameRes().isBottomItemSingleDynamicBg()) {
            for (int i = 0; i < groups.size(); i++) {
                scoreViews[i].setText(groups.get(i).getScore() + "");
                singleParentViews[i].setBackgroundResource(mGame.getGameRes().getBottomItemSingleBg(i));
            }
        } else {
            for (int i = 0; i < groups.size(); i++) {
                scoreViews[i].setText(groups.get(i).getScore() + "");
            }
        }
    }

    /**
     * 显示玩家信息
     *
     * @param game 游戏信息对象
     */
    public void withGame(BaseGame game) {
        if (game == null) return;
        List<Group> groups = game.getGroupList();
        if (groups.size() > 0) {
            removeAllViews();
            this.mGame = game;
            scoreViews = new TextView[groups.size()];
            if (mGame.getGameMode().getPlayerNumInGroup() == 1) {
                groupHeadViews = null;
                singleParentViews = new RelativeLayout[groups.size()];
                for (int i = 0; i < groups.size(); i++) {
                    Group group = groups.get(i);
                    View view = inflate(getContext(), R.layout.view_game_play_bottom_pleyer_single, null);
                    RelativeLayout parent = view.findViewById(R.id.player_single_parent_rl);
                    ImageView head = view.findViewById(R.id.player_single_head_iv);
                    TextView name = view.findViewById(R.id.player_single_name_tv);
                    TextView score = view.findViewById(R.id.player_single_score_tv);
                    parent.setBackgroundResource(mGame.getGameRes().getBottomItemSingleBg(i));
                    Player player = group.getPlayerList().get(0);
                    player.loadHead(head);
                    name.setText(player.getName());
                    score.setText(group.getScore() + "");
                    singleParentViews[i] = parent;
                    scoreViews[i] = score;
                    addView(view);
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    if (params != null && i != 0) {
                        params.leftMargin = 100;
                        view.setLayoutParams(params);
                    }
                }
            } else {
                singleParentViews = null;
                groupHeadViews = new GroupHead[2];
                Group group_left = groups.get(0);
                View group_left_view = inflate(getContext(), R.layout.view_game_play_bottom_group_left, null);
                ImageView left_player_1st_head = group_left_view.findViewById(R.id.player_group_left_head_1st_iv);
                ImageView left_player_2nd_head = group_left_view.findViewById(R.id.player_group_left_head_2nd_iv);
                ImageView left_player_3rd_head = group_left_view.findViewById(R.id.player_group_left_head_3rd_iv);
                TextView left_score = group_left_view.findViewById(R.id.player_group_left_score_tv);
                group_left_view.setBackgroundResource(mGame.getGameRes().getBottomItemGroupBg(0));
                left_score.setText(group_left.getScore() + "");
                scoreViews[0] = left_score;
                List<Player> left_player_list = group_left.getPlayerList();
                groupHeadViews[0] = new GroupHead(group_left, left_player_list.size());
                switch (left_player_list.size()) {
                    case 2:
                        left_player_1st_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_2v2_left_1st_bg);
                        left_player_2nd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_2v2_left_2nd_bg);
                        left_player_list.get(0).loadHead(left_player_1st_head);
                        left_player_list.get(1).loadHead(left_player_2nd_head);
                        groupHeadViews[0].playersHead[0] = left_player_1st_head;
                        groupHeadViews[0].playersHead[1] = left_player_2nd_head;
                        left_player_3rd_head.setVisibility(GONE);
                        break;
                    case 3:
                        left_player_1st_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_left_1st_bg);
                        left_player_2nd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_left_2nd_bg);
                        left_player_3rd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_left_3rd_bg);
                        left_player_list.get(0).loadHead(left_player_1st_head);
                        left_player_list.get(1).loadHead(left_player_2nd_head);
                        left_player_list.get(2).loadHead(left_player_3rd_head);
                        groupHeadViews[0].playersHead[0] = left_player_1st_head;
                        groupHeadViews[0].playersHead[1] = left_player_2nd_head;
                        groupHeadViews[0].playersHead[2] = left_player_3rd_head;
                        break;
                }
                addView(group_left_view);
                Group group_right = groups.get(1);
                View group_right_view = inflate(getContext(), R.layout.view_game_play_bottom_group_right, null);
                ImageView right_player_1st_head = group_right_view.findViewById(R.id.player_group_right_head_1st_iv);
                ImageView right_player_2nd_head = group_right_view.findViewById(R.id.player_group_right_head_2nd_iv);
                ImageView right_player_3rd_head = group_right_view.findViewById(R.id.player_group_right_head_3rd_iv);
                TextView right_score = group_right_view.findViewById(R.id.player_group_right_score_tv);
                group_right_view.setBackgroundResource(mGame.getGameRes().getBottomItemGroupBg(1));
                right_score.setText(group_right.getScore() + "");
                scoreViews[1] = right_score;
                List<Player> right_player_list = group_right.getPlayerList();
                groupHeadViews[1] = new GroupHead(group_right, right_player_list.size());
                switch (right_player_list.size()) {
                    case 2:
                        right_player_1st_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_2v2_right_1st_bg);
                        right_player_2nd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_2v2_right_2nd_bg);
                        right_player_list.get(0).loadHead(right_player_1st_head);
                        right_player_list.get(1).loadHead(right_player_2nd_head);
                        groupHeadViews[1].playersHead[0] = right_player_1st_head;
                        groupHeadViews[1].playersHead[1] = right_player_2nd_head;
                        right_player_3rd_head.setVisibility(GONE);
                        break;
                    case 3:
                        right_player_1st_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_right_1st_bg);
                        right_player_2nd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_right_2nd_bg);
                        right_player_3rd_head.setBackgroundResource(R.mipmap.game_play_bottom_player_group_3v3_right_3rd_bg);
                        right_player_list.get(0).loadHead(right_player_1st_head);
                        right_player_list.get(1).loadHead(right_player_2nd_head);
                        right_player_list.get(2).loadHead(right_player_3rd_head);
                        groupHeadViews[1].playersHead[0] = right_player_1st_head;
                        groupHeadViews[1].playersHead[1] = right_player_2nd_head;
                        groupHeadViews[1].playersHead[2] = right_player_3rd_head;
                        break;
                }
                addView(group_right_view);
                LayoutParams params = (LayoutParams) group_right_view.getLayoutParams();
                if (params != null) {
                    params.leftMargin = 100;
                    group_right_view.setLayoutParams(params);
                }
            }
        }
    }

    class GroupHead {
        Group group;
        ImageView[] playersHead;

        GroupHead(Group group, int player_num) {
            this.group = group;
            playersHead = new ImageView[player_num];
        }

        void refresh(boolean is_current) {
            List<Player> player_list = group.getPlayerListOrderByHit();
            if (is_current) {
                int current_position = group.getCurrentPlayerHitPosition();
                for (int i = 0; i < playersHead.length; i++) {
                    player_list.get((current_position + i) % player_list.size()).loadHead(playersHead[i]);
                }
            } else {
                for (int i = 0; i < playersHead.length; i++) {
                    player_list.get(i).loadHead(playersHead[i]);
                }
            }
        }
    }
}
