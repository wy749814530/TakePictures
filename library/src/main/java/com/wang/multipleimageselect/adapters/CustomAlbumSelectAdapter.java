package com.wang.multipleimageselect.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.wang.multipleimageselect.models.Album;
import com.wang.multipleimageselect.models.Image;
import com.wang.takephoto.R;

import java.util.ArrayList;

/**
 * Created by Darshan on 4/14/2015.
 */
public class CustomAlbumSelectAdapter extends RecyclerView.Adapter<CustomAlbumSelectAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<Album> albumsList = new ArrayList<>();

    public CustomAlbumSelectAdapter(Context context, ArrayList<Album> albums) {
        mContext = context;
        albumsList.clear();
        if (albums != null) {
            albumsList.addAll(albums);
        }
    }

    public void setData(ArrayList<Album> albums) {
        albumsList.clear();
        if (albums != null) {
            albumsList.addAll(albums);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.grid_view_item_album_select, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Album album = albumsList.get(position);
        viewHolder.textView.setText(album.name);
        Glide.with(mContext).load(album.cover).into(viewHolder.imageView);

        viewHolder.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(albumsList.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(Album image, int index);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public FrameLayout frameItemLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_album_image);
            textView = (TextView) itemView.findViewById(R.id.text_view_album_name);
            frameItemLay = itemView.findViewById(R.id.frame_layout_album_select);
        }

        public void setItemClickListener(View.OnClickListener clickListener) {
            frameItemLay.setOnClickListener(clickListener);
        }
    }

    public void releaseResources() {
        mListener = null;
        albumsList.clear();
        mContext = null;
    }
}