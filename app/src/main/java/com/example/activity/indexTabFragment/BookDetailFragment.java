package com.example.activity.indexTabFragment;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.activity.R;
import com.example.activity.activity.BookDetailActivity;
import com.example.activity.adapter.BookChapterAdapter;
import com.example.activity.bean.BookChapter;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.RoundImageView;
import com.example.activity.util.httpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends Fragment {

    private static BookIntro bookIntro;
    private static String userId;
    private static String bookId;
    private static int pageIndex=0;
    private static int pageCount=1;
    private static boolean atShelf=false;
    private RoundImageView mImageView;
    private ImageView mBgImageView;
    private TextView mBookNameView;
    private TextView mBookTitleView;
    private TextView mBookIdView;
    private TextView mAuthorNameView;
    private TextView mTypeNameView;
    private TextView mBookStatusView;
    private TextView mBookIntroView;
    private Button mPrevPageView;
    private Button mNextPageView;
    private Button mAddShelfView;
    private Button mStartReadView;

    private Toolbar toolbar;
    private ActionBar actionBar;
    private List<BookChapter> latestChapterList = new ArrayList<>();
    private List<BookChapter> totalChapterList = new ArrayList<>();
    private BookChapterAdapter latestAdapter, totalAdapter;
    private httpUtil http = new httpUtil();
    private static String[] httpUrl= new String[]{
            "http://192.168.0.109:3000/bookInfo/latestCatalog", //0
            "http://192.168.0.109:3000/bookInfo/getCatalog", //1
            "http://192.168.0.109:3000/bookInfo/getCatCount", //2
            "http://192.168.0.109:3000/bookInfo/checkShelf", //3
            "http://192.168.0.109:3000/bookInfo/addToShelf", //4
            "http://192.168.0.109:3000/bookInfo/addBookVisits", //5
    };
    private static final int LOAD_TOTAL_CATALOG = 1;
    private static final int LOAD_LATEST_CATALOG = 2;
    private static final int LOAD_PAGE_COUNT = 3;
    private static final int check_shelf_callback = 4;
    private static final int add_shelf_callback = 5;
    private static final int NETWORK_ERROR = 13;
    private static final int JSON_PARSE_ERROR = 15;
    //用来接收联网线程结束后发送的信号并进行UI相应处理
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("handler",""+msg);
            switch (msg.what){
                case LOAD_TOTAL_CATALOG:
                    totalAdapter.notifyDataSetChanged();
                    break;
                case LOAD_LATEST_CATALOG:
                    latestAdapter.notifyDataSetChanged();
                    break;
                case LOAD_PAGE_COUNT:
                    pageCount = msg.arg1;
                    break;
                case check_shelf_callback:
                    if(msg.arg1 == 1){
                        mAddShelfView.setEnabled(false);
                        mAddShelfView.setText("已在书架");
                    }else {
                        mAddShelfView.setEnabled(true);
                        mAddShelfView.setText("加入书架");
                    }
                    break;
                case add_shelf_callback:
                    if(msg.arg1 == 1){
                        showMessage("添加成功");
                        mAddShelfView.setEnabled(false);
                        mAddShelfView.setText("已在书架");
                    }else {
                        showMessage("添加失败");
                    }
                    break;
                case JSON_PARSE_ERROR:
                    showMessage("Json parse error !");
                    break;
                case NETWORK_ERROR:
                    showMessage(msg.obj.toString());
                    break;
            }
        }
    };
    public BookDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BookDetailFragment newInstance(BookIntro intro, String uId) {
        BookDetailFragment fragment = new BookDetailFragment();
        //根据参数初始化当前页面数据模型
        bookIntro = intro;
        bookId = String.valueOf(bookIntro.getBookId());
        userId = uId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里错误的又重新初始化一次handler使得后面的handler发送的信号无法被处理，搞得我整了好久
        //handler=new Handler();
        loadPageCount();
        checkShelf();
    }
    // TODO:加载最新章节信息
    private void loadPageCount(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("bookId", bookId);
        http.sendPost(httpUrl[2], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                int res = parseJsonSingle(response);
                if(res == -1){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else {
                    Message msg = new Message();
                    msg.what = LOAD_PAGE_COUNT;
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
    // TODO:检查是否在书架
    private void checkShelf(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("bookId", bookId);
        http.sendPost(httpUrl[3], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                boolean atShelf = parseJsonShelf(response);
                Message msg = new Message();
                msg.what = check_shelf_callback;
                msg.arg1 = atShelf?1:0;
                handler.sendMessage(msg);
            }
            @Override
            public void onError(String err) {
                Message msg = new Message();
                msg.what = NETWORK_ERROR;
                msg.arg1 = -1;
                handler.sendMessage(msg);
            }
        });
    }
    // TODO:加入书架
    private void addToShelf(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("bookId", bookId);
        http.sendPost(httpUrl[4], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                boolean addShelf = parseJsonShelf(response);
                Message msg = new Message();
                msg.what = add_shelf_callback;
                msg.arg1 = addShelf?1:0;
                handler.sendMessage(msg);
            }
            @Override
            public void onError(String err) {
                Message msg = new Message();
                msg.what = NETWORK_ERROR;
                msg.arg1 = -1;
                handler.sendMessage(msg);
            }
        });
    }
    // TODO:解析单层数据的Json
    private boolean parseJsonShelf(String strResult){
        try {
            JSONObject status = new JSONObject(strResult);
            return status.getBoolean("success");
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
    }
    // TODO:解析单层数据的Json
    private int parseJsonSingle(String strResult){
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            if(success){
                return status.getInt("data");
            }else{
                return 0;
            }
        }catch (JSONException e){
            e.printStackTrace();
            return -1;
        }
    }
    // TODO:加载最新章节信息
    private void loadLatestCatalog(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("bookId", bookId);
        http.sendPost(httpUrl[0], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                int res = parseJsonMulti(response,1);
                if(res == -1){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else if(res ==1){
                    handler.sendEmptyMessage(LOAD_LATEST_CATALOG);
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
    //TODO:加载分页展示的目录
    private void loadTotalCatalog(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("bookId", bookId);
        map.put("catalogIndex", String.valueOf(pageIndex));
        // TODO:加载最新章节信息
        http.sendPost(httpUrl[1], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                int res = parseJsonMulti(response,2);
                if(res == -1){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else if(res ==1){
                    handler.sendEmptyMessage(LOAD_TOTAL_CATALOG);
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
     * @param type int 解析的数据存放的方式，是最新章节totalChapterList的还是全部章节latestChapterList的
     * @return 0-返回结果为空 1-解析成功且数据不为空 -1-解析失败
     */
    private int parseJsonMulti(String strResult, int type) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            JSONArray jsonArray = status.getJSONArray("data");
            if(jsonArray.length()==0){
                return 0;
            }
            //TODO:如果是分页展示则每次全部清空
            if(type == 2){
                totalChapterList.clear();
            }else {
                latestChapterList.clear();
            }
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObj = (JSONObject)jsonArray.get(i);

                int bookId = jsonObj.getInt("bookId");
                String bookChapterId = jsonObj.getString("bookChapterId");
                String chapterTitle = jsonObj.getString("chapterTitle");
                BookChapter chapter = new BookChapter(bookId, bookChapterId, chapterTitle);
                if(type == 1){
                    latestChapterList.add(chapter);
                }else {
                    totalChapterList.add(chapter);
                }
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        // TODO:控制当前页面全屏显示,这种声明会出现页面占满全屏，连手机状态栏也被挤占情况
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        // TODO:在fragment中使用onCreateOptionsMenu时需要在onCrateView中添加此方法，否则不会调用
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.bd_toolbar);

        // TODO:设置导航图标要在setSupportActionBar方法之后
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
        // TODO:顶部返回控件点击事件监听
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
                loadPageCount();
                checkShelf();
                loadLatestCatalog();
                loadTotalCatalog();
                checkState();
                refreshLayout.setRefreshing(false);
            }
        });

        // TODO:获取页面文本图片控件
        mBookTitleView = view.findViewById(R.id.bd_title);
        mBookNameView = view.findViewById(R.id.bd_bookName);
        mAuthorNameView = view.findViewById(R.id.bd_authorName);
        mImageView = view.findViewById(R.id.bd_bookImg);
        mBgImageView = view.findViewById(R.id.bd_bookDetail);
        mBookIdView = view.findViewById(R.id.bd_bookId);
        mTypeNameView = view.findViewById(R.id.bd_typeName);
        mBookStatusView = view.findViewById(R.id.bd_status);
        mBookIntroView = view.findViewById(R.id.bd_intro);
        // TODO:获取页面按钮控件
        mPrevPageView = view.findViewById(R.id.bd_prev_page);
        mNextPageView = view.findViewById(R.id.bd_next_page);
        mAddShelfView = view.findViewById(R.id.bd_add_shelf);
        mStartReadView = view.findViewById(R.id.bd_start_read);
        // TODO:监听上一页点击事件
        mPrevPageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mPrevPageView","click"+pageIndex);
                //如果当前页面序号大于零
                if(pageIndex > 0){
                    pageIndex--;
                    loadTotalCatalog();
                    checkState();
                }else {
                    showMessage("当前已是第一页");
                }
            }
        });
        // TODO:监听下一页点击事件
        mNextPageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mNextPageView","click"+pageIndex);
                //如果当前页面序号小于总页面数
                if(pageIndex < pageCount){
                    pageIndex++;
                    loadTotalCatalog();
                    checkState();
                }else {
                    showMessage("当前已是最后一页");
                }
            }
        });
        // TODO:监听加入书架点击事件
        mAddShelfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToShelf();
            }
        });
        // TODO:监听加入书架点击事件
        mStartReadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startRead();
            }
        });
        initView();
        latestAdapter = new BookChapterAdapter(getContext(), R.layout.book_chapter_item, latestChapterList);
        totalAdapter = new BookChapterAdapter(getContext(), R.layout.book_chapter_item, totalChapterList);
        ListView latestListView = view.findViewById(R.id.bd_latest_chapter);
        ListView totalListView = view.findViewById(R.id.bd_total_chapter);
        latestListView.setAdapter(latestAdapter);
        totalListView.setAdapter(totalAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String result = parent.getItemAtPosition(position).toString();//获取选择项的值
//                Toast.makeText(getContext(),"您点击了"+result,Toast.LENGTH_SHORT).show();
//
//            }
//        });
        checkState();
        return view;
    }
    // TODO:获取参数并渲染到界面
    private void initView() {
        //根据数据模型初始化界面
        if(bookIntro!= null){
            String imgSrc = bookIntro.getImageSrc();
            Glide.with(getContext()).load(imgSrc).into(mImageView);

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
        loadLatestCatalog();
        loadTotalCatalog();
    }
    // TODO:检查当前页面序号和页数对比并更新按钮状态
    private void checkState(){
        Log.e("checkState pageIndex"+pageIndex,"pageIndex"+pageCount);
        if(pageIndex == 0){
            mPrevPageView.setEnabled(false);
            mPrevPageView.setBackgroundColor(getResources().getColor(R.color.colorGray));
        }else {
            mPrevPageView.setEnabled(true);
            mPrevPageView.setBackgroundColor(getResources().getColor(R.color.buttonColor));
        }
        if(pageIndex +1 == pageCount || pageCount == 0){
            mNextPageView.setEnabled(false);
            mNextPageView.setBackgroundColor(getResources().getColor(R.color.colorGray));
        }else{
            mNextPageView.setEnabled(true);
            mNextPageView.setBackgroundColor(getResources().getColor(R.color.buttonColor));
        }
    }
    private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    // TODO:加入顶部菜单栏布局
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

    // TODO:主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    //  TODO:监听到返回按钮点击事件
                    //showMessage("回退");
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }
}
