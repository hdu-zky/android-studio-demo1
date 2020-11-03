package com.example.activity.indexTabFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.activity.activity.BookDetailActivity;
import com.example.activity.activity.MainActivity;
import com.example.activity.util.httpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment {

    private View view;
    ImageView blurImageView;
    ImageView avatarImageView;
    TextView nickNameView;
    TextView sexView;
    TextView signatureView;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "userId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Toolbar toolbar;
    private httpUtil http = new httpUtil();
    private static String httpUrl="http://192.168.0.109:3000/getProfile";
    private static String baseUrl="http://192.168.0.109:3000";
    private static String headImg;
    private static String sex;
    private static String nickName;
    private static String signature;
    private static final int GET_DATA_SUCCESS = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int JSON_PARSE_ERROR = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    initView();
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
    public UserSettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserSettingsFragment newInstance(String param1, String userId) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            loadNextData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        Button btn_logout = view.findViewById(R.id.btn_logout);
        //在fragment中使用onCreateOptionsMenu时需要在onCrateView中添加此方法，否则不会调用
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.user_settings_toolbar);

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
                    case R.id.action_message:
                        Toast.makeText(getActivity(), "Settings !", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mContextSp;
                mContextSp = getActivity().getSharedPreferences( "userInfo", Context.MODE_PRIVATE );
                SharedPreferences.Editor editor = mContextSp.edit();
                editor.putBoolean("saveAccount", false);
                editor.putBoolean("autoLogin", false);
                editor.apply();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        } );
        initView();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_user_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
        //实现个人中心头部磨砂布局
//        blurImageView= view.findViewById(R.id.iv_blur);
//        avatarImageView = view.findViewById(R.id.iv_avatar);
//        nickNameView = view.findViewById(R.id.user_name);
//        sexView = view.findViewById(R.id.user_val);
//        nickNameView.setText(nickName);
//        sexView.setText(sex.equals("1")?"男":"女");
//        Glide.with(getContext()).load(baseUrl+headImg).bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity())).into(blurImageView);
//        Glide.with(getContext()).load(baseUrl+headImg).bitmapTransform(new CropCircleTransformation(getActivity())).into(avatarImageView);
//    }
    // 初始化界面
    private void initView(){
        blurImageView= view.findViewById(R.id.iv_blur);
        avatarImageView = view.findViewById(R.id.iv_avatar);
        nickNameView = view.findViewById(R.id.user_name);
        sexView = view.findViewById(R.id.user_val);
        if(nickName!=null){
            nickNameView.setText(nickName);
        }
        if(sex!=null){
            sexView.setText(sex.equals("1")?"男":"女");
        }
        if(headImg!=null){
            Glide.with(getContext()).load(baseUrl+headImg).bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity())).into(blurImageView);
            Glide.with(getContext()).load(baseUrl+headImg).bitmapTransform(new CropCircleTransformation(getActivity())).into(avatarImageView);
        }
       }
    /**
     * 发起HTTP请求获取数据并展示在recyclerView上
     * */
    private void loadNextData(){
        Map<String, String> map = new HashMap<String, String>();
        String id = getArguments().getString(ARG_PARAM2);
        map.put("userId", id);
        http.sendPost(httpUrl, map, new httpUtil.HttpCallback() {
            @Override
            public void onFinish(String response) {
                Message msg = new Message();
                int res = parseJsonMulti(response);
                if(res== -1){
                    msg.what = JSON_PARSE_ERROR;
                    handler.sendMessage(msg);
                }else if(res == 0){
                    msg.what = NETWORK_ERROR;
                    msg.obj = "获取数据失败";
                    handler.sendMessage(msg);
                }else if(res ==1){
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
    // TODO:解析多个数据的Json
    private int parseJsonMulti(String strResult) {
        try {
            JSONObject status = new JSONObject(strResult);
            boolean success = status.getBoolean("success");
            System.out.println("success: "+success);
            if(!success){
                return 0;
            }else{
                JSONObject jsonObj = status.getJSONObject("data");
                headImg = jsonObj.getString("headImg");
                nickName = jsonObj.getString("nickName");
                sex = jsonObj.getString("sex");
                signature = jsonObj.getString("signature");
            }
            return 1;
        } catch (JSONException e) {
            System.out.println("Json parse error !");
            e.printStackTrace();
            return -1;
        }
    }
    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
