package com.example.comiccollection.application;

import android.app.Application;

import com.example.comiccollection.di.AppComponent;
import com.example.comiccollection.di.DaggerAppComponent;

/*
This is a custom application class to allow for dependency injection.
 */
public class ComicCollectionApplication extends Application {
    AppComponent appComponent = DaggerAppComponent.create();

    public AppComponent getAppComponent() {
        return appComponent;
    }
}

