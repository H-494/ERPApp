package com.example.huangxinhui.erpapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huangxinhui.erpapp.JavaBean.Receive;
import com.example.huangxinhui.erpapp.R;

import java.util.List;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ViewHolder> {
    private static final int TYPE_LAYOUT = 1;
    private static final int TYPE_WATCH = 2;
    private static final int TYPE_BUTTON = 3;
    private OnButtonClickListener onButtonClickListener;
    private OnButtonClickListener onWatchClickListener;

    private List<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX> data;
    private LayoutInflater inflater;

    public ReceiverAdapter(List<Receive.DataBean.InfosBean.TabsBean.ListInfoBeanX> data, Context context) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setOnWatchClickListener(OnButtonClickListener onWatchClickListener) {
        this.onWatchClickListener = onWatchClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == data.size()+1) {
            return TYPE_BUTTON;
        }else if(position == data.size()){
            return TYPE_WATCH;
        } else {
            return TYPE_LAYOUT;
        }
    }

    @NonNull
    @Override
    public ReceiverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == TYPE_BUTTON) {
            Log.i("onCreateViewHolder", "TYPE_BUTTON");
            return new ViewHolder(inflater.inflate(R.layout.list_button, viewGroup, false));
        }else if(type == TYPE_WATCH){
            return new ViewHolder(inflater.inflate(R.layout.list_watch, viewGroup, false));
        } else {
            return new ViewHolder(inflater.inflate(R.layout.list_information, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverAdapter.ViewHolder holder, final int i) {
        switch (getItemViewType(i)) {
            case TYPE_LAYOUT:
                holder.name.setText(data.get(i).getKey());
                holder.num.setText(data.get(i).getValue());
                break;
            case TYPE_WATCH:
                if(onWatchClickListener != null){
                    holder.watch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onWatchClickListener.onCLick(view,i);
                        }
                    });
                }
                break;
            case TYPE_BUTTON:
                holder.button.setText("点击入库");
                if (onButtonClickListener != null) {
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onButtonClickListener.onCLick(view, i);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 2;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        TextView num;

        Button button;

        LinearLayout watch;


        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            num = view.findViewById(R.id.num);
            button = view.findViewById(R.id.button);
            watch = view.findViewById(R.id.watch);
        }
    }

    /**
     * 按钮的点击事件
     */
    public interface OnButtonClickListener {
        void onCLick(View view, int position);
    }

}
