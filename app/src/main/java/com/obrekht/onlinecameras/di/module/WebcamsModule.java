package com.obrekht.onlinecameras.di.module;

import com.obrekht.onlinecameras.app.WebcamsApi;
import com.obrekht.onlinecameras.app.WebcamsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApiModule.class})
public class WebcamsModule {

    @Provides
    @Singleton
    public WebcamsService provideWebcamsService(WebcamsApi webcamsApi) {
        return new WebcamsService(webcamsApi);
    }
}
