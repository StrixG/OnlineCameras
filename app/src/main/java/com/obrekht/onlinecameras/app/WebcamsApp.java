/*
 * Copyright (c) 2016 Nikita Obrekht
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.obrekht.onlinecameras.app;

import android.app.Application;
import android.content.Context;

import com.obrekht.onlinecameras.di.AppComponent;
import com.obrekht.onlinecameras.di.DaggerAppComponent;
import com.obrekht.onlinecameras.di.module.ContextModule;
import com.obrekht.onlinecameras.di.module.RetrofitModule;

public class WebcamsApp extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .retrofitModule(new RetrofitModule("https://webcamstravel.p.mashape.com/webcams/",
                        "qzg5ev8xnumshtEkxiDM8GffBmdnp1CwCGMjsnCPPkUVcQwajO"))
                .build();
    }

    public static WebcamsApp get(Context context) {
        return (WebcamsApp) context.getApplicationContext();
    }

    public static AppComponent getComponent() {
        return appComponent;
    }
}
