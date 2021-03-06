package com.mwm.loyal.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyal.kit.TimeUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseRecyclerViewHolder;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.impl.IContactImpl;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import butterknife.BindView;

public class FeedBackAdapter extends SwipeMenuAdapter<FeedBackAdapter.ViewHolder> implements IContactImpl {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedBackBean feedBackBean = beanList.get(position);
        String time = TimeUtil.subEndTime(feedBackBean.getTime());
        holder.itemTime.setText(time);
        holder.itemContent.setText(feedBackBean.getContent());
    }

    @Override
    public int getItemCount() {
        return null == beanList ? 0 : beanList.size();
    }

    class ViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.item_content)
        TextView itemContent;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
        }
    }
}
