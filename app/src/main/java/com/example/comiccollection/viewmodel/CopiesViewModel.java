package com.example.comiccollection.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.CopiesDeletionListener;
import com.example.comiccollection.data.CopiesListener;
import com.example.comiccollection.data.SingleIssueListener;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;

import javax.inject.Inject;

public class CopiesViewModel extends ViewModel implements SingleIssueListener, CopiesListener,
        CopiesDeletionListener {
    private String mIssueTitle;
    private String mIssueNumber;

    /*
    Information about all known copies of an issue, whether owned, unowned, or sold, are
    stored as member data in the Issue object.
     */
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

    /*
    Get a LiveData for this issue to observe on.
     */
    public LiveData<Issue> getIssue() {
        if( mIssue == null ) {
            mIssue = new MutableLiveData<>();
            loadIssue();
        }

        /*
        The loadIssue() call is asynchronous.  So this method will almost certainly initially
        return an empty LiveData object when first called.  The observer of the object (in this case the
        CopiesActivity) will be notified when the object is 'filled' with the returned
        issue data and can update the UI at that point.
         */
        return mIssue;
    }

    /*
    Add the provided Copy to the Issue being tracked here.
     */
    public void addCopyToIssue(Copy copy) {
        mRepository.addCopyOfIssue(copy, mIssue.getValue(), this);

        /*
        Propagate the change to the display.
         */
        loadIssue();
    }

    /*
    Add the modified copy to the data store, replacing the copy already attached to the issue.

    This does not change the copy type and move the copy from one list to another.
     */
    public void modifyCopy(Copy copy) {
        mRepository.modifyCopy(copy, mIssue.getValue(), this);
    }

    /*
    Delete a copy from the data store.
     */
    public void deleteCopy(Copy copy) {
        mRepository.deleteCopy(copy, mIssue.getValue(),this);

        /*
        Propagate the change to the display.
         */
        loadIssue();
    }

    public void recordSaleOfCopy(Copy copy) {
        mRepository.recordSaleOfCopy(copy, mIssue.getValue(), this);
    }

    public void purchaseCopy(Copy copy) {
        mRepository.purchaseCopy(copy, mIssue.getValue(), this);
    }

    public void addOfferToCopy(Copy copy, Copy.Offer offer) {
        mRepository.addOfferToCopy(copy, offer, mIssue.getValue(), this);
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

    @Override
    public void onCopyChange(Copy copy) {
        /*
        Reload the issue to propagate the change to the display.
         */
        Log.d(TAG, "Copy of " + copy.getTitle() + " " + copy.getIssue() + " changed successfully");
        loadIssue();
    }

    @Override
    public void onCopyChangeFailed(String message) {
        Log.e(TAG, "Failed to change copy: " + message);
    }

    @Override
    public void onCopyReady(Copy copy, Issue issue) {
        /*
        Replace the live Issue object owned by this ViewModel with another Issue object with
        this copy added.
         */
        Issue thisIssue = mIssue.getValue();
        if( thisIssue == null ) {
            Log.e(TAG, "Attempt to add copy to a null Issue.");
            return;
        }

        switch( copy.getCopyType() ) {
            case OWNED:
                thisIssue.getOwnedCopies().add(copy);
                break;

            case FORSALE:
                thisIssue.getForSaleCopies().add(copy);
                break;

                /*
                In this case a 'sold' copy is one that was sold in the market, and is
                categorized as 'unowned'.
                 */
            case SOLD:
                thisIssue.getSoldCopies().add(copy);
                break;

            case SOLDBYME:
                thisIssue.getSoldByMeCopies().add(copy);
                break;
        }
        mIssue.setValue(thisIssue);
    }

    @Override
    public void onCopiesDeleteFailed(String message) {
        Log.e(TAG, "Deletion of " + mIssueTitle + " " + mIssueNumber + " failed: " + message);
    }
}
