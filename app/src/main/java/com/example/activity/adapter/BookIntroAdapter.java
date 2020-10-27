package com.example.activity.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activity.fragment.MyImageView;
import com.example.activity.R;
import com.example.activity.bean.BookIntro;

import java.util.List;

public class BookIntroAdapter extends RecyclerView.Adapter<BookIntroAdapter.ViewHolder>  {

    private List<BookIntro> mBookInfoList;

    public BookIntroAdapter() {
    }

    public BookIntroAdapter(List<BookIntro> mBookInfoList) {
        this.mBookInfoList = mBookInfoList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.book_intro_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mBookInfoList != null) {
            BookIntro bookInfo = mBookInfoList.get(position);

            String imgUrl = bookInfo.getImageSrc();
            holder.mImageView.setImageURL(imgUrl);
            holder.mBookNameView.setText(bookInfo.getBookName());
            holder.mAuthorNameView.setText(bookInfo.getBookAuthor());
            holder.mStatusView.setText(bookInfo.getBookStatus());
            holder.mBookIntroView.setText(bookInfo.getBookIntroduction());
        }

    }

    @Override
    public int getItemCount() {
        return mBookInfoList != null ? mBookInfoList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final MyImageView mImageView;
        TextView mBookNameView;
        TextView mAuthorNameView;
        TextView mStatusView;
        TextView mBookIntroView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_bookImg);
            mBookNameView = itemView.findViewById(R.id.tv_bookName);
            mAuthorNameView = itemView.findViewById(R.id.tv_authorName);
            mStatusView = itemView.findViewById(R.id.tv_status);
            mBookIntroView = itemView.findViewById(R.id.tv_intro);
        }
    }
}
