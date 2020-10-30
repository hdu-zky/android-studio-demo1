package com.example.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

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
//    private String[] tabTitle = {"tab1", "tab2", "tab3", "tab4", "tab5", "tab6"};
//    private List<BookIntroFragment> BookIntroFragmentList = new ArrayList<>();
//    private List<BookIntro> bookIntroList = new ArrayList<>();
    private BottomNavigationBar mBottomNavigationBar;
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;

    private BookRankFragment mBookRankFragment; //排行
    private BookSortFragment mBookSortFragment; //分类
    private BookShelfFragment mBookShelfFragment; //书架
    private UserSettingsFragment mUserSettingsFragment;  //个人设置

    private int lastSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        initBottomNavBar();

        //添加监听  实现BottomNavigationBar.OnTabSelectedListener接口
        //public void onTabSelected(int position) 未选中 -> 选中
        //public void onTabUnselected(int position) 选中 -> 未选中
        //public void onTabReselected(int position) 选中 -> 选中
        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();

//        TabLayout mTabLayout = findViewById(R.id.tab_layout);
//        ViewPager viewPager = findViewById(R.id.view_pager);
//
//        //添加tab
//        for (int i = 0; i < tabTitle.length; i++) {
//            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
//            BookIntroFragmentList.add(BookIntroFragment.newInstance(tabTitle[i]));
//        }
//        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//            @NonNull
//            @Override
//            public Fragment getItem(int position) {
//                return BookIntroFragmentList.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return BookIntroFragmentList.size();
//            }
//
//            @Nullable
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return tabTitle[position];
//            }
//        });
//
//        //设置TabLayout和ViewPager联动
//        mTabLayout.setupWithViewPager(viewPager,false);
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
                        .setInactiveIconResource(R.drawable.rank_in_active).setInActiveColorResource(R.color.tabInActive).setBadgeItem(mTextBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.menu, "分类").setActiveColorResource(R.color.tabActive)
                        .setInactiveIconResource(R.drawable.menu_in_active).setInActiveColorResource(R.color.tabInActive).setBadgeItem(mTextBadgeItem))
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
                    mBookRankFragment = BookRankFragment.newInstance("1","2");
                    mTransaction.add(R.id.fl_content,
                            mBookRankFragment);
                } else {
                    mTransaction.show(mBookRankFragment);
                }
                break;
            case 1:    // 分类
                if (mBookSortFragment == null) {
                    mBookSortFragment = BookSortFragment.newInstance("1","2");
                    mTransaction.add(R.id.fl_content,
                            mBookSortFragment);
                } else {
                    mTransaction.show(mBookSortFragment);
                }
                break;
            case 2:  // 书架
                if (mBookShelfFragment == null) {
                    mBookShelfFragment = BookShelfFragment.newInstance("1","2");
                    mTransaction.add(R.id.fl_content,
                            mBookShelfFragment);
                } else {
                    mTransaction.show(mBookShelfFragment);
                }
                break;
            case 3:  // 我的
                if (mUserSettingsFragment == null) {
                    mUserSettingsFragment = UserSettingsFragment.newInstance("1","2");
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

    private void setDefaultFragment() {
        mBookRankFragment = BookRankFragment.newInstance("1","2");
        mManager = getSupportFragmentManager();
        mTransaction = mManager.beginTransaction();
        mTransaction.add(R.id.fl_content, mBookRankFragment);
        mTransaction.commit();
    }
}
