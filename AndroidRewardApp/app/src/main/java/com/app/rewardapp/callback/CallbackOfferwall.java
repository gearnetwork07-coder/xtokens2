package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

public class CallbackOfferwall {

    @SerializedName("id")
    String id;

    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    @SerializedName("offerwall_slug")
    String offerwall_slug;

    @SerializedName("offerwall_url")
    String offerwall_url;

    @SerializedName("thumb")
    String thumb;

    @SerializedName("level")
    String level;

    @SerializedName("offer_type")
    String offer_type;

    @SerializedName("card_color")
    String card_color;

    @SerializedName("text_color")
    String text_color;

    @SerializedName("u_tag")
    String u_tag;

    @SerializedName("advid_tag")
    String advid_tag;

    @SerializedName("uid_type")
    String uid_type;

    @SerializedName("browser_type")
    int browser_type;

    public int getBrowser_type() {
        return browser_type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOfferwall_slug() {
        return offerwall_slug;
    }

    public String getOfferwall_url() {
        return offerwall_url;
    }

    public String getThumb() {
        return thumb;
    }

    public String getLevel() {
        return level;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public String getCard_color() {
        return card_color;
    }

    public String getText_color() {
        return text_color;
    }

    public String getU_tag() {
        return u_tag;
    }

    public String getAdvid_tag() {
        return advid_tag;
    }

    public String getUid_type() {
        return uid_type;
    }
}
