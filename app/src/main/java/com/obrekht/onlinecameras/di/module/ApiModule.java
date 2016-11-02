package com.obrekht.onlinecameras.di.module;

import com.obrekht.onlinecameras.app.WebcamsApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {RetrofitModule.class})
public class ApiModule {

    @Provides
    @Singleton
    public WebcamsApi provideApi(Retrofit retrofit) {
        return retrofit.create(WebcamsApi.class);
    }
}
