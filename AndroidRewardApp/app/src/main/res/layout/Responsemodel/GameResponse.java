package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GameResponse{

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