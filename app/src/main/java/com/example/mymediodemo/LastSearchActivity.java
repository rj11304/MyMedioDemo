package com.example.mymediodemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.Adapter.AlbumAdapter;
import com.example.Adapter.ArtistAdapter;

/**
 * Created by admin on 2016/12/26.
 */
public class LastSearchActivity extends AppCompatActivity {

    private ViewPager pager;
    private RadioButton artistButton;
    private RadioButton albumButton;

    private String artistName;
    private String albumName;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_search_activity);
        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        artistName = intent.getStringExtra("ARTIST");//艺术家
        albumName = intent.getStringExtra("ALBUM");//专辑
        pager = (ViewPager) findViewById(R.id.pager);
        artistButton = (RadioButton) findViewById(R.id.artist_tab);
        albumButton = (RadioButton) findViewById(R.id.album_tab);
        artistButton.setTextColor(Color.parseColor("#054faf"));
        artistButton.setOnClickListener(listener);
        albumButton.setOnClickListener(listener);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(onPageChangeListener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.album_tab:
                    pager.setCurrentItem(1);
                    break;
                case R.id.artist_tab:
                    pager.setCurrentItem(0);
                    break;
            }
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            switch (position){
                case 0:
                    artistButton.setTextColor(Color.parseColor("#054faf"));
                    albumButton.setTextColor(Color.BLACK);
                    artistButton.setChecked(true);
                    break;
                case 1:
                    artistButton.setTextColor(Color.BLACK);
                    albumButton.setTextColor(Color.parseColor("#054faf"));
                    albumButton.setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageSelected(int position) {}

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView view = new ListView(LastSearchActivity.this);
            switch(position){
                case 0:
                    view.setAdapter(new ArtistAdapter(LastSearchActivity.this,artistName));
                    break;
                case 1:
                    view.setAdapter(new AlbumAdapter(LastSearchActivity.this,albumName));
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };
}
