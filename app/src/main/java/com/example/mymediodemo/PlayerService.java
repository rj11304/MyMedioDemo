package com.example.mymediodemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.Event.ProgressEvent;
import com.ypy.eventbus.EventBus;

/**
 * Created by admin on 2016/12/26.
 */
public class PlayerService extends Service {
    private MediaPlayer mediaPlayer =  new MediaPlayer();       //媒体播放器对象
    private String path;                        //音乐文件路径
    private boolean isPause;                    //暂停状态
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("URL");
        if(url == null && path == null){
            return super.onStartCommand(intent, flags, startId);
        }
        if(url != null && !url.isEmpty()){
            path = url;
        }
        int msg = intent.getIntExtra("MSG", 0);
        if(msg == AppConstant.PlayerMsg.PLAY_MSG) {
            play(0);
        } else if(msg == AppConstant.PlayerMsg.PAUSE_MSG) {
            pause();
        } else if(msg == AppConstant.PlayerMsg.STOP_MSG) {
            stop();
        } else if (msg == AppConstant.PlayerMsg.CHANG_MSG) {
            int progress = intent.getIntExtra("PROGRESS", 0);
            seekMedia(progress);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 播放音乐
     * @param position
     */
    private void play(int position) {
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *设置播放进度
     *@param position
     */
    public void seekMedia(int position){
        mediaPlayer.seekTo((int)(position*1.0f/100*mediaPlayer.getDuration()));
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 停止音乐
     */
    private void stop(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus.getDefault().register(this);
        mHandler.post(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null){
                int position =  mediaPlayer.getCurrentPosition();
                int max = mediaPlayer.getDuration();
                ProgressEvent event = new ProgressEvent();
                event.progress = (int)(position*1.0f/max*100);
                EventBus.getDefault().post(event);
                mHandler.postDelayed(this,500);
            }
        }
    };

    @Override
    public void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        //EventBus.getDefault().unregister(this);
    }
    /**
     *
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     *
     */
    private final class PreparedListener implements OnPreparedListener {
        private int positon;

        public PreparedListener(int positon) {
            this.positon = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if(positon > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(positon);
            }
        }
    }

}
