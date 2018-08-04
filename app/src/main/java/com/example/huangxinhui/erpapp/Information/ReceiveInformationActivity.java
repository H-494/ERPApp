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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huangxinhui.erpapp.Fragment.ReceiveFragment;
import com.example.huangxinhui.erpapp.JavaBean.Machine;
import com.example.huangxinhui.erpapp.JavaBean.Query;
import com.example.huangxinhui.erpapp.JavaBean.Receive;
import com.example.huangxinhui.erpapp.JavaBean.Wear;
import com.example.huangxinhui.erpapp.R;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveInformationActivity extends AppCompatActivity {

    ArrayList<Receive.DataBean.InfosBean> list_data;

    ArrayList<Receive.DataBean.CfyBean.ListInfoBean> list_cfy;

    private Map<String, String> data_map;

    @BindView(R.id.titleName)
    TextView titleName;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.pager)
    ViewPager pager;

    ReceiveFragment[] fragments;

    String[] titles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_information);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        list_data = (ArrayList<Receive.DataBean.InfosBean>) getIntent().getExtras().getSerializable("data");
        list_cfy = (ArrayList<Receive.DataBean.CfyBean.ListInfoBean>) getIntent().getExtras().getSerializable("cfy");
        titleName.setText(list_data.get(0).getTabs().getList_info().get(3).getValue() == null ? "" : list_data.get(0).getTabs().getList_info().get(3).getValue() );
        fragments = new ReceiveFragment[list_data.size()];
        titles = new String[list_data.size()];
        for (int i = 0; i < list_data.size(); i++) {
            fragments[i] = ReceiveFragment.getInstance(
                    (ArrayList<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX>) list_data.get(i).getTabs().getList_info(),
                    (ArrayList<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX>) list_data.get(i).getZbs().getList_info(),
                    list_cfy
            );
            titles[i] = list_data.get(i).getTabs().getList_info().get(0).getValue();
        }
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
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

    /**
     * 将Wear集合转化为Map，便于取值
     *
     * @param wears
     * @return
     */
    private Map<String, String> getWear(List<Wear> wears) {
        Map<String, String> result = new HashMap<>();
        for (Wear bean : wears) {
            result.put(bean.getValue(), bean.getKey());
        }
        return result;
    }

    /**
     * 将Machine集合转化为Map，便于取值
     *
     * @param machine
     * @return
     */
    private Map<String, String> getMachine(List<Machine> machine) {
        Map<String, String> result = new HashMap<>();
        for (Machine bean : machine) {
            result.put(bean.getValue(), bean.getKey());
        }
        return result;
    }





    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

}
