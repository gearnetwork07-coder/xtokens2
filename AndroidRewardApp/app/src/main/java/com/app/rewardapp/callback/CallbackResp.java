package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CallbackResp {

	@SerializedName("data")
	private String data;

	@SerializedName("balance")
	private String balance;

	@SerializedName("interval")
	private int interval;

	public int getInterval() {
		return interval;
	}

	@SerializedName("code")
	private int code;

	@SerializedName("spinlimit")
	private int spinlimit;

	@SerializedName("count")
	private int count;

	@SerializedName("msg")
	private String msg;

	@SerializedName("WkdWMmFXTmxYMmxr")
	private String WkdWMmFXTmxYMmxr;

	public String getWkdWMmFXTmxYMmxr() {
		return WkdWMmFXTmxYMmxr;
	}

	public String getBalance() {
		return balance;
	}

	public String getData(){
		return data;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public int getSpinlimit() {
		return spinlimit;
	}

	public int getCount() {
		return count;
	}

	@SerializedName("user")
	private CallbackLogin.User user;

	public CallbackLogin.User getUser(){
		return user;
	}

	public class User{

		@SerializedName("reason")
		private Object reason;

		@SerializedName("balance")
		private int balance;

		@SerializedName("phone")
		private String phone;

		@SerializedName("ip")
		private String ip;

		@SerializedName("name")
		private String name;

		@SerializedName("refferal_id")
		private String refferalId;

		@SerializedName("cust_id")
		private String custId;

		@SerializedName("inserted_at")
		private String insertedAt;

		@SerializedName("email")
		private String email;

		@SerializedName("token")
		private String token;



		@SerializedName("status")
		private int status;

		@SerializedName("type")
		private String type;
		public String getType() {
			return type;
		}

		public Object getReason(){
			return reason;
		}

		public int getBalance(){
			return balance;
		}

		public String getPhone(){
			return phone;
		}

		public String getIp(){
			return ip;
		}

		public String getName(){
			return name;
		}

		public String getRefferalId(){
			return refferalId;
		}

		public String getCustId(){
			return custId;
		}

		public String getInsertedAt(){
			return insertedAt;
		}

		public String getEmail(){
			return email;
		}

		public String getToken(){
			return token;
		}

		public int getStatus(){
			return status;
		}
	}
}