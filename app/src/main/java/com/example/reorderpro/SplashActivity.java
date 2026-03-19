package com.example.reorderpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1400;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent duplicate instance
        if (!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        setupAnimation();

        navigateRunnable = () -> {
            if (!isFinishing() && !isDestroyed()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };

        handler.postDelayed(navigateRunnable, SPLASH_DELAY);
    }

    // ================= ANIMATION =================

    private void setupAnimation() {

        try {
            MaterialCardView logoCard = findViewById(R.id.logoCard);
            View title = findViewById(R.id.title);
            View subtitle = findViewById(R.id.subtitle);

            if (logoCard != null) {
                logoCard.setAlpha(0f);
                logoCard.setScaleX(0.85f);
                logoCard.setScaleY(0.85f);

                logoCard.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(600)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }

            if (title != null) {
                title.setAlpha(0f);
                title.setTranslationY(40f);

                title.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setStartDelay(200)
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }

            if (subtitle != null) {
                subtitle.setAlpha(0f);
                subtitle.setTranslationY(40f);

                subtitle.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setStartDelay(350)
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }

        } catch (Exception ignored) {}
    }

    // ================= CLEANUP =================

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (navigateRunnable != null) {
            handler.removeCallbacks(navigateRunnable);
        }
    }
}