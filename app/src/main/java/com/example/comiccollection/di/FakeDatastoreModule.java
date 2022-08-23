package com.example.comiccollection.di;

import com.example.comiccollection.data.test.FakeComicRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class FakeDatastoreModule {

    @Provides
    FakeComicRepository provideFakeComicRepository() {
        return FakeComicRepository.getInstance();
    }
}
