package com.example.comiccollection.application;

import android.app.Application;

import com.example.comiccollection.data.FirestoreComicRepository;
import com.example.comiccollection.viewmodel.TitlesViewModelFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

/*
This is a custom application class to allow for dependency injection for better testability.

At some point I will learn Dagger and do the DI automatically.
 */
public class ComicCollectionApplication extends Application {
    public AppContainer appContainer = new AppContainer();

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
