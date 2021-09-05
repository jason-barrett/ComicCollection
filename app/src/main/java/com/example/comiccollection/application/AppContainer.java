package com.example.comiccollection.application;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;
import com.example.comiccollection.viewmodel.IssuesViewModelFactory;
import com.example.comiccollection.viewmodel.TitlesViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppContainer {

    private FirebaseFirestore db;
    private FirestoreComicRepository comicRepository;

    public TitlesViewModelFactory titlesViewModelFactory;
    public IssuesViewModelFactory issuesViewModelFactory;

    private boolean initialized = false;

    public void initialize() {

        if( initialized ) {
            return;
        }
        /*
        Create one single instance of the repository that will be used by all
        Activities.
        */

        /*
        Firestore knows the details of the database to fetch because of the configuration
        in the google-services.json config file in the app directory.
        */
        db = FirebaseFirestore.getInstance();
        comicRepository = new FirestoreComicRepository(db);

        /*
        Create single instances of the ViewModel factories with access to the repository.
         */
        titlesViewModelFactory = new TitlesViewModelFactory(comicRepository);

        issuesViewModelFactory = new IssuesViewModelFactory(comicRepository);

        initialized = true;
    }

    public TitlesViewModelFactory getTitlesViewModelFactory() {
        return titlesViewModelFactory;
    }

    public IssuesViewModelFactory getIssuesViewModelFactory() {
        return issuesViewModelFactory;
    }
}
