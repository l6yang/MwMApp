package com.mwm.loyal.activity;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.loyal.kit.GsonUtil;
import com.loyal.kit.Low2UpCase;
import com.loyal.kit.ResUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.adapter.AutoCompleteAdapter;
import com.mwm.loyal.adapter.PinnedCityAdapter;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.CityBean;
import com.mwm.loyal.databinding.ActivityCityBinding;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.SortCity;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.views.PinnedHeaderListView;
import com.mwm.loyal.views.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class CityActivity extends BaseSwipeActivity<ActivityCityBinding> implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_cityName)
    AutoCompleteTextView editCity;
    @BindView(R.id.view_layout)
    View viewQuery;
    @BindView(R.id.query_cancel)
    AppCompatTextView textCancel;
    @BindView(R.id.text_city_query)
    AppCompatTextView textCity;
    @BindView(R.id.city_pinned_listView)
    PinnedHeaderListView cityPinnedView;
    @BindView(R.id.sidebar)
    SideBar sideBar;
    @BindView(R.id.sidebar_dialog)
    AppCompatTextView textSideBar;
    private PinnedCityAdapter pinnedCityAdapter;
    private LinkedHashMap<String, List<CityBean>> linkedHashMap = new LinkedHashMap<>();
    private AutoCompleteAdapter completeAdapter;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_city;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        initViews();
        initData();
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    private void initViews() {
        toolbar.setTitle("选择城市");
        setSupportActionBar(toolbar);
        changeInput(false);
    }

    private void initData() {
        editCity.setAdapter(completeAdapter = new AutoCompleteAdapter(this, new ArrayList<CityBean>()));
        cityPinnedView.setAdapter(pinnedCityAdapter = new PinnedCityAdapter(this, linkedHashMap));
        String json = ResUtil.assetsFile2String(CityActivity.this, "json/allCity.json");
        List<CityBean> beanList = GsonUtil.json2BeanList(json, CityBean.class);
        Collections.sort(beanList, new SortCity());
        for (CityBean cityBean : beanList) {
            String letter = cityBean.getCityLetter().toUpperCase();
            List<CityBean> getList = linkedHashMap.get(letter);
            if (getList == null) {
                getList = new ArrayList<>();
                linkedHashMap.put(letter, getList);
            }
            getList.add(cityBean);
        }
        pinnedCityAdapter.refreshData(linkedHashMap);
        completeAdapter.notifyList(beanList);
        sideBar.setTextView(textSideBar);
        sideBar.setOnTouchingLetterChangedListener(this);
        cityPinnedView.setOnItemClickListener(new ItemClickListener());
        editCity.setOnItemClickListener(onCompleteItemClick);
        editCity.setTransformationMethod(new Low2UpCase());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_city_query:
                changeInput(true);
                break;
            case R.id.query_cancel:
                changeInput(false);
                break;
            case R.id.view_layout:
                changeInput(false);
                break;
        }
    }

    private void changeInput(boolean isQuery) {
        editCity.setVisibility(isQuery ? View.VISIBLE : View.GONE);
        textCity.setVisibility(isQuery ? View.GONE : View.VISIBLE);
        textCancel.setVisibility(isQuery ? View.VISIBLE : View.GONE);
        viewQuery.setVisibility(isQuery ? View.VISIBLE : View.GONE);
        /*adjustPan时不需要，adjustResize需要设置如下*/
        //sideBar.setVisibility(isQuery ? View.GONE : View.VISIBLE);
        if (isQuery) {
            ToastUtil.showInputPan(this);
            editCity.requestFocus();
        } else {
            editCity.getText().clear();
            hideKeyBoard(editCity);
        }
    }

    @Override
    public void onTouchingLetterChanged(String str) {
        Iterator iterator = linkedHashMap.entrySet().iterator();
        int i = 0;
        while (true) {
            Map.Entry localEntry;
            if (iterator.hasNext()) {
                localEntry = (Map.Entry) iterator.next();
                if ((localEntry.getKey()).equals(str)) {
                    cityPinnedView.setSelection(i);
                }
            } else {
                return;
            }
            i += 1 + ((List) localEntry.getValue()).size();
        }
    }

    private AdapterView.OnItemClickListener onCompleteItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            List<CityBean> filterList = completeAdapter.getFilterList();
            CityBean cityBean = filterList.get(position);
            back2Last(cityBean);
        }
    };

    private class ItemClickListener extends PinnedHeaderListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
            CityBean cityBean = (CityBean) pinnedCityAdapter.getItem(section, position);
            back2Last(cityBean);
        }

        @Override
        public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
        }
    }

    private void back2Last(CityBean cityBean) {
        if (null == cityBean || TextUtils.isEmpty(cityBean.getCityName()))
            return;
        final String cityName = cityBean.getCityName();
        editCity.setText(cityName);
        editCity.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("cityName", cityName);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 500);
    }
}