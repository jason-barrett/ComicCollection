package com.example.comiccollection.di;

import javax.inject.Singleton;

import dagger.Component;

/*
This component is for testing and uses a fake repository.  Otherwise, it extends the behavior
of AppComponent.  In particular, it has injectors for the same activities and fragments.
 */
@Singleton
@Component(modules = {FakeRepositoryModule.class, FakeDatastoreModule.class})
public interface TestAppComponent extends AppComponent {
}
