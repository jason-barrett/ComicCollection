package com.example.comiccollection.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.comiccollection.data.entities.Title;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirestoreComicRepository implements ComicRepository {
    /*
    This class manages access to the Firestore data.
     */
    private ArrayList<Title> mComicData;
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

    public synchronized static FirestoreComicRepository getInstance() {
        if( mInstance == null ) {
            mInstance = new FirestoreComicRepository();
        }

        return mInstance;
    }

    @Override
    public void getTitles(final TitlesListener titlesListener) {

        final List<Title> titles = new ArrayList<Title>();

        db.collection(ComicDbHelper.CC_COLLECTION_TITLE).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if( task.isSuccessful() ) {
                            Log.i(TAG, "Fetch collection " + ComicDbHelper.CC_COLLECTION_TITLE
                                    + " SUCCESSFUL");

                            /*
                            Every document in the result is a title.  Read them into a list of
                            Title entities.
                             */
                            for( QueryDocumentSnapshot document : task.getResult() ) {
                                if( document.exists() ) {
                                    Title title = new Title();
                                    title.setName(document.getString(ComicDbHelper.CC_TITLE_NAME));

                                    Long firstIssue = document.getLong(ComicDbHelper.CC_TITLE_FIRST_ISSUE);
                                    if( firstIssue != null ) {
                                        title.setFirstIssue(firstIssue.toString());
                                    }

                                    Long lastIssue = document.getLong(ComicDbHelper.CC_TITLE_LAST_ISSUE);
                                    if( lastIssue != null ) {
                                        title.setLastIssue(lastIssue.toString());
                                    }

                                    titles.add(title);
                                    Log.d(TAG, "Added title " + title.getName()
                                        + " , first issue " + title.getFirstIssue()
                                        + ". last issue " + title.getLastIssue());
                                }
                            }

                            titlesListener.onTitlesReady(titles);

                        } else {
                            Log.e(TAG, "Error fetching " + ComicDbHelper.CC_COLLECTION_TITLE
                                    + task.getException());

                            titlesListener.onTitleLoadFailed();
                        }
                    }
                } // new OnCompleteListener()
        ); // addOnCompleteListener()
    }

    @Override
    public void addTitle(Title title) {

    }

    @Override
    public void modifyTitle(Title title) {

    }
}
