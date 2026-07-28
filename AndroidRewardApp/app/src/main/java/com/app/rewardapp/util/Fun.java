package com.app.rewardapp.util;

import static com.app.rewardapp.util.Const.getAK;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.rewardapp.App;
import com.app.rewardapp.R;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.databinding.LayoutGlobalMsgBinding;
import com.app.rewardapp.restApi.ApiClient;
import com.app.rewardapp.restApi.ApiInterface;
import com.app.rewardapp.ui.fragments.FragmentMain;
import com.app.rewardapp.ui.fragments.Home2Fragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fun {
	private static int getRandom() {
		Random random = new Random();
		return random.nextInt(900);
	}

	public static Fragment getDefaultFragment(){
		Fragment fragment = null;
		int uiStyle= App.getPref().getIntData("uiStyle");
		if(uiStyle==0){
			fragment= new FragmentMain();
		}else if(uiStyle==1){
			fragment= new Home2Fragment();
		}
		return fragment;
	}


	public static boolean isConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();

		return nInfo != null && nInfo.isConnectedOrConnecting();
	}
	public static String getDate(String uid, int id, int type, int cnt){
	return encrypt_code(Objects.requireNonNull(md5(uid + "_" + id + "_" + type + "_" + cnt + getAK)));
	}

	public static String encrypt_code(String coded) {
		byte[] encodeValue = Base64.encode(coded.getBytes(), 0);
		return new String(encodeValue);
	}

	public static AlertDialog GlobalMsg(Context context, LayoutGlobalMsgBinding lyt) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(lyt.getRoot()).create();
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		Window w = dialog.getWindow();
		if (w != null) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}
		return dialog;
	}
	public static String md5(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.getBytes());
			byte[] messageDigest = digest.digest();
			StringBuilder hexString = new StringBuilder();

			for (byte b : messageDigest) {
				hexString.append(String.format("%02x", b));
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException var5) {
			var5.printStackTrace();
			return null;
		}
	}

	public static String encrypt_(String coded) {
		coded = encrypt_code(coded);
		byte[] encodeValue = Base64.encode(coded.getBytes(), 0);
		return new String(encodeValue);
	}
	public static String getDat(){
		Calendar c = Calendar.getInstance();
		@SuppressLint("SimpleDateFormat") SimpleDateFormat dd = new SimpleDateFormat("dd");
		return dd.format(c.getTime());
	}


	public static String encryption(String coded) {
		byte[] valueDecoded = new byte[0];

		valueDecoded = Base64.decode(coded.getBytes(StandardCharsets.UTF_8), 0);

		return new String(valueDecoded);
	}


	public static String encrypt(String coded) {
		coded = encryption(coded);
		byte[] valueDecoded = new byte[0];

		valueDecoded = Base64.decode(coded.getBytes(StandardCharsets.UTF_8), 0);

		return new String(valueDecoded);
	}


	public static void statusBar(Activity activity) {
		activity.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
	}


	public static String data(String s1, String s2, String s3, String s4, String s5, String s6, int i, int i1, String i2,int cnt){
		String s= String.valueOf(getRandom());
		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new ApiClient());
		switch (i){
			case 0:
				jsObj.addProperty("name",s1);
				jsObj.addProperty("email",s2);
				jsObj.addProperty("password",s3);
				jsObj.addProperty("phone",s4);
				jsObj.addProperty("token",s5);
				jsObj.addProperty("refferal_id",s6);
				break;

			case 1:
				jsObj.addProperty("email",s2);
				jsObj.addProperty("password",s3);
				jsObj.addProperty("randomString",s4);
				break;

			case 3:
				jsObj.addProperty("name",s1);
				jsObj.addProperty("email",s2);
				jsObj.addProperty("google",s3);
				jsObj.addProperty("profile",s4);
				jsObj.addProperty("token",s5);
				jsObj.addProperty("refferal_id",s6);
				break;

		}

		jsObj.addProperty("id",i1);
		jsObj.addProperty("ex_id",i2);
		jsObj.addProperty("ex",s1);
		jsObj.addProperty("type",i);
		jsObj.addProperty("i4",s);
		jsObj.addProperty("x_",cnt);
		jsObj.addProperty("ts", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
		jsObj.addProperty("dt_x_X",getDate(i2,i1,i,cnt));
		return encrypt_(jsObj.toString());
	}

	public static String data1(String oldpaass, String newpass){
		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new ApiClient());
		jsObj.addProperty("oldp",oldpaass);
		jsObj.addProperty("newp",newpass);
		return encrypt_(jsObj.toString());
	}

	public static String data2(String email, String number){
		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new Gson());
		jsObj.addProperty("email",email);
		jsObj.addProperty("number",number);
		return encrypt_(jsObj.toString());
	}


	public static void hideKeyboard(View view, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static String deviceId(Context ctx) {
		@SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
		return Objects.requireNonNull(md5(android_id)).toUpperCase();
	}

	public static String getFormatedDate(String date_str) {
		if (date_str != null && !date_str.trim().equals("")) {
			@SuppressLint("SimpleDateFormat") SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			@SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm");

			try {
				return newFormat.format(Objects.requireNonNull(oldFormat.parse(date_str)));
			} catch (ParseException var4) {
				return "";
			}
		} else {
			return "";
		}
	}
	public static String getShortFormatedDate(String date_str) {
		if (date_str != null && !date_str.trim().equals("")) {
			@SuppressLint("SimpleDateFormat") SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			@SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM");

			try {
				return newFormat.format(Objects.requireNonNull(oldFormat.parse(date_str)));
			} catch (ParseException var4) {
				return "";
			}
		} else {
			return "";
		}
	}

	public static boolean ncp() {
		String iface = "";

		try {
			Iterator<NetworkInterface> var1 = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();

			do {
				if (!var1.hasNext()) {
					return false;
				}

				NetworkInterface networkInterface = (NetworkInterface)var1.next();
				if (networkInterface.isUp()) {
					iface = networkInterface.getName();
				}
			} while(!iface.contains("tun") && !iface.contains("ppp") && !iface.contains("pptp"));

			return true;
		} catch (SocketException var3) {
			var3.printStackTrace();
			return false;
		}
	}

	public static AlertDialog loading(Context context) {
		@SuppressLint("InflateParams") AlertDialog dialog = new AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.layout_loading, null)).create();
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		Window w = dialog.getWindow();
		if (w != null) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}
		return dialog;
	}

	public static AlertDialog Alert(Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.layout_alert, null)).create();
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		Window w = dialog.getWindow();
		if (w != null) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}
		return dialog;
	}
	public static void AlertInfo(Activity context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.layout_alert, null)).create();
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		Window w = dialog.getWindow();
		if (w != null) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}

		TextView tv=dialog.findViewById(R.id.txt);
		Objects.requireNonNull(tv).setText(context.getString(R.string.adblocker_detected));
		Button btn=dialog.findViewById(R.id.close);
		Objects.requireNonNull(btn).setText(context.getString(R.string.close));
		btn.setOnClickListener(v -> {
			context.finish();
		});
		dialog.show();
	}

	public static void launchCustomTabs(Activity activity, String url) {
		CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
		customIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
		customIntent.setExitAnimations(activity, R.anim.exit, R.anim.enter);
		customIntent.setStartAnimations(activity, R.anim.enter, R.anim.exit);
		customIntent.setUrlBarHidingEnabled(true);
		customIntent.build().launchUrl(activity, Uri.parse(url));
	}


	public static String coolNumberFormat(long count) {
		if (count < 1000L) {
			return String.valueOf(count);
		} else {
			int exp = (int)(Math.log((double)count) / Math.log(1000.0));
			DecimalFormat format = new DecimalFormat("0.#");
			String value = format.format((double)count / Math.pow(1000.0, (double)exp));
			return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
		}
	}

	public static void updateGlobalStatus(Activity activity,String id){
		try {
			Objects.requireNonNull(ApiClient.getClient(activity)).create(ApiInterface.class).updateGlobalMsg(id).enqueue(new Callback<CallbackResp>() {
				@Override
				public void onResponse(Call<CallbackResp> call, Response<CallbackResp> response) {

				}

				@Override
				public void onFailure(Call<CallbackResp> call, Throwable t) {

				}
			});
		}catch (Exception ignored){}
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	public static void showToast(Activity activity, String type, String msg){
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout= inflater.inflate(R.layout.layout_toast, (ViewGroup) activity.findViewById(R.id.toast_root));
		TextView text = (TextView) layout.findViewById(R.id.msg);
		switch (type){
			case "":
				text.setTextColor(activity.getResources().getColor(R.color.black));
				text.setBackground(activity.getResources().getDrawable(R.drawable.bg_white));
				break;

			case "warning":
				text.setBackground(activity.getResources().getDrawable(R.drawable.bg_toast_warning));
				break;

			case "error":
				text.setBackground(activity.getResources().getDrawable(R.drawable.bg_toast_error));
				break;

			case "success":
				text.setBackground(activity.getResources().getDrawable(R.drawable.bg_toast_success));
				break;
		}

		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, 30);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		text.setText(msg);
		toast.show();
	}
}
