package com.example.activity.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.activity.R;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.MyImageView;
import com.example.activity.component.RoundImageView;

import java.util.List;

public class BookIntroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<BookIntro> mBookInfoList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NOMORE = 2;
    private static int pageIndex = 1;
    private static int pageCount = -1;
    private Context mContext;
    public BookIntroAdapter() {

    }
    public BookIntroAdapter(List<BookIntro> mBookInfoList) {
        this.mBookInfoList = mBookInfoList;
    }
    public void setPageIndexAndCount(int index, int count){
        pageIndex = index;
        pageCount = count;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
        //点击书籍操作按钮对应的处理函数，供主调界面重写
        void initPopWindow(View v);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return mBookInfoList != null ? mBookInfoList.size() : 0;
    }

    public BookIntro getItem(int position){
        return mBookInfoList.get(position);
    }
    @Override
    public int getItemViewType(int position) {
        // TODO:如果当前位置是最后一个且数据不为一
        if ( (position + 1 == getItemCount()) && position != 0) {
            //如果到达了底页或者页面数为0则返回没有更多数据提示
            if(pageIndex == pageCount || (pageCount == 0)){
                Log.d("position"+position,"pageIndex "+pageIndex+" pageCount "+pageCount);
                return TYPE_NOMORE;
            }
            //否则返回加载更多数据提示
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.book_intro_card, parent,
                    false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }else if(viewType == TYPE_NOMORE){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_nomore, parent,
                    false);
            return new NoMoreViewHolder(view);
        }
        return null;
    }
    class ItemViewHolder extends ViewHolder {
        //final MyImageView mImageView;
        final RoundImageView mImageView;
        //ImageView mImageView;
        TextView mBookNameView;
        TextView mBookIdView;
        TextView mAuthorNameView;
        TextView mTypeNameView;
        TextView mBookStatusView;
        TextView mBookIntroView;
        Button bookOptions;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_bookImg);
            mBookNameView = itemView.findViewById(R.id.tv_bookName);
            mBookIdView = itemView.findViewById(R.id.tv_bookId);
            mAuthorNameView = itemView.findViewById(R.id.tv_authorName);
            mTypeNameView = itemView.findViewById(R.id.tv_typeName);
            mBookStatusView = itemView.findViewById(R.id.tv_status);
            mBookIntroView = itemView.findViewById(R.id.tv_intro);
            bookOptions = itemView.findViewById(R.id.btn_show);
        }
    }
    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
    class NoMoreViewHolder extends RecyclerView.ViewHolder {

        public NoMoreViewHolder(View view) {
            super(view);
        }
    }
    @Override
    public void onBindViewHolder(
            @NonNull
            final ViewHolder holder, int position) {
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
//                ((ItemViewHolder) holder).mBookStatusView.setText(bookInfo.getBookStatus());
                ((ItemViewHolder) holder).mBookIdView.setText(String.valueOf(bookInfo.getBookId()));
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
                //添加点击书籍操作按钮事件监听
                ((ItemViewHolder) holder).bookOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.initPopWindow(holder.itemView);
                    }
                });
            }

        }
    }
}
