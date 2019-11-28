package com.example.a17_friends;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AudienceActivity extends Activity {

    private static final String TAG = AudienceActivity.class.getSimpleName();
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FrameLayout mFlSS;
    private RtcEngine mRtcEngine;
    private String ChannelKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audience);
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            mFlSS = (FrameLayout) findViewById(R.id.fl_screenshare);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            initEngineAndJoin();

        }
    }

    private void initEngineAndJoin() {
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), new IRtcEngineEventHandler() {

                public void onUserOffline(int uid, int reason) {
                finish();
                }

                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.d(TAG, "onJoinChannelSuccess: " + (uid&0xFFFFFFL));

                }

                @Override
                public void onUserJoined(final int uid, int elapsed) {
                    Log.d(TAG, "onUserJoined: " + (uid&0xFFFFFFL));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupRemoteView(uid);
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.enableVideo();
        mRtcEngine.enableAudio(); // 开启音频功能
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        if (getIntent().hasExtra("key")){

            ChannelKey = getIntent().getStringExtra("key");
        }

        mRtcEngine.joinChannel(null, ChannelKey, "Extra Optional Data", Constant.AUDIENCE_UID);
    }

    private void setupRemoteView(int uid) {
        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);

        if (uid == Constant.SCREEN_SHARE_UID){
            mFlSS.addView(surfaceV, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            Log.e(TAG, "unknown uid");
        }

        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_FIT, uid));
    }
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoin();
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRtcEngine.leaveChannel();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RtcEngine.destroy();
        mRtcEngine = null;
    }

}
