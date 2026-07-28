package com.app.rewardapp.ui.activity;

import static com.app.rewardapp.util.Constant_Api.AUTH;
import static com.app.rewardapp.util.Constant_Api.MAINTENANCE;
import static com.app.rewardapp.util.Constant_Api.UPDATE;
import static com.app.rewardapp.util.Constant_Api.isNpv;
import static com.app.rewardapp.util.Fun.data;
import static com.app.rewardapp.util.Fun.encrypt;
import static com.app.rewardapp.util.Fun.isConnected;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.rewardapp.App;
import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.databinding.LayoutDialogBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.util.AdUnit;
import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.app.rewardapp.util.progresshub.Helper;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    Session session;
    AlertDialog alertDialog, dialog;
    Activity activity;
    LayoutDialogBinding layoutDialogBinding;
    boolean checkAppStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        activity = this;
        checkAppStatus = checkApp(getFilesDir().getPath());

        session = new Session(this);
        alertDialog = Fun.Alert(this);


        if (isConnected(this) && !Helper.ab(activity)) {
            loadConfig();
        } else {
            showAlert(getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void showAlert(String msg) {
        alertDialog.show();
        TextView tv = alertDialog.findViewById(R.id.txt);
        assert tv != null;
        tv.setText(msg);
        Button btn = alertDialog.findViewById(R.id.close);
        Objects.requireNonNull(btn).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void loadConfigSetting() {
        Call<CallbackConfig> call = Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).getConfig();
        call.enqueue(new Callback<CallbackConfig>() {
            @Override
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getSuccess() != 0) {
                    isNpv = response.body().isVpn();
                    App.getPref().getPref()
                            .edit()
                            .putString("ref", response.body().getRef())
                            .putInt("uiStyle", response.body().getData().get(0).getUi_style())
                            .putInt("homeStyle", response.body().getData().get(0).getHome_style())
                            .putString("banner_id", response.body().getData().get(0).getBanner_id())
                            .putString("app_email", response.body().getData().get(0).getApp_email())
                            .putString("app_desc", response.body().getData().get(0).getApp_description())
                            .putString("app_author", response.body().getData().get(0).getAppAuthor())
                            .putInt("banner_type", response.body().getData().get(0).getBanner())
                            .putString("inter_id", response.body().getData().get(0).getInter_id())
                            .putInt("inter_type", response.body().getData().get(0).getInter())
                            .putInt("inter_count", response.body().getData().get(0).getInterstitalCount())
                            .putString("native_id", response.body().getData().get(0).getNative_id())
                            .putInt("native_type", response.body().getData().get(0).getNativeType())
                            .putInt("native_count", response.body().getData().get(0).getNativeCount())
                            .putInt("ad_not_load_credit", response.body().getData().get(0).getAdNotLoadCredit())
                            .putString("active_ad", response.body().getData().get(0).getActive_ad())
                            .apply();

                    parseData(response.body().getAds());
                    if (response.body().getData().get(0).isUp_status()) {
                        if (response.body().getData().get(0).getUp_mode().equals(UPDATE)) {
                            if (BuildConfig.VERSION_CODE < response.body().getData().get(0).getUp_version()) {
                                show(response.body().getData().get(0).getUp_msg(), "update", response.body().getData().get(0).getUp_link(), response.body().getData().get(0).isUp_btn());
                            } else {
                                doTask();
                            }
                        } else {
                            show(response.body().getData().get(0).getUp_msg(), MAINTENANCE, "", false);
                        }
                    } else {
                        doTask();
                    }
                } else {
                    showAlert(Objects.requireNonNull(response.body()).getMessage());
                }
            }

            @Override
            public void onFailure(Call<CallbackConfig> call, Throwable t) {
                System.out.println("config__fallack___ " + t.getLocalizedMessage());
            }
        });
    }

    private void parseData(List<CallbackConfig.Ads> data) {
        try {
            AdUnit.get().saveFromServer(data);
        } catch (Exception e) {
            Toast.makeText(activity, "Parse Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void doTask() {
        if (session.getBoolean(session.LOGIN)) {
            reqLogin(session.getData(session.EMAIL), session.getData(session.PASSWORD), session.Auth());
        } else {
            prepareLang();
            startActivity(new Intent(Splash.this, IntroActivity.class));
        }
    }

    private void loadConfig() {
        loadConfigSetting();
    }

    private void prepareLang() {
        Locale myLocale = new Locale(session.getLang());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void reqLogin(String email, String password, String randomNumber) {
        Call<CallbackResp> login = Objects.requireNonNull(ApiClient.getClient(this)).create(ApiInterface.class).ApiUser(data("", email, password, randomNumber, "", "", 1, 0, session.Auth(), 0));
        login.enqueue(new Callback<CallbackResp>() {
            @Override
            public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {
                if (response.isSuccessful() && response.body().getCode() == 201) {
                    session.setData(session.TOKEN, AUTH + encrypt(response.body().getWkdWMmFXTmxYMmxr()).replace(response.body().getUser().getToken(), ""));
                    session.setData(session.WALLET, response.body().getUser().getBalance());
                    prepareLang();
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    prepareLang();
                    Intent intent = new Intent(Splash.this, FrontLogin.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CallbackResp> call, Throwable t) {
                Log.e("splash", "onFailure: " + t.getMessage());
            }
        });
    }

    void show(String msg, String type, String url, boolean skip) {
        layoutDialogBinding = LayoutDialogBinding.inflate(getLayoutInflater());
        dialog = new AlertDialog.Builder(activity).setView(layoutDialogBinding.getRoot()).create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        layoutDialogBinding.no.setText(getString(R.string.skip));

        if (type.equals(MAINTENANCE)) {
            layoutDialogBinding.yes.setVisibility(View.GONE);
            layoutDialogBinding.img.setImageResource(R.drawable.ic_baseline_cloud_off_24);
            layoutDialogBinding.congrts.setText(getString(R.string.maintenance));
            layoutDialogBinding.congrts.setTextColor(getResources().getColor(R.color.red));
            layoutDialogBinding.no.setText(getString(R.string.close));
        } else {
            layoutDialogBinding.img.setImageResource(R.drawable.ic_baseline_autorenew_24);
            layoutDialogBinding.congrts.setText(getString(R.string.update_available));
            layoutDialogBinding.yes.setVisibility(View.VISIBLE);
            layoutDialogBinding.yes.setText(getString(R.string.update));
        }
        if (!skip) {
            layoutDialogBinding.no.setVisibility(View.GONE);
        }
        layoutDialogBinding.no.setOnClickListener(view -> {
            if (type.equals(MAINTENANCE)) {
                dialog.dismiss();
            } else {
                doTask();
            }
        });

        layoutDialogBinding.yes.setOnClickListener(view -> {
            if (type.equals(MAINTENANCE)) {
                dialog.dismiss();
            } else {
                openUrl(url);
            }
        });
    }

    public static boolean checkApp(String path) {
        int count = getDotCount(path);
        return count > 3;
    }

    public static String generateRandomString(String resp, String refid) {
        String rep = refid.trim();
        return resp.replace(rep, "");
    }

    private void openUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception ignored) {
        }
    }

    public static int getDotCount(String path) {
        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (count > 3) {
                break;
            }
            if (path.charAt(i) == '.') {
                count++;
            }
        }
        return count;
    }
}
