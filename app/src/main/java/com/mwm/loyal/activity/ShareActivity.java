package com.mwm.loyal.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityShareBinding;
import com.mwm.loyal.libs.manager.AppAdapter;
import com.mwm.loyal.libs.manager.AppBean;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.ResUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

public class ShareActivity extends BaseSwipeActivity<ActivityShareBinding> implements View.OnClickListener, PullToRefreshView.OnRefreshListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.recycle_list)
    RecyclerView recyclerView;
    @BindView(R.id.pull_to_refresh)
    PullToRefreshView pullToRefreshView;
    private QueryAppAsync mQueryAuth;
    private List<AppBean> appList = new ArrayList<>();
    private AppAdapter appAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_share;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
        appAdapter = new AppAdapter(this, appList);
        recyclerView.setAdapter(appAdapter);
        queryList();
    }

    private void queryList() {
        if (mQueryAuth != null)
            return;
        mQueryAuth = new QueryAppAsync();
        mQueryAuth.execute();
    }

    private void initViews() {
        pubMenu.setVisibility(View.GONE);
        pubTitle.setText("分享");
        pubBack.setOnClickListener(this);
        pullToRefreshView.setEnabled(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        pullToRefreshView.setOnRefreshListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        appAdapter.clear();
        queryList();
        pullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshView.setRefreshing(false);
            }
        }, 2000);
    }

    private class QueryAppAsync extends AsyncTask<Void, Integer, Void> {
        private int totalApps;
        private int actualApps;

        QueryAppAsync() {
            showDialog();
            actualApps = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final PackageManager packageManager = getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            Set<String> hiddenApps = PreferencesUtil.getHiddenApps(ShareActivity.this);
            totalApps = packages.size() + hiddenApps.size();
            Collections.sort(packages, new Comparator<PackageInfo>() {
                @Override
                public int compare(PackageInfo p1, PackageInfo p2) {
                    return packageManager.getApplicationLabel(p1.applicationInfo).toString().toLowerCase().compareTo(packageManager.getApplicationLabel(p2.applicationInfo).toString().toLowerCase());
                }
            });

            for (PackageInfo packageInfo : packages) {
                if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        try {
                            AppBean tempApp = new AppBean(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
                            appList.add(tempApp);
                        } catch (OutOfMemoryError e) {
                            AppBean tempApp = new AppBean(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getDrawable(R.mipmap.icon), false);
                            appList.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                actualApps++;
                publishProgress((actualApps * 100) / totalApps);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (null != progressDialog)
                progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            disMissDialog();
            mQueryAuth = null;
            appAdapter.refreshData(appList);
            pullToRefreshView.setEnabled(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mQueryAuth = null;
        }
    }
}
