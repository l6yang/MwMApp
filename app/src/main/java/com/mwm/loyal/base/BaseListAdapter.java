package com.mwm.loyal.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import com.mwm.loyal.impl.Contact;

import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseListAdapter<T> extends BaseAdapter implements Contact {
    protected final LayoutInflater inflater;
    private List<T> arrList;

    public BaseListAdapter(Context context, List<T> arrList) {
        inflater = LayoutInflater.from(context);
        this.arrList = arrList;
    }

    public void refreshList(List<T> arrList) {
        this.arrList = arrList;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return arrList == null ? null : arrList.get(position);
    }

    @Override
    public int getCount() {
        return arrList == null ? 0 : arrList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected List<T> getArrList() {
        return arrList;
    }

    protected String replaceNull(CharSequence sequence) {
        return Str.replaceNull(sequence);
    }

    public static class ViewHolder {

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
