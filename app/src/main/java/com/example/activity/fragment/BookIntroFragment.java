package com.example.activity.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activity.R;
import com.example.activity.adapter.BookIntroAdapter;
import com.example.activity.bean.BookIntro;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link BookIntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  BookIntroFragment extends Fragment {

    private List<BookIntro> bookIntroList = new ArrayList<>();
    public BookIntroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param label Parameter 1.
     * @return A new instance of fragment BookIntroFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookIntroFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        BookIntroFragment fragment = new BookIntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BookIntro apple = new BookIntro("http://r.m.biquge5200.com/files/article/image/0/7/7s.jpg",
                "校园修仙","无言123","1",
                "...111111111111111");
        bookIntroList.clear();
        bookIntroList.add(apple);
        bookIntroList.add(apple);
        bookIntroList.add(apple);
        bookIntroList.add(apple);
        bookIntroList.add(apple);
        bookIntroList.add(apple);
        bookIntroList.add(apple);
//        select book.Introduction, book.bookImg, book.bookId, book.bookName, bookinfo.authorName, bookinfo.bookTypeName
        String url="http://192.168.0.109:3000/home/recommend";
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("selectType", "1");
        JSONObject params = new JSONObject(map);
        String result = httpPost(url, params.toString());
        System.out.println(params.toString());
        System.out.println(result);

//        JSONObject jsonObject = JSONObject.parseObject(result);

        //需要利用实现了嵌套滚动机制的控件，才能出现AppBarLayout往上推的效果
        View rootView = inflater.inflate(R.layout.fragment_book_intro, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);    //若用其他风格显示---需定义LinearLayoutManager显示类型

        recyclerView.setAdapter(new BookIntroAdapter(bookIntroList));
        return rootView;
    }
    //url 请求的URL地址,params POST参数,context 联系上下文
    public static String httpPost(String httpUrl, String params){
        String result = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);//请求地址
            //表示设置请求体的类型是json类型
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(50000);//超时时间
            conn.setDoInput(true);//表示从服务器获取数据
            conn.setDoOutput(true);//表示向服务器写数据
            conn.connect();
            //conn.setRequestProperty("User-Agent", Other.getUserAgent(context));
//            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
//            out.write(params);
//            out.flush();
//            out.close();
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(URLEncoder.encode(params, "UTF-8").getBytes());
            outputStream.flush();
            outputStream.close();
            System.out.println("conn.getResponseCode "+conn.getResponseCode());
//            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream=conn.getInputStream();

                result = changeInputStream(inputStream, "UTF-8");
                System.out.println(result);
//            }

        } catch (Exception  e) {
            System.out.println("-----"+e);
            String string2="{\"success\":-1}";

            return string2;
        }finally {
            conn.disconnect();
        }
        return result;
    }
    /**
     * 将一个输入流转换成字符串
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream,String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

}
