package com.example.activity.indexTabFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.activity.R;
import com.example.activity.activity.BookDetailActivity;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.RoundImageView;

import jp.wasabeef.glide.transformations.BlurTransformation;


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
    private ImageView mBgImageView;
    private TextView mBookNameView;
    private TextView mBookTitleView;
    private TextView mBookIdView;
    private TextView mAuthorNameView;
    private TextView mTypeNameView;
    private TextView mBookStatusView;
    private TextView mBookIntroView;

    private Toolbar toolbar;
    private ActionBar actionBar;
    Handler handler;
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
        handler=new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (ViewGroup)  inflater.inflate(R.layout.fragment_book_detail, container, false);

        //控制当前页面全屏显示
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        //在fragment中使用onCreateOptionsMenu时需要在onCrateView中添加此方法，否则不会调用
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.bd_toolbar);

        //设置导航图标要在setSupportActionBar方法之后
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        showMessage("Search !");
                        break;
                    case R.id.action_message:
                        showMessage("Settings !");
                        break;
                }
                return true;
            }
        });
        TextView btnBack = view.findViewById(R.id.bd_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("回退");
                getActivity().onBackPressed();
            }
        });
        // TODO:实现下拉刷新效果
        final SwipeRefreshLayout refreshLayout= view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorGray));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                loadNextData(true);
                refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });

        //获取页面控件
        mBookTitleView = view.findViewById(R.id.bd_title);
        mBookNameView = view.findViewById(R.id.bd_bookName);
        mAuthorNameView = view.findViewById(R.id.bd_authorName);
        mImageView = view.findViewById(R.id.bd_bookImg);
        mBgImageView = view.findViewById(R.id.bd_bookDetail);
        mBookIdView = view.findViewById(R.id.bd_bookId);
        mTypeNameView = view.findViewById(R.id.bd_typeName);
        mBookStatusView = view.findViewById(R.id.bd_status);
        mBookIntroView = view.findViewById(R.id.bd_intro);
        initView(view);
        return view;
    }
    // TODO:获取参数并渲染到界面
    private void initView(View view) {
        //根据数据模型初始化界面
        if(bookIntro!= null){
            String imgSrc = bookIntro.getImageSrc();
            Glide.with(getContext()).load(imgSrc).into(mImageView);
//            Glide.with(getContext()).load(imgSrc).bitmapTransform(new BlurTransformation(getContext(), 25),
//                    new CenterCrop(getContext())).into(mBgImageView);

            mBookNameView.setText(bookIntro.getBookName());
            mBookTitleView.setText(bookIntro.getBookName());
            mAuthorNameView.setText(bookIntro.getBookAuthor());
            String id = String.valueOf(bookIntro.getBookId());
            if("".equals(id)){
                mBookIdView.setText(id);
            }
            mTypeNameView.setText(bookIntro.getBookTypeName());
            mBookIntroView.setText(bookIntro.getBookIntroduction());
        }else {
            showMessage("获取书籍信息失败,请刷新");
        }
    }
    private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                    showMessage("回退");
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }
}
