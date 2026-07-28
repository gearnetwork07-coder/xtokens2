package com.app.rewardapp.callback;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallbackConfig {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("success")
    private int success;

    @SerializedName("message")
    private String message;

    @SerializedName("vpn")
    private boolean vpn;

    @SerializedName("cpx")
    private String cpx;

    public String getCpx() {
        return cpx;
    }

    public boolean isVpn() {
        return vpn;
    }

    @SerializedName("ads")
    private List<Ads> ads;

    public List<Ads> getAds() {
        return ads;
    }

    @SerializedName("spin")
    private List<SpinItem> spin;

    public List<DataItem> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getSuccess() {
        return success;
    }

    public List<SpinItem> getSpin() {
        return spin;
    }

    @SerializedName("ref")
    private String ref;
    public String getRef() {
        return ref;
    }

    public static class DataItem {
        @SerializedName("native_id")
        private String native_id;

        @SerializedName("app_contact")
        private String appContact;

        @SerializedName("vpn")
        private boolean vpn;

        @SerializedName("banner")
        private int banner;

        @SerializedName("banner_id")
        private String banner_id;

        @SerializedName("inter")
        private int inter;

        @SerializedName("inter_id")
        private String inter_id;

        @SerializedName("interstital_count")
        private int interstitalCount;

        @SerializedName("ad_not_load_credit")
        private int adNotLoadCredit;

        @SerializedName("native")
        private int nativeType;

        @SerializedName("active_ad")
        private String activeAdRaw;

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

        public String getAppContact() {
            return appContact;
        }

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

        @SerializedName("app_author")
        private String appAuthor;

        @SerializedName("app_email")
        private String app_email;

        public String getApp_email() {
            return app_email;
        }
        @SerializedName("app_description")
        private String app_description;
        public String getApp_description() {
            return app_description;
        }
        @SerializedName("homepage")
        private int home_style;

        @SerializedName("ui_style")
        private int ui_style;

        @SerializedName("nativeCount")
        private int nativeCount;


        public String getActiveAdRaw() {
            return activeAdRaw;
        }

        public int getNativeCount() {
            return nativeCount;
        }

        public int getUi_style() {
            return ui_style;
        }

        public int getHome_style() {
            return home_style;
        }

        public void setHome_style(int home_style) {
            this.home_style = home_style;
        }
        public boolean isVpn() {
            return vpn;
        }

        public String getAppAuthor() {
            return appAuthor;
        }

        public String getActive_ad() {
            return activeAdRaw;
        }


        public int getBanner() {
            return banner;
        }

        public String getBanner_id() {
            return banner_id;
        }

        public int getInter() {
            return inter;
        }

        public String getInter_id() {
            return inter_id;
        }

        public int getInterstitalCount() {
            return interstitalCount;
        }

        public int getAdNotLoadCredit() {
            return adNotLoadCredit;
        }

        public int getNativeType() {
            return nativeType;
        }

        public String getNative_id() {
            return native_id;
        }
    }
    public class SpinItem {

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

        public String getPosition1() {
            return position1;
        }

        public String getPosition2() {
            return position2;
        }

        public String getPosition3() {
            return position3;
        }

        public String getPosition4() {
            return position4;
        }

        public String getPosition5() {
            return position5;
        }

        public String getPosition6() {
            return position6;
        }

        public String getPosition7() {
            return position7;
        }

        public String getPc1() {
            return pc1;
        }

        public String getPosition8() {
            return position8;
        }

        public String getPc2() {
            return pc2;
        }

        public String getPc3() {
            return pc3;
        }

        public String getPc8() {
            return pc8;
        }

        public int getId() {
            return id;
        }

        public String getPc4() {
            return pc4;
        }

        public String getPc5() {
            return pc5;
        }

        public String getPc6() {
            return pc6;
        }

        public String getPc7() {
            return pc7;
        }
    }

    public class  Ads{
        @SerializedName("ad_id")
        private int adId;

        @SerializedName("slug")
        private String slug;
        @SerializedName("app_id")
        private String appId;

        @SerializedName("inter_id")
        private String interId;
        @SerializedName("reward_id")
        private String rewardId;

        public int getAdId() {
            return adId;
        }

        public String getSlug() {
            return slug;
        }

        public String getInterId() {
            return interId;
        }

        public String getAppId() {
            return appId;
        }

        public String getRewardId() {
            return rewardId;
        }
    }

}