package com.example.activity.indexTabFragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.activity.R;
import com.example.activity.adapter.BookShelfAdapter;
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


    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userId;
    private String mParam2;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<BookShelf> bookShelfList = new ArrayList<>();
    private BookShelfAdapter mAdapter = new BookShelfAdapter(bookShelfList);
    private httpUtil http = new httpUtil();
    private static String httpUrl="http://192.168.0.109:3000/bookShelf";
    Handler handler = new Handler();

    public BookShelfFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BookShelfFragment newInstance(String userId, String param2) {
        BookShelfFragment fragment = new BookShelfFragment();
        Bundle args = new Bundle();
        String uId = "1001";
        args.putString(ARG_PARAM1, uId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        if (getArguments() != null) {
            // TODO:初始化参数及请求数据
            userId = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            loadNextData(false);
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
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("onItemLongClick", "item position = " + position);
            }
        });
        return view;
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
        String id = getArguments().getString(ARG_PARAM1);
        map.put("userId", id);
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
