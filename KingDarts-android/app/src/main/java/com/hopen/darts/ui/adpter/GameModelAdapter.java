package com.hopen.darts.ui.adpter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameModelAdapter extends RecyclerView.Adapter<GameModelAdapter.Holder> {

    private int layout = 0;
    public int selected = 0;
    private BaseActivity baseActivity;
    private List<String> list;
    private AdapterListener l;

    public GameModelAdapter(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public GameModelAdapter(BaseActivity baseActivity,int layout) {
        this.layout = layout;
        this.baseActivity = baseActivity;
    }

    public void notifyDataSetChanged(int index) {
        selected = index;
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<String> list, int index) {
        this.list = list;
        notifyDataSetChanged(index);
    }

    public void setOnSelectListener(AdapterListener adpterListener){
        this.l = adpterListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layout != 0){
            return new Holder(baseActivity.getViewFromId(layout, null));
        }
        return new Holder(baseActivity.getViewFromId(R.layout.adapter_game_model, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String content = list.get(position);
        if (selected == position) {
            holder.flBg.setSelected(true);
            holder.tvGameName.setTextColor(baseActivity.getResources().getColor(R.color.white));
            if (l != null){
                l.onSelectGameModelItem(this,position);
            }
        } else {
            holder.flBg.setSelected(false);
            holder.tvGameName.setTextColor(baseActivity.getResources().getColor(R.color.text_white_80));
        }
        holder.tvGameName.setText(content);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_game_name)
        TextView tvGameName;
        @BindView(R.id.fl_bg)
        FrameLayout flBg;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
