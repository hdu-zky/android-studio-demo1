package com.example.activity.indexTabFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activity.R;
import com.example.activity.bean.BookIntro;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BookRankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookRankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View v;
    private String[] tabTitle = {"总点击", "总收藏", "总下载"};
    private List<RankTabFragment> BookIntroFragmentList = new ArrayList<>();
    private List<BookIntro> bookIntroList = new ArrayList<>();

    public BookRankFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static BookRankFragment newInstance(String param1, String param2) {
        BookRankFragment fragment = new BookRankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /**
         * 这里不能直接 return inflater.inflate(R.layout.fragment_book_rank, container, false);
         * 因为findViewById(R.id.tab_layout);需要使用当前view
         * */
        v = inflater.inflate(R.layout.fragment_book_rank, container, false);
        TabLayout  mTabLayout =  v.findViewById(R.id.tab_layout);
        ViewPager viewPager = v.findViewById(R.id.rank_view_pager);

        //添加tab
        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
            BookIntroFragmentList.add(RankTabFragment.newInstance(tabTitle[i], i+1));
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return BookIntroFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return BookIntroFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitle[position];
            }
        });
        // 预加载界面个数，当前显示一个,预先加载两个
//        viewPager.setOffscreenPageLimit(2);
        //设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(viewPager,false);
        return v;
    }

}
