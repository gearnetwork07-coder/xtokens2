package com.app.rewardapp.callback;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallbackRedeem {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("success")
    private int success;

    public List<DataItem> getData() {
        return data;
    }

    public int getSuccess() {
        return success;
    }

    public class DataItem {

        @SerializedName("id")
        private String id;
        @SerializedName("image")
        private String image;
        @SerializedName("title")
        private String title;

        @SerializedName("pointvalue")
        private String pointvalue;

        @SerializedName("description")
        private String description;


        @SerializedName("hint")
        private String hint;

        @SerializedName("input_type")
        private int input_type;

        @SerializedName("points")
        private String points;

        @SerializedName("status")
        private int status;



        public String getImage() {
            return image;
        }

        public String getPointvalue() {
            return pointvalue;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getHint() {
            return hint;
        }

        public int getInput_type() {
            return input_type;
        }

        public String getPoints() {
            return points;
        }

        public int getStatus() {
            return status;
        }
    }
}