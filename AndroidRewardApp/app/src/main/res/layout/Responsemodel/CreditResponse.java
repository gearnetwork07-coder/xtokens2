package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CreditResponse {

	@SerializedName("data")
	private String data;

	@SerializedName("balance")
	private int balance;

	@SerializedName("success")
	private int success;

	public int getBalance() {
		return balance;
	}

	public String getData(){
		return data;
	}

	public int getSuccess(){
		return success;
	}
}