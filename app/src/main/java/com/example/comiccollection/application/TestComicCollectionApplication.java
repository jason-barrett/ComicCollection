package com.example.comiccollection.application;

import android.app.Application;

import com.example.comiccollection.di.TestAppComponent;
import com.example.comiccollection.di.DaggerTestAppComponent;

/*
This class is used for instrumented testing.
 */
public class TestComicCollectionApplication extends ComicCollectionApplication {
    TestAppComponent appComponent = DaggerTestAppComponent.create();

    public TestAppComponent getAppComponent() {
        return appComponent;
    }

}
