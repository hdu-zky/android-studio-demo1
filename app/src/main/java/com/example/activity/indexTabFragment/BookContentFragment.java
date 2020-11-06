package com.example.activity.indexTabFragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.R;
import com.example.activity.bean.BookContent;
import com.example.activity.util.httpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BookContentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "bookId";
    private static final String ARG_PARAM2 = "bookChapterId";

    // TODO: Rename and change types of parameters
    private String bookId;
    private String bookChapterId;
    private BookContent bookContent;
    private httpUtil http = new httpUtil();
    private static String[] httpUrl= new String[]{"http://192.168.0.109:3000/bookChapter/bookContent"};
    private TextView mContentTitleView;
    private TextView mContentView;
    //private WebView mWebView;

    private TextView mTopBackView;
    private TextView mTopTitle;
    private TextView mTopSetting;
    private RelativeLayout mMainContent;
    private LinearLayout mTopContent;
    private LinearLayout mPageContent;
    private LinearLayout mPageSelect;
    private Button mPrevPageBtn;
    private Button mBackCatalogBtn;
    private Button mNextPageBtn;
    private PopupWindow popTopWindow;
    private PopupWindow popWindow;
    private static final int get_bookContent_callback = 1;
    private static final int NETWORK_ERROR = 13;
    private static final int JSON_PARSE_ERROR = 15;
    //用来接收联网线程结束后发送的信号并进行UI相应处理
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("handler",""+msg);
            switch (msg.what){
                case get_bookContent_callback:
                    if(msg.arg1 == 2){
                        showMessage("查找书籍信息失败，请刷新");
                    }else if(msg.arg1 == 0){
                        showMessage("未查找到相关书籍信息");
                    }
                    initView();
                    break;
                case JSON_PARSE_ERROR:
                    showMessage("解析数据失败!");
                    break;
                case NETWORK_ERROR:
                    showMessage(msg.obj.toString());
                    break;
            }
        }
    };
    public BookContentFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static BookContentFragment newInstance(String bookId, String bookChapterId) {
        BookContentFragment fragment = new BookContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, bookId);
        args.putString(ARG_PARAM2, bookChapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_PARAM1);
            bookChapterId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_content, container, false);
        // TODO:控制当前页面全屏显示,这种声明会出现页面占满全屏，连手机状态栏也被挤占情况
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**
         * Fragment切换完成后，点击空白区域或者底部的时候，出现点击区域冲突的问题。
         * 就是在新的fragment中，点击底部的空白区域，触发了上一个fragment的点击事件
         * 加上下面这一句可以解决https://www.cnblogs.com/shoneworn/p/5075247.html
         * */
        view.setOnClickListener(null);

        mMainContent = view.findViewById(R.id.bc_main_content);
        mContentTitleView = view.findViewById(R.id.bc_content_title);
        mContentView = view.findViewById(R.id.bc_content);
        //mWebView = view.findViewById(R.id.bc_content);

        mTopBackView = view.findViewById(R.id.bc_back);
        mTopTitle = view.findViewById(R.id.bc_top_title);
        mTopSetting = view.findViewById(R.id.bc_top_setting);
        mTopBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
            }
        });
        mPrevPageBtn = view.findViewById(R.id.bc_prev_page);
        mBackCatalogBtn = view.findViewById(R.id.bc_back_catalog);
        mNextPageBtn = view.findViewById(R.id.bc_next_page);
        mPrevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookContent==null){
                    showMessage("页面信息错误");
                }else{
                    bookChapterId = String.valueOf(bookContent.getPrevChapterId());
                    bookContent();
                }
            }
        });
        mBackCatalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mNextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookContent==null){
                    showMessage("页面信息错误");
                }else{
                    bookChapterId = String.valueOf(bookContent.getNextChapterId());
                    bookContent();
                }
            }
        });
        //TODO:增加点击页面中间事件监听，并实现单击弹出顶部及底部菜单，双击隐藏
        mPageContent = view.findViewById(R.id.bc_page_content);
        mTopContent =  view.findViewById(R.id.bc_top_content);
        mPageSelect =  view.findViewById(R.id.bc_page_select);
        mPageContent.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTopContent.getVisibility() == View.INVISIBLE){
                    mTopContent.setVisibility(View.VISIBLE);
                }else {
                    mTopContent.setVisibility(View.INVISIBLE);
                }
                if(mPageSelect.getVisibility() == View.INVISIBLE){
                    mPageSelect.setVisibility(View.VISIBLE);
                }else {
                    mPageSelect.setVisibility(View.INVISIBLE);
                }
            }
        });
        bookContent();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // TODO:主界面获取焦点
        Log.e("onResume","search");
        getView().setFocusable(true);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().requestFocusFromTouch();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    //  TODO:监听到返回按钮点击事件
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }
    private void initView(){
        if(bookContent!=null){
            mTopTitle.setText(bookContent.getBookName());
            mContentTitleView.setText(bookContent.getChapterTitle());
            String sourceStr = bookContent.getBookContent();
            String data0 = sourceStr.replace("<p>","");
            String data1 = data0.replace("</p>","\n");
            mContentView.setText(data1);
//            mWebView.loadDataWithBaseURL(null,bookContent.getBookContent(), "text/html",  "utf-8", null);
//            mWebView.loadData(bookContent.getBookContent(), "text/html","utf-8");
            if(bookContent.getPrevChapterId()!=0){
                mPrevPageBtn.setEnabled(true);
            }
            if(bookContent.getBookChapterId()!=0){
                mBackCatalogBtn.setEnabled(true);
            }
            if(bookContent.getNextChapterId()!=0){
                mNextPageBtn.setEnabled(true);
            }
        }else {
            mPrevPageBtn.setEnabled(false);
            mBackCatalogBtn.setEnabled(false);
            mNextPageBtn.setEnabled(false);
            showMessage("获取页面数据失败，请退出当前页面重新进入");
        }

    }
    private void bookContent(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("bookId", bookId);
        map.put("bookChapterId",  bookChapterId);
        http.sendPost(httpUrl[0], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                int res = parseJsonBookInfo(response);
                if(res == -1){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else {
                    Message msg = new Message();
                    msg.what = get_bookContent_callback;
                    msg.arg1 = res;
                    handler.sendMessage(msg);
                }
            }
            @Override
            public void onError(String err) {
                Message msg = new Message();
                msg.what = NETWORK_ERROR;
                msg.obj = err;
                handler.sendMessage(msg);
            }
        });
    }
    /**
     * TODO:解析多个数据的Json
     * @param strResult String 待解析的json数据
     * @return 0-返回结果为空 1-解析成功且数据不为空 -1-解析失败
     */
    private int parseJsonBookInfo(String strResult) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            if(!success){
                return 0;
            }
            JSONObject jsonObj = status.getJSONObject("data");

            int bookId = jsonObj.getInt("bookId");
            String bookName = jsonObj.getString("bookName");
            int prevChapterId = jsonObj.getInt("prevChapterId");
            int bookChapterId = jsonObj.getInt("bookChapterId");
            int nextChapterId = jsonObj.getInt("nextChapterId");
            String chapterTitle = jsonObj.getString("chapterTitle");
            String content = jsonObj.getString("bookContent");
            bookContent = new BookContent(bookId, bookName, prevChapterId,bookChapterId,nextChapterId, chapterTitle, content);
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
//    //TODO:初始化底部弹出菜单
//    private void initBottomOptionsMenu(){
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.book_content_bottom, null, false);
//
//        //1.构造一个PopupWindow，参数依次是加载的View，宽高
//        popWindow = new PopupWindow(view,
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popWindow.setAnimationStyle(R.anim.pop_anim);  //设置加载动画
//
//        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
//        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
//        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
//        popWindow.setTouchable(true);
//        popWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            }
//        });
//        //要为popWindow设置一个背景才有效
//        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        //设置透明的颜色背景。
//        //popWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
//        popWindow.showAtLocation(mMainContent, Gravity.BOTTOM, 0, 0);
//        mPrevPageBtn = view.findViewById(R.id.bc_prev_page);
//        mBackCatalogBtn = view.findViewById(R.id.bc_back_catalog);
//        mNextPageBtn = view.findViewById(R.id.bc_next_page);
//        mPrevPageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bookContent==null){
//                    showMessage("页面信息错误");
//                }else{
//                    bookChapterId = String.valueOf(bookContent.getPrevChapterId());
//                    bookContent();
//                }
//            }
//        });
//        mBackCatalogBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//        mNextPageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bookContent==null){
//                    showMessage("页面信息错误");
//                }else{
//                    bookChapterId = String.valueOf(bookContent.getNextChapterId());
//                    bookContent();
//                }
//            }
//        });
//        if(bookContent!=null){
//            if(bookContent.getPrevChapterId()!=0){
//                mPrevPageBtn.setEnabled(true);
//            }
//            if(bookContent.getBookChapterId()!=0){
//                mBackCatalogBtn.setEnabled(true);
//            }
//            if(bookContent.getNextChapterId()!=0){
//                mNextPageBtn.setEnabled(true);
//            }
//        }else {
//            mPrevPageBtn.setEnabled(false);
//            mBackCatalogBtn.setEnabled(false);
//            mNextPageBtn.setEnabled(false);
//            showMessage("获取页面数据失败，请退出当前页面重新进入");
//        }
//    }
}
