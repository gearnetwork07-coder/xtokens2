package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VideoResponse{

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

		@SerializedName("point")
		private String point;

		@SerializedName("video_id")
		private String videoId;

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

		public String getPoint(){
			return point;
		}

		public String getVideoId(){
			return videoId;
		}

		public int getStatus(){
			return status;
		}
	}
}