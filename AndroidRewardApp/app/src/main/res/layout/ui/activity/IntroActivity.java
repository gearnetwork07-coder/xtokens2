package com.app.rewardapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.rewardapp.Config.Config;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackIntro;
import com.app.rewardapp.adapters.IntroAdapter;
import com.app.rewardapp.util.Session;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RelativeLayout nextLayout, getStartedLayout;
    private IntroAdapter adapter;
    private List<CallbackIntro> callbackIntros;
    private TabLayout tabLayout;
    private Button goNext;
    private Button getStarted, skipButton;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        session = new Session(this);
        session.setBoolean(session.FIRSTTIME,false);
        viewPager = findViewById(R.id.vp4);
        nextLayout = findViewById(R.id.ll7);
        getStartedLayout = findViewById(R.id.ll8);
        callbackIntros = new ArrayList<>();
        callbackIntros.add(new CallbackIntro(Config.SLIDE_ONE_ICON, Config.SLIDE_ONE_HEADLINE,  Config.SLIDE_ONE_DESCRIPTION));
        callbackIntros.add(new CallbackIntro(Config.SLIDE_TWO_ICON, Config.SLIDE_TWO_HEADLINE,  Config.SLIDE_TWO_DESCRIPTION));
        callbackIntros.add(new CallbackIntro(Config.SLIDE_THREE_ICON, Config.SLIDE_THREE_HEADLINE,  Config.SLIDE_THREE_DESCRIPTION));
        adapter = new IntroAdapter(getApplicationContext(), callbackIntros);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(false, new DepthPagerTransformer());

        tabLayout = findViewById(R.id.tabLayout1);
        tabLayout.setupWithViewPager(viewPager);

        goNext = findViewById(R.id.goNext);
        getStarted = findViewById(R.id.getStarted);
        skipButton = findViewById(R.id.skipButton);
        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                if (position < callbackIntros.size()) {
                    position++;
                    nextLayout.setVisibility(View.VISIBLE);
                    skipButton.setVisibility(View.VISIBLE);
                    getStartedLayout.setVisibility(View.GONE);
                    viewPager.setCurrentItem(position);
                } else if (position == callbackIntros.size() - 1) {
                    doVisibilityOperation();
                }
            }
        });

        getStarted.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this,FrontLogin.class)));

        skipButton.setOnClickListener(v -> viewPager.setCurrentItem(callbackIntros.size() - 1));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == callbackIntros.size() - 1) {
                    doVisibilityOperation();
                } else {
                    nextLayout.setVisibility(View.VISIBLE);
                    skipButton.setVisibility(View.VISIBLE);
                    getStartedLayout.setVisibility(View.GONE);
                    getStarted.animate().setInterpolator(new BounceInterpolator()).scaleXBy(1).scaleX(0).scaleYBy(1).scaleY(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void doVisibilityOperation() {
        getStartedLayout.setVisibility(View.VISIBLE);
        nextLayout.setVisibility(View.GONE);
        skipButton.setVisibility(View.INVISIBLE);
        getStarted.animate()
                .setInterpolator(new BounceInterpolator())
                .setDuration(1000)
                .scaleXBy(0)
                .scaleX(1)
                .scaleYBy(0)
                .scaleY(1);
    }

    private class DepthPagerTransformer implements ViewPager.PageTransformer {
        public static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();

            if (position < -1) {
                //left most
                page.setAlpha(0);
            } else if (position <= 0) {
                //from center to first left
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleY(1);
                page.setScaleX(1);

            } else if (position <= 1) {
                //at center to first right
                page.setAlpha(1 - position);
                page.setTranslationX(pageWidth * -position);

                float scaling = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaling);
                page.setScaleY(scaling);
                page.animate().setInterpolator(new AccelerateDecelerateInterpolator());
            } else {
                page.setAlpha(0);
            }

        }
    }
}
