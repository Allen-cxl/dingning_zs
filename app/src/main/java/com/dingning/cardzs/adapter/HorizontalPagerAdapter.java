package com.dingning.cardzs.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dingning.cardzs.R;
import com.dingning.cardzs.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {


    private Context mContext;

    private List<Image> list;

    private OnImageSingleTapClickListener mListener;


    public HorizontalPagerAdapter(Context context, OnImageSingleTapClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(List<Image> list){
        if(list == null){
            this.list = new ArrayList<>();
        }else{
            this.list = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        ImageView imageView = new ImageView(mContext);
        imageView.setBackgroundColor(0xffffffff);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        Image image = list.get(position);
        Glide.with(mContext)
                .load(image.getImg_url())
                .crossFade()
                .error(R.drawable.default_pic)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onImageSingleTap();
            }
        });
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    public interface OnImageSingleTapClickListener{
        void onImageSingleTap();
    }
}
