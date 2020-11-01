package com.example.activity.indexTabFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.activity.R;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.RoundImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends Fragment {

    private static BookIntro bookIntro;
    private RoundImageView mImageView;
    private TextView mBookNameView;
    private TextView mBookIdView;
    private TextView mAuthorNameView;
    private TextView mTypeNameView;
    private TextView mBookStatusView;
    private TextView mBookIntroView;


    public BookDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BookDetailFragment newInstance(BookIntro intro) {
        BookDetailFragment fragment = new BookDetailFragment();
        //根据参数初始化当前页面数据模型
        bookIntro = intro;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (ViewGroup)  inflater.inflate(R.layout.fragment_book_detail, container, false);

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        //获取页面控件
        mBookNameView = view.findViewById(R.id.tv_bookName);
        mAuthorNameView = view.findViewById(R.id.tv_authorName);
        mImageView = view.findViewById(R.id.iv_bookImg);
        mBookIdView = view.findViewById(R.id.tv_bookId);
        mTypeNameView = view.findViewById(R.id.tv_typeName);
        mBookStatusView = view.findViewById(R.id.tv_status);
        mBookIntroView = view.findViewById(R.id.tv_intro);

        //根据数据模型初始化界面
        String imgSrc = bookIntro.getImageSrc();
        Glide.with(getContext()).load(imgSrc).into(mImageView);
        mBookNameView.setText(bookIntro.getBookName());
        mAuthorNameView.setText(bookIntro.getBookAuthor());
        mBookIdView.setText(String.valueOf(bookIntro.getBookId()));
        mTypeNameView.setText(bookIntro.getBookTypeName());
        mBookIntroView.setText(bookIntro.getBookIntroduction());
        //控制当前页面全屏显示
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        getFocus();
    }
    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // 监听到返回按钮点击事件
                    return true;
                }
                return false;
            }
        });
    }
}
