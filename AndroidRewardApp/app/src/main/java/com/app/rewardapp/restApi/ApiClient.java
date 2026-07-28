package com.app.rewardapp.restApi;

import static com.app.rewardapp.util.Constant_Api.Authorization;
import static com.app.rewardapp.util.Constant_Api.CONTENT_TYPE;
import static com.app.rewardapp.util.Constant_Api.isNpv;
import static com.app.rewardapp.util.Fun.encryption;
import static com.app.rewardapp.util.Fun.ncp;

import android.app.Activity;
import android.widget.Toast;

import com.app.rewardapp.util.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public  static Retrofit retrofit;

    public static Retrofit getClient(Activity context) {
        Session session;
        session = new Session(context);

        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.level(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request;
                        request = chain.request().newBuilder()
                                .addHeader("Accept", CONTENT_TYPE)
                                .addHeader("lang", session.getData(session.SELECTED_LANGUAGE))
                                .addHeader(Authorization, session.getData(session.TOKEN))
                                .build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WebApi.Api.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        if(isNpv && ncp()){
            Toast.makeText(context, encryption("RG8gbm90IFVzZSBWUE4="), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            return retrofit;
        }
    }
}
