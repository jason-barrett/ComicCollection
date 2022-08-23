package com.example.comiccollection;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import com.example.comiccollection.application.TestComicCollectionApplication;

/*
This class provides instrumented tests with an application whose DI graph is wired to substitute
in fake components such as the fake repository.
 */
public class ComicCollectionTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        return super.newApplication(cl, TestComicCollectionApplication.class.getName(), context);
    }
}
