package com.mwm.loyal.activity;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.adapter.FeedBackAdapter;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityListFeedbackBinding;
import com.mwm.loyal.imp.SubscribeListener;
import com.mwm.loyal.libs.swipback.utils.SwipeBackLayout;
import com.mwm.loyal.utils.DividerItemDecoration;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ListFeedBackActivity extends BaseSwipeActivity<ActivityListFeedbackBinding> implements View.OnClickListener, SubscribeListener<ResultBean>, SwipeMenuCreator, OnSwipeMenuItemClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.listFeedBack)
    SwipeMenuRecyclerView recyclerView;
    private List<FeedBackBean> beanList = new ArrayList<>();
    private FeedBackAdapter feedBackAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_list_feedback;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ResUtil.getBackground(this));
        queryHistory();
        initViews();
    }

    @Override
    public int setEdgePosition() {
        return SwipeBackLayout.EDGE_LEFT;
    }

    private void queryHistory() {
        String account = getIntent().getStringExtra("account");
        BaseProgressSubscriber<ResultBean> subscriber = new BaseProgressSubscriber<>(this, this);
        RetrofitManage.rxExecuted(subscriber.getSelfFeed(account), subscriber);
    }

    private void initViews() {
        pubMenu.setImageResource(R.drawable.src_sync_img);
        pubMenu.setOnClickListener(this);
        pubBack.setOnClickListener(this);
        pubTitle.setText("反馈历史");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));// 添加分割线。
        recyclerView.setSwipeMenuCreator(this);
        recyclerView.setSwipeMenuItemClickListener(this);// 监听拖拽，更新UI。
        recyclerView.setAdapter(feedBackAdapter = new FeedBackAdapter(this, beanList));
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_back:
                System.gc();
                finish();
                break;
            case R.id.pub_menu:
                beanList.clear();
                feedBackAdapter.refreshList(beanList);
                queryHistory();
                break;
        }
    }

    @Override
    public void onResult(int what, ResultBean resultBean) {
        try {
            if (1 == resultBean.getResultCode()) {
                List<FeedBackBean> beanList = GsonUtil.getBeanListFromJson(resultBean.getResultMsg(), FeedBackBean.class);
                this.beanList = null == beanList ? new ArrayList<FeedBackBean>() : beanList;
                if (null != feedBackAdapter) {
                    feedBackAdapter.refreshList(this.beanList);
                }
            }
        } catch (Exception e) {
            showDialog("解析历史反馈数据异常\n" + e.toString(), false);
        }
    }

    @Override
    public void onError(int what, Throwable e) {
        showErrorDialog(e.toString(), false);
    }

    @Override
    public void onCompleted(int what) {
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
            BaseProgressSubscriber<ResultBean> subscriber = new BaseProgressSubscriber<>(this, 2, new SubscribeListener<ResultBean>() {
                @Override
                public void onResult(int what, ResultBean resultBean) {
                    if (null != resultBean) {
                        if (1 == resultBean.getResultCode()) {
                            showToast("删除成功");
                            beanList.remove(adapterPosition);
                            feedBackAdapter.notifyItemRemoved(adapterPosition);
                        } else showDialog(resultBean.getResultMsg(), false);
                    } else showDialog("解析删除反馈数据失败", false);
                }

                @Override
                public void onError(int what, Throwable e) {
                    showDialog(e.toString(), false);
                }

                @Override
                public void onCompleted(int what) {

                }
            });
            RetrofitManage.rxExecuted(subscriber.deleteSelfFeed(beanList.get(adapterPosition).toString()), subscriber);
        }
    }
}
