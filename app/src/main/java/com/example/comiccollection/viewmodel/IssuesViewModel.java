package com.example.comiccollection.viewmodel;

import android.util.Log;

import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.CopiesListener;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.entities.Issue;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class IssuesViewModel extends ViewModel implements IssuesListener, CopiesListener {

    /*
    The main data repository that feeds information into the ViewModel for
    display.
     */
    private ComicRepository repository;

    /*
    The ViewModel retains a LiveData array of issues, which the Activity will observe.
     */
    private MutableLiveData<ArrayList<Issue>> mIssuesList = new MutableLiveData<ArrayList<Issue>>();

    /*
    A helper class to perform operations on the master list of Issues.
     */
    private IssuesListManager mIssuesListManager = new IssuesListManager();

    private String TAG = IssuesViewModel.class.getSimpleName();

    /*
    Constructor.  Inject the repository dependency.
     */
    @Inject
    public IssuesViewModel(ComicRepository repository) {

        this.repository = repository;
    }

    /*
    Get the LiveData Issues list.
     */
    public MutableLiveData<ArrayList<Issue>> getIssuesList() {
        return mIssuesList;
    }

    /*
    Launch the initial load of data for issues of the given title.
    */
    public void loadIssues(String title) {
        /*
        This is going to perform all of the collection group queries to fetch all the copy
        information one time.
         */
        repository.getIssuesByTitleOnce(title, this);

        /*
        This is going to listen for changes to the issue itself.
         */

        repository.getIssuesByTitleAndListen(title, this);
    }

    public void modifyIssue(Issue issue) {
        repository.modifyIssue(issue, this);
    }

    /*
    Note that the new Copy has already been added to the Issue object passed in here.  This is
    a precondition of this call.  This method will add that copy to the issue's representation
    in the data store.
     */
    public void addOwnedCopyOfIssue(Copy ownedCopy, Issue issue) {
        repository.addCopyOfIssue(ownedCopy, issue, this);
    }

    /*************************************************************************
     * Implemented methods from IssuesListener.
     *************************************************************************/

    /*
    This method is called when the initial load of issues is complete, including the load
    of the sub-documents for the copies.
     */
    @Override
    public void onIssuesReady(List<Issue> issues) {
        Log.d(TAG, "Issues load is ready.");

         mIssuesList.setValue((ArrayList<Issue>)issues);
    }

    /*
    This method is called when something in the issues data has changed.  It may be information
    about an issue itself, or about one of the copies associated with it.  It may be that an
    issue has been deleted from the list.

    This method is called with a list of issues to add or replace, and another list of issues
    to remove.  We have to do the work on our local LiveData.
     */
    @Override
    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {
        Log.d(TAG, "Issue changes are ready.");
        ArrayList<Issue> localIssues = mIssuesList.getValue();

        ArrayList<Issue> modifiedIssues = mIssuesListManager.implementIssuesListModifications(localIssues,
                issuesToAddOrReplace, issuesToRemove);

        mIssuesList.setValue(modifiedIssues);
    } // on IssueChangesReady()

    /*
    If the issues for a particular title don't load, we want to report a problem and
    go back to the TitlesActivity to try again.
     */
    @Override
    public void onIssueLoadFailed() {

    }

    /*************************************************************************
     * Implemented methods from CopiesListener.
     *************************************************************************/

    /*
    This method is called when the repository has finished adding a new Copy to an Issue
    in this list.
     */
    @Override
    public void onCopyReady(Copy copy, Issue issue) {
        List<Issue> issuesToAddOrReplace = new ArrayList<>();
        List<Issue> issuesToRemove = new ArrayList<>();
        issuesToAddOrReplace.add(issue);

        onIssueChangesReady(issuesToAddOrReplace, issuesToRemove);
    }
}
