package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class SpinResponse{

	@SerializedName("spinlimit")
	private int spinlimit;

	@SerializedName("success")
	private int success;

	@SerializedName("count")
	private int count;

	public int getSpinlimit(){
		return spinlimit;
	}

	public int getSuccess(){
		return success;
	}

	public int getCount(){
		return count;
	}
}