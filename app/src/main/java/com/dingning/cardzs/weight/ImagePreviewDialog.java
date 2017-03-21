package com.dingning.cardzs.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.dingning.cardzs.R;
import com.dingning.cardzs.adapter.HorizontalPagerAdapter;
import com.dingning.cardzs.model.Image;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.List;


/**
 * Created by Allen on 2016/12/6.
 */

public class ImagePreviewDialog extends Dialog implements HorizontalPagerAdapter.OnImageSingleTapClickListener {


    private HorizontalInfiniteCycleViewPager hicvp;
    private HorizontalPagerAdapter adapter;

    public ImagePreviewDialog(Context context) {
        super(context);
        initView(context);
    }

    public ImagePreviewDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    protected ImagePreviewDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_preview,null);
        setContentView(view);

        hicvp = (HorizontalInfiniteCycleViewPager) view.findViewById(R.id.hicvp);
        adapter = new HorizontalPagerAdapter(context,this);

    }

    public void initData(List<Image> list){

        adapter.setData(list);
        adapter.notifyDataSetChanged();
        hicvp.setAdapter(adapter);
    }

    @Override
    public void onImageSingleTap() {
        dismiss();
    }
}
