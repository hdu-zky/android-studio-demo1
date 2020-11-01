package com.example.activity.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.activity.R;
import com.example.activity.bean.BookIntro;
import com.example.activity.component.RoundImageView;
import com.example.activity.indexTabFragment.BookDetailFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BookDetailActivity extends AppCompatActivity {

    private static BookIntro bookIntro;
    private RoundImageView mImageView;
    private TextView mBookNameView;
    private ImageView mBgImageView;
    private TextView mBookIdView;
    private TextView mAuthorNameView;
    private TextView mTypeNameView;
    private TextView mBookStatusView;
    private TextView mBookIntroView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    public BookDetailActivity() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static BookDetailFragment newInstance(BookIntro intro) {
        BookDetailFragment fragment = new BookDetailFragment();
        //根据参数初始化当前页面数据模型
        bookIntro = intro;
        return fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // TODO:使sdk高版本的仍可以在主进程中进行http请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //在fragment中使用onCreateOptionsMenu时需要在onCrateView中添加此方法，否则不会调用
        //setHasOptionsMenu(true);
        toolbar = findViewById(R.id.bd_toolbar);

        //设置导航图标要在setSupportActionBar方法之后
        this.setSupportActionBar(toolbar);
        actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(BookDetailActivity.this, "Search !", Toast.LENGTH_SHORT).show();
                        break;
//                    case R.id.action_notifications:
//                        Toast.makeText(getActivity(), "Notificationa !", Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.action_message:
                        Toast.makeText(BookDetailActivity.this, "Settings !", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        initView();
    }
    /**
     * 在默认情况下，当您通过Intent启到一个Activity的时候，就算已经存在一个相同的正在运行的Activity,
     * 系统都会创建一个新的Activity实例并显示出来。为了不让Activity实例化多次，
     * 我们需要通过在AndroidManifest.xml配置activity的加载方式（launchMode）
     * launchMode为singleTask的时候，通过Intent启到一个Activity,如果系统已经存在一个实例，
     * 系统就会将请求发送到这个实例上，但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法，
     * 而是调用onNewIntent方法（因为不是从onCreate起执行，因此singleTask时只有在这里getIntent）
     * */
    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);
        //must store the new intent unless getIntent() will return the old one
        initView();

    }
    // TODO:获取参数并渲染到界面
    public void initView() {
        Intent intent = getIntent();
        //接收String类型的值
        String imageSrc = intent.getStringExtra("imageSrc");
        String bookName = intent.getStringExtra("bookName");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookTypeName = intent.getStringExtra("bookTypeName");
        String bookId = intent.getStringExtra("bookId");
        String bookIntroduction = intent.getStringExtra("bookIntroduction");
        //获取页面控件
        mBookNameView = findViewById(R.id.bd_bookName);
        mAuthorNameView = findViewById(R.id.bd_authorName);
        mImageView = findViewById(R.id.bd_bookImg);
        mBgImageView = findViewById(R.id.bd_bookDetail);
        mBookIdView = findViewById(R.id.bd_bookId);
        mTypeNameView = findViewById(R.id.bd_typeName);
        mBookStatusView = findViewById(R.id.bd_status);
        mBookIntroView = findViewById(R.id.bd_intro);

        if(actionBar!= null){
            actionBar.setTitle(bookName);
        }
        //根据数据模型初始化界面
        Glide.with(this).load(imageSrc).into(mImageView);
        Glide.with(this).load(imageSrc).bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this)).into(mBgImageView);

        mBookNameView.setText(bookName);
        mAuthorNameView.setText(bookAuthor);
        mBookIdView.setText(bookId);
        mTypeNameView.setText(bookTypeName);
        mBookIntroView.setText(bookIntroduction);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 这条表示加载菜单文件，第一个参数表示通过那个资源文件来创建菜单
        // 第二个表示将菜单传入那个对象中。这里我们用Menu传入menu
        // 这条语句一般系统帮我们创建好
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.detail_more:
                Toast.makeText(BookDetailActivity.this, "detail_more !", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
