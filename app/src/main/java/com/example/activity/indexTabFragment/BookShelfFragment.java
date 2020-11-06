package com.example.activity.indexTabFragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.activity.R;
import com.example.activity.adapter.BookShelfAdapter;
import com.example.activity.bean.BookIntro;
import com.example.activity.bean.BookShelf;
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
 * Use the {@link BookShelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookShelfFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "userId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String userId;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<BookShelf> bookShelfList = new ArrayList<>();
    private BookShelfAdapter mAdapter = new BookShelfAdapter(bookShelfList);
    private httpUtil http = new httpUtil();
    private static String[] httpUrl= new String[]{
            "http://192.168.0.109:3000/bookShelf",
            "http://192.168.0.109:3000/bookShelf/removeBook",
    };
    private static final int GET_DATA_SUCCESS = 1;
    private static final int remove_shelf_callback = 2;
    private static final int NETWORK_ERROR = 13;
    private static final int JSON_PARSE_ERROR = 15;
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private BookDetailFragment bookDetailFragment;
    private BookSearchFragment bookSearchFragment;
    //用来接收联网线程结束后发送的信号并进行UI相应处理
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("handler",""+msg);
            switch (msg.what){
                case remove_shelf_callback:
                    if(msg.arg1 == 1){
                        if(msg.arg2 >= 0){
                            bookShelfList.remove(msg.arg2);
                            mAdapter.notifyItemRemoved(msg.arg2);
                            Toast.makeText(getContext(), "删除成功~", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "删除失败,该项不存在", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getContext(), "删除失败~", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_DATA_SUCCESS:
                    mAdapter.notifyDataSetChanged();
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

    public BookShelfFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BookShelfFragment newInstance(String param1, String userId) {
        BookShelfFragment fragment = new BookShelfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager= getActivity().getSupportFragmentManager();
        // TODO:使sdk高版本的仍可以在主进程中进行http请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (getArguments() != null) {
            // TODO:初始化参数及请求数据
            mParam1 = getArguments().getString(ARG_PARAM1);
            userId= getArguments().getString(ARG_PARAM2);
            loadNextData(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_shelf, container, false);
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.shelf_toolbar);

        //设置导航图标要在setSupportActionBar方法之后
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(getActivity(), "Search !", Toast.LENGTH_SHORT).show();
                        //打开书籍详细页面
                        mTransaction = mManager.beginTransaction();
                        //根据数据对象初始化书籍细节信息页面并向容器加入该碎片
                        bookSearchFragment = BookSearchFragment.newInstance(
                                "123",
                                userId
                        );
                        // 设置动画效果
                        mTransaction.setCustomAnimations(
                                R.anim.slide_right_in,
                                R.anim.slide_left_out,
                                R.anim.slide_left_in,
                                R.anim.slide_right_out
                        ).replace(R.id.index_content,bookSearchFragment);
                        //加入返回栈
                        mTransaction.addToBackStack(null);
                        mTransaction.commit();
                        break;
//                    case R.id.action_notifications:
//                        Toast.makeText(getActivity(), "Notificationa !", Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.action_settings:
                        Toast.makeText(getActivity(), "Settings !", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
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
                        loadNextData(true);
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });
        // TODO:初始化recyclerView并绑定adapter
        recyclerView = view.findViewById(R.id.bookShelf_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        // TODO:添加点击事件
        mAdapter.setOnItemClickListener(new BookShelfAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //根据点击位置从适配器中取得对应数据对象
                BookShelf bookShelf = mAdapter.getItem(position);
                mTransaction = mManager.beginTransaction();
                //根据数据对象初始化书籍细节信息页面并向容器加入该碎片
                bookDetailFragment = BookDetailFragment.newInstance(
                        String.valueOf(bookShelf.getBookId()),
                        userId
                );
                // 设置动画效果
                //mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mTransaction.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out
                ).replace(R.id.index_content,bookDetailFragment);
                //加入返回栈
                mTransaction.addToBackStack(null);
                mTransaction.commit();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("onItemLongClick", "item position = " + position);
            }
            //TODO:重写书籍操作按钮事件
            @Override
            public void initPopWindow(View v, int position){
                bookOptionsMenu(v, position);
            }
        });
        return view;
    }
    private void bookOptionsMenu(@NonNull View itemView, final int position) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.book_shelf_options, null, false);
        Button removeBookView = (Button) view.findViewById(R.id.btn_remove_book);
        Button quit = (Button) view.findViewById(R.id.btn_quit);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.pop_anim);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(itemView.findViewById(R.id.btn_show), -20, 0);

        TextView bookIdView = itemView.findViewById(R.id.tv_bookId);
//        final String bookId = bookIdView.getText().toString();
        final String bookId = String.valueOf(bookShelfList.get(position).getBookId());
//        final String userId = getArguments().getString("userId");
        //设置popupWindow里的按钮的事件
        removeBookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBook(bookId, position);
                popWindow.dismiss();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
    }
    // TODO:从书架移除
    private void removeBook(String bookId, final int position){
        Map<String, String> map = new HashMap<String, String>();
        Log.d("userId"," "+userId);
        Log.d("bookId"," "+bookId);
        map.put("userId", userId);
        map.put("bookId", bookId);
        http.sendPost(httpUrl[1], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                boolean res = parseJsonSuc(response);
                Message msg = new Message();
                msg.what = remove_shelf_callback;
                msg.arg1 = res?1:0;
                msg.arg2 = position;
                handler.sendMessage(msg);
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
    private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    // TODO:解析单层数据的Json
    private boolean parseJsonSuc(String strResult){
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: " + success);
            return success;
        }catch (JSONException e){
            System.out.println("Json parse error !");
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_shelf, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    /**
     * 发起HTTP请求获取数据并展示在recyclerView上
     * @param reFresh 是否下拉刷新
     * */
    private void loadNextData(final boolean reFresh){
        Map<String, String> map = new HashMap<String, String>();
        String id = getArguments().getString(ARG_PARAM2);
        map.put("userId", id);
        http.sendPost(httpUrl[0], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                boolean res = parseJsonMulti(response, reFresh);
                if(!res){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else {
                    handler.sendEmptyMessage(GET_DATA_SUCCESS);
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
    // TODO:解析多个数据的Json
    private boolean parseJsonMulti(String strResult, boolean reFresh) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            JSONArray jsonArray = status.getJSONArray("data");
            if(reFresh){
                bookShelfList.clear();
            }
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObj = (JSONObject)jsonArray.get(i);

                int bookId = jsonObj.getInt("bookId");
                String bookImg = jsonObj.getString("bookImg");
                String bookName = jsonObj.getString("bookName");
                String authorName = jsonObj.getString("authorName");
                String bookTypeName = jsonObj.getString("bookTypeName");
                String updateTime = jsonObj.getString("updateTime");
                int state = jsonObj.getInt("status");
                String latestChapter = jsonObj.getString("latestChapter");
                String latestChTitle = jsonObj.getString("latestChTitle");

                BookShelf intro = new BookShelf(bookImg, bookName,authorName,bookTypeName,
                        bookId,updateTime, state,latestChapter,latestChTitle);
                bookShelfList.add(intro);
            }
            return true;
        } catch (JSONException e) {
            System.out.println("Json parse error !");
            e.printStackTrace();
            return false;
        }
    }
}
