package com.example.huangxinhui.erpapp.Information;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.huangxinhui.erpapp.Fragment.QueryFragment;
import com.example.huangxinhui.erpapp.JavaBean.Machine;
import com.example.huangxinhui.erpapp.JavaBean.Query;
import com.example.huangxinhui.erpapp.JavaBean.Wear;
import com.example.huangxinhui.erpapp.R;
import com.example.huangxinhui.erpapp.Util.JsonReader;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueryInformationActivity extends AppCompatActivity {


    @BindView(R.id.titleName)
    TextView titleName;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.pager)
    ViewPager pager;

    QueryFragment[] fragments;

    String[] titles;

    private Map<String, String> wear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_information);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ArrayList<Query.DataBean> list_data = (ArrayList<Query.DataBean>) getIntent().getExtras().getSerializable("data");
        wear = getWear(JSON.parseArray(JsonReader.getJson("wear.json", this), Wear.class));
        titleName.setText(getIntent().getExtras().getString("title") == null ? "" : getIntent().getExtras().getString("title"));
        fragments = new QueryFragment[list_data.size()];
        titles = new String[list_data.size()];
        for (int i = 0; i < list_data.size(); i++) {
            fragments[i] = QueryFragment.getInstance(list_data.get(i).getList_info());
            list_data.get(i).setName(wear.get(list_data.get(i).getName()));
            titles[i] = list_data.get(i).getName();
        }
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tlTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tlTab.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#40a9ff"));
        tlTab.setupWithViewPager(pager);
    }
    /**
     * 将Wear集合转化为Map，便于取值
     *
     * @param wears
     * @return
     */
    private Map<String, String> getWear(List<Wear> wears) {
        Map<String, String> result = new HashMap<>();
        for (Wear bean : wears) {
            result.put(bean.getValue(),bean.getKey());
        }
        return result;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
