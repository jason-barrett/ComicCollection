package com.example.comiccollection.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.data.map.TitlesMapper;
import com.example.comiccollection.data.modifiers.TitlesModifier;
import com.example.comiccollection.data.modifiers.TitlesSorter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirestoreComicRepository implements ComicRepository {
    /*
    This class manages access to the Firestore data.
     */
    private String TAG = this.getClass().getSimpleName();

    private static FirestoreComicRepository mInstance = null;

    private FirebaseFirestore db;

    private FirestoreComicRepository() {
        /*
        Firestore knows the details of the database to fetch because of the configuration
        in the google-services.json config file in the app directory.
         */
        db = FirebaseFirestore.getInstance();
    }

    private List<TitlesModifier> modifiers = new ArrayList<TitlesModifier>();

    public synchronized static FirestoreComicRepository getInstance() {
        if( mInstance == null ) {
            mInstance = new FirestoreComicRepository();
        }

        return mInstance;
    }

    @Override
    public void loadAndListenForTitles(final TitlesListener titlesListener) {

        /*
        Register modifiers to implement the business logic.

         - Sort the titles alphabetically.
         */
        modifiers.add(new TitlesSorter());

        /*
        Define a listener to be called on any changes to the collection (including the initial
        load).
         */
        db.collection(ComicDbHelper.CC_COLLECTION_TITLE).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        /*
                        Check for exception, otherwise I can assume I got updates.
                         */
                        if( e != null ) {
                            Log.e(TAG, "Error fetching " + ComicDbHelper.CC_COLLECTION_TITLE
                                    + ": " + e);

                            titlesListener.onTitleLoadFailed();
                        }

                        /*
                        We're good.  Map the query result into a list to send back.
                         */
                        Log.i(TAG, "Fetch collection " + ComicDbHelper.CC_COLLECTION_TITLE
                                + " SUCCESSFUL");
                        List<Title> titles = TitlesMapper.map(value);

                        /*
                        Before sending the list back, apply any registered business logic methods.
                        */
                        for( TitlesModifier m : modifiers ) {
                            Log.i(TAG, "Running logic in " + m.getClass().getSimpleName());
                            titles = m.modify((ArrayList<Title>)titles);
                        }

                        /*
                        The list goes back to the ViewModel layer.
                         */
                        titlesListener.onTitlesReady(titles);

                    } // on Event()
                } // new EventListener()
        ); // addSnapshotListener()
    }

    @Override
    public void addTitle(Title title) {

    }

    @Override
    public void modifyTitle(Title title) {

    }
}
