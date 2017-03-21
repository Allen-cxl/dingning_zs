package com.dingning.cardzs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingning.cardzs.R;
import com.dingning.cardzs.model.Parent;
import com.dingning.cardzs.utils.GlideRoundTransform;

import java.util.List;


public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.ViewHolder>{
    public List<Parent> mDatas;
    Context mContext;

    public interface OnItemClickListener{
        void onItemClick(ViewHolder vh, int position);

        void onItemLongClick(ViewHolder vh, int position);
    }
    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public ObjectAdapter(Context context,List<Parent> mDatas) {
        mContext =context;
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_family,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder, position);

                }
            });


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder, position);
                    return true;
                }
            });

        }
        Parent sendObject = mDatas.get(position);
        Glide.with(mContext)
                .load(sendObject.getParent_pic())
                .crossFade()
                .centerCrop()
                .transform(new GlideRoundTransform(mContext,10))
                .error(R.drawable.parent)
                .into(holder.ivAvatar);
        holder.tvName.setText(sendObject.getRelation());
        if (sendObject.isSelected()){
            holder.ivselect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.on_select));
        }else{
            holder.ivselect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.off_select));
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public Parent getItem(int position) {
        if (position > mDatas.size() - 1) {
            return null;
        }
        return mDatas.get(position);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView  tvName;
        public ImageView ivAvatar;
        public ImageView ivselect;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            ivselect = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }
}
