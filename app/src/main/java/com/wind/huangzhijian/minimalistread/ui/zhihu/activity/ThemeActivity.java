package com.wind.huangzhijian.minimalistread.ui.zhihu.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wind.huangzhijian.minimalistread.Component.ImageLoader;
import com.wind.huangzhijian.minimalistread.R;
import com.wind.huangzhijian.minimalistread.Util.SnackbarUtil;
import com.wind.huangzhijian.minimalistread.Util.SystemUtil;
import com.wind.huangzhijian.minimalistread.base.BaseActivity;
import com.wind.huangzhijian.minimalistread.module.bean.ThemeChildListBean;
import com.wind.huangzhijian.minimalistread.presenter.ThemeChildPresenter;
import com.wind.huangzhijian.minimalistread.presenter.contract.ThemeChildContract;
import com.wind.huangzhijian.minimalistread.ui.zhihu.adapter.ThemeChildAdapter;
import com.wind.huangzhijian.minimalistread.widget.ProgressImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huangzhijian on 2017/3/22.
 */
public class ThemeActivity extends BaseActivity<ThemeChildPresenter> implements ThemeChildContract.View {

    @BindView(R.id.rv_theme_child_list)
    RecyclerView rvThemeChildList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.view_progress)
    ProgressImageView ivProgress;
    @BindView(R.id.iv_theme_child_blur)
    ImageView ivBlur;
    @BindView(R.id.iv_theme_child_origin)
    ImageView ivOrigin;
    @BindView(R.id.tv_theme_child_des)
    TextView tvDes;
    @BindView(R.id.theme_child_appbar)
    AppBarLayout appbar;

    ThemeChildAdapter mAdapter;
    List<ThemeChildListBean.StoriesBean> mList;

    int id ;

    @Override
    protected void initEventAndData() {
        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");
        ivProgress.start();
        mList=new ArrayList<>();
        mAdapter=new ThemeChildAdapter(mContext,mList);
        rvThemeChildList.setLayoutManager(new LinearLayoutManager(mContext));
        rvThemeChildList.setAdapter(mAdapter);
        mpresenter.getThemeChildData(id);
        mAdapter.setOnItemClickListener(new ThemeChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mpresenter.insertReadToDB(mList.get(position).getId());
                mAdapter.setReadState(position,true);
                mAdapter.notifyItemChanged(position);
                Intent intent = new Intent();
                intent.setClass(mContext,ZhihuDetailActivity.class);
                intent.putExtra("id",mList.get(position).getId());
                if(view !=null){
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mContext,view,"view").toBundle());
                }else {
                    startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(mContext).toBundle());
                }
            }
        });
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(verticalOffset>=0){
                    swipeRefresh.setEnabled(true);
                }else {
                    swipeRefresh.setEnabled(false);
                    float rate = (float) (SystemUtil.dp2px(mContext, 256) + verticalOffset * 2) / SystemUtil.dp2px(mContext, 256);
                    if(rate>=0){
                        ivOrigin.setAlpha(rate);
                    }
                }
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mpresenter.getThemeChildData(id);
            }
        });
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_theme;
    }

    @Override
    public void showContent(ThemeChildListBean themeChildListBean) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            ivProgress.stop();
        }
        setToolBar(mToolBar,themeChildListBean.getName());
        mList.clear();
        mList.addAll(themeChildListBean.getStories());
        mAdapter.notifyDataSetChanged();
        ImageLoader.load(mContext, themeChildListBean.getBackground(), ivOrigin);
        Glide.with(mContext).load(themeChildListBean.getBackground()).bitmapTransform(new BlurTransformation(mContext)).into(ivBlur);
        tvDes.setText(themeChildListBean.getDescription());
    }

    @Override
    public void showError(String msg) {
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            ivProgress.stop();
        }
        SnackbarUtil.showShort(getWindow().getDecorView(),msg);
    }
}
