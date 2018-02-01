package com.mwm.loyal.base;

import android.content.Context;
import android.view.View;

import com.loyal.base.adapter.ABasicListAdapter;
import com.mwm.loyal.impl.IContact;

import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseListAdapter<T, V extends ABasicListAdapter.ViewHolder> extends ABasicListAdapter<T, V> implements IContact {

    public BaseListAdapter(Context context) {
        super(context);
    }

    public BaseListAdapter(Context context, List<T> arrList) {
        super(context, arrList);
    }

    public BaseListAdapter(Context context, String json, Class<T> t, boolean fromRes) {
        super(context, json, t, fromRes);
    }

    public class ViewHolder extends ABasicListAdapter.ViewHolder {

        @Override
        public void bindViews(View view) {
            ButterKnife.bind(this, view);
        }

        public ViewHolder(View view) {
            super(view);
        }
    }
}
