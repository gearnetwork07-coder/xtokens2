package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppResponse {

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("status")
	private int status;

	public List<DataItem> getData(){
		return data;
	}

	public int getStatus(){
		return status;
	}

	public class DataItem{

		@SerializedName("app_name")
		private String appName;

		@SerializedName("image")
		private String image;

		@SerializedName("details")
		private String details;

		@SerializedName("id")
		private String id;

		@SerializedName("appurl")
		private String appurl;

		@SerializedName("inserted_at")
		private String insertedAt;

		@SerializedName("url")
		private String url;

		@SerializedName("points")
		private String points;

		@SerializedName("status")
		private int status;

		public String getAppName(){
			return appName;
		}

		public String getImage(){
			return image;
		}

		public String getDetails(){
			return details;
		}

		public String getId(){
			return id;
		}

		public String getAppurl(){
			return appurl;
		}

		public String getInsertedAt(){
			return insertedAt;
		}

		public String getUrl(){
			return url;
		}

		public String getPoints(){
			return points;
		}

		public int getStatus(){
			return status;
		}
	}
}