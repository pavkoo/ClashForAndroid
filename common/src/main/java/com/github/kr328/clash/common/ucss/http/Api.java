package com.github.kr328.clash.common.ucss.http;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author shangji_cd
 */
public class Api {
    private static String TAG = Api.class.getSimpleName();
    private static final String BASE_URL = "https://ucss.moe";
    /**
     * 单例模式请求
     */
    private static Api retrofitAPI;

    static {
        retrofitAPI = new Api();
    }

    private final Retrofit retrofit;

    private Api() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        int timeout = 20;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        // 带有拦截器的请求客户端
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }


    public static <T> T createReq(final Class<T> clazz) {
        return retrofitAPI.retrofit.create(clazz);
    }


}
