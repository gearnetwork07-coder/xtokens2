package com.app.rewardapp.restApi;

import com.app.rewardapp.BuildConfig;
import com.app.rewardapp.util.Fun;

public interface WebApi {

    public interface Api{
         String BASE_URL= Fun.encrypt(BuildConfig.API);
         String IMAGES= BASE_URL+"images/";
         String API= "api/v1/";
         String Login =API+ "login";
         String Offers =API+ "offers";
         String ABOUTS =API+ "abouts";
         String SIGNUP = API+ "signup";
         String USER_COIN =API+"user_coin";
         String WEB =API+"web";
         String HISTORY = API+"history";
         String REWARD_HISTORY = API+"rewards";
         String REWARDS =API+"redeem";
         String VIDEOS =API+"video";
         String CHECK_SPIN =API+"checkspin";
         String APPS =API+"apps";
         String CREDIT_WEB =API+"reward_web";
         String CREDIT_VIDEO =API+"reward_video";
         String CREDIT_SPIN =API+"reward_spin";
         String CREDIT_DAILY =API+"reward_daily";
         String CREDIT_APP =API+"reward_app";
         String REDEEM =API+"reward-request";
         String CHECK =API+"check";
         String RESET_PASSWORD =API+"reset-password";
         String VERIFY_EMAIL =API+"send_otp";
         String VERIFY_OTP =API+"verify-otp";
         String SLIDE_BANNER = API+"sldiebanner";
         String UPDATE_PASSWORD =API+"update_password";
         String GAMES = API+"games";
         String CREDIT_GAME = API+"credit_game";
    }
}
