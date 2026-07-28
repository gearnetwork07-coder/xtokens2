package com.app.rewardapp.callback;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AboutResponse{

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("success")
	private int success;

	@SerializedName("message")
	private String message;

	@SerializedName("spin")
	private List<SpinItem> spin;

	public List<DataItem> getData(){
		return data;
	}

	public String getMessage() {
		return message;
	}

	public int getSuccess(){
		return success;
	}

	public List<SpinItem> getSpin(){
		return spin;
	}

	public class DataItem {

		@SerializedName("app_icon")
		private String appIcon;

		@SerializedName("app_contact")
		private String appContact;

		@SerializedName("vpn")
		private boolean vpn;

		@SerializedName("interstital")
		private boolean interstital;

		@SerializedName("interstital_type")
		private String interstital_type;

		@SerializedName("interstital_count")
		private int interstital_count;

		@SerializedName("interstital_ID")
		private String interstital_ID;

		@SerializedName("share_msg")
		private String share_msg;

		@SerializedName("up_msg")
		private String up_msg;

		@SerializedName("up_link")
		private String up_link;

		@SerializedName("up_mode")
		private String up_mode;

		@SerializedName("up_status")
		private boolean up_status;

		@SerializedName("up_btn")
		private boolean up_btn;

		@SerializedName("up_version")
		private int up_version;

		public String getShare_msg() {
			return share_msg;
		}

		public String getUp_msg() {
			return up_msg;
		}

		public String getUp_link() {
			return up_link;
		}

		public String getUp_mode() {
			return up_mode;
		}

		public boolean isUp_status() {
			return up_status;
		}

		public int getUp_version() {
			return up_version;
		}

		public boolean isUp_btn() {
			return up_btn;
		}

		@SerializedName("app_developed")
		private String appDeveloped;

		@SerializedName("app_version")
		private String appVersion;

		@SerializedName("app_author")
		private String appAuthor;

		@SerializedName("startappid")
		private String startappid;

		@SerializedName("unity_gameid")
		private String unityGameid;

		@SerializedName("id")
		private int id;

		@SerializedName("bannerid")
		private String bannerid;

		@SerializedName("telegram")
		private String telegram;

		@SerializedName("adcolony_appID")
		private String adcolonyAppID;

		@SerializedName("unity_rewardid")
		private String unityRewardid;

		@SerializedName("statartapp_reward")
		private boolean statartappReward;

		@SerializedName("app_name")
		private String appName;

		@SerializedName("adcolony_reward")
		private boolean adcolonyReward;

		@SerializedName("applovin_reward")
		private boolean applovinReward;

		@SerializedName("app_email")
		private String appEmail;

		@SerializedName("banner_type")
		private String bannerType;

		@SerializedName("privacy_policy")
		private String privacyPolicy;

		@SerializedName("adcolony_zoneid")
		private String adcolonyZoneid;

		@SerializedName("applovin_rewardID")
		private String applovinRewardID;

		@SerializedName("fb")
		private String fb;

		@SerializedName("unity_reward")
		private boolean unityReward;

		@SerializedName("app_website")
		private String appWebsite;

		@SerializedName("app_description")
		private String appDescription;

		public boolean isInterstital() {
			return interstital;
		}

		public String getInterstital_type() {
			return interstital_type;
		}

		public int getInterstital_count() {
			return interstital_count;
		}

		public String getInterstital_ID() {
			return interstital_ID;
		}

		public boolean isStatartappReward() {
			return statartappReward;
		}

		public boolean isAdcolonyReward() {
			return adcolonyReward;
		}

		public boolean isApplovinReward() {
			return applovinReward;
		}

		public boolean isUnityReward() {
			return unityReward;
		}

		public boolean isVpn() {
			return vpn;
		}

		public String getAppIcon() {
			return appIcon;
		}

		public String getAppContact() {
			return appContact;
		}

		public String getAppDeveloped() {
			return appDeveloped;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public String getAppAuthor() {
			return appAuthor;
		}

		public String getStartappid() {
			return startappid;
		}

		public String getUnityGameid() {
			return unityGameid;
		}

		public int getId() {
			return id;
		}

		public String getBannerid() {
			return bannerid;
		}

		public String getTelegram() {
			return telegram;
		}

		public String getAdcolonyAppID() {
			return adcolonyAppID;
		}

		public String getUnityRewardid() {
			return unityRewardid;
		}

		public boolean getStatartappReward() {
			return statartappReward;
		}

		public String getAppName() {
			return appName;
		}

		public String getAppEmail() {
			return appEmail;
		}

		public String getBannerType() {
			return bannerType;
		}

		public String getPrivacyPolicy() {
			return privacyPolicy;
		}

		public String getAdcolonyZoneid() {
			return adcolonyZoneid;
		}

		public String getApplovinRewardID() {
			return applovinRewardID;
		}

		public String getFb() {
			return fb;
		}

		public boolean getUnityReward() {
			return unityReward;
		}

		public String getAppWebsite() {
			return appWebsite;
		}

		public String getAppDescription() {
			return appDescription;
		}

	}

	public class SpinItem{

		@SerializedName("position_1")
		private String position1;

		@SerializedName("position_2")
		private String position2;

		@SerializedName("position_3")
		private String position3;

		@SerializedName("position_4")
		private String position4;

		@SerializedName("position_5")
		private String position5;

		@SerializedName("position_6")
		private String position6;

		@SerializedName("position_7")
		private String position7;

		@SerializedName("pc_1")
		private String pc1;

		@SerializedName("position_8")
		private String position8;

		@SerializedName("pc_2")
		private String pc2;

		@SerializedName("pc_3")
		private String pc3;

		@SerializedName("pc_8")
		private String pc8;

		@SerializedName("id")
		private int id;

		@SerializedName("pc_4")
		private String pc4;

		@SerializedName("pc_5")
		private String pc5;

		@SerializedName("pc_6")
		private String pc6;

		@SerializedName("pc_7")
		private String pc7;

		public String getPosition1(){
			return position1;
		}

		public String getPosition2(){
			return position2;
		}

		public String getPosition3(){
			return position3;
		}

		public String getPosition4(){
			return position4;
		}

		public String getPosition5(){
			return position5;
		}

		public String getPosition6(){
			return position6;
		}

		public String getPosition7(){
			return position7;
		}

		public String getPc1(){
			return pc1;
		}

		public String getPosition8(){
			return position8;
		}

		public String getPc2(){
			return pc2;
		}

		public String getPc3(){
			return pc3;
		}

		public String getPc8(){
			return pc8;
		}

		public int getId(){
			return id;
		}

		public String getPc4(){
			return pc4;
		}

		public String getPc5(){
			return pc5;
		}

		public String getPc6(){
			return pc6;
		}

		public String getPc7(){
			return pc7;
		}
	}
}