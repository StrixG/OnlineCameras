package com.obrekht.onlinecameras.di.module;

import android.content.Context;

import com.obrekht.onlinecameras.di.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @ApplicationContext
    public Context provideApplicationContext() {
        return context;
    }
}
