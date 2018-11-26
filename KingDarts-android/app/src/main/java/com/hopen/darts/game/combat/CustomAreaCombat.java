package com.hopen.darts.game.combat;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.utils.Animation.AnimationListener;
import com.hopen.darts.utils.Animation.AnimationView;
import com.hopen.darts.utils.Animation.enums.AnimationType;
import com.hopen.darts.utils.Animation.impl.AnimationSetting;
import com.hopen.darts.views.GameCombatHealthView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 拳王格斗赛中间区域
 */

public class CustomAreaCombat extends GameCustomArea<GameCombat> {

    @BindView(R.id.fighter_rv)
    RecyclerView fighterRv;

    FighterAdapter fighterAdapter;

    protected CustomAreaCombat(FrameLayout parent, GameCombat game) {
        super(parent, game);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.view_game_play_custom_area_combat);
        ButterKnife.bind(this, parentLayout);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initControl() {
        fighterRv.addItemDecoration(new SpacesItemDecoration(30));
        fighterAdapter = new FighterAdapter();
        fighterRv.setAdapter(fighterAdapter);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onRefresh() {
        fighterAdapter.notifyDataSetChanged();
    }

    private boolean isWoman(AnimationType.Sex sex) {
        return sex == AnimationType.Sex.Woman;
    }

    private void showDefault(AnimationView img, AnimationType.Sex sex) {
        img.setConfig(AnimationSetting.setting(AnimationType.WoManBeatenStatus.LevelOne, sex));
        img.onAnimationListener(null);
        img.setRepeat(true);
        img.play(0);
    }

    class FighterAdapter extends RecyclerView.Adapter<FighterAdapter.ViewHolder> {

        @NonNull
        @Override
        public FighterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(mContext,
                    R.layout.item_game_play_custom_area_combat, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FighterAdapter.ViewHolder holder, int position) {
            Fighter fighter = mGame.fighters[position];
            holder.healthHv.setHealth(mGame.getFighterGroup(fighter).getScore(), mGame.getGameRule().getOriginScore());
            //性别
            final AnimationType.Sex sex = position % 2 == 0 ? AnimationType.Sex.Woman : AnimationType.Sex.Man;
            //减血状态临时变量
            AnimationType.ManBeatenStatus man_beaten;
            AnimationType.WoManBeatenStatus woman_beaten;
            //减血状态变量赋值
            switch (Math.abs(mGame.lastCurrentGroupHitState)) {
                case 2://双倍区
                    man_beaten = AnimationType.ManBeatenStatus.LevelTwo;
                    woman_beaten = AnimationType.WoManBeatenStatus.LevelThree;
                    break;
                case 3://三倍区
                    man_beaten = AnimationType.ManBeatenStatus.LevelThree;
                    woman_beaten = AnimationType.WoManBeatenStatus.LevelFour;
                    break;
                case 1://单倍区
                default:
                    man_beaten = AnimationType.ManBeatenStatus.LevelOne;
                    woman_beaten = AnimationType.WoManBeatenStatus.LevelTwo;
                    break;
            }
            if (mGame.lastCurrentGroupHitState < 0 && mGame.lastBruiseGroup != null) {//说明最近一次的格斗信息是减血
                //减血动画播放
                if (fighter.groupId == mGame.getCurrentGroup().getId()) {//当前是本方正在攻击
                    if (isWoman(sex)) {//本方是女方
                        holder.bodyAv.setConfig(AnimationSetting.setting(man_beaten, sex));
                    } else {//本方是男方
                        holder.bodyAv.setConfig(AnimationSetting.setting(woman_beaten, sex));
                    }
                    holder.bodyAv.onAnimationListener(new TAnimationListener(holder.bodyAv, sex));
                    holder.bodyAv.setRepeat(false);
                    holder.bodyAv.play(0);
                } else if (fighter.groupId == mGame.lastBruiseGroup.getId()) {//当前是本方受到攻击
                    if (isWoman(sex)) {//本方是女方
                        holder.bodyAv.setConfig(AnimationSetting.setting(woman_beaten, sex));
                    } else {//本方是男方
                        holder.bodyAv.setConfig(AnimationSetting.setting(man_beaten, sex));
                    }
                    holder.bodyAv.onAnimationListener(new TAnimationListener(holder.bodyAv, sex));
                    holder.bodyAv.setRepeat(false);
                    holder.bodyAv.play(0);
                } else showDefault(holder.bodyAv, sex);//显示默认状态
            } else if (mGame.lastCurrentGroupHitState > 0) {//说明最近一次的格斗信息是加血
                showDefault(holder.bodyAv, sex);//所有人都显示默认状态
                if (fighter.groupId == mGame.getCurrentGroup().getId()) {//给本方加血
                    holder.cureAv.setConfig(AnimationSetting.setting(AnimationType.Module.AddBlood));
                    holder.cureAv.play(0);
                }
            } else showDefault(holder.bodyAv, sex);//没有最近状态，所有人都显示默认状态
            holder.score1Tv.setText(fighter.scoreArea[0] + "");
            holder.score2Tv.setText(fighter.scoreArea[1] + "");
            holder.score3Tv.setText(fighter.scoreArea[2] + "");
        }

        @Override
        public int getItemCount() {
            return mGame.fighters.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            GameCombatHealthView healthHv;
            AnimationView bodyAv;
            AnimationView cureAv;
            TextView score1Tv;
            TextView score2Tv;
            TextView score3Tv;

            ViewHolder(View view) {
                super(view);
                healthHv = view.findViewById(R.id.health_hv);
                bodyAv = view.findViewById(R.id.body_av);
                cureAv = view.findViewById(R.id.cure_av);
                score1Tv = view.findViewById(R.id.score1_tv);
                score2Tv = view.findViewById(R.id.score2_tv);
                score3Tv = view.findViewById(R.id.score3_tv);
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.left = space;
        }
    }

    class TAnimationListener implements AnimationListener {
        AnimationView bodyAv;
        AnimationType.Sex sex;

        TAnimationListener(AnimationView bodyAv, AnimationType.Sex sex) {
            this.bodyAv = bodyAv;
            this.sex = sex;
        }

        @Override
        public void onAnimationEnd() {
            showDefault(bodyAv, sex);
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationRepeat() {

        }

        @Override
        public void onAnimationIndex(int currentIndex) {

        }
    }
}
