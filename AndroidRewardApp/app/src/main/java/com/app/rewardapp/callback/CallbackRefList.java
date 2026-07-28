package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallbackRefList {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("success")
    private int success;

    @SerializedName("today")
    private String today;

    @SerializedName("total")
    private String total;

    public String getToday() {
        return today;
    }

    public String getTotal() {
        return total;
    }

    public List<DataItem> getData() {
        return data;
    }

    public int getSuccess() {
        return success;
    }

    public class DataItem {
        @SerializedName("name")
        private String name;

        @SerializedName("inserted_at")
        private String insertedAt;

        @SerializedName("profile")
        private String profile;

        public String getProfile() {
            return profile;
        }

        public String getName() {
            return name;
        }

        public String getInsertedAt() {
            return insertedAt;
        }

    }
}