package com.example.comiccollection.data.firestore;

import android.util.Log;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.modifiers.IssuesSorter;
import com.example.comiccollection.viewmodel.IssuesViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
This class acts as an adapter layer between the Firestore repository and the ViewModels.
 */
public class FirestoreIssuesDataAdapter {

    private final String TAG = FirestoreIssuesDataAdapter.class.getSimpleName();

    /*
    This adapter will use the IssuesViewModel to retrieve a copy of its master Issues list.
    It will not mutate the list, but will return to the ViewModel an updated list.
     */
    private final IssuesViewModel issuesViewModel;

    public FirestoreIssuesDataAdapter(IssuesViewModel issuesViewModel) {
        this.issuesViewModel = issuesViewModel;
    }

    /*
    This method is called when something in the issues data has changed.  It may be information
    about an issue itself, or about one of the copies associated with it.  It may be that an
    issue has been deleted from the list.

    This method is called with a list of issues to add or replace, and another list of issues
    to remove.  We have to do the work on our local LiveData.
    */
    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {
        Log.d(TAG, "Issue changes are ready.");

        ArrayList<Issue> localIssues = issuesViewModel.getIssuesList().getValue();

        ArrayList<Issue> modifiedIssues = implementIssuesListModifications(localIssues,
                issuesToAddOrReplace, issuesToRemove);

        //mIssuesList.setValue(modifiedIssues);
        issuesViewModel.onIssuesReady(modifiedIssues);
    } // on IssueChangesReady()

    /*
    This method atomically implements a set of changes (adds, replacements, and removals)
    to a list of Issues.

    Depending on the timing, it is not impossible that a group of changes represented by the
    'replace' and 'remove' lists may contain both a 'replace' and a 'remove' of the same document
    ID.  The removal is considered the overriding change in this case.

    The document ID created by Firestore is a randomly assigned unique number.  So a
    'remove-then-add' for the same document ID should be impossible.

     */
    private ArrayList<Issue> implementIssuesListModifications(List<Issue> masterIssuesList,
                                                             List<Issue> issuesToAddOrReplace,
                                                             List<Issue> issuesToRemove) {

        /*
        The master list should be modified atomically.  It should not be left in a half-changed
        state if an unchecked exception occurs, for instance.  So we make a copy of the list
        and return the copy when we're done, instead of acting directly on the reference
        passed in.
         */
        ArrayList<Issue> localIssues;
        if( masterIssuesList != null ) {
            localIssues = new ArrayList<>(masterIssuesList);
        } else {
            Log.w(TAG, "Modifications posted to empty list");
            localIssues = new ArrayList<>();
        }

        /*
        In the general case, the list of issues will be much larger than the add/remove lists
        passed in.
         */
        Iterator<Issue> replaceIssuesIterator = issuesToAddOrReplace.iterator();
        while( replaceIssuesIterator.hasNext() ) {
            Issue replaceIssue = replaceIssuesIterator.next();

            /*
            As a first step, take the issues to be replaced out of the local copy of the
            master list.

            Leaving the issue on the replace list means it will get added at the
            end with the 'new' issues'.  If I add it now, I'll hit a
            ConcurrentModificationException.
             */
            localIssues.removeIf(issue -> replaceIssue.getDocumentId().equals(issue.getDocumentId()));
        }

        /*
        Second step, add everything new or being replaced to the local copy of the master list.
         */
        for( Issue addIssue : issuesToAddOrReplace ) {
            Log.i(TAG, "Adding issue " + addIssue.getTitle() + " " + addIssue.getIssueNumber());
            localIssues.add(addIssue);
        }

        /*
        Sweep through the deletions last, because they take precedence over the replacements.
        If we get a set of changes with a modification and a deletion of the same document, the
        deletion 'wins'.
         */
        Iterator<Issue> removeIssuesIterator = issuesToRemove.iterator();
        while( removeIssuesIterator.hasNext() ) {
            Issue removeIssue = removeIssuesIterator.next();

            Iterator<Issue> localIssuesIterator = localIssues.iterator();
            while( localIssuesIterator.hasNext() ) {
                Issue issue = localIssuesIterator.next();
                if( removeIssue.getDocumentId().equals(issue.getDocumentId()) ) {
                    Log.i(TAG, "Removing issue " + removeIssue.getTitle() + " "
                            + removeIssue.getIssueNumber());

                    localIssuesIterator.remove();
                }
            }

        }

        /*
        Finally, sort the new list for presentation.
         */
        IssuesSorter issuesSorter = new IssuesSorter();
        localIssues = (ArrayList<Issue>) issuesSorter.modify(localIssues);
        return localIssues;
    }

}
