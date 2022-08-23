package com.example.comiccollection.di;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.test.FakeComicRepository;

import dagger.Binds;
import dagger.Module;

/*
 This class provides a fake ComicRepository implementation.
 */
@Module
abstract class FakeRepositoryModule {

    /*
    When this module is wired into the dependency graph, provide a FakeComicRepository
    when a ComicRepository is requested.
     */
    @Binds
    abstract ComicRepository fakeComicRepository(FakeComicRepository fakeComicRepository);

}
