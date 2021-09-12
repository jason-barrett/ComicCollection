package com.example.comiccollection.di;

import com.example.comiccollection.ui.IssuesActivity;
import com.example.comiccollection.ui.TitlesActivity;

import javax.inject.Singleton;

import dagger.Component;

/*
There is one component graph, scoped to the application.
 */
@Singleton
@Component (modules = DatastoreModule.class)
public interface AppComponent {
    void inject(TitlesActivity titlesActivity);
    void inject(IssuesActivity issuesActivity);
}
