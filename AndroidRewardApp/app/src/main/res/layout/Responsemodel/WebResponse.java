package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class WebResponse{

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

		@SerializedName("timer")
		private String timer;

		@SerializedName("id")
		private String id;

		@SerializedName("title")
		private String title;

		@SerializedName("url")
		private String url;

		@SerializedName("point")
		private String point;

		@SerializedName("status")
		private int status;

		public String getTimer(){
			return timer;
		}

		public String getId(){
			return id;
		}

		public String getTitle(){
			return title;
		}

		public String getUrl(){
			return url;
		}

		public String getPoint(){
			return point;
		}

		public int getStatus(){
			return status;
		}
	}
}