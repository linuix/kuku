package com.neointernet.neo360.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.neointernet.neo360.R;
import com.neointernet.neo360.listener.CardboardEventListener;
import com.neointernet.neo360.listener.VideoTimeListener;
import com.neointernet.neo360.renderer.VideoRenderer;
import com.neointernet.neo360.view.MyCardboardView;

/**
 * Created by neo-202 on 2016-03-22.
 */
public class VideoActivity extends CardboardActivity implements VideoTimeListener, CardboardEventListener {

    private MyCardboardView view;
    private VideoRenderer renderer;
    private View barLayout;
    private ImageButton vrButton, playButton;
    private SeekBar videoSeekBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new MyCardboardView(VideoActivity.this);
        view.setSettingsButtonEnabled(false);
        view.setVRModeEnabled(false);
        //vr中的框框
        view.setDistortionCorrectionEnabled(false);
        view.setAlignmentMarkerEnabled(true);
        setContentView(view);
        setCardboardView(view);



        Intent intent = getIntent();
        String videoPath = "";
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)){
            videoPath = intent.getData().getPath();
            renderer = new VideoRenderer(VideoActivity.this, videoPath);
        }else {
            renderer = new VideoRenderer(VideoActivity.this, intent.getStringExtra("videopath"));
        }


        view.setRenderer(renderer);
        view.setSurfaceRenderer(renderer);
        renderer.setVideoTimeListener(this);

        view.addCardboardEventListener(renderer);
        view.addCardboardEventListener(this);
//        view.setRotationX(0);
//        view.setRotationY(0);
//        view.setRotation(30);
        LayoutInflater layoutInflater = getLayoutInflater();
        barLayout = (View) layoutInflater.inflate(R.layout.video_controller, null);
        addContentView(barLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        vrButton = (ImageButton) findViewById(R.id.vrButton);
        playButton = (ImageButton) findViewById(R.id.playButton);
        videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);

        vrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.changeVRMode();
            }
        });



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (renderer.isPlaying()) {
                    playButton.setImageResource(R.drawable.stop);
                    renderer.onPause();
                } else {
                    playButton.setImageResource(R.drawable.play);
                    renderer.onResume();
                }
            }
        });

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    renderer.setMediaPlayerSeekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onCardboardTrigger() {
//        super.onCardboardTrigger();
    }

    /**
     * 准备播放前的回调
     * @param length
     */
    @Override
    public void onVideoInit(int length) {
        videoSeekBar.setMax(length);

    }

    @Override
    public void listenTime(int time) {
        videoSeekBar.setProgress(time);
    }

    @Override
    public void onCardboardTouch(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int visibility = barLayout.getVisibility();
            if (visibility == View.INVISIBLE) {
                barLayout.setVisibility(View.VISIBLE);
            } else if (visibility == View.VISIBLE) {
                barLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

}