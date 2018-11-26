package com.hopen.darts.ui;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.base.K;
import com.hopen.darts.utils.JumpIntent;
import com.hopen.darts.utils.SerialPort.KeyEventUtil;
import com.hopen.darts.utils.ToastUtils;
import com.hopen.darts.views.CustomFontTextView;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择先攻界面
 */
public class StartGameActivity extends BaseActivity {

    @BindView(R.id.gv_players)
    GridView gvPlayers;
    @BindView(R.id.iv_fx)
    ImageView ivFx;
    @BindView(R.id.iv_done)
    ImageView ivDone;

    private int[] bigImages = {R.mipmap.user_big_green,R.mipmap.user_big_yellow,R.mipmap.user_big_pueple,R.mipmap.user_big_blue};
    private int[] smallImages = {R.mipmap.user_small_green,R.mipmap.user_small_yellow,R.mipmap.user_small_pueple,R.mipmap.user_small_blue};
    private MyAdapter adapter = null;
    private int[] players;
    private PopupWindow popupWindow;
    private int lineCount = 0;
    private Boolean isShowAlert = false;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_start_game);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        int count = bundle.getInt(K.PlayerCount);
        int type = bundle.getInt(K.GameType);
        players = new int[count];
        if (type==1){
            initVSGridView(count);
            lineCount = count/2;
        }else {
            initNormalGridView(count);
            lineCount = count;
        }
        adapter = new MyAdapter();
        gvPlayers.setAdapter(adapter);
        gvPlayers.setSelector(R.color.transparent);
        gvPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.notifyDataSetChanged(i);
            }
        });
        adapter.notifyDataSetChanged(0);
    }


    public void showShareWindow(){
        View mContentView = null;
        if(mContentView == null){
            mContentView = getViewFromId(R.layout.dialog_start_game,null);
        }

        if(popupWindow == null){
            popupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            mContentView.setFocusableInTouchMode(true);
            mContentView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (C.app.debug && keyEvent.getAction() == KeyEvent.ACTION_UP){
                        return KeyEventUtil.onKeyDown(StartGameActivity.this,i);
                    }else {
                        return false;
                    }
                }
            });
        }
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
    private void initVSGridView(int count){
        if (count%2==0&&count/2<=smallImages.length){
            for (int i = 0; i < count; i++) {
                if (i<count/2){
                    players[i] = i;
                }else if (i==count/2) {
                    players[i] = i-1;
                }else {
                    players[i] = i-1-count/2;
                }
            }
        }else {
            for (int i = 0; i < count; i++) {
                if (i<smallImages.length){
                    players[i] = i;
                }else  {
                    players[i] = i-count/2;
                }
            }
        }
        gvPlayers.setNumColumns(count%2==0?count/2:(count/2+1));
    }

    private void initNormalGridView(int count){
        for (int i = 0; i < count; i++) {
            if (i<smallImages.length){
                players[i] = i;
            }else  {
                players[i] = i-smallImages.length;
            }
        }
        gvPlayers.setGravity(Gravity.CENTER_VERTICAL);
        gvPlayers.setNumColumns(count);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    class MyAdapter extends BaseAdapter{

        private int selected = -1;

        public void notifyDataSetChanged(int index) {
            selected = index;
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return players.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (i == selected){
                view = getViewFromId(R.layout.adapter_start_game_player_big,null);
                view.findViewById(R.id.rl_item).setBackgroundResource(bigImages[players[i]]);
            }else {
                view = getViewFromId(R.layout.adapter_start_game_player_small,null);
                view.findViewById(R.id.rl_item).setBackgroundResource(smallImages[players[i]]);

            }
            CustomFontTextView tv_player_name = (CustomFontTextView)view.findViewById(R.id.tv_player_name);
            tv_player_name.setText(C.player.common_name + (i + 1));
            ImageView icon = (ImageView)view.findViewById(R.id.iv_icon);
            Glide.with(StartGameActivity.this)
                    .load(C.player.BasePlayerImagePath + C.player.common_player + i + ".jpg")
                    .into(icon);
            return view;
        }
    }

    public void onKeyLeft(){
        if (isShowAlert)return;
        int index = adapter.selected;
        if (index % lineCount > 0){
            adapter.notifyDataSetChanged(index - 1);
        }
    }

    public void onKeyRight(){
        if (isShowAlert)return;
        int index = adapter.selected;
        if (index % lineCount < (lineCount - 1)){
            adapter.notifyDataSetChanged(index + 1);
        }
    }

    public void onKeyTop(){
        if (isShowAlert)return;
        int index = adapter.selected;
        if ((index + 1) > lineCount){
            adapter.notifyDataSetChanged(index - lineCount);
        }
    }

    public void onKeyBottom(){
        if (isShowAlert)return;
        int index = adapter.selected;
        if ((index + lineCount) < players.length){
            adapter.notifyDataSetChanged(index + lineCount);
        }
    }

    public void onKeyCancel(){
        if (isShowAlert){
            popupWindow.dismiss();
            isShowAlert = false;
        }
    }

    public void onKeyConfirm(){
        if (isShowAlert){
            ToastUtils.show("准备开始游戏");
            JumpIntent.jump(this,GameSettingActivity.class);
        }
        if (!isShowAlert){
            showShareWindow();
            isShowAlert = true;
        }

    }
}
