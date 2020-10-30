package com.example.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.activity.R;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.MyImageView;

import java.util.List;

public class BookIntroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<BookIntro> mBookInfoList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    public BookIntroAdapter() {

    }
    public BookIntroAdapter(List<BookIntro> mBookInfoList) {
        this.mBookInfoList = mBookInfoList;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return mBookInfoList != null ? mBookInfoList.size() : 0;
    }
    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View childView = inflater.inflate(R.layout.book_intro_card, parent, false);
//        ViewHolder viewHolder = new ViewHolder(childView);
//        return viewHolder;

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.book_intro_card, parent,
                    false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }
    static class ItemViewHolder extends ViewHolder {
        //final MyImageView mImageView;
        ImageView mImageView;
        TextView mBookNameView;
        TextView mAuthorNameView;
        TextView mTypeNameView;
        TextView mBookIdView;
        TextView mBookIntroView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_bookImg);
            mBookNameView = itemView.findViewById(R.id.tv_bookName);
            mAuthorNameView = itemView.findViewById(R.id.tv_authorName);
            mTypeNameView = itemView.findViewById(R.id.tv_typeName);
            mBookIdView = itemView.findViewById(R.id.tv_status);
            mBookIntroView = itemView.findViewById(R.id.tv_intro);
        }
    }
    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //如果当前是展示视图
        if (holder instanceof ItemViewHolder) {
            // TODO:展示数据到列表item控件上
            if (mBookInfoList != null) {
                BookIntro bookInfo = mBookInfoList.get(position);

                String imgUrl = bookInfo.getImageSrc();
                //holder.mImageView.setImageURL(imgUrl);
                Glide.with(mContext).load(imgUrl).error(R.drawable.book_img_error)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//表示只缓存原始图片
                        .into(((ItemViewHolder) holder).mImageView);

                ((ItemViewHolder) holder).mBookNameView.setText(bookInfo.getBookName());
                ((ItemViewHolder) holder).mAuthorNameView.setText(bookInfo.getBookAuthor());
                ((ItemViewHolder) holder).mTypeNameView.setText(bookInfo.getBookTypeName());
                //((ItemViewHolder) holder).mBookIdView.setText(bookInfo.getBookId());
                ((ItemViewHolder) holder).mBookIntroView.setText(bookInfo.getBookIntroduction());
            }
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }

        }
    }
}
