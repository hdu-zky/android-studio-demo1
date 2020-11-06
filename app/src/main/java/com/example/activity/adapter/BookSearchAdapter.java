package com.example.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.activity.R;
import com.example.activity.bean.BookSearch;

import java.util.List;

public class BookSearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<BookSearch> bookSearchList;
    public BookSearchAdapter(Context context, List<BookSearch> list){
        mContext = context;
        bookSearchList = list;
    }
    @Override
    public int getCount(){
        return bookSearchList != null ? bookSearchList.size() : 0;
    }
    @Override
    public BookSearch getItem(int position){
        return bookSearchList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= new ViewHolder();
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_search_result_item, parent,false);
        //holder.bookId = view.findViewById(R.id.bs_bookId);
        holder.bookName = view.findViewById(R.id.bs_bookName);
        holder.authorName = view.findViewById(R.id.bs_authorName);
        holder.bookTypeName = view.findViewById(R.id.bs_bookTypeName);
        holder.status = view.findViewById(R.id.bs_status);
        //holder.bookId.setText(bookSearchList.get(position).getBookId());
        holder.bookName.setText(bookSearchList.get(position).getBookName());
        holder.authorName.setText(bookSearchList.get(position).getAuthorName());
        holder.bookTypeName.setText(bookSearchList.get(position).getBookTypeName());
        holder.status.setText(bookSearchList.get(position).getStatus().equals("1")?"连载中":"已完结");
        return view;
    }
    //ViewHolder静态类
    public final class ViewHolder{
        TextView bookId;
        TextView bookName;
        TextView authorName;
        TextView bookTypeName;
        TextView status;
    }
}
