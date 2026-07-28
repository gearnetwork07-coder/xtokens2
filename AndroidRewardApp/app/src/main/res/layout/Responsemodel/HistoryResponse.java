package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class HistoryResponse{

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

		@SerializedName("remained_balance")
		private String remainedBalance;

		@SerializedName("amount")
		private String amount;

		@SerializedName("tran_type")
		private String tranType;

		@SerializedName("type")
		private String type;

		@SerializedName("admin_remarks")
		private String adminRemarks;

		@SerializedName("encoded_fields")
		private String encodedFields;

		@SerializedName("webId")
		private int webId;

		@SerializedName("api_type")
		private String apiType;

		@SerializedName("user_id")
		private int userId;

		@SerializedName("spinhit")
		private int spinhit;

		@SerializedName("id")
		private int id;

		@SerializedName("inserted_at")
		private String insertedAt;

		@SerializedName("taskId")
		private int taskId;

		@SerializedName("remarks")
		private String remarks;

		@SerializedName("video_id")
		private int videoId;

		public String getRemainedBalance(){
			return remainedBalance;
		}

		public String getAmount(){
			return amount;
		}

		public String getTranType(){
			return tranType;
		}

		public String getType(){
			return type;
		}

		public String getAdminRemarks(){
			return adminRemarks;
		}

		public String getEncodedFields(){
			return encodedFields;
		}

		public int getWebId(){
			return webId;
		}

		public String getApiType(){
			return apiType;
		}

		public int getUserId(){
			return userId;
		}

		public int getSpinhit(){
			return spinhit;
		}

		public int getId(){
			return id;
		}

		public String getInsertedAt(){
			return insertedAt;
		}

		public int getTaskId(){
			return taskId;
		}

		public String getRemarks(){
			return remarks;
		}

		public int getVideoId(){
			return videoId;
		}
	}
}