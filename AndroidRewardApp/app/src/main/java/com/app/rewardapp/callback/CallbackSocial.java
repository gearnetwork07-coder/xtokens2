package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CallbackSocial {

	@SerializedName("title")
	private String title;

	@SerializedName("url")
	private String url;

	@SerializedName("code")
	private String code;

	public String getCode() {
		return code;
	}

	@SerializedName("image")
	private String image;

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getImage() {
		return image;
	}
}