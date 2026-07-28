package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CallbackHistory {

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


	@SerializedName("api_type")
	private String apiType;

	@SerializedName("user_id")
	private String userId;


	@SerializedName("id")
	private int id;

	@SerializedName("inserted_at")
	private String insertedAt;


	@SerializedName("remarks")
	private String remarks;

	@SerializedName("date")
	private String date;

	@SerializedName("orginal_amount")
	private String orginalAmount;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("mobile_no")
	private String mobileNo;

	@SerializedName("detail")
	private String detail;

	@SerializedName("request_id")
	private int requestId;

	@SerializedName("status")
	private String status;

	private int viewType=0;

	public int getViewType() {
		return viewType;
	}

	public CallbackHistory setViewType(int viewType) {
		this.viewType = viewType;
		return  this;
	}

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


	public String getApiType(){
		return apiType;
	}

	public String getUserId(){
		return userId;
	}

	public int getId(){
		return id;
	}

	public String getInsertedAt(){
		return insertedAt;
	}


	public String getRemarks(){
		return remarks;
	}

	public String getDate() {
		return date;
	}

	public String getOrginalAmount() {
		return orginalAmount;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public String getDetail() {
		return detail;
	}

	public int getRequestId() {
		return requestId;
	}

	public String getStatus() {
		return status;
	}
}