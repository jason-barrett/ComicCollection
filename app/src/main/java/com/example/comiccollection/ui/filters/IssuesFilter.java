package com.example.comiccollection.ui.filters;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.ui.IssuesToggleState;

import java.util.ArrayList;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

public class IssuesFilter {
    /*
    This method applies the global filters to the master set of issue data retrieved from the
    ViewModel.  It returns a filtered set of issue data to send to the adapter.
     */
    public @NonNull ArrayList<Issue> getFilteredIssueData(final IssuesToggleState state, final ArrayList<Issue> masterList) {
        ArrayList<Issue> filteredList = new ArrayList<Issue>();

        /*
        Filter the list.
         */
        if( !state.getShowWanted() && state.getShowOwned() ) {
            //I want to see my collection.
            filteredList = (ArrayList<Issue>)masterList.stream()
                    .filter(i -> i.numberOfCopiesOwned() > 0)
                    .collect(Collectors.toList());
        } else if( state.getShowWanted() && !state.getShowOwned()) {
            //I want to see my want list.
            filteredList = (ArrayList<Issue>)masterList.stream()
                    .filter(i -> i.isWanted())
                    .collect(Collectors.toList());
        } else if( !state.getShowWanted() && !state.getShowOwned() ) {
            //For some reason, I want to see issues I have no interest in.
            filteredList = (ArrayList<Issue>)masterList.stream()
                    .filter(i-> (i.numberOfCopiesOwned() == 0 && !i.isWanted()))
                    .collect(Collectors.toList());
        } else {
            // Just show everything.
            filteredList = (ArrayList<Issue>) new ArrayList<>(masterList);
        }
        return filteredList;
    }

}
