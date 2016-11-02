package com.obrekht.onlinecameras.di.module;

import android.content.Context;

import com.obrekht.onlinecameras.di.ApplicationContext;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class RxPermissionsModule {

    @Provides
    @Singleton
    public RxPermissions provideRxPermissions(@ApplicationContext Context context) {
        return RxPermissions.getInstance(context);
    }
}
