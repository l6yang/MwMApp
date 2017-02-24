package com.mwm.loyal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseListAdapter;
import com.mwm.loyal.beans.CityBean;
import com.mwm.loyal.base.SectionedBaseAdapter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class PinnedCityAdapter extends SectionedBaseAdapter {
    private final LayoutInflater inflater;
    private LinkedHashMap<String, List<CityBean>> linkedHashMap;

    public PinnedCityAdapter(Context context, LinkedHashMap<String, List<CityBean>> list) {
        inflater = LayoutInflater.from(context);
        linkedHashMap = list;
    }

    public void refreshData(LinkedHashMap<String, List<CityBean>> list) {
        linkedHashMap = list;
        notifyDataSetChanged();
    }

    @Override
    public int getSectionCount() {
        return linkedHashMap == null ? 0 : linkedHashMap.size();
    }

    @Override
    public int getCountForSection(int section) {
        Iterator localIterator = linkedHashMap.entrySet().iterator();
        for (int i = 0; localIterator.hasNext(); i++) {
            Map.Entry localEntry = (Map.Entry) localIterator.next();
            if (i == section)
                return ((List) localEntry.getValue()).size();
        }
        return 0;
    }

    @Override
    public Object getItem(int section, int position) {
        Iterator localIterator = linkedHashMap.entrySet().iterator();
        for (int i = 0; localIterator.hasNext(); i++) {
            Map.Entry localEntry = (Map.Entry) localIterator.next();
            if (i == section)
                return ((List) localEntry.getValue()).get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        Iterator localIterator = linkedHashMap.entrySet().iterator();
        for (int i = 0; localIterator.hasNext(); i++) {
            position += 1 + ((List) ((Map.Entry) localIterator.next()).getValue()).size();
            if (i == section)
                return position;
        }
        return 0;
    }

    /***
     * 子
     */
    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_city_list, parent, false);
            viewHolderItem = new ViewHolderItem(convertView);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }
        Iterator localIterator = linkedHashMap.entrySet().iterator();
        for (int i = 0; localIterator.hasNext(); i++) {
            Map.Entry localEntry = (Map.Entry) localIterator.next();
            if (i == section)
                viewHolderItem.cityName.setText(((CityBean) ((List) localEntry.getValue()).get(position)).getCityName());
        }
        return convertView;
    }

    /**
     * 组
     */
    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        ViewHolderHead viewHolderHead;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_header_pinned, parent, false);
            viewHolderHead = new ViewHolderHead(convertView);
            convertView.setTag(viewHolderHead);
        } else {
            viewHolderHead = (ViewHolderHead) convertView.getTag();
        }
        Iterator localIterator = linkedHashMap.entrySet().iterator();
        for (int i = 0; localIterator.hasNext(); i++) {
            Map.Entry localEntry = (Map.Entry) localIterator.next();
            if (i == section)
                viewHolderHead.cityLetter.setText((CharSequence) localEntry.getKey());
        }
        return convertView;
    }

    //组
    static class ViewHolderHead extends BaseListAdapter.ViewHolder {
        @BindView(R.id.item_cityLetter_list)
        TextView cityLetter;

        ViewHolderHead(View view) {
            super(view);
        }
    }

    //子项
    static class ViewHolderItem extends BaseListAdapter.ViewHolder {

        @BindView(R.id.item_cityName_list)
        TextView cityName;

        ViewHolderItem(View view) {
            super(view);
        }
    }
}
