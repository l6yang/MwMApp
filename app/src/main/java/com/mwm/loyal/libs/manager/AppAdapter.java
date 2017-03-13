package com.mwm.loyal.libs.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.imp.Contact;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private List<AppBean> beanList;
    private final Context context;
    private ExtractApkFileAsync mExtraAuth;
    private HandlerClass mHandler;

    public AppAdapter(Context context, List<AppBean> beanList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.beanList = beanList;
        mHandler = new HandlerClass(this);
    }

    public void refreshData(List<AppBean> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    public void clear() {
        beanList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appAdapterView = inflater.inflate(R.layout.item_list_share, parent, false);
        return new ViewHolder(appAdapterView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppBean appBean = beanList.get(position);
        holder.itemAppName.setText(appBean.getName());
        holder.itemPackage.setText(appBean.getApk());
        holder.imgIcon.setImageDrawable(appBean.getIcon());
        holder.btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractApk(appBean);
            }
        });
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsApp.copyFile(appBean);
                Intent shareIntent = UtilsApp.getShareIntent(UtilsApp.getOutputFilename(appBean));
                context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send_to), appBean.getName())));
            }
        });
    }

    private void extractApk(AppBean appBean) {
        if (mExtraAuth != null)
            return;
        mExtraAuth = new ExtractApkFileAsync(context, appBean,mHandler);
        mExtraAuth.execute();
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    final static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_appName)
        TextView itemAppName;
        @BindView(R.id.item_package)
        TextView itemPackage;
        @BindView(R.id.imgIcon)
        ImageView imgIcon;
        @BindView(R.id.btnExtract)
        Button btnExtract;
        @BindView(R.id.btnShare)
        Button btnShare;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class HandlerClass extends Handler {
        private final WeakReference<AppAdapter> weakReference;

        HandlerClass(AppAdapter appAdapter) {
            weakReference = new WeakReference<>(appAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppAdapter adapter = weakReference.get();
            switch (msg.what) {
                case Contact.Int.async2Null:
                    adapter.mExtraAuth = null;
                    break;
            }
        }
    }
}
