package com.app.rewardapp.restApi;

import com.app.rewardapp.callback.CallbackBanner;
import com.app.rewardapp.callback.CallbackResp;
import com.app.rewardapp.callback.CallbackConfig;
import com.app.rewardapp.callback.CallbackGame;
import com.app.rewardapp.callback.CallbackHistory;
import com.app.rewardapp.callback.CallbackLeaderboard;
import com.app.rewardapp.callback.CallbackOffer;
import com.app.rewardapp.callback.CallbackOfferwall;
import com.app.rewardapp.callback.CallbackRedeem;
import com.app.rewardapp.callback.CallbackRefList;
import com.app.rewardapp.callback.CallbackSocial;
import com.app.rewardapp.callback.CallbackTask;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(WebApi.Api.ABOUTS)
    Call<CallbackConfig> getConfig();

    @GET(WebApi.Api.Offers)
    Call<CallbackOffer> getOffers();

    @GET(WebApi.Api.SLIDE_BANNER)
    Call<CallbackBanner> SLideBanner();

    @GET(WebApi.Api.Spin)
    Call<CallbackConfig> getSpin();
    @GET(WebApi.Api.get_refer_list)
    Call<CallbackRefList> getReferList(@Path("refid") String refid);

    @GET(WebApi.Api.get_lang)
    Call<List<CallbackSocial>> get_lang();

    @GET(WebApi.Api.GAMES)
    Call<CallbackGame> getGame();

    @GET(WebApi.Api.Offerwall)
    Call<List<CallbackOfferwall>> getOfferwall();

    @GET(WebApi.Api.SOCIAL_LINK)
    Call<List<CallbackSocial>> getSocialLinks();

    @POST(WebApi.Api.VERIFY_OTP)
    Call<CallbackResp> Verify_OTP(@Query("otp") String otp,
                              @Query("email") String email);
    @FormUrlEncoded
    @POST(WebApi.Api.UPDATE_PROFILE_PASS)
    Call<CallbackResp> updateProfilePass(@Query("data") String data);

    @FormUrlEncoded
    @POST(WebApi.Api.UPDATE_PROFILE)
    Call<CallbackResp> updateProfile(@Field("data") String data);


    @Multipart
    @POST(WebApi.Api.UPDATE_PROFILE)
    Call<CallbackResp> updateProfilewithProfile(@Part MultipartBody.Part part, @Query("data") String data);

    @POST(WebApi.Api.RESET_PASSWORD)
    Call<CallbackResp> ResetPass(@Query("email") String email);

    @POST(WebApi.Api.UPDATE_PASSWORD)
    Call<CallbackResp> UpdatePass(@Query("email") String email,
                                   @Query("password") String password,
                                   @Query("otp") String otp);


    @GET(WebApi.Api.REWARDS)
    Call<CallbackRedeem> getRedeem(@Path("id") String id);

    @GET(WebApi.Api.redeem_cat)
    Call<CallbackRedeem> getRedeemCat();

    @GET(WebApi.Api.LEADERBOARD)
    Call<List<CallbackLeaderboard>> getLeaderboard();

    @GET(WebApi.Api.CREDIT_GAME)
    Call<CallbackResp> GameReward();

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<List<CallbackHistory>> ApiTransaction(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<List<CallbackTask>> ApiTask(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/v1/datas")
    Call<CallbackResp> Api(@Field("data") String body);

    @FormUrlEncoded
    @POST("api/v1/user")
    Call<CallbackResp> ApiUser(@Field("data") String body);


    @GET(WebApi.Api.get_hotoffer)
    Call<List<CallbackTask>> getHotOffer(@Path("id") String id);


    @GET(WebApi.Api.update_global_msg)
    Call<CallbackResp> updateGlobalMsg(@Path("id") String id);


    @GET(WebApi.Api.get_global_msg)
    Call<List<CallbackTask>> getGlobalMsg(@Path("id") String id);

    @Multipart
    @POST(WebApi.Api.submit_hotoffer)
    Call<CallbackResp> submitWithAttach(@Part MultipartBody.Part part,
                                   @Query("link") String link,
                                   @Query("id") String id,
                                   @Query("uid") String uid);



    @POST(WebApi.Api.submit_hotoffer)
    Call<CallbackResp> submit(@Query("link") String link,
                         @Query("id") String id,
                              @Query("uid") String uid);
}
