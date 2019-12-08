package com.mwm.loyal.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityShareBinding;
import com.mwm.loyal.libs.manager.AppAdapter;
import com.mwm.loyal.libs.manager.AppBean;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferUtil;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

public class ShareActivity extends BaseSwipeActivity<ActivityShareBinding> implements PullToRefreshView.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycle_list)
    RecyclerView recyclerView;
    @BindView(R.id.pull_to_refresh)
    PullToRefreshView pullToRefreshView;
    private QueryAppAsync mQueryAuth;
    private List<AppBean> appList = new ArrayList<>();
    private AppAdapter appAdapter;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_share;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        initViews();
        appAdapter = new AppAdapter(this, appList);
        recyclerView.setAdapter(appAdapter);
        queryList();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    private void queryList() {
        if (mQueryAuth != null)
            return;
        mQueryAuth = new QueryAppAsync();
        mQueryAuth.execute();
    }

    private void initViews() {
        toolbar.setTitle("分享");
        setSupportActionBar(toolbar);
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
            showProgressDialog();
            actualApps = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final PackageManager packageManager = getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            Set<String> hiddenApps = PreferUtil.getHiddenApps(ShareActivity.this);
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
                            AppBean tempApp = new AppBean(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, ContextCompat.getDrawable(ShareActivity.this, R.mipmap.icon), false);
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
