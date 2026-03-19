package com.example.reorderpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ القاعدة الذهبية: الـ SplashScreen لازم تكون أول سطر قبل super
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ربط العناصر
        ImageView logo = findViewById(R.id.splash_logo);
        TextView title = findViewById(R.id.app_name_text);
        TextView developer = findViewById(R.id.dev_name_text);

        // أنيميشن احترافي (Fade In)
        logo.setAlpha(0f);
        title.setAlpha(0f);

        logo.animate().alpha(1f).setDuration(1000).start();
        title.animate().alpha(1f).setDuration(1000).setStartDelay(500).start();

        // الانتقال للشاشة الرئيسية بعد 2.5 ثانية
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // إغلاق شاشة السبلاش عشان ما يرجع ليها المستخدم
        }, 2500);
    }
}