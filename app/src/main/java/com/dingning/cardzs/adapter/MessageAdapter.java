package com.dingning.cardzs.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingning.cardzs.R;
import com.dingning.cardzs.listener.OnItemClickListener;
import com.dingning.cardzs.model.Message;
import com.dingning.cardzs.utils.DialogUtils;
import com.dingning.cardzs.utils.GlideRoundTransform;


/**
 * Created by Allen on 2015/12/8.
 */
public class MessageAdapter extends BaseLoadMoreRecyclerAdapter<Message, MessageAdapter.ViewHolder>{

    protected Context mContext;
    protected OnItemClickListener mListener;

    public MessageAdapter(Context context){
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBindItemViewHolder(final ViewHolder holder, final int position) {
        Message item = getItem(position);

        Glide.with(mContext)
                .load(item.getParent_pic())
                .crossFade()
                .centerCrop()
                .transform(new GlideRoundTransform(mContext,10))
                .error(R.drawable.parent)
                .into(holder.ivAvatar);
        holder.tvMessage.setText("("+item.getParent_relation()+")"+item.getContent());
        int state = Integer.valueOf(item.getState());
        holder.tvTip.setText(state == Message.read ? mContext.getString(R.string.read) :mContext.getString(R.string.unread));
        holder.tvTip.setTextColor(state == Message.read  ? ContextCompat.getColor(mContext, R.color.white) :ContextCompat.getColor(mContext, R.color.yellow_dark));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.itemView, position);

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;
        public TextView tvMessage,tvTip;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            tvTip = (TextView) itemView.findViewById(R.id.tv_tip);
        }
    }
}
