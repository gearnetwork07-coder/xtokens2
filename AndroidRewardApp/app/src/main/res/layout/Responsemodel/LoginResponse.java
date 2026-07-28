package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

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