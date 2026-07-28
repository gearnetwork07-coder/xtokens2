package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RewardHistoryResponse{

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

		@SerializedName("date")
		private String date;

		@SerializedName("amount")
		private String amount;

		@SerializedName("orginal_amount")
		private String orginalAmount;

		@SerializedName("updated_at")
		private String updatedAt;

		@SerializedName("user_id")
		private String userId;

		@SerializedName("mobile_no")
		private String mobileNo;

		@SerializedName("detail")
		private String detail;

		@SerializedName("type")
		private String type;

		@SerializedName("request_id")
		private int requestId;

		@SerializedName("remarks")
		private String remarks;

		@SerializedName("status")
		private String status;

		public String getDate(){
			return date;
		}

		public String getAmount(){
			return amount;
		}

		public String getOrginalAmount(){
			return orginalAmount;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}

		public String getUserId(){
			return userId;
		}

		public String getMobileNo(){
			return mobileNo;
		}

		public String getDetail(){
			return detail;
		}

		public String getType(){
			return type;
		}

		public int getRequestId(){
			return requestId;
		}

		public String getRemarks(){
			return remarks;
		}

		public String getStatus(){
			return status;
		}
	}
}