package com.example.comiccollection.di;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.firestore.FirestoreComicRepository;

import dagger.Binds;
import dagger.Module;

/*
This class simply tells Dagger that when a class asks for a ComicRepository, it should get a
FirestoreComicRepository.
 */
@Module
abstract class FirebaseFirestoreModule {

    @Binds
    abstract ComicRepository firestoreComicRepository(FirestoreComicRepository repository);
}
