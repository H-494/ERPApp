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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huangxinhui.erpapp.Fragment.IntoFragment;
import com.example.huangxinhui.erpapp.Fragment.MoveFragment;
import com.example.huangxinhui.erpapp.JavaBean.Query;
import com.example.huangxinhui.erpapp.R;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntoInformationActivity extends AppCompatActivity {

    @BindView(R.id.titleName)
    TextView titleName;
    @BindView(R.id.imageTop)
    LinearLayout imageTop;
    @BindView(R.id.pager)
    ViewPager pager;
    Fragment[] fragments;
    String[] titles;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into_information);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ArrayList<Query.DataBean> list_data = (ArrayList<Query.DataBean>) getIntent().getExtras().getSerializable("data");
        titleName.setText(getIntent().getExtras().getString("title") == null ? "" : getIntent().getExtras().getString("title"));
        fragments = new IntoFragment[list_data.size()];
        titles = new String[list_data.size()];
        for (int i = 0; i < list_data.size(); i++) {
            fragments[i] = IntoFragment.getInstance(list_data.get(i).getList_info(),list_data.get(i).getName());
            titles[i] = list_data.get(i).getName();
        }
        pager.setAdapter(new IntoInformationActivity.MyPagerAdapter(getSupportFragmentManager()));
        tlTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tlTab.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#40a9ff"));
        tlTab.setupWithViewPager(pager);
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

    @OnClick({R.id.back, R.id.tl_tab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tl_tab:
                break;
        }
    }
}
