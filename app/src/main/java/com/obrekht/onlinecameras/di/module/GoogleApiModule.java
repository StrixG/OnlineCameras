package com.obrekht.onlinecameras.di.module;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.obrekht.onlinecameras.di.ApplicationContext;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GoogleApiModule {

    @Provides
    @Singleton
    @Named("location")
    public GoogleApiClient provideLocationApiClient(@ApplicationContext Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
    }
}
