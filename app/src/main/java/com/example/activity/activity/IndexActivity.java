package com.example.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.activity.R;
import com.example.activity.indexTabFragment.BookRankFragment;
import com.example.activity.indexTabFragment.BookShelfFragment;
import com.example.activity.indexTabFragment.BookSortFragment;
import com.example.activity.indexTabFragment.UserSettingsFragment;

public class IndexActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private Long firstBackTime=(long)0;
    private BottomNavigationBar mBottomNavigationBar;
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;

    private BookRankFragment mBookRankFragment; //排行
    private BookSortFragment mBookSortFragment; //分类
    private BookShelfFragment mBookShelfFragment; //书架
    private UserSettingsFragment mUserSettingsFragment;  //个人设置

    private int lastSelectedPosition;
    SharedPreferences mContextSp;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mContextSp = this.getSharedPreferences( "userInfo", Context.MODE_PRIVATE );
        userId = mContextSp.getString( "userId", " " );
//        Log.e("userId"," "+userId);
        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        initBottomNavBar();

        //添加监听  实现BottomNavigationBar.OnTabSelectedListener接口
        //public void onTabSelected(int position) 未选中 -> 选中
        //public void onTabUnselected(int position) 选中 -> 未选中
        //public void onTabReselected(int position) 选中 -> 选中
        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();

    }
    private void initBottomNavBar() {
        // TODO 设置模式
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        // TODO 设置背景色样式
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.setBarBackgroundColor(R.color.white);
        //消息提示
        TextBadgeItem mTextBadgeItem = new TextBadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.info)
                .setText("5")
                .setTextColorResource(R.color.white)
                .setBorderColorResource(R.color.info)  //外边界颜色
                .setHideOnSelect(true);

        ShapeBadgeItem mShapeBadgeItem = new ShapeBadgeItem()
                .setShape(ShapeBadgeItem.SHAPE_OVAL)
                .setShapeColor(R.color.info)
                .setShapeColorResource(R.color.info)
                .setSizeInDp(this, 10, 10)
                .setEdgeMarginInDp(this, 2)
//                .setSizeInPixels(30,30)
//                .setEdgeMarginInPixels(-1)
                .setGravity(Gravity.TOP | Gravity.END)
                .setHideOnSelect(false);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.rank_active, "排行").setActiveColorResource(R.color.tabActive)
                        .setInactiveIconResource(R.drawable.rank_in_active).setInActiveColorResource(R.color.tabInActive))
                .addItem(new BottomNavigationItem(R.drawable.menu, "分类").setActiveColorResource(R.color.tabActive)
                        .setInactiveIconResource(R.drawable.menu_in_active).setInActiveColorResource(R.color.tabInActive))
                .addItem(new BottomNavigationItem(R.drawable.shelf, "书架").setActiveColorResource(R.color.tabActive)
                        .setInactiveIconResource(R.drawable.shelf_in_active).setInActiveColorResource(R.color.tabInActive).setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.user_active, "设置").setActiveColorResource(R.color.tabActive)
                        .setInactiveIconResource(R.drawable.user_in_active).setInActiveColorResource(R.color.tabInActive).setBadgeItem(mTextBadgeItem))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

    }
    @Override
    public void onTabSelected(int position) {

        lastSelectedPosition = position;
        //开启事务
        mTransaction = mManager.beginTransaction();
        hideFragment(mTransaction);

        /**
         * fragment 用 add + show + hide 方式
         * 只有第一次切换会创建fragment，再次切换不创建
         *
         * fragment 用 replace 方式
         * 每次切换都会重新创建
         *
         */
        switch (position) {
            case 0:   // 排行
                if (mBookRankFragment == null) {
                    mBookRankFragment = BookRankFragment.newInstance("1", userId);
                    mTransaction.add(R.id.fl_content,
                            mBookRankFragment);
                } else {
                    mTransaction.show(mBookRankFragment);
                }
                break;
            case 1:    // 分类
                if (mBookSortFragment == null) {
                    mBookSortFragment = BookSortFragment.newInstance("1",userId);
                    mTransaction.add(R.id.fl_content,
                            mBookSortFragment);
                } else {
                    mTransaction.show(mBookSortFragment);
                }
                break;
            case 2:  // 书架
                if (mBookShelfFragment == null) {
                    mBookShelfFragment = BookShelfFragment.newInstance("1", userId);
                    mTransaction.add(R.id.fl_content,
                            mBookShelfFragment);
                } else {
                    mTransaction.show(mBookShelfFragment);
                }
                break;
            case 3:  // 我的
                if (mUserSettingsFragment == null) {
                    mUserSettingsFragment = UserSettingsFragment.newInstance("1", userId);
                    mTransaction.add(R.id.fl_content,
                            mUserSettingsFragment);
                } else {
                    mTransaction.show(mUserSettingsFragment);
                }
                break;
        }
        // 事务提交
        mTransaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private void hideFragment(FragmentTransaction transaction) {
        if (mBookRankFragment != null) {
            transaction.hide(mBookRankFragment);
        }
        if (mBookSortFragment != null) {
            transaction.hide(mBookSortFragment);
        }
        if (mBookShelfFragment != null) {
            transaction.hide(mBookShelfFragment);
        }
        if (mUserSettingsFragment != null) {
            transaction.hide(mUserSettingsFragment);
        }
    }
    //设置默认打开的碎片
    private void setDefaultFragment() {
        mBookRankFragment = BookRankFragment.newInstance("1",userId);
        mManager = getSupportFragmentManager();
        mTransaction = mManager.beginTransaction();
        mTransaction.add(R.id.fl_content, mBookRankFragment);
        mTransaction.commit();
    }
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-firstBackTime>2000){
            Toast.makeText(this, "再次点击返回键退出", Toast.LENGTH_SHORT).show();
            firstBackTime=System.currentTimeMillis();
        }else{
            super.onBackPressed();
            //停止进程
            //其中System.exit(0)代表进程正常退出  System.exit(1)代表程序异常退出
            System.exit(0);
        }
    }
}
