package com.example.comiccollection.viewmodel;

import android.util.Log;

import com.example.comiccollection.data.FirestoreComicRepository;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TitlesViewModel extends ViewModel implements TitlesListener {

    private MutableLiveData< ArrayList<Title> > mLiveTitles = new MutableLiveData<ArrayList<Title>>();
    private FirestoreComicRepository repository = FirestoreComicRepository.getInstance();

    private boolean mTryLoadAgain = true;

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
        repository.loadAndListenForTitles(this);
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
        for( Title title : titles ) {
            Log.d(TAG, "Loaded title " + title.getName());
        }

        mLiveTitles.setValue( (ArrayList<Title>) titles );
    }

    @Override
    public void onTitleLoadFailed() {

        /*
        Maybe it was a glitch.  Give it a second try.
         */
        if( mTryLoadAgain ) {
            Log.w(TAG, "Could not load titles, will try again.");
            loadTitles();
            mTryLoadAgain = false;
        } else {
            Log.w(TAG, "Failed to load titles on second try, giving up.");
            mTryLoadAgain = true;   // Reset for next time.

            /* TODO Do we want to provide some indication back to the user here, especially if
            the contents of the LiveData are null?
             */
        }
    }
}
