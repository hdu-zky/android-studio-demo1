package com.example.activity.indexTabFragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
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
import android.widget.Toast;

import com.example.activity.R;
import com.example.activity.adapter.BookIntroAdapter;
import com.example.activity.bean.BookIntro;
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
 * Use the {@link BookSortFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookSortFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<BookIntro> bookIntroList = new ArrayList<>();
    private BookIntroAdapter mAdapter = new BookIntroAdapter(bookIntroList);
    private Toolbar toolbar;
    private httpUtil http = new httpUtil();;
    private static String httpUrl="http://192.168.0.109:3000/bookSort";

    private static String bookTypeId = "10";// 书籍类型编号
    private static int pageIndex = 1;// 页面序号
    private static int pageCount =0;
    private static String pageSize = "20";// 每页数据量大小
    Handler handler = new Handler();
    boolean isLoading;

    public BookSortFragment() {
        // Required empty public constructor
    }

    public static BookSortFragment newInstance(String param1, String param2) {
        BookSortFragment fragment = new BookSortFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO:使sdk高版本的仍可以在主进程中进行http请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

//        if (getArguments() != null) {
            loadNextData(true);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_book_sort, container, false);

        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.sort_toolbar);

        //设置导航图标要在setSupportActionBar方法之后
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(getActivity(), "Search !", Toast.LENGTH_SHORT).show();
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
        final SwipeRefreshLayout refreshLayout= view.findViewById(R.id.bookSort_swipeRefreshLayout);
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
        recyclerView = view.findViewById(R.id.bookSort_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    //Log.d("test", "loading executed lastVisibleItemPosition="+lastVisibleItemPosition);
                    // TODO:如果正在下拉刷新则移除适配器列表最后加载更多或没有更多数据一项
                    boolean isRefreshing = refreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    // TODO:如果当前非加载状态且页面序号小于页面总数则进入加载状态加载数据
                    if (!isLoading && pageIndex < pageCount) {
                        isLoading = true;
                        pageIndex++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextData(false);
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        // TODO:添加点击事件
        mAdapter.setOnItemClickListener(new BookIntroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("onItemLongClick", "item position = " + position);
            }
            //TODO:重写书籍操作按钮事件
            @Override
            public void initPopWindow(View v){
                bookOptionsMenu(v);
            }
        });
        return view;
    }
    private void bookOptionsMenu(View v) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.book_options, null, false);
        Button addBookToShelf = (Button) view.findViewById(R.id.btn_add_book);
        Button quit = (Button) view.findViewById(R.id.btn_quit);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.menu.pop_anim);  //设置加载动画

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
        popWindow.showAsDropDown(v.findViewById(R.id.btn_show), 0, 0);

        //设置popupWindow里的按钮的事件
        addBookToShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "你点击了嘻嘻~", Toast.LENGTH_SHORT).show();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "你点击了呵呵~", Toast.LENGTH_SHORT).show();
                popWindow.dismiss();
            }
        });
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
    private boolean loadNextData(boolean reFresh){
        Map<String, String> map = new HashMap<String, String>();
        // TODO:如果是下拉刷新则重置当前页面序号
        if(reFresh){
            pageIndex=1;
            pageCount = 0;
            bookIntroList.clear();
        }
        String index = String.valueOf(pageIndex);
        map.put("bookTypeId", bookTypeId);
        map.put("pageIndex", index);
        map.put("pageSize", pageSize);

        String data = http.httpPost(httpUrl, map);

        boolean res = parseJsonMulti(data, reFresh);
        mAdapter.notifyDataSetChanged();

        return res;
    }
    // TODO:解析多个数据的Json
    private boolean parseJsonMulti(String strResult, boolean reFresh) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            pageCount = status.getInt("pageCount");

            // TODO:重置适配器中当前页面序号和页面数
            mAdapter.setPageIndexAndCount(pageIndex, pageCount);

            System.out.println("success: "+success);
            System.out.println("pageCount: "+pageCount);
            JSONArray jsonArray = status.getJSONArray("data");
            if(reFresh){
                bookIntroList.clear();
            }
            if(pageCount < pageIndex || jsonArray.length()==0){
                Toast.makeText(getActivity(),"已经到底了", Toast.LENGTH_SHORT).show();
                return false;
            }
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObj = (JSONObject)jsonArray.get(i);

                String Introduction = jsonObj.getString("Introduction");
                String authorName = jsonObj.getString("authorName");
                int bookId = jsonObj.getInt("bookId");
                String bookImg = jsonObj.getString("bookImg");
                String bookName = jsonObj.getString("bookName");
                String bookTypeName = jsonObj.getString("bookTypeName");
                BookIntro intro = new BookIntro(bookImg, bookName,authorName,bookTypeName,bookId,Introduction);
                bookIntroList.add(intro);
            }
            return true;
        } catch (JSONException e) {
            System.out.println("Json parse error !");
            e.printStackTrace();
            return false;
        }
    }
}
