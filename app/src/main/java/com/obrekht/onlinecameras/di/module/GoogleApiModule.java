package com.obrekht.onlinecameras.di.module;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
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
    public FusedLocationProviderClient provideLocationApiClient(@ApplicationContext Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
