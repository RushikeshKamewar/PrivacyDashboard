package rk.android.app.privacydashboard.activities.splash;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Bundle bundle = ActivityOptions.makeCustomAnimation(SplashActivity.this,
                    android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent,bundle);
            finish();

        },500);

    }
}
