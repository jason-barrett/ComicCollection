package com.example.comiccollection;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/*
This class will test that a set of titles is correctly read from a Firestore database, using
an emulator for the database.

The class under test is TitlesViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class TitlesReadFromDbTest {

    /*
    We're going to set up a repository instance that replaces the actual cloud Firestore
    database with an emulated database running locally.
     */
    FirebaseFirestore db;

    FirestoreComicRepository repository;

    TitlesViewModel titlesViewModel;

    private final CountDownLatch waiter = new CountDownLatch(1);

    @Before
    public void setup() {
        db = FirebaseFirestore.getInstance();

        /*
        10.0.2.2 is the localhost address defined by the Firestore emulator.

        I changed the port from 8080 (the default) to 8082 because Tomcat happens to use
        8080 and is configured on my laptop.
         */
        db.useEmulator("10.0.2.2", 8082);

        /*
        "Note:  The Cloud Firestore emulator clears database contents when shut down. Since the
        offline cache of the Firestore SDK is not automatically cleared, you may want to disable
        local persistence in your emulator configuration to avoid discrepancies between the
        emulated database and local caches; in the Web SDK, persistence is disabled by default."
         */
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        /*
        Inject the emulated database into a comic repository for test purposes.
         */
        repository = new FirestoreComicRepository(db);

        /*
        Now inject THAT repository into the ViewModel under test.
         */
        titlesViewModel = new TitlesViewModel(repository);
    }

    //@Test
    public void titles_loadTitles_Correct() {
        titlesViewModel.loadTitles();

        /*
        Wait a period of time for the titles load to finish.  After this time has passed, the
        ViewModel's mLiveTitles array should have the correct list of titles.
         */
        try{
            waiter.await(2000, TimeUnit.MILLISECONDS);
        } catch( InterruptedException ex ) {

        }

        ArrayList<Title> titles = titlesViewModel.getTitles().getValue();
        assert( titles.size() > 0 );
    }

}