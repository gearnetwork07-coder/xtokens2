package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallbackOffer {

	@SerializedName("data")
	private List<OfferItem> data;

	@SerializedName("code")
	private int code;

	@SerializedName("msg")
	private String msg;

	public List<OfferItem> getData(){
		return data;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public class OfferItem{

		public void setOfferTitle(String offerTitle) {
			this.offerTitle = offerTitle;
		}

		public void setOffer_icon(String offer_icon) {
			this.offer_icon = offer_icon;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		@SerializedName("offer_title")
		private String offerTitle;

		@SerializedName("offer_icon")
		private String offer_icon;

		@SerializedName("type")
		private String type;

		public String getType() {
			return type;
		}

		public String getOffer_icon() {
			return offer_icon;
		}

		@SerializedName("status")
		private int status;

		public String getOfferTitle(){
			return offerTitle;
		}

		public int getStatus(){
			return status;
		}
	}}