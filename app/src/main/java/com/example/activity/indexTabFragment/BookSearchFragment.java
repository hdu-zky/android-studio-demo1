package com.example.activity.indexTabFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.R;
import com.example.activity.adapter.BookSearchAdapter;
import com.example.activity.bean.BookSearch;
import com.example.activity.util.httpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String userId;

    private SearchView searchView;
    private TextView resultCountView;
    private ListView listView;
    private List<BookSearch> searchList = new ArrayList<>();
    private BookSearchAdapter bookSearchAdapter;

    private httpUtil http = new httpUtil();
    private static String[] httpUrl= new String[]{"http://192.168.0.109:3000/search"};
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private BookDetailFragment bookDetailFragment;
    private static final int get_bookInfo_callback = 1;
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
                case get_bookInfo_callback:
                    if(msg.arg1 == 2){
                        showMessage("查找书籍信息失败，请刷新");
                    }else if(msg.arg1 == 0){
                        showMessage("未查找到相关书籍信息");
                    }
                    bookSearchAdapter.notifyDataSetChanged();
                    resultCountView.setText("共找到 "+searchList.size()+" 条数据");
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
    public BookSearchFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static BookSearchFragment newInstance(String param1, String uId) {
        BookSearchFragment fragment = new BookSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, uId);
        fragment.setArguments(args);
        userId = uId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager= getActivity().getSupportFragmentManager();
        Log.e("onCreate","search");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);

        Log.e("onCreateView","search");
        /**
         * Fragment切换完成后，点击空白区域或者底部的时候，出现点击区域冲突的问题。
         * 就是在新的fragment中，点击底部的空白区域，触发了上一个fragment的点击事件
         * 加上下面这一句可以解决https://www.cnblogs.com/shoneworn/p/5075247.html
         * */
        view.setOnClickListener(null);

        // TODO:顶部返回控件点击事件监听
        TextView btnBack = view.findViewById(R.id.bs_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        resultCountView = view.findViewById(R.id.search_result_count);
        searchView = view.findViewById(R.id.search_searchView);
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 0) {
                    searchList.clear();
                    searchBook(s);
                    //添加下面一句,防止数据两次加载
                    searchView.setIconified(true);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("onQueryTextSubmit", "TextChange --> " + s);
                return false;
            }
        });
        bookSearchAdapter = new BookSearchAdapter(getContext(), searchList);

        listView = view.findViewById(R.id.search_result_listView);
        listView.setAdapter(bookSearchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //根据点击位置从适配器中取得对应数据对象
                BookSearch bookSearch = bookSearchAdapter.getItem(position);
                //打开书籍详细页面
                mTransaction = mManager.beginTransaction();
                //根据数据对象初始化书籍细节信息页面并向容器加入该碎片
                bookDetailFragment = BookDetailFragment.newInstance(
                        String.valueOf(bookSearch.getBookId()),
                        userId
                );
                // 设置动画效果
                mTransaction.setCustomAnimations(
                        R.anim.slide_right_in,
                        R.anim.slide_left_out,
                        R.anim.slide_left_in,
                        R.anim.slide_right_out
                ).replace(R.id.index_content,bookDetailFragment);
                //加入返回栈
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                //Toast.makeText(getContext(),"您点击了"+bookSearchAdapter.getItem(position).getBookId() ,Toast.LENGTH_SHORT).show();
            }
        });
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
    private void searchBook(String keyWord){
        Map<String, String> map = new HashMap<String, String>();
        map.put("keyWord", keyWord);
        map.put("type", "2");
        http.sendPost(httpUrl[0], map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                int res = parseJsonBookInfo(response);
                if(res == -1){
                    handler.sendEmptyMessage(JSON_PARSE_ERROR);
                }else {
                    Message msg = new Message();
                    msg.what = get_bookInfo_callback;
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
                return 2;
            }
            JSONArray jsonArray = status.getJSONArray("data");
            if(jsonArray.length()==0){
                return 0;
            }
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObj = (JSONObject)jsonArray.get(i);
                int bookId = jsonObj.getInt("bookId");
                String bookName = jsonObj.getString("bookName");
                String authorName = jsonObj.getString("authorName");
                String bookTypeName = jsonObj.getString("bookTypeName");
                String state = jsonObj.getString("status");
                BookSearch bookInfo = new BookSearch(bookId,bookName,authorName,bookTypeName, state);
                searchList.add(bookInfo);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
