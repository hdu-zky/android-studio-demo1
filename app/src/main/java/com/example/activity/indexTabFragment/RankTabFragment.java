package com.example.activity.indexTabFragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.activity.BookDetailActivity;
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
    private httpUtil http = new httpUtil();;
    private static String[] httpUrl= new String[]{
            "http://192.168.0.109:3000/bookRank/getTopList",
            "http://192.168.0.109:3000/bookRank/getCount",
            "http://192.168.0.109:3000/bookInfo/addToShelf",
    };
    private static int pageIndex =1;
    private static int pageCount =0;
    private static String userId;
    private static final int GET_DATA_SUCCESS = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int JSON_PARSE_ERROR = 3;
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    // TODO:重置适配器中当前页面序号和页面数
                    mAdapter.setPageIndexAndCount(pageIndex, pageCount);
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
    boolean isLoading;
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private static RankTabFragment fragment;
    private BookDetailFragment bookDetailFragment;
    public RankTabFragment() {
        // Required empty public constructor
    }
    public static RankTabFragment newInstance(String label, int type, String uId) {
        Bundle args = new Bundle();

        String strType = String.valueOf(type);
        args.putString("label", label);
        args.putString("SelectType", strType);
        args.putString("userId", uId);
        userId = uId;
        RankTabFragment fragment = new RankTabFragment();
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

        // TODO:初始化参数及请求数据
        if (getArguments() != null) {
            loadNextData(true);
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
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        loadNextData(true);
                        refreshLayout.setRefreshing(false);
//                    }
//                },2000);
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
//                Log.d("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    //Log.d("test", "loading executed lastVisibleItemPosition="+lastVisibleItemPosition);

                    boolean isRefreshing = refreshLayout.isRefreshing();
                    // TODO:如果正在下拉刷新则移除适配器列表最后加载更多或没有更多数据一项
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    // TODO:如果当前非加载状态且页面序号小于页面总数则进入加载状态加载数据
                    if (!isLoading && pageIndex < pageCount) {
                        isLoading = true;
                        pageIndex++;

                        loadNextData(false);

                        Log.d("test", "load more completed");
                        isLoading = false;

//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        }, 1000);
                    }
                }
            }
        });
        //添加点击事件
        mAdapter.setOnItemClickListener(new BookIntroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //根据点击位置从适配器中取得对应数据对象
                BookIntro bookIntro = mAdapter.getItem(position);
                //打开书籍详细页面
//                Intent intent = new Intent(getContext(), BookDetailActivity.class);
//
//                intent.putExtra("imageSrc", bookIntro.getImageSrc());
//                intent.putExtra("bookName", bookIntro.getBookName());
//                intent.putExtra("bookAuthor", bookIntro.getBookAuthor());
//                intent.putExtra("bookTypeName", bookIntro.getBookTypeName());
//                intent.putExtra("bookId", String.valueOf(bookIntro.getBookId()));
//                intent.putExtra("bookIntroduction", bookIntro.getBookIntroduction());
//                startActivity(intent);
                mTransaction = mManager.beginTransaction();
                //根据数据对象初始化书籍细节信息页面并向容器加入该碎片
                bookDetailFragment = BookDetailFragment.newInstance(bookIntro);
                mTransaction.replace(R.id.index_content,bookDetailFragment)
                        .setCustomAnimations(
                            R.anim.slide_right_in,
//                            R.anim.slide_left_out,
//                            R.anim.slide_left_in,
                            R.anim.slide_right_out
                        );
                //加入返回栈
                mTransaction.addToBackStack(null);
                // 设置动画效果
                mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mTransaction.commit();
            }
            //TODO:重写书籍操作按钮事件
            @Override
            public void initPopWindow(View v){
                bookOptionsMenu(v);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("onItemLongClick", "item position = " + position);
            }
        });
        return rootView;
    }
    private void hideFragment(FragmentTransaction transaction) {
        if (bookDetailFragment != null) {
            transaction.hide(bookDetailFragment);
        }
    }
    private void bookOptionsMenu(View itemView) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.book_options, null, false);
        Button addBookToShelf = (Button) view.findViewById(R.id.btn_add_book);
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
        final String bookId = bookIdView.getText().toString();
//        final String userId = getArguments().getString("userId");
        //设置popupWindow里的按钮的事件
        addBookToShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                Log.d("userId"," "+userId);
                Log.d("bookId"," "+bookId);
                map.put("userId", userId);
                map.put("bookId", bookId);
                String data = http.httpPost(httpUrl[2], map);

                boolean res = parseJsonSuc(data);
                if(res){
                    Toast.makeText(getContext(), "添加成功~", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "添加失败~", Toast.LENGTH_SHORT).show();
                }
                popWindow.dismiss();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "你点击了呵呵~", Toast.LENGTH_SHORT).show();
                popWindow.dismiss();
            }
        });
    }
    /**
     * 发起HTTP请求获取数据并展示在recyclerView上
     * @param reFresh 是否下拉刷新
     * */
    private void loadNextData(boolean reFresh){
        Map<String, String> map = new HashMap<String, String>();
        String type = getArguments().getString("SelectType");
        String index = String.valueOf(pageIndex);
        map.put("selectType", type);
        // TODO:如果是下拉刷新则重置当前页面序号和页面数
        if(reFresh){
            pageIndex = 1;
            pageCount = 0;
            bookIntroList.clear();
            http.sendPost(httpUrl[1], map, new httpUtil.HttpCallback() {
                @Override
                public void onFinish(String response) {
                    int res = parseJsonSingle(response);
                    Message msg = new Message();
                    if(res == -1){
                        msg.what = JSON_PARSE_ERROR;
                        handler.sendMessage(msg);
                    }else{
                        pageCount = res;
                        msg.what = GET_DATA_SUCCESS;
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
        map.put("pageIndex", index);
        // TODO:根据当前页面序号和选择类型发起请求数据
        http.sendPost(httpUrl[0], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                boolean res = parseJsonMulti(response);
                if(!res){
                    msg.what = JSON_PARSE_ERROR;
                    handler.sendMessage(msg);
                }else {
                    msg.what = GET_DATA_SUCCESS;
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
    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    // TODO:解析多个数据的Json
    private boolean parseJsonMulti(String strResult) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            JSONArray jsonArray = status.getJSONArray("data");
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
    // TODO:解析单层数据的Json
    private int parseJsonSingle(String strResult){
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: " + success);
            if(success){
                int pageCount = status.getInt("data");
                return pageCount;
            }else{
                return 0;
            }
        }catch (JSONException e){
            System.out.println("Json parse error !");
            e.printStackTrace();
            return -1;
        }
    }
    // TODO:解析单层数据的Json
    private boolean parseJsonSuc(String strResult){
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: " + success);
            if(success){
                return true;
            }else{
                return false;
            }
        }catch (JSONException e){
            System.out.println("Json parse error !");
            e.printStackTrace();
            return false;
        }
    }

}
