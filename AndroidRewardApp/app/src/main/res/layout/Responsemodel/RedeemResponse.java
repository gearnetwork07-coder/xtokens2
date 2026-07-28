package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RedeemResponse{

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

		@SerializedName("pointvalue")
		private String pointvalue;

		@SerializedName("description")
		private String description;

		@SerializedName("id")
		private String id;

		@SerializedName("title")
		private String title;

		@SerializedName("points")
		private String points;

		@SerializedName("status")
		private int status;

		public String getImage(){
			return image;
		}

		public String getPointvalue(){
			return pointvalue;
		}

		public String getDescription(){
			return description;
		}

		public String getId(){
			return id;
		}

		public String getTitle(){
			return title;
		}

		public String getPoints(){
			return points;
		}

		public int getStatus(){
			return status;
		}
	}
}