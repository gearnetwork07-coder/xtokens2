package com.app.rewardapp.util;

import android.os.CountDownTimer;

public class MyCountDownTimer {
    public interface Listener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    private final long interval;
    private long timeRemaining;
    private CountDownTimer internalTimer;
    private final Listener listener;
    private boolean isPaused = true;
    private boolean isRunning = false;

    public MyCountDownTimer(long millisInFuture, long interval, Listener listener) {
        this.timeRemaining = millisInFuture;
        this.interval = interval;
        this.listener = listener;
    }

    public void start() {
        if (isRunning) return;
        isPaused = false;
        isRunning = true;
        internalTimer = createTimer(timeRemaining);
        internalTimer.start();
    }

    public void pause() {
        if (!isRunning || isPaused) return;
        isPaused = true;
        internalTimer.cancel();
    }

    public void resume() {
        if (!isRunning || !isPaused) return;
        isPaused = false;
        internalTimer = createTimer(timeRemaining);
        internalTimer.start();
    }

    public void cancel() {
        if (internalTimer != null) {
            internalTimer.cancel();
        }
        isRunning = false;
        isPaused = true;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private CountDownTimer createTimer(long millis) {
        return new CountDownTimer(millis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                listener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                listener.onFinish();
            }
        };
    }
}
