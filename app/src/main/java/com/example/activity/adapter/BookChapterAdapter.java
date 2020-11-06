package com.example.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.activity.bean.BookChapter;

import java.util.List;
import com.example.activity.R;

public class BookChapterAdapter extends BaseAdapter {

    private Context mContext;
    private List<BookChapter> bookChapterList;
    public BookChapterAdapter(Context context, List<BookChapter> chapterList){
        mContext = context;
        bookChapterList = chapterList;
    }
    @Override
    public int getCount(){
        return bookChapterList != null ? bookChapterList.size() : 0;
    }
    @Override
    public  BookChapter getItem(int position){
        return bookChapterList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= new ViewHolder();
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_chapter_item, parent, false);

        //根据自定义的Item布局加载布局
        //BookChapter bookChapter = (BookChapter) getItem(position);
        holder.chapterTitle = (TextView) view.findViewById(R.id.bc_chapterTitle);
        //holder.bookId = (TextView) view.findViewById(R.id.bc_bookId);
        //holder.bookChapterId = (TextView) view.findViewById(R.id.bc_bookChapterId);//获取该布局内的文本视图
        holder.chapterTitle.setText( bookChapterList.get(position).getChapterTitle());

        return view;
    }
    //ViewHolder静态类
    public final class ViewHolder{
        TextView bookId;
        TextView bookChapterId;
        TextView chapterTitle;
    }
}
