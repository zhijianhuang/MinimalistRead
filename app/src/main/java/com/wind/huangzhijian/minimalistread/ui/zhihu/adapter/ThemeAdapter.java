package com.wind.huangzhijian.minimalistread.ui.zhihu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wind.huangzhijian.minimalistread.APP.App;
import com.wind.huangzhijian.minimalistread.Component.ImageLoader;
import com.wind.huangzhijian.minimalistread.R;
import com.wind.huangzhijian.minimalistread.Util.SystemUtil;
import com.wind.huangzhijian.minimalistread.module.bean.ThemeListBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangzhijian on 2017/3/20.
 */
public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<ThemeListBean.OthersBean> mList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public ThemeAdapter(Context mContext, List<ThemeListBean.OthersBean> mList) {
        this.mList = mList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_theme,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Glide在加载GridView等时,由于ImageView和Bitmap实际大小不符合,第一次时加载可能会变形(我这里出现了放大),必须在加载前再次固定ImageView大小
        ViewGroup.LayoutParams lp = holder.ivBg.getLayoutParams();
        lp.width = (App.SCREEN_WIDTH - SystemUtil.dp2px(mContext,12)) / 2;
        lp.height = SystemUtil.dp2px(mContext,120);

        ImageLoader.load(mContext, mList.get(position).getThumbnail(), holder.ivBg);
        holder.tvKind.setText(mList.get(position).getName());
        final int id = mList.get(position).getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.theme_bg)
        ImageView ivBg;
        @BindView(R.id.theme_kind)
        TextView tvKind;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }
}
