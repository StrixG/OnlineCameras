package com.obrekht.onlinecameras.di.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
public class RetrofitModule {

    private final String baseUrl;
    private final String windyKey;

    public RetrofitModule(String baseUrl, String windyKey) {
        this.baseUrl = baseUrl;
        this.windyKey = windyKey;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    HttpUrl newUrl = request.url().newBuilder()
                            .addQueryParameter("lang", Locale.getDefault().getLanguage()).build();

                    Request newRequest = request.newBuilder()
                            .addHeader("x-windy-key", windyKey)
                            .url(newUrl)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Retrofit.Builder builder) {
        return builder.baseUrl(baseUrl).build();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(Converter.Factory converterFactory, CallAdapter.Factory callAdapterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .client(okHttpClient);
    }

    @Provides
    @Singleton
    CallAdapter.Factory provideCallAdapterFactory() {
        return RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    @Provides
    @Singleton
    Converter.Factory provideConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}
