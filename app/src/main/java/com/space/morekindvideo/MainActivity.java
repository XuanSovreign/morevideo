package com.space.morekindvideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private VideoView mTextureBig;
    private VideoView mTextureOne;
    private VideoView mTextureTwo;
    private VideoView mTextureThree;
    private VideoView mTextuteFour;
    private ListView mLvCard;
    private PhotoListAdapter mAdapter;
    private List<UserBean> mUserBeans;
    private File[] mVideos;
    private List<VideoView> mVideoViews;
    private int videoIndex = 0;
    private int[] flags = {1, 1, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mTextureBig = findViewById(R.id.texture_big);
        mTextureOne = findViewById(R.id.texture_one);
        mTextureTwo = findViewById(R.id.texture_two);
        mTextureThree = findViewById(R.id.texture_three);
        mTextuteFour = findViewById(R.id.texture_four);
        mLvCard = findViewById(R.id.lv_card);
        initData();
        initListener();
    }

    private void initData() {
        mUserBeans = new ArrayList<>();
        mAdapter = new PhotoListAdapter(this, mUserBeans);
        mLvCard.setAdapter(mAdapter);
        mVideoViews = new ArrayList<>();
        mVideoViews.add(mTextureOne);
        mVideoViews.add(mTextureTwo);
        mVideoViews.add(mTextureThree);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int permission = -1;
            for (int i = 0; i < permissions.length; i++) {
                permission = ActivityCompat.checkSelfPermission(this, permissions[i]);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 0x0010);
            } else {
                checkFile();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean check = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                check = false;
                break;
            }
        }
        if (check) {
            checkFile();
        } else {
            Toast.makeText(this, "请申请相应权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFile() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File directory = Environment.getExternalStorageDirectory();
        File videoDic = new File(directory, "morevideo/video");
        File pictureDic = new File(directory, "morevideo/picture");
        if (!videoDic.exists() || !pictureDic.exists()) {
            return;
        }
        int length = videoDic.list().length;
        if (length <= 0 || pictureDic.list().length <= 0) {
            return;
        }
        mVideos = videoDic.listFiles();
        File[] pictures = pictureDic.listFiles();
        for (int i = 0; i < pictures.length; i++) {
            UserBean userBean = new UserBean();
            userBean.fileName = pictures[i];
            mUserBeans.add(userBean);
        }
        mAdapter.notifyDataSetChanged();
        playVideo();
    }

    private void playVideo() {
        mTextureBig.setVideoPath(mVideos[0].getAbsolutePath());
        Log.e("====", "playVideo: " + mVideos[0].getAbsolutePath());
        mTextureBig.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }

    private void initListener() {
        mLvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUserBeans.get(position).isChoice = !mUserBeans.get(position).isChoice;
                int count = 0;
                for (int i = 0; i < mUserBeans.size(); i++) {
                    if (mUserBeans.get(i).isChoice) {
                        count++;
                        Log.e("ddddddddd", "onItemClick: " + count);
                    }
                }

                Log.e("=========", "onItemClick: " + count);
                if (count > 3) {
                    Toast.makeText(MainActivity.this, "超过数量了", Toast.LENGTH_SHORT).show();
                    mUserBeans.get(position).isChoice = !mUserBeans.get(position).isChoice;
                    return;
                }
                int currentPosition = 0;
                if (mTextureBig.isPlaying()) {
                    mTextureBig.getCurrentPosition();
                } else {
                    mTextureBig.getCurrentPosition();
                }

                if (mUserBeans.get(position).isChoice) {
                    for (int i = 0; i < flags.length; i++) {
                        if (flags[i] == 1) {
                            flags[i] = 0;
                            videoIndex = i;
                            break;
                        }
                    }
                    mUserBeans.get(position).index = videoIndex;
                    mVideoViews.get(videoIndex).setVisibility(View.VISIBLE);
                    mVideoViews.get(videoIndex).setVideoPath(mVideos[position + 1].getAbsolutePath());
                    mVideoViews.get(videoIndex).seekTo(currentPosition);
                    mVideoViews.get(videoIndex).setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    });

                } else {
                    videoIndex = mUserBeans.get(position).index;
                    mVideoViews.get(videoIndex).setVisibility(View.GONE);
                    if (mVideoViews.get(videoIndex).isPlaying()) {
                        mVideoViews.get(videoIndex).stopPlayback();
                    }
                    mUserBeans.get(position).index = -1;
                    flags[videoIndex] = 1;
                }
                mAdapter.notifyDataSetChanged();

                Log.e("video", "onItemClick: " + videoIndex);

                if (count >= 3) {
                    mTextuteFour.setVideoPath(mVideos[0].getAbsolutePath());
                    mTextuteFour.seekTo(currentPosition);
                    mTextuteFour.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    });
                    mTextuteFour.setVisibility(View.VISIBLE);
                    mTextureBig.stopPlayback();
                    mTextureBig.setVisibility(View.GONE);
                } else {
                    if (mTextureBig.isPlaying()) {
                        return;
                    }
                    mTextureBig.setVideoPath(mVideos[0].getAbsolutePath());
                    mTextureBig.seekTo(currentPosition);
                    mTextureBig.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    });
                    mTextureBig.setVisibility(View.VISIBLE);
                    mTextuteFour.stopPlayback();
                    mTextuteFour.setVisibility(View.GONE);
                }
            }
        });


    }
}
