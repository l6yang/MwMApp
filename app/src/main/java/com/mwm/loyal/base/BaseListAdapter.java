package com.mwm.loyal.base;

import android.content.Context;
import android.view.View;

import com.loyal.basex.adapter.ABasicListAdapter;
import com.loyal.basex.adapter.ABasicListViewHolder;
import com.mwm.loyal.impl.IContactImpl;

import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseListAdapter<T, VH extends BaseListAdapter.ViewHolder> extends ABasicListAdapter<T, VH> implements IContactImpl {

    public BaseListAdapter(Context context) {
        super(context);
    }

    public BaseListAdapter(Context context, List<T> arrList) {
        super(context, arrList);
    }

    public BaseListAdapter(Context context, String json, Class<T> t, boolean fromRes) {
        super(context, json, t, fromRes);
    }

    public class ViewHolder extends ABasicListViewHolder {

        @Override
        public void bindView(View view) {
            ButterKnife.bind(this, view);
        }

        public ViewHolder(View view) {
            super(view);
        }
    }
}
