package com.mwm.loyal.activity;

import android.graphics.Color;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.loyal.kit.GsonUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.adapter.FeedBackAdapter;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityListFeedbackBinding;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.DividerItemDecoration;
import com.mwm.loyal.utils.ImageUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ListFeedBackActivity extends BaseSwipeActivity<ActivityListFeedbackBinding> implements RxSubscriberListener<String>, SwipeMenuCreator, OnSwipeMenuItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listFeedBack)
    SwipeMenuRecyclerView recyclerView;
    private List<FeedBackBean> beanList = new ArrayList<>();
    private FeedBackAdapter feedBackAdapter;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_list_feedback;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("反馈历史");
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
        queryHistory();
        initViews();
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    private void queryHistory() {
        String account = getIntent().getStringExtra("account");
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this, "192.168.0.110");
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.getFeedback(account), subscriber);
    }

    private void initViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));// 添加分割线。
        recyclerView.setSwipeMenuCreator(this);
        recyclerView.setSwipeMenuItemClickListener(this);// 监听拖拽，更新UI。
        recyclerView.setAdapter(feedBackAdapter = new FeedBackAdapter(this, beanList));
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        try {
            ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
            if (TextUtils.equals("1", resultBean.getCode())) {
                this.beanList = GsonUtil.json2List(resultBean.getMessage(), FeedBackBean.class);
                if (null != feedBackAdapter) {
                    feedBackAdapter.refreshList(this.beanList);
                }
            }
        } catch (Exception e) {
            showDialog("解析历史反馈数据异常\n" + e.toString(), false);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog(e.toString(), false);
    }

    @Override
    public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
        int width = getResources().getDimensionPixelSize(R.dimen.item_height);
        // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        SwipeMenuItem deleteItem = new SwipeMenuItem(this)
                .setBackgroundDrawable(android.R.color.holo_red_light)
                .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);
    }

    @Override
    public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
        closeable.smoothCloseMenu();// 关闭被点击的菜单。
        // 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
        if (menuPosition == 0) {// 删除按钮被点击。
            RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this, "192.168.0.110");
            subscriber.setSubscribeListener(new RxSubscriberListener<String>() {
                @Override
                public void onResult(int what, Object tag, String result) {
                    ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
                    if (null != resultBean) {
                        if (TextUtils.equals("1", resultBean.getCode())) {
                            showToast("删除成功");
                            beanList.remove(adapterPosition);
                            feedBackAdapter.notifyItemRemoved(adapterPosition);
                        } else showDialog(resultBean.getMessage(), false);
                    } else showDialog("解析删除反馈数据失败", false);
                }

                @Override
                public void onError(int what, Object tag, Throwable e) {
                    showDialog(e.toString(), false);
                }
            });
            RxUtil.rxExecute(subscriber.deleteFeedback(beanList.get(adapterPosition).toString()), subscriber);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                beanList.clear();
                feedBackAdapter.refreshList(beanList);
                queryHistory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
