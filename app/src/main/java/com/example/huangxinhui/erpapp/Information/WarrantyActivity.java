package com.example.huangxinhui.erpapp.Information;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.huangxinhui.erpapp.JavaBean.Query;
import com.example.huangxinhui.erpapp.JavaBean.Receive;
import com.example.huangxinhui.erpapp.R;
import com.example.huangxinhui.erpapp.Util.JsonReader;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WarrantyActivity extends AppCompatActivity {

    @BindView(R.id.list_warranty)
    RecyclerView listWarranty;

    List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX> list_zbs;

    List<Receive.DataBean.CfyBean.ListInfoBean> list_cfy;

    WarrantyAdapter adapter;


    private Map<String, String> bb;

    private Map<String, String> bc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warranty);
        ButterKnife.bind(this);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        list_zbs = (List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX>) getIntent().getExtras().getSerializable("zbs");
        list_cfy = (List<Receive.DataBean.CfyBean.ListInfoBean>) getIntent().getExtras().getSerializable("cfy");
        bb = JSON.parseObject(JsonReader.getJson("bb.json", this), Map.class);
        if (list_zbs == null || list_cfy == null) {
            Toast.makeText(this, "数据有误，请联系管理员", Toast.LENGTH_SHORT).show();
        } else {
            listWarranty.setLayoutManager(new LinearLayoutManager(this));
//            adapter = new WarrantyAdapter(list_data.get(1).getList_info(), list_data.get(2).getList_info(), getBookCode(list_data.get(0).getList_info()), this);
            adapter = new WarrantyAdapter(
                    list_zbs,
                    list_cfy,
                    list_zbs.get(0).getValue(),
                    this
            );
            listWarranty.setAdapter(adapter);
        }
    }

    private String getBookCode(List<Query.DataBean.Info> info) {
        for (Query.DataBean.Info i : info) {
            if (i.getKey().equals("质保书编号")) {
                return i.getValue();
            }
        }
        return "";
    }


    class WarrantyAdapter extends RecyclerView.Adapter<WarrantyAdapter.ViewHolder> {

        private final int TYPE_HEAD = 0;
        private final int TYPE_LAYOUT = 1;
        private final int TYPE_GRID = 2;

        List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX> data;
        List<Receive.DataBean.CfyBean.ListInfoBean> cell;
        LayoutInflater inflater;
        String book_code;


        public WarrantyAdapter(List<Receive.DataBean.InfosBean.ZbsBean.ListInfoBeanXX> data, List<Receive.DataBean.CfyBean.ListInfoBean> cell, String book_code, Context context) {
            this.data = data;
            this.cell = cell;
            this.book_code = book_code;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEAD;
            } else if (position == data.size() + 1) {
                return TYPE_GRID;
            } else {
                return TYPE_LAYOUT;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            switch (type) {
                case TYPE_HEAD:
                    return new ViewHolder(inflater.inflate(R.layout.item_head, viewGroup, false));
                case TYPE_LAYOUT:
                    return new ViewHolder(inflater.inflate(R.layout.list_information, viewGroup, false));
                case TYPE_GRID:
                    return new ViewHolder(inflater.inflate(R.layout.item_grid, viewGroup, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_HEAD:
                    holder.finish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    holder.book_code.setText(book_code);
                    break;
                case TYPE_LAYOUT:
                    holder.name.setText(data.get(position - 1).getKey());
                    holder.num.setText(data.get(position - 1).getValue());
                    break;
                case TYPE_GRID:
                    holder.list_grid.setLayoutManager(new GridLayoutManager(WarrantyActivity.this, 4));
                    holder.list_grid.setAdapter(new CellAdapter(cell, inflater));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.size() + 2;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @Nullable
            @BindView(R.id.finish)
            ImageView finish;
            @Nullable
            @BindView(R.id.book_code)
            TextView book_code;

            @Nullable
            @BindView(R.id.name)
            TextView name;
            @Nullable
            @BindView(R.id.num)
            TextView num;

            @Nullable
            @BindView(R.id.list_grid)
            RecyclerView list_grid;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder> {

        List<Receive.DataBean.CfyBean.ListInfoBean> data;
        LayoutInflater inflater;

        public CellAdapter(List<Receive.DataBean.CfyBean.ListInfoBean> data, LayoutInflater inflater) {
            this.data = data;
            this.inflater = inflater;
        }

        @NonNull
        @Override
        public CellAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_cell, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CellAdapter.ViewHolder holder, int position) {
            if(Pattern.compile("(?i)[A-z]").matcher(data.get(position).getValue()).find()){
                holder.key.setTextColor(Color.parseColor("#FF0000"));
                holder.value.setTextColor(Color.parseColor("#FF0000"));
                holder.key.setText(data.get(position).getKey());
                holder.value.setText(data.get(position).getValue().substring(0,data.get(position).getValue().length()-2));
            }else {
                holder.key.setText(data.get(position).getKey());
                holder.value.setText(data.get(position).getValue());
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.key)
            TextView key;
            @BindView(R.id.value)
            TextView value;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
