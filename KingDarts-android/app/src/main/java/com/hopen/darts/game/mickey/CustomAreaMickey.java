package com.hopen.darts.game.mickey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseAdaptor;
import com.hopen.darts.game.base.GameCustomArea;
import com.hopen.darts.game.base.Group;
import com.hopen.darts.game.base.Player;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 2018/6/28.
 */

public class CustomAreaMickey extends GameCustomArea<GameMickey> {

    @BindView(R.id.score_area_lv)
    ListView scoreAreaLv;

    FirstAdapter firstAdapter;

    protected CustomAreaMickey(FrameLayout parent, GameMickey game) {
        super(parent, game);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.view_game_play_custom_area_mickey);
        ButterKnife.bind(this, parentLayout);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initControl() {
        firstAdapter = new FirstAdapter(mContext);
        scoreAreaLv.setAdapter(firstAdapter);
        firstAdapter.refresh(mGame.occupyScoreArea);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onRefresh() {
        firstAdapter.refresh(mGame.occupyScoreArea);
    }

    class FirstAdapter extends BaseAdaptor<ScoreArea> {

        public FirstAdapter(Context context) {
            super(context);
        }

        @Override
        public void refresh(List<ScoreArea> list) {
            mList.clear();
            mList.add(new ScoreArea(-1));
            notifyDataSetChanged();
            loadMore(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder mHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_game_play_custom_area_mickey_first, null);
                mHolder = new ViewHolder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            ScoreArea item = getItem(position);
            SecondAdapter adapter = new SecondAdapter(item);
            mHolder.scoreAreaRv.setAdapter(adapter);
            if (item.isDeath(mGame.getGameMode()))
                mHolder.deathIv.setVisibility(View.VISIBLE);
            else
                mHolder.deathIv.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            RecyclerView scoreAreaRv;
            ImageView deathIv;

            ViewHolder(View view) {
                scoreAreaRv = view.findViewById(R.id.score_area_rv);
                deathIv = view.findViewById(R.id.death_iv);
            }
        }
    }

    class SecondAdapter extends RecyclerView.Adapter<SecondAdapter.ViewHolder> {
        ScoreArea mData;
        List<Group> mList;
        int centerPosition;

        SecondAdapter(ScoreArea data) {
            mData = data;
            mList = mGame.getGroupList();
            centerPosition = getItemCount() / 2;
        }

        @NonNull
        @Override
        public SecondAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(mContext,
                    R.layout.item_game_play_custom_area_mickey_second, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SecondAdapter.ViewHolder holder, int position) {
            Group group;
            if (position < centerPosition) {
                group = mList.get(position);
            } else if (position > centerPosition) {
                group = mList.get(position - 1);
            } else {
                group = null;
            }
            if (group == null) {//显示分区
                ViewGroup.LayoutParams params = holder.contentTv.getLayoutParams();
                if (params == null)
                    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = 196;
                holder.contentTv.setLayoutParams(params);
                holder.contentTv.setVisibility(View.VISIBLE);
                holder.timesIv.setVisibility(View.INVISIBLE);
                if (mData.isDeath(mGame.getGameMode())) {
                    holder.contentTv.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                } else
                    holder.contentTv.setTextColor(ContextCompat.getColor(mContext, R.color.theme_color));
                if (mData.score == -1) {
                    holder.contentTv.setText("");
                } else {
                    if (mData.score == 25)
                        holder.contentTv.setText("BULL");
                    else
                        holder.contentTv.setText(mData.score + "");
                }
            } else {
                ViewGroup.LayoutParams params = holder.contentTv.getLayoutParams();
                if (params == null)
                    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.contentTv.setLayoutParams(params);
                if (mData.score == -1) {//显示玩家名
                    holder.contentTv.setVisibility(View.VISIBLE);
                    holder.timesIv.setVisibility(View.INVISIBLE);
                    if (mData.isDeath(mGame.getGameMode())) {
                        holder.contentTv.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                    } else
                        holder.contentTv.setTextColor(ContextCompat.getColor(mContext, R.color.theme_color));
                    Player player = group.getPlayerList().get(0);
                    holder.contentTv.setText("P" + player.getId());
                } else {
                    holder.contentTv.setVisibility(View.INVISIBLE);
                    holder.timesIv.setVisibility(View.VISIBLE);
                    for (ScoreArea.Occupy occupy : mData.occupyList) {
                        if (occupy.id == group.getId()) {
                            if (occupy.num >= 3) {
                                if (mData.isDeath(mGame.getGameMode())) {
                                    holder.timesIv.setImageResource(R.mipmap.mickey_round_item_three_times_gray);
                                } else
                                    holder.timesIv.setImageResource(R.mipmap.mickey_round_item_three_times);
                            } else if (occupy.num == 2) {
                                holder.timesIv.setImageResource(R.mipmap.mickey_round_item_two_times);
                            } else if (occupy.num == 1) {
                                holder.timesIv.setImageResource(R.mipmap.mickey_round_item_one_times);
                            } else {
                                holder.timesIv.setImageResource(R.color.transparent);
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList.size() + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView contentTv;
            ImageView timesIv;

            ViewHolder(View view) {
                super(view);
                contentTv = view.findViewById(R.id.content_tv);
                timesIv = view.findViewById(R.id.times_iv);
            }
        }
    }
}
