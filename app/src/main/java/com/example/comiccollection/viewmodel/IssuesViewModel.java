package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.FirestoreComicRepository;
import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.entities.Issue;

import java.util.Iterator;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IssuesViewModel extends ViewModel implements IssuesListener {

    /*
    The main data repository that feeds information into the ViewModel for
    display.
     */
    private FirestoreComicRepository repository;

    /*
    The ViewModel retains a LiveData array of issues, which the Activity will observe.
     */
    private MutableLiveData<List<Issue>> mIssuesList = new MutableLiveData<List<Issue>>();

    /*
    A helper class to perform operations on the master list of Issues.
     */
    private IssuesListManager mIssuesListManager = new IssuesListManager();

    /*
    Constructor.  Inject the repository dependency.
     */
    public IssuesViewModel(FirestoreComicRepository repository) {
        this.repository = repository;
    }

    /*
    Get the LiveData Issues list.
     */
    public MutableLiveData<List<Issue>> getIssuesList() {
        return mIssuesList;
    }

    /*
    This method is called when the initial load of issues is complete, including the load
    of the sub-documents for the copies.
     */
    @Override
    public void onIssuesReady(List<Issue> issues) {
        mIssuesList.setValue(issues);
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
        List<Issue> localIssues = mIssuesList.getValue();

        List<Issue> modifiedIssues = mIssuesListManager.implementIssuesListModifications(localIssues,
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
}
