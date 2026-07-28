package com.app.rewardapp.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.R;
import com.app.rewardapp.restApi.ApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.ads.banner.Banner;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;

import de.mateware.snacky.Snacky;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class  Method {
	public static AlertDialog win_dialog;

	public static boolean isConnected(Context ctx) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				return false;
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				for (NetworkInfo networkInfo : info) {
					if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String coolNumberFormat(long count) {
		if (count < 1000) return "" + count;
		int exp = (int) (Math.log(count) / Math.log(1000));
		DecimalFormat format = new DecimalFormat("0.#");
		String value = format.format(count / Math.pow(1000, exp));
		return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
	}


	public static String deviceId(Context ctx) {
		String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase();
		return deviceId;
	}

	public static void UNITY_Banner(Activity context, RelativeLayout adContainer) {
		BannerView topBanner = new BannerView(context, Constant_Api.BANNER_ID, new UnityBannerSize(320, 50));
		topBanner.setListener(IListener.bannerListener);
		topBanner.load();
		adContainer.addView(topBanner);
	}

	private static int getRandomSalt() {
		Random random = new Random();
		return random.nextInt(900);
	}

	public static String Obj(Activity activity){
		Session session = new Session(activity);
		String salt= "" + getRandomSalt();
		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new ApiClient());
		jsObj.addProperty("sal", Fun.md5(encrypt(BuildConfig.API_KEY) +salt ));
		jsObj.addProperty("sig",salt);
		jsObj.addProperty("_mid", session.Auth());
		jsObj.addProperty("_tid", Constant_Api.TID);
		jsObj.addProperty("data", Constant_Api.P1);
		jsObj.addProperty("pack", activity.getPackageName());

		if(Constant_Api.API_TYPE.equals("signup")){
			jsObj.addProperty("u_name",Constant_Api.P1);
			jsObj.addProperty("u_email",Constant_Api.P2);
			jsObj.addProperty("u_password",Constant_Api.P3);
			jsObj.addProperty("u_phone",Constant_Api.P4);
			jsObj.addProperty("u_token",Constant_Api.P5);
			jsObj.addProperty("u_refferal_id",Constant_Api.P6);
			Constant_Api.P1 = ""; Constant_Api.P2 = ""; Constant_Api.P3 = "";
			Constant_Api.P4 = ""; Constant_Api.P5 = "";	Constant_Api.API_TYPE = "";
		}
		else if(Constant_Api.API_TYPE.equals("login")){
			jsObj.addProperty("uemail",Constant_Api.P1);
			jsObj.addProperty("upassword",Constant_Api.P2);
			Constant_Api.P1 = ""; Constant_Api.P2 = ""; Constant_Api.API_TYPE = "";
		}else if(Constant_Api.API_TYPE.equals("redeem")){
			jsObj.addProperty("type",Constant_Api.P2);
			jsObj.addProperty("og",Constant_Api.P3);
			jsObj.addProperty("amt",Constant_Api.P4);
			jsObj.addProperty("detail",Constant_Api.P5);
			Constant_Api.P1 = ""; Constant_Api.P2 = ""; Constant_Api.API_TYPE = "";
			Constant_Api.P3 = ""; Constant_Api.P4 = ""; Constant_Api.P5 = "";
		}
		return encrypt_(jsObj.toString());
	}

	public static String md5(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.getBytes());
			byte messageDigest[] = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (int i=0; i<messageDigest.length; i++)
				hexString.append(String.format("%02x", messageDigest[i]));
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void Error(Activity ctx, String msg) {
		Snacky.builder()
				.setActivity(ctx)
				.setText(msg)
				.setDuration(6000)
				.error()
				.show();
	}
	public static void Success(Activity ctx, String msg) {
		Snacky.builder()
				.setActivity(ctx)
				.setText(msg)
				.setDuration(6000)
				.success()
				.show();
	}

	public static AlertDialog loading(Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)).create();
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		Window w = dialog.getWindow();
		if (w != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}
		return dialog;
	}

	public static AlertDialog Alert(Context context) {
		AlertDialog dialog = new AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.layout_alert, null)).create();
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.getWindow().setWindowAnimations(R.style.Dialoganimation);
		dialog.setCanceledOnTouchOutside(false);
		Window w = dialog.getWindow();
		if (w != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			w.setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
		}
		return dialog;
	}

	public static void hideKeyboard(View view, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

//	public static void alert(Activity context, String message) {
//		AlertDialog dialog = new AlertDialog.Builder(context)
//				.setTitle("#Notice")
//				.setMessage(message)
//				.setNegativeButton("ok", (dialog1, which) -> dialog1.dismiss())
//				.create();
//		dialog.show();
//	}

	public static void alert2(Activity context, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setMessage(message)
				.setCancelable(false)
				.create();
		dialog.show();
	}


	public static void STARTAPP_Banner(Context context, RelativeLayout adContainer) {
		Banner startAppBanner = new Banner(context);
		RelativeLayout.LayoutParams bannerParameters =
				new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adContainer.addView(startAppBanner, bannerParameters);
	}

	public static boolean vpn(){
		String iface ="";
		try {
			for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (networkInterface.isUp())
					iface = networkInterface.getName();
				if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
					return true;
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	public static String encryption(String coded){
		byte[] valueDecoded= new byte[0];
		try {
			valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
		}
		return new String(valueDecoded);
	}

	public static String encrypt(String coded){
		coded= encryption(coded);
		byte[] valueDecoded= new byte[0];
		try {
			valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
		}
		return new String(valueDecoded);
	}

	public static String encrypt_code(String coded){
		byte[] encodeValue = Base64.encode(coded.getBytes(), Base64.DEFAULT);
		return new String(encodeValue);
	}
	public static String encrypt_(String coded){
		coded= encrypt_code(coded);
		byte[] encodeValue = Base64.encode(coded.getBytes(), Base64.DEFAULT);
		return new String(encodeValue);
	}

	public static void launchCustomTabs(Activity activity, String url) {
		CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
		customIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
		customIntent.setExitAnimations(activity, R.anim.exit, R.anim.enter);
		customIntent.setStartAnimations(activity, R.anim.enter, R.anim.exit);
		customIntent.setUrlBarHidingEnabled(true);
		customIntent.build().launchUrl(activity, Uri.parse(url));

	}
}
