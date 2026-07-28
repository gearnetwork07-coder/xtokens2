package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class SignupResponse {

	@SerializedName("response")
	private int response;

	@SerializedName("message")
	private String message;

	public int getResponse(){
		return response;
	}

	public String getMessage(){
		return message;
	}

}