package com.app.rewardapp.restApi;

import com.app.rewardapp.util.Constant_Api;

public interface WebApi {

    public interface Api{
         String BASE_URL= Constant_Api.APi;
         String IMAGES= BASE_URL+"images/";
         String USER_IMAGES= BASE_URL+"images/user/";
         String API= "api/v1/";
         String Login =API+ "login";
         String Offers =API+ "offers";
         String get_refer_list =API+ "get_refer_list/{refid}";
         String ABOUTS =API+ "configv2";
         String get_lang =API+ "get_lang";
         String get_lang_data =API+ "get_lang_data/{lang}";
         String SIGNUP = API+ "signup";
         String USER_COIN =API+"user_coin";
         String WEB =API+"web";
         String HISTORY = API+"history";
         String REWARD_HISTORY = API+"rewards";
         String REWARDS =API+"redeem_by_cat/{id}";
         String redeem_cat =API+"redeem_cat";
         String VIDEOS =API+"video";
         String CHECK_SPIN =API+"checkspin";
         String APPS =API+"apps";
         String CREDIT_WEB =API+"reward_web";
         String CREDIT_VIDEO =API+"reward_video";
         String CREDIT_SPIN =API+"reward_spin";
         String get_hotoffer =API+"hotoffer/{id}";
         String get_global_msg =API+"global_msg/{id}";
         String update_global_msg =API+"global_msg/status/{id}";
         String CREDIT_DAILY =API+"reward_daily";
         String CREDIT_APP =API+"reward_app";
         String REDEEM =API+"reward-request";
         String CHECK =API+"check";
         String RESET_PASSWORD =API+"reset-password";
         String UPDATE_PROFILE_PASS =API+"update-profile-pass";
         String UPDATE_PROFILE =API+"update-profile";
         String VERIFY_EMAIL =API+"send_otp";
         String VERIFY_OTP =API+"verify-otp";
         String SLIDE_BANNER = API+"sldiebanner";
         String UPDATE_PASSWORD =API+"update_password";
         String Spin =API+ "get_spin";
         String GAMES = API+"games";
         String Offerwall = API+"offerwall";
         String delete_account = BASE_URL+"delete-account";
         String SOCIAL_LINK = API+"social-links";
         String CREDIT_GAME = API+"credit_game";
        String LEADERBOARD =API+"leaderboard";
        String submit_hotoffer =API+"submit_hotoffer";
    }
}
