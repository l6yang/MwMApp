package com.mwm.loyal.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseListAdapter;
import com.mwm.loyal.beans.CityBean;
import com.mwm.loyal.impl.IContact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AutoCompleteAdapter extends BaseListAdapter<CityBean, AutoCompleteAdapter.ViewHolder> implements Filterable, IContact {
    private ListFilter listFilter;
    private List<CityBean> filterList = new ArrayList<>();
    private final Object objLock = new Object();

    public AutoCompleteAdapter(Context context, List<CityBean> beanList) {
        super(context, beanList);
    }

    public List<CityBean> getFilterList() {
        return filterList;
    }

    @Override
    public int getCount() {
        return filterList == null ? 0 : filterList.size();
    }

    @Override
    public CityBean getItem(int position) {
        return filterList == null ? null : filterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected int adapterLayout() {
        return R.layout.item_grid_city;
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onViewHolder(ViewHolder holder, int position) {
        CityBean cityBean = filterList.get(position);
        String itemStr = cityBean == null ? "" : StrImpl.replaceNull(cityBean.getCityName());
        holder.itemSpinner.setText(itemStr);
    }

    class ViewHolder extends BaseListAdapter.ViewHolder {
        @BindView(R.id.item_grid_city)
        TextView itemSpinner;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CityBean> beanList = getArrList();
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                synchronized (objLock) {
                    List<CityBean> objLists = new ArrayList<>(beanList);
                    results.values = objLists;
                    results.count = objLists.size();
                }
            } else {
                String filterStr = constraint.toString().toUpperCase();
                int count = beanList.size();
                List<CityBean> newList = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    CityBean objList = beanList.get(i);
                    String cityName = objList == null ? "" : StrImpl.replaceNull(objList.getCityName());
                    String cityLetter = objList == null ? "" : StrImpl.replaceNull(objList.getCityLetter());
                    if (cityName.toUpperCase().startsWith(filterStr) || cityName.contains(filterStr) || TextUtils.equals(filterStr, cityLetter)) {
                        newList.add(objList);
                    }
                }
                results.values = newList;
                results.count = newList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {//有符合过滤规则的数据
                filterList = (List<CityBean>) results.values;
                notifyDataSetChanged();
            } else {//没有符合过滤规则的数据
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }
    }
}