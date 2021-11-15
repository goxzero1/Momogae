package com.example.momogae.Board;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class BoardActivity extends AppCompatActivity {

    String userID;
    StorageReference mStorage;

    private BoardFragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SearchView mSearchView;

    FloatingActionButton fabNewPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = getUid(); //파이어베이스 사용자 고유번호
        mStorage = FirebaseStorage.getInstance().getReference();

        mPagerAdapter = new BoardFragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setFragment(Arrays.asList(new QuestionPostsFragment(),
                                                new FreePostsFragment(),
                                                new SharePostsFragment()),

                Arrays.asList(getString(BoardType.QUESTION.getTitleRes()),
                              getString(BoardType.FREE.getTitleRes()),
                              getString(BoardType.SHARE.getTitleRes())));

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerAdapter.setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPagerAdapter.setCurrentPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 패브버튼 이용해서 새로운 글 작성하기
        fabNewPost = (FloatingActionButton) findViewById(R.id.fab);
        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BoardActivity.this, NewPostActivity.class));
            }
        });

        mSearchView = findViewById(R.id.sv_content);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPagerAdapter.search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mPagerAdapter.search("");
                return false;
            }
        });
    }

    public String getUid() {
        return SharedPreference.getAttribute(getApplicationContext(), "userID");
    }


}

