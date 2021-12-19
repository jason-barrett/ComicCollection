package com.example.comiccollection.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.SingleIssueListener;
import com.example.comiccollection.data.entities.Issue;

import javax.inject.Inject;

public class CopiesViewModel extends ViewModel implements SingleIssueListener {
    private String mIssueTitle;
    private String mIssueNumber;

    private MutableLiveData<Issue> mIssue;

    private final ComicRepository mRepository;

    private static final String TAG = CopiesViewModel.class.getSimpleName();

    /*
    Constructor.  Inject the repository dependency.
     */
    @Inject
    public CopiesViewModel(ComicRepository repository) {
        mRepository = repository;
    }

    public void setIssueDetails(String issueTitle, String issueNumber) {
        mIssueTitle = issueTitle;
        mIssueNumber = issueNumber;
    }

    /*
    Kick off a one-time fetch of the issue details (including all of the copies on record).
     */
    private void loadIssue() {
        if( mIssueTitle != null && mIssueNumber != null ) {
            mRepository.getIssue(mIssueTitle, mIssueNumber, this);
        }
    }

    public LiveData<Issue> getIssue() {
        if( mIssue == null ) {
            mIssue = new MutableLiveData<>();
            loadIssue();
        }

        /*
        The loadIssue() call is asynchronous.  So this method will almost certainly initially
        return an empty LiveData object.  The observer of the object (in this case the
        CopiesActivity) will be notified when the object is 'filled' with the returned
        issue data and can update the UI at that point.
         */
        return mIssue;
    }

    @Override
    public void onIssueReady(Issue issue) {
        Log.d(TAG, "Got copies information for " + mIssueTitle + " " + mIssueNumber);
        mIssue.setValue(issue);
    }

    @Override
    public void onIssueLoadFailed() {
        Log.e(TAG, "Failed to load issue " + mIssueTitle + " " + mIssueNumber);
    }

}
