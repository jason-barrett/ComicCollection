package com.example.comiccollection.viewmodel;

import android.util.Log;

import com.example.comiccollection.data.FirestoreComicRepository;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Title;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TitlesViewModel extends ViewModel implements TitlesListener {

    private MutableLiveData< ArrayList<Title> > mLiveTitles = new MutableLiveData<ArrayList<Title>>();
    private FirestoreComicRepository repository = FirestoreComicRepository.getInstance();

    private TitlesListManager mTitlesListManager = new TitlesListManager();

    private boolean mTryLoadAgain = true;

    private Map<String, Integer> mListPositionByStartLetter;

    private ListenerRegistration titlesRegistration;

    private String TAG = TitlesListener.class.getSimpleName();

    public MutableLiveData< ArrayList<Title> > getTitles() { return mLiveTitles; }


    /****************************************************************************************
     * Interface methods to the UI.
     ****************************************************************************************/
    public void loadTitles() {
        /*
        Eventually add logic here to determine whether to go back to the database.
         */

        /*
        This is an asynchronous call to the repository, which will call one of our callback
        methods when complete.
         */
        titlesRegistration = repository.loadAndListenForTitles(this);
    }

    public void addTitle(Title title) {
        repository.addTitle(title);
    }

    public void deleteTitle(Title title) { repository.deleteTitle(title); }

    public void modifyTitle(Title title) { repository.modifyTitle(title); }

    /****************************************************************************************
     * Listeners to the data (model) layer.
     ****************************************************************************************/
    @Override
    public void onTitlesReady(List<Title> titles) {
        Log.i(TAG, "Loaded titles successfully.");

        mListPositionByStartLetter = mTitlesListManager.mapListPositionByStartLetter(titles);

        //TODO: If I change this to run in a worker thread, this call needs to be to postValue().
        mLiveTitles.setValue( (ArrayList<Title>) titles );

        /*
        A successful load resets the flag to try the load again on the next failure.
         */
        mTryLoadAgain = true;
    }

    @Override
    public void onTitleLoadFailed() {

        /*
        Maybe it was a glitch.  Give it a second try.
         */
        if( mTryLoadAgain ) {
            Log.w(TAG, "Could not load titles, will try again.");

            /*
            Clear the registration so that we don't get a second copy of the listener.
             */
            titlesRegistration.remove();
            mTryLoadAgain = false;

            /*
             The Firestore documentation (https://firebase.google.com/docs/firestore/query-data/listen)
             says, "After an error, the listener will not receive any more events..."

             Take one shot at restarting it.
             */
            loadTitles();

        } else {
            Log.w(TAG, "Failed to load titles on second try, giving up.");
            mTryLoadAgain = true;   // Reset for next time.

            /*
            TODO Do we want to provide some indication back to the user here, especially if
            the contents of the LiveData are null?

            As currently coded, the second failure basically means 'restart the app'.
             */
        }
    }

    /****************************************************************************************
     * Helper methods for the UI display.
     ****************************************************************************************/

    public Map<String, Integer> getListPositionByStartLetter() {
        return mListPositionByStartLetter;
    }

    public boolean titleNameExists(Title newTitle) {
        ArrayList<Title> titles = mLiveTitles.getValue();

        return mTitlesListManager.titleNameExists(titles, newTitle);
    }

}
