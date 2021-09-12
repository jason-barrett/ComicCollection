package com.example.comiccollection.di;

import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;

@Module
public class DatastoreModule {

    @Provides
    FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }
}
