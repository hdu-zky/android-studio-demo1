package com.example.activity.indexTabFragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * to handle interaction events.
 * Use the {@link RankTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  RankTabFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<BookIntro> bookIntroList = new ArrayList<>();
    private BookIntroAdapter mAdapter = new BookIntroAdapter(bookIntroList);
    private httpUtil http;
    private static String httpUrl="http://192.168.0.109:3000/bookRank/getTopList";
    private static int pageIndex =0;
    Handler handler = new Handler();
    boolean isLoading;

    public RankTabFragment() {
        // Required empty public constructor
    }
    public static RankTabFragment newInstance(String label, int type) {
        Bundle args = new Bundle();

        String strType = String.valueOf(type);
        args.putString("label", label);
        args.putString("SelectType", strType);

        RankTabFragment fragment = new RankTabFragment();
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

        // TODO:初始化参数及请求数据
        if (getArguments() != null) {
            loadNextData(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO:需要利用实现了嵌套滚动机制的控件，才能出现AppBarLayout往上推的效果
        View rootView = inflater.inflate(R.layout.rank_tab_fragment, container, false);

        // TODO:实现下拉刷新效果
        final SwipeRefreshLayout refreshLayout= rootView.findViewById(R.id.swipeRefreshLayout);
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
        recyclerView = rootView.findViewById(R.id.rankTopVisit_recycler_view);
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

                    boolean isRefreshing = refreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
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
        //添加点击事件
        mAdapter.setOnItemClickListener(new BookIntroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("test", "item position = " + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("onItemLongClick", "item position = " + position);
            }
        });
        return rootView;
    }
    /**
     * 发起HTTP请求获取数据并展示在recyclerView上
     * @param reFresh 是否下拉刷新
     * */
    private boolean loadNextData(boolean reFresh){
        Map<String, String> map = new HashMap<String, String>();
        String type = getArguments().getString("SelectType");
        Log.d("loadNextData", "pageIndex= "+pageIndex);
        String index = String.valueOf(pageIndex);
        map.put("selectType", type);
        map.put("pageIndex", index);
        http = new httpUtil();
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
                bookIntroList.clear();
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
