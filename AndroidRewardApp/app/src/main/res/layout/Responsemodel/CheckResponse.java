package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CheckResponse {

	@SerializedName("data")
	private String data;

	@SerializedName("success")
	private int success;

	public String getData(){
		return data;
	}

	public int getSuccess(){
		return success;
	}
}