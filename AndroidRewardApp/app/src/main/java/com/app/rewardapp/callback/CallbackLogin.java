package com.app.rewardapp.callback;
import com.google.gson.annotations.SerializedName;

public class CallbackLogin {

	@SerializedName("response")
	private int response;

	@SerializedName("WkdWMmFXTmxYMmxr")
	private String wkdWMmFXTmxYMmxr;

	@SerializedName("message")
	private String message;

	@SerializedName("user")
	private User user;

	public int getResponse(){
		return response;
	}

	public String getWkdWMmFXTmxYMmxr(){
		return wkdWMmFXTmxYMmxr;
	}

	public String getMessage(){
		return message;
	}

	public User getUser(){
		return user;
	}

	public class User{

		@SerializedName("reason")
		private Object reason;

		@SerializedName("balance")
		private String balance;

		@SerializedName("phone")
		private String phone;

		@SerializedName("ip")
		private String ip;

		@SerializedName("name")
		private String name;

		@SerializedName("refferal_id")
		private String refferalId;

		@SerializedName("email")
		private String email;

		@SerializedName("token")
		private String token;

		@SerializedName("emailVerified")
		private String emailVerified;

		@SerializedName("uid")
		private String google;

		@SerializedName("profile")
		private String profile;

		public String getProfile() {
			return profile;
		}

		public String getGoogle() {
			return google;
		}

		@SerializedName("from_refer")
		private int from_refer;

		public String getEmailVerified() {
			return emailVerified;
		}

		@SerializedName("status")
		private int status;

		public int getFrom_refer() {
			return from_refer;
		}

		public Object getReason(){
			return reason;
		}

		public String getBalance(){
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