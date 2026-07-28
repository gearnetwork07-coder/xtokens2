package com.app.rewardapp.util.progresshub;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Helper {

    private static float scale;

    public static int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    public static boolean isHostsFileModified() {
        File hosts = new File("/system/etc/hosts");
        if (!hosts.canRead()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(hosts))) {
            String line;
            while ((line = br.readLine()) != null) {
                // look for common ad domains
                if (line.contains("pagead2.googlesyndication.com")
                        || line.contains("adservice.google.com")
                        || line.contains("ads.pubmatic.com")) {
                    return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean testAdNetworkReachability() {
        final boolean[] blocked = {false};
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .dns(Dns.SYSTEM)   // force system DNS
                        .build();
                Request req = new Request.Builder()
                        .url("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js")
                        .head()
                        .build();
                Response res = client.newCall(req).execute();
                blocked[0] = !(res.isSuccessful() && res.code() == 200);
            } catch (IOException e) {
                blocked[0] = true;
            }
        });
        return blocked[0];
    }

    public static boolean runWebViewAdTest(Context context){
        final boolean[] a = new boolean[1];
        WebView wv = new WebView(context);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
               a[0] =true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("adsbygoogle.js")) {

                }
            }
        });
        wv.loadUrl("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js");
        return a[0];
    }

    public static boolean ab(Activity a){
        boolean globalAdAllowed = false;

        if (isHostsFileModified()) {
            globalAdAllowed=true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            globalAdAllowed=testAdNetworkReachability();
        }

//        if (runWebViewAdTest(a)) {
//            globalAdAllowed=true;
//        }

        return globalAdAllowed;
    }

}
