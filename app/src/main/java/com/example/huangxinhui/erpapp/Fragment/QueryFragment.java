package com.example.huangxinhui.erpapp.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.example.huangxinhui.erpapp.Adapter.QueryAdapter;
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
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class QueryFragment extends Fragment {
    Unbinder unbinder;

    List<Query.DataBean.Info> data;

    @BindView(R.id.list_query)
    RecyclerView listQuery;

    private Map<String, String> wear;

    private Map<String, String> status;

    private Map<String, String> machine;

    private Map<String, String> bb;

    private Map<String, String> bc;


    public static QueryFragment getInstance(ArrayList<Query.DataBean.Info> data) {
        QueryFragment f = new QueryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = (List<Query.DataBean.Info>) getArguments().getSerializable("data");
        status = JSON.parseObject(JsonReader.getJson("status.json", getActivity()), Map.class);
        wear = getWear(JSON.parseArray(JsonReader.getJson("wear.json", getActivity()), Wear.class));
        machine = getMachine(JSON.parseArray(JsonReader.getJson("machine.json", getActivity()), Machine.class));
        bb = JSON.parseObject(JsonReader.getJson("bb.json", getActivity()), Map.class);
        bc = JSON.parseObject(JsonReader.getJson("bc.json", getActivity()), Map.class);
        for (int i=0;i<data.size();i++){
            if(data.get(i).getKey().equals("状态")) data.get(i).setValue(status.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("库位")) data.get(i).setValue(wear.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("去向")) data.get(i).setValue(wear.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("连铸机号")) data.get(i).setValue(machine.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("班次")) data.get(i).setValue(bc.get(data.get(i).getValue()));
            if(data.get(i).getKey().equals("班别")) data.get(i).setValue(bb.get(data.get(i).getValue()));
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
            result.put(bean.getValue(),bean.getKey());
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
            result.put(bean.getValue(),bean.getKey());
        }
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        listQuery.setLayoutManager(new LinearLayoutManager(getActivity()));
        listQuery.setAdapter(new QueryAdapter(data, getActivity()));
        ScreenAdapterTools.getInstance().loadView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
