package com.example.comiccollection.viewmodel;

import android.util.Log;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.IssuesDeletionListener;
import com.example.comiccollection.data.firestore.FirestoreComicRepository;
import com.example.comiccollection.data.TitlesDeletionListener;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class TitlesViewModel extends ViewModel implements TitlesListener, TitlesDeletionListener,
        IssuesDeletionListener {

    private MutableLiveData< ArrayList<Title> > mLiveTitles = new MutableLiveData<ArrayList<Title>>();

    private ComicRepository repository;

    private TitlesListManager mTitlesListManager = new TitlesListManager();

    private boolean mTryLoadAgain = true;

    /*
    There is a scrolling list of alpha characters on the bottom of the screen.  If the user
    clicks on a character, the screen will scroll to the first title starting with that
    character.  This map maps each letter to the position in the Titles list of the first title
    that starts with that letter.
     */
    private Map<String, Integer> mListPositionByStartLetter;

    private String TAG = TitlesListener.class.getSimpleName();


    /*
    Constructor.
     */
    @Inject
    public TitlesViewModel(ComicRepository comicRepository) {
        this.repository = comicRepository;
    }

    public MutableLiveData< ArrayList<Title> > getTitles() {
        return mLiveTitles;
    }

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

    public void deleteTitle(Title title) { repository.deleteTitle(title, this); }

    public void modifyTitle(Title title) { repository.modifyTitle(title); }

    /*
    This method looks a little out of place because it is acting upon Issue objects and this is
    the *Titles* ViewModel.  The TitlesActivity needs to add issues to the repository directly
    in the case where a new title is created or new issues added in bulk.
     */
    public void addIssuesToTitle(Title title, int firstIssue, int lastIssue) {
        /*
         Add issues to the database for the first-last issue range specified.  Default
         everything to the want list.
         */

        ArrayList<Issue> newIssues = new ArrayList<Issue>();
        for( int issueNumber = firstIssue; issueNumber <= lastIssue; issueNumber++) {
            Issue issue = new Issue();
            issue.setTitle(title.getName());
            issue.setIssueNumber(Integer.toString(issueNumber));
            issue.setWanted(true);

            newIssues.add(issue);
        }

        repository.addIssuesBatch(newIssues);
    }

    /*
    This method deletes issues in bulk from the given Title.
     */
    public void deleteIssuesFromTitle(Title title, int firstIssue, int lastIssue) {
        repository.deleteIssuesByRange(title, firstIssue, lastIssue, this);
    }

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

    @Override
    public void onTitlesDeleteFailed(String message) {
        /*
        TODO: Propagate this message back to the Activity to show in a snackbar.  (Or just have
        the activity implement this interface.)
         */
    }

    @Override
    public void onIssuesDeleteFailed(String message) {
        /*
        TODO: Propagate this message back to the Activity to show in a snackbar.  (Or just have
        the activity implement this interface.)
         */
    }

    /****************************************************************************************
     * Helper methods for the UI display.
     ****************************************************************************************/

    public Map<String, Integer> getListPositionByStartLetter() {
        return mListPositionByStartLetter;
    }

    /*
    Check if a new title object represents a title that already exists in the dataset managed
    by this ViewModel.
     */
    public boolean titleNameExists(Title newTitle) {
        ArrayList<Title> titles = mLiveTitles.getValue();

        return mTitlesListManager.titleNameExists(titles, newTitle);
    }

}
