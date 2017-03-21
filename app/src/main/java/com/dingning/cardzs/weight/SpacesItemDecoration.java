package com.dingning.cardzs.weight;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Allen on 2016/12/11.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    public static final int LinearLayout = 0;
    public static final int GridLayout = 1;
    private int layout;
    private int space;

    public SpacesItemDecoration(int space, int layout) {
        this.space = space;
        this.layout = layout;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        //不是第一个的格子都设一个左边和底部的间距
        if(layout == GridLayout){
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %3==0) {
                outRect.left = 0;
            }
        }

        if(layout == LinearLayout){
            outRect.bottom = space;
        }

    }
}
