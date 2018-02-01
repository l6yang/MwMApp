package com.mwm.loyal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseRecyclerViewHolder;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.impl.IContact;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import butterknife.BindView;

public class FeedBackAdapter extends SwipeMenuAdapter<FeedBackAdapter.ViewHolder> implements IContact {
    private List<FeedBackBean> beanList;
    private final LayoutInflater inflater;
    private BaseRecyclerViewHolder.OnItemClickListener itemClickListener;

    public FeedBackAdapter(Context context, List<FeedBackBean> list) {
        inflater = LayoutInflater.from(context);
        this.beanList = list;
    }

    public void refreshList(List<FeedBackBean> list) {
        this.beanList = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(BaseRecyclerViewHolder.OnItemClickListener clickListener) {
        itemClickListener = clickListener;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.item_feedback_list, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FeedBackBean feedBackBean = beanList.get(position);
        String time = IStr.replaceNull(feedBackBean.getTime());
        if (time.endsWith("00:00:00"))
            time = time.replace(" 00:00:00", "");
        holder.itemTime.setText(time);
        holder.itemContent.setText(feedBackBean.getContent());
    }

    @Override
    public int getItemCount() {
        return null == beanList ? 0 : beanList.size();
    }

    static class ViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.item_content)
        TextView itemContent;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
        }
    }
}
