package com.example.comiccollection.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.comiccollection.R.string.upgrade_wanted;

public class IssuesAdapter extends RecyclerView.Adapter {

    /*
    The list of issues currently being displayed in the activity.
     */
    public List<Issue> issueList;

    public String TAG = IssuesAdapter.class.getSimpleName();

    /*
    The adapter needs to know about the selection tracker so it can set views that have
    been selected as 'activated' for display purposes.
     */
    private SelectionTracker<Long> selectionTracker = null;

    /*
    Construct and initialize the adapter with a given list of issues.
     */
    public IssuesAdapter() {
        issueList = new ArrayList<Issue>();
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    /*
    Provide a new list of issues to supersede the current list this adapter is
    managing.  For instance, the list may have been filtered to a smaller list, and
    the smaller list should be displayed.
     */
    public void updateIssues(List<Issue> issueList) {
        this.issueList = issueList;
    }

    public List<Issue> getCurrentIssueList() {
        return issueList;
    }

    public Issue getIssueAt(int position) {
        return issueList.get(position);
    }

    @NonNull
    @Override
    public IssuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_item_layout, parent, false);

        return new IssuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IssuesViewHolder issuesViewHolder = (IssuesViewHolder)holder;

        Issue issue = issueList.get(position);

        /*
        Concatenate the title and issue number.  This is the main part of the display.
         */
        String titleAndIssue = issue.getTitleAndIssueNumber();
        Log.d(TAG, "Binding to " + titleAndIssue + " at position " + position);
        issuesViewHolder.getTitleAndIssueView().setText(titleAndIssue);

        /*
        I want an upgrade if I own at least one copy, and the 'want it' flag is still
        set to true.
         */
        if( issue.upgradeWanted() ) {
            Log.d(TAG, "User wants upgrade of " + titleAndIssue);
            issuesViewHolder.getUpgradeView().setText(upgrade_wanted);
        } else {
            issuesViewHolder.getUpgradeView().setText("");
        }

        /*
        TODO: Leaving value unimplemented for now.  Will add a calculator in a later build.
         */
        //issuesViewHolder.getValueView().setText("$0.00");

        /*
        Set the activation status of the master item view according to whether or not it is
        currently selected.
         */
        issuesViewHolder.getItemView().setActivated(selectionTracker.isSelected((long) position));

        Log.d(TAG, "OnBindViewHolder: done");
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    /*
    Define the IssuesViewHolder class as an implementation of ViewHolder.
     */
    public class IssuesViewHolder extends RecyclerView.ViewHolder {

        /*
        The composite view that holds the layout that goes in each issue 'row'.
         */
        private View mIssuesItemView;

        /*
        The individual view elements of the layout.
         */
        private TextView titleAndIssueView;
        private TextView upgradeView;
        private TextView valueView;

        public IssuesViewHolder(View view) {
            super(view);
            mIssuesItemView = view;

            titleAndIssueView = (TextView) mIssuesItemView.findViewById(R.id.tvTitleAndIssue);
            upgradeView = (TextView) mIssuesItemView.findViewById(R.id.tvUpgrade);
            valueView = (TextView) mIssuesItemView.findViewById(R.id.tvValue);
        }

        public View getItemView() {
            return mIssuesItemView;
        }

        public TextView getTitleAndIssueView() {
            return titleAndIssueView;
        }

        public TextView getUpgradeView() {
            return upgradeView;
        }

        public TextView getValueView() {
            return valueView;
        }

        /*
        This method returns an object that the system can use to get details on the view
        that a user pressed or tapped, to start or continue a multiple-item selection.
         */
        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return IssuesViewHolder.this.getAbsoluteAdapterPosition();
                }

               @Override
                public Long getSelectionKey() {
                    return IssuesAdapter.this.getItemId(getPosition());
                }
            };
        }
    }
}
