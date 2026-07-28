package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OfferResponse{

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("success")
	private int success;

	@SerializedName("message")
	private String message;

	public List<DataItem> getData(){
		return data;
	}

	public int getSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public class DataItem{

		@SerializedName("offer_icon")
		private String offerIcon;

		@SerializedName("updated_at")
		private String updatedAt;

		@SerializedName("id")
		private int id;

		@SerializedName("offer_title")
		private String offerTitle;

		@SerializedName("type")
		private String type;

		@SerializedName("status")
		private int status;

		public String getOfferIcon(){
			return offerIcon;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}

		public int getId(){
			return id;
		}

		public String getOfferTitle(){
			return offerTitle;
		}

		public String getType(){
			return type;
		}

		public int getStatus(){
			return status;
		}
	}
}