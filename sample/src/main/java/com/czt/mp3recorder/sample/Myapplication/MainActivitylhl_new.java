package com.czt.mp3recorder.sample.Myapplication;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.czt.mp3recorder.sample.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivitylhl_new extends AppCompatActivity implements IUdeleteLisedata, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener   {

    ImageView sound_recording;
    ImageView sound_mic;

    private MP3Recorder mMRecorder;

    private File mFile;
    public static String filePath;
    MediaPlayer  Mp3mediaPlayer;
    AnimationDrawable animationDrawable;
    ArrayList<mLv_recordABean> mLv_recordList = new ArrayList<>();
    private mLv_recordAdapter mLv_recordAdapter;
    private ListView mLv_record;
    int ID = 0;
    int mposition = 0;
    private long mMStart_time;
    private long mStart_timeonPrepared;
    private long mStartplayUrl_time;

    // String testUrl = "http://test.10jrw.com/20161123175819.mp3" ;
    //  String testUrl = "127.0.0.1:8080/MyRecordingsTest12_95.mp3" ;
 	String testUrl ="http://abv.cn/music/光辉岁月.mp3";


   String test_Location_path ="/storage/emulated/0/xpg.mp3";//     /storage/emulated/0/xpg.mp3
    private Button mPlay_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        initView();
        initdata();
        bindListener();


    }

    private void bindListener() {
        sound_mic.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                            initfilePath();
                            //开始录制//
                            handleStartRecord();
                        } else if (MotionEvent.ACTION_MOVE == motionEvent.getAction()) {
                            //                  不做处理(后续可以做上滑取消)
                        } else if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                            long end_time = System.currentTimeMillis();
                            long spend_time = (end_time - mMStart_time) / 1000;
                            System.out.println("这是一个新世界:手指抬起的时间" + end_time + "ms" + ", spend_time==" + spend_time);
                            mLv_recordABean mrecordABean = new mLv_recordABean();
                            mrecordABean.id = ID;
                            mrecordABean.filePath = filePath;
                            mrecordABean.length = spend_time + "\"";
                            mLv_recordList.add(mrecordABean);
                            update();
                            handleStopRecord();
                            ID++;
                        }
                        return true;
                    }
                }
        );
    }
    private void initView() {
        sound_mic = (ImageView) findViewById(R.id.sound_mic);
        sound_recording = (ImageView) findViewById(R.id.sound_recording);
        mLv_record = (ListView) findViewById(R.id.lv_record);
        mPlay_test = (Button) findViewById(R.id.play_test);
        mPlay_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //  playUrl(filePath); //播放本地录音
                        playUrl(testUrl );//播放网络音乐
                    }
                }).start();
            }
        });
    }

    private void update() {
        //待定;
        if (mLv_recordAdapter == null) {
            mLv_recordAdapter = new mLv_recordAdapter(this, mLv_recordList);

        }

        //将适配器设置进listview里面去
        mLv_record.setAdapter(mLv_recordAdapter);

        mLv_recordAdapter.notifyDataSetChanged();
    }

    private void initdata() {
        animationDrawable = (AnimationDrawable) sound_recording.getDrawable();
        mLv_recordAdapter = new mLv_recordAdapter(this, mLv_recordList);
        mLv_recordAdapter.setIUdeleteLisedata(this);
        //mMRecorder = new MP3Recorder();
        try {
            Mp3mediaPlayer = new MediaPlayer();
            Mp3mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Mp3mediaPlayer.setOnCompletionListener(this);
            Mp3mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLv_record.setOnScrollListener(new AbsListView.OnScrollListener() {
            //            public static int SCROLL_STATE_IDLE = 0;      // 空闲
            //            public static int SCROLL_STATE_TOUCH_SCROLL = 1; // 触摸滚动
            //            public static int SCROLL_STATE_FLING = 2;     // 惯性滑动

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                System.out.println("scrollState: " + scrollState);
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    mLv_recordAdapter.closeAllItems();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

    }



    private final class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话来了
                    if (Mp3mediaPlayer.isPlaying()) {
                        int currentPosition = Mp3mediaPlayer.getCurrentPosition();// 获得当前播放位置
                        Mp3mediaPlayer.stop();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE: //通话结束

                    break;
            }
        }
    }


    private void initfilePath() {
        if (checkSdCardExist()) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRecordingsTest12_9" + ID + ".mp3";
        } else {
            /*filePath = "/data/data/" + getPackageName()+"/MyRecordings"+ID+".amr";*/
            filePath = "/data/data/" + getPackageName() + "/MyRecordingsTest12_9" + ID + ".mp3";
        }
        System.out.println("这是一个新世界filePath==" + filePath);  //   这是一个新世界filePath==/storage/emulated/0/MyRecordingsTest0.mp3
    }


    private boolean checkSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    private void handleStartRecord() {
        sound_mic.setImageResource(R.drawable.sound_mic2);
        sound_recording.setVisibility(View.VISIBLE);
        animationDrawable.start();
        //开启倒计时
        countDown();
        try {
            mFile = new File(filePath);
            mMRecorder = new MP3Recorder(mFile);
          //  mMRecorder.SetMP3Recorder(mFile);
            mMRecorder.start();
            mMStart_time = System.currentTimeMillis();
            System.out.println("这是一个新世界:真正录制的时间点:" + ", mStart_time " + mMStart_time + "s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStopRecord() {
        sound_mic.setImageResource(R.drawable.sound_mic1);
        sound_recording.setVisibility(View.GONE);
        animationDrawable.stop();
        if (mMRecorder == null) {
            return;
        }
        try {
            long end_time = System.currentTimeMillis();
            long spend_time_recoder =( end_time - mMStart_time )/1000;
            System.out.println("这是一个新世界:handleStopRecord()真正结束的录制时间:" + ", end_time= " + end_time + ",  spend_time_recoder=="+spend_time_recoder+"s");
            mMRecorder.stop();
           mMRecorder = null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void countDown() {

    }

    //删除音频
    private void deleteFile() {
        fileExists(true);
    }

    //删除音频
    private void deleteFile(int position) {


        fileExists(true, position);
    }

    //判断是否文件存在
    private boolean fileExists(boolean needDelete) {
        File file = new File(filePath);
        if (file.exists()) {
            if (needDelete) {
                file.delete();
            }
            return true;
        }
        return false;
    }

    private boolean fileExists(boolean needDelete, int position) {
        System.out.println("这是一个新世界position==" + position);
        File file = new File(mLv_recordList.get(position).filePath);

        if (file.exists()) {
            if (needDelete) {
                file.delete();
            }
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Mp3mediaPlayer != null) {
            Mp3mediaPlayer.release();
        }
        mMRecorder.stop();
        for (int i = 0; i < mLv_recordList.size(); i++) {
            if (mLv_recordList.get(i).anim != null) {
                mLv_recordList.get(i).anim = null;
            }
        }
    }



    //停止播放
    public void stopSound(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteLisedata(List<mLv_recordABean> list, int position) {
        //先暂定播放
        stopSound(Mp3mediaPlayer);
        deleteFile(position);
        mLv_recordList.remove(position);
        update();

    }

    @Override
    public void showSound( final List<mLv_recordABean> list, final int position, AnimationDrawable anim4) {
        if (fileExists(false, position)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                  playUrl(list.get(position).filePath);
                   // playUrl(testUrl);
                }
            }).start();

        } else {
            Toast.makeText(MainActivitylhl_new.this, "请先录制声音", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void play_finish(List<mLv_recordABean> list, int position) {
        mposition = position;
    }

    public void playUrl(String url) {
        try {
            Mp3mediaPlayer.reset();
            Mp3mediaPlayer.setDataSource(url);
            Mp3mediaPlayer.prepare();
            mStartplayUrl_time = System.currentTimeMillis();
            System.out.println("这是一个新世界:startplayUrl_time==;"+ mStartplayUrl_time);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放结束后停止动画并初始化图片的位置
        long stop_timeonPrepared = System.currentTimeMillis();
        long mStartplayUrl_Spendtime = (stop_timeonPrepared - mStartplayUrl_time)/1000;
        long timeonPrepared_Spendtime = (stop_timeonPrepared - mStart_timeonPrepared)/1000;
        System.out.println("这是一个新世界:stop_timeonPrepared);"+ stop_timeonPrepared+",mStartplayUrl_Spendtime==="+mStartplayUrl_Spendtime+"s,   timeonPrepared_Spendtime=="+timeonPrepared_Spendtime+"s");
 /*       mStartplayUrl_time
                mStart_timeonPrepared*/

        if (mLv_recordList != null && mposition < mLv_recordList.size() && mLv_recordList.get(mposition) != null &&  mLv_recordList.get(mposition).anim.isRunning()) {
            //第一帧
            System.out.println("这是一个新世界setOnCompletionListener: "+"position==" +mposition+", mlist.size()=="+mLv_recordList.size());
            mLv_recordList.get(mposition).anim.selectDrawable(0);
            mLv_recordList.get(mposition).anim.stop();
        }
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mStart_timeonPrepared = System.currentTimeMillis();
        System.out.println("这是一个新世界:public void onPrepared(MediaPlayer mp);"+ mStart_timeonPrepared);

        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

}
