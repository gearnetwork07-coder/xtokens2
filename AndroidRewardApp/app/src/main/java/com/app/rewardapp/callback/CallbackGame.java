package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallbackGame {

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("success")
	private int success;

	public List<DataItem> getData(){
		return data;
	}

	public int getSuccess(){
		return success;
	}

	public class DataItem{

		@SerializedName("image")
		private String image;

		@SerializedName("link")
		private String link;

		@SerializedName("created_at")
		private String createdAt;

		@SerializedName("id")
		private String id;

		@SerializedName("title")
		private String title;

		@SerializedName("time")
		private String time;

		@SerializedName("browser_type")
		private String browser_type;

		public String getBrowser_type() {
			return browser_type;
		}
		@SerializedName("orientation")
		private String orientation;
		@SerializedName("description")
		private String description;

		public String getDescription() {
			return description;
		}

		@SerializedName("coin")
		private String coin;

		public String getTime() {
			return time;
		}

		public String getOrientation() {
			return orientation;
		}

		public String getCoin() {
			return coin;
		}

		public String getImage(){
			return image;
		}

		public String getLink(){
			return link;
		}
		public String getCreatedAt(){
			return createdAt;
		}

		public String getId(){
			return id;
		}

		public String getTitle(){
			return title;
		}
	}
}