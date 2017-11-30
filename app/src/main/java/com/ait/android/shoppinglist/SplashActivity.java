package com.ait.android.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ellamary on 11/8/17.
 */

public class SplashActivity extends AppCompatActivity {

    private Timer timer;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);


        final ImageView ivSplash = findViewById(R.id.ivSplash);
        final Animation anim = AnimationUtils.loadAnimation(
                SplashActivity.this, R.anim.show_anim);



        ivSplash.startAnimation(anim);

        timer = new Timer();
        timer.schedule(new MyTimerTask(), 3000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
