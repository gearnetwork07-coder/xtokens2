package com.app.rewardapp.restApi;

import android.app.Activity;

import com.app.rewardapp.util.Fun;
import com.app.rewardapp.util.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public  static Retrofit retrofit;

    public static Retrofit getClient(Activity context){
        Session session;
        session = new Session(context);

        if(retrofit==null){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.level(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder().addQueryParameter("data", Fun.Obj(context)).build();
                        request  = chain.request().newBuilder()
                                .header("Accept", "application/json")
                                .addHeader("Authorization" ,session.getData(session.TOKEN))
                                .url(url).build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .build();

            Gson gson= new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit= new Retrofit.Builder()
                    .baseUrl(WebApi.Api.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
