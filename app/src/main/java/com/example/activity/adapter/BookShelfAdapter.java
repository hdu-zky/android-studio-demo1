package com.example.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.activity.R;
import com.example.activity.bean.BookShelf;
import com.example.activity.component.RoundImageView;

import java.util.List;

public class BookShelfAdapter extends Adapter<RecyclerView.ViewHolder> {
    private List<BookShelf> mBookShelfList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    public BookShelfAdapter(){

    }
    public BookShelfAdapter(List<BookShelf> bookShelfList) {
        this.mBookShelfList = bookShelfList;
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
        return mBookShelfList != null ? mBookShelfList.size() : 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.book_shelf_card, parent, false);
        ViewHolder viewHolder = new ShelfItemViewHolder(childView);
        return viewHolder;
    }
    static class ShelfItemViewHolder extends ViewHolder {
        //final MyImageView mImageView;
        final RoundImageView mImageView;
//        ImageView mImageView;
        TextView mBookNameView;
        TextView mAuthorNameView;
        TextView mTypeNameView;
        TextView mBookStatusView;
        TextView mBookUpdateTime;
        TextView mBookLatestChapter;

        public ShelfItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_bookImg);
            mBookNameView = itemView.findViewById(R.id.tv_bookName);
            mAuthorNameView = itemView.findViewById(R.id.tv_authorName);
            mTypeNameView = itemView.findViewById(R.id.tv_typeName);
            mBookStatusView = itemView.findViewById(R.id.tv_status);
            mBookUpdateTime = itemView.findViewById(R.id.tv_shelf_update_time);
            mBookLatestChapter = itemView.findViewById(R.id.tv_shelf_latest_chapter);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        if (holder instanceof ShelfItemViewHolder) {
            // TODO:展示数据到列表item控件上
            if (mBookShelfList != null) {
                BookShelf bookInfo = mBookShelfList.get(position);

                String imgUrl = bookInfo.getImageSrc();
                //holder.mImageView.setImageURL(imgUrl);
                Glide.with(mContext).load(imgUrl).error(R.drawable.book_img_error)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)//表示只缓存原始图片
                        .into(((ShelfItemViewHolder) holder).mImageView);

                ((ShelfItemViewHolder) holder).mBookNameView.setText(bookInfo.getBookName());
                ((ShelfItemViewHolder) holder).mAuthorNameView.setText(bookInfo.getBookAuthor());
                ((ShelfItemViewHolder) holder).mTypeNameView.setText(bookInfo.getBookTypeName());
                ((ShelfItemViewHolder) holder).mBookStatusView.setText(bookInfo.getStatus()==1?"连载中":"已完结");
                ((ShelfItemViewHolder) holder).mBookUpdateTime.setText(bookInfo.getUpdateTime());
                ((ShelfItemViewHolder) holder).mBookLatestChapter.setText(bookInfo.getLatestChTitle());
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
//        }
    }

}
