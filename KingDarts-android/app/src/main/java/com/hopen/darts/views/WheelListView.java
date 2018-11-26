package com.hopen.darts.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;



/**
 * 模仿滚轮动画缩放的ListView
 */
public class WheelListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "XuListView";

    /**
     * 中点的Y坐标
     */
    private float centerY = 0f;
    /**
     * 可视的item数
     */
    private int mVisibleItemCount = -1;
    /**
     * 没调整之前每个item的高度
     */
    private float olditemheight = 0;
    /**
     * 调整过后的每个item的高度
     */
    private float newitemheight = -1;
    /**
     * 当前选中项发生变化的监听者
     */
    private onSelectionChangeLisenter selectionChangeLisenter;
    /**
     * 当前选中项的序号
     */
    private int curPosition = -1;

    public WheelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置一个滚动监听
        setOnScrollListener(this);
    }

    /**
     * 设置选中项的监听者
     */
    public void setSelectionChangeLisenter(onSelectionChangeLisenter selectionChangeLisenter) {
        this.selectionChangeLisenter = selectionChangeLisenter;
    }

    /**
     * 设置ListView的显示item数
     *
     * @param count ：必须是奇数    如果为-1 则表示只是使用动画效果的普通ListView
     */
    public boolean setVisibleItemCount(int count) {
        if (count % 2 == 0) {
            return false;
        } else {
            mVisibleItemCount = count;
            return true;
        }

    }

    /**
     * 在这里第一次调整item高度
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mVisibleItemCount != -1) {
            getNewItemHeight();
            reSetItemHeight();
        }
    }

    /**
     * 调整每个可视的item的高度 以及对内容进行缩放
     */
    public void reSetItemHeight() {
        if (true)return;

        for (int i = 0; i < getChildCount(); i++) {
            //获取item
            View temp_view = getChildAt(i);
            //设置item的高度
            ViewGroup.LayoutParams lp = temp_view.getLayoutParams();
            lp.height = (int) newitemheight;
            temp_view.setLayoutParams(lp);
            if (temp_view instanceof ViewGroup) {
                ViewGroup temp_viewLl = (ViewGroup) temp_view;
                for (int j = 0; j < temp_viewLl.getChildCount(); j++) {
                    View tempItem = temp_viewLl.getChildAt(j);
                    tempItem.setScaleY((newitemheight / olditemheight) < 0 ? 0 : (newitemheight / olditemheight));
                    tempItem.setScaleX((newitemheight / olditemheight) < 0 ? 0 : (newitemheight / olditemheight));
                }

            }
        }
    }


    /**
     * 计算在给定的可视item数目下  每个item应该设置的高度
     */
    private void getNewItemHeight() {
        //先把旧的item存起来
        olditemheight = getChildAt(0).getHeight();
        //计算新的高度
        newitemheight = getHeight() / mVisibleItemCount;
        if ((getHeight() / mVisibleItemCount) % newitemheight > 0) {
            //除不尽的情况下把余数分给各个item，暂时发现分一次余数就够了，如果效果不理想就做个递归多分几次
            float remainder = (getHeight() / mVisibleItemCount) % newitemheight;
            newitemheight = remainder / mVisibleItemCount;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //滚动结束之后开始正常回滚item并记录最中间的item为选中项  (必须设置可视项，ListView才会改为选择器模式)
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mVisibleItemCount != -1) {
            //使离中间最近的item回滚到中点位置
            smoothScrollToPosition(getFirstVisiblePosition());
            //计算当前选中项的序号
            int nowPosition = getFirstVisiblePosition() + mVisibleItemCount / 2;
            //把当前选中项的序号存起来并通过listener回调出去
            if (selectionChangeLisenter != null && nowPosition != curPosition) {
                curPosition = nowPosition;
                selectionChangeLisenter.onSelectionChange(curPosition);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (int i = 0; i < visibleItemCount; i++) {
            //获取item
            View temp_view = getChildAt(i);
            int centerIndex = visibleItemCount/2;
            scaleView(temp_view,(Math.abs(centerIndex - i) + 1),i);
        }
        //计算中点
//        centerY = getHeight() / 2;
//        //判断中点的有效性
//        if (centerY <= 0) {
//            return;
//        }
//        //开始对当前显示的View进行缩放
//        for (int i = 0; i < visibleItemCount; i++) {
//            //获取item
//            View temp_view = getChildAt(i);
//            //计算item的中点Y坐标
//            float itemY = temp_view.getBottom() - (temp_view.getHeight() / 2);
//            //计算离中点的距离
//            float distance = centerY;
//            if (itemY > centerY) {
//                distance = itemY - centerY;
//            } else {
//                distance = centerY - itemY;
//            }
//            //根据距离进行缩放
//            temp_view.setScaleY(1.1f - (distance / centerY) < 0 ? 0 : 1.1f - (distance / centerY));
//            temp_view.setScaleX(1.1f - (distance / centerY) < 0 ? 0 : 1.1f - (distance / centerY));
//            //根据距离改变透明度
//            temp_view.setAlpha(1.1f - (distance / centerY) < 0 ? 0 : 1.1f - (distance / centerY));
//        }
    }

    private void scaleView(View view, int scale, int index ){
        View convertView = getAdapter().getView(getFirstVisiblePosition() + index,null,this);
        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 192 / (scale + 1));
        } else {
            layoutParams.height = 192 / (scale + 1);
        }
        convertView.setLayoutParams(layoutParams);
        ListAdapter listAdapter = getAdapter();
        view.setScaleX(1.f);
        view.setScaleY(1.f/scale);
        if (view instanceof ViewGroup) {
            ViewGroup temp_viewLl = (ViewGroup) view;
            for (int j = 0; j < temp_viewLl.getChildCount(); j++) {
                View tempItem = temp_viewLl.getChildAt(j);
                tempItem.setScaleX(1.f/scale);
                tempItem.setScaleY(1.f);
            }

        }
    }

    public interface onSelectionChangeLisenter {
        void onSelectionChange(int position);
    }


}
