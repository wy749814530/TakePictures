package com.wang.multipleimageselect.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wang.multipleimageselect.models.Image;
import com.wang.takephoto.R;

import java.util.ArrayList;

/**
 * Created by Darshan on 4/18/2015.
 */
public class CustomImageSelectAdapter extends RecyclerView.Adapter<CustomImageSelectAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Image> imageList = new ArrayList<>();

    public CustomImageSelectAdapter(Context context, ArrayList<Image> images) {
        mContext = context;
        imageList.clear();
        if (images != null) {
            imageList.addAll(images);
        }
    }

    public void setData(ArrayList<Image> dataList) {
        this.imageList.clear();
        if (null != dataList) {
            this.imageList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_view_item_image_select, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (imageList.get(position).isSelected) {
            holder.view.setAlpha(0.5f);
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.view.setAlpha(0.0f);
            holder.ivSelect.setVisibility(View.GONE);
        }
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.library_icon_default);
        Log.i("ImageSelectAdapter", "path: " + imageList.get(position).path);
        Glide.with(mContext).load(imageList.get(position).path).apply(options).into(holder.imageView);

        holder.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(imageList.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(Image image, int index);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public FrameLayout frameItemLay;
        public View view;
        public ImageView ivSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameItemLay = itemView.findViewById(R.id.frame_item_lay);
            imageView = itemView.findViewById(R.id.image_view_image_select);
            view = itemView.findViewById(R.id.view_alpha);
            ivSelect = itemView.findViewById(R.id.ivSelect);
        }

        public void setItemClickListener(View.OnClickListener clickListener) {
            frameItemLay.setOnClickListener(clickListener);
        }
    }

    public void destory() {
        mListener = null;
        if (imageList != null) {
            imageList.clear();
        }
        imageList = null;
        mContext = null;
    }
}

