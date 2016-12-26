package com.example.mymediodemo;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.Adapter.MusicAdapter;
import com.example.Event.ProgressEvent;
import com.example.model.MusicModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView music_Player;
    private ImageView next_Player;
    private ImageView disk;
    private ListView mListView;
    private TextView textName;
    private TextView textArtlist;
    private SeekBar mSeekBar;

    private MusicAdapter adapter;
    private int playerStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music_Player = (ImageView)findViewById(R.id.player);
        next_Player = (ImageView) findViewById(R.id.next_music);
        disk = (ImageView) findViewById(R.id.disk);
        mListView = (ListView) findViewById(R.id.list);
        textName = (TextView) findViewById(R.id.music_name);
        textArtlist = (TextView) findViewById(R.id.music_artist);
        mSeekBar = (SeekBar) findViewById(R.id.player_seekbar);
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
        music_Player.setOnClickListener(onClickListener);
        next_Player.setOnClickListener(onClickListener);
        disk.setOnClickListener(onClickListener);
        mListView.setOnItemClickListener(onItemClickListener);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /*
     *更新播放进度及更新播放器状态
     */
    public void onEventMainThread(ProgressEvent event){
        mSeekBar.setProgress(event.progress);
        if(event.progress >= 99){
            playerStatus = 0;
            music_Player.setImageResource(android.R.drawable.ic_media_play);
            Intent intent = new Intent(MainActivity.this,PlayerService.class);
            intent.putExtra("MSG",AppConstant.PlayerMsg.PAUSE_MSG);
            startService(intent);
        }
    }

    private void initData(){
        new AsyncTask<String,String,List<MusicModel>>(){
            @Override
            protected List<MusicModel> doInBackground(String... params) {
               List<MusicModel> musicModels = new ArrayList<MusicModel>();
                String[] projection = new String[]{MediaStore.Audio.Media.ARTIST_ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ALBUM};
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
                while (cursor.moveToNext()){
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                    artist = artist.replace("<unknown>","未知");
                    MusicModel model = new MusicModel();
                    model.album = album;
                    model.musicName = name;
                    model.musicArtist = artist;
                    model.path = path;
                    musicModels.add(model);
                }
                cursor.close();
                return musicModels;
            }

            @Override
            protected void onPostExecute(List<MusicModel> musicModels) {
                adapter = new MusicAdapter(MainActivity.this,musicModels);
                mListView.setAdapter(adapter);
                super.onPostExecute(musicModels);
            }
        }.execute();

    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final MusicModel model = (MusicModel) adapter.getItem(position);
            adapter.checkedIndex = position;
            ImageLoader.getInstance().displayImage(model.imgUrl,disk, MyApplication.options);
            textName.setText(model.musicName);
            textArtlist.setText(model.musicArtist);
            playerStatus = 1;
            music_Player.setImageResource(android.R.drawable.ic_media_pause);
            Intent intent = new Intent(MainActivity.this,PlayerService.class);
            intent.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
            intent.putExtra("URL",model.path);
            startService(intent);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.player:
                    ImageView img = (ImageView) v;
                    if(playerStatus == 0){//播放
                        playerStatus = 1;
                        img.setImageResource(android.R.drawable.ic_media_pause);
                        intent = new Intent(MainActivity.this,PlayerService.class);
                        intent.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
                        startService(intent);
                    }else if(playerStatus == 1){//暂停
                        playerStatus = 0;
                        img.setImageResource(android.R.drawable.ic_media_play);
                        intent = new Intent(MainActivity.this,PlayerService.class);
                        intent.putExtra("MSG",AppConstant.PlayerMsg.PAUSE_MSG);
                        startService(intent);
                    }
                    break;
                case R.id.next_music:
                    adapter.checkedIndex++;
                    MusicModel model = (MusicModel) adapter.getItem(adapter.checkedIndex);
                    intent = new Intent(MainActivity.this,PlayerService.class);
                    intent.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
                    intent.putExtra("URL",model.path);
                    startService(intent);
                    ImageLoader.getInstance().displayImage(model.imgUrl,disk, MyApplication.options);
                    textName.setText(model.musicName);
                    textArtlist.setText(model.musicArtist);
                    playerStatus = 1;
                    music_Player.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case R.id.disk:
                    if(adapter.checkedIndex > 0){
                        MusicModel musicModel = (MusicModel) adapter.getItem(adapter.checkedIndex);
                        Log.v("tag",musicModel.musicArtist+"__"+musicModel.album);
                        intent = new Intent(MainActivity.this,LastSearchActivity.class);
                        intent.putExtra("ARTIST",musicModel.musicArtist);
                        intent.putExtra("ALBUM",musicModel.album);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Intent intent = new Intent(MainActivity.this,PlayerService.class);
            intent.putExtra("MSG",AppConstant.PlayerMsg.CHANG_MSG);
            intent.putExtra("PROGRESS",seekBar.getProgress());
            startService(intent);
        }
    };
}
