package com.example.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.activity.bean.BookChapter;

import java.util.List;
import com.example.activity.R;

public class BookChapterAdapter extends ArrayAdapter {

    private final int resourceId;
    private Context mContext;
    public BookChapterAdapter(Context context, int textViewResourceId, List<BookChapter> objects){
        super(context, textViewResourceId, objects);
        mContext = context;
        resourceId = textViewResourceId;
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        BookChapter bookChapter = (BookChapter) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(mContext).inflate(resourceId, null);//实例化一个对象
        TextView chapterTitle = (TextView) view.findViewById(R.id.bc_chapterTitle);//获取该布局内的文本视图
        //TextView bookId = (TextView) view.findViewById(R.id.bc_bookId);//获取该布局内的文本视图
        //TextView bookChapterId = (TextView) view.findViewById(R.id.bc_bookChapterId);//获取该布局内的文本视图
        chapterTitle.setText(bookChapter.getChapterTitle());//为文本视图设置文本内容
        return view;
    }

}
