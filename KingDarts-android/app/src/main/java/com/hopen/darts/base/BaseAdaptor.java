package com.hopen.darts.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 2017/12/25.
 */

public abstract class BaseAdaptor<E> extends BaseAdapter {
    protected Context mContext;
    protected List<E> mList;

    public BaseAdaptor(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public BaseAdaptor(Context context, List<E> list) {
        mContext = context;
        if (list != null)
            mList = list;
        else
            mList = new ArrayList<>();
    }

    public void refresh(List<E> list) {
        mList.clear();
        notifyDataSetChanged();
        loadMore(list);
    }

    public void loadMore(List<E> list) {
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public E getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
