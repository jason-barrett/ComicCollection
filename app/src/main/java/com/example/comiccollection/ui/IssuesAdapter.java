package com.example.comiccollection.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Issue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.comiccollection.R.string.upgrade_wanted;

public class IssuesAdapter extends RecyclerView.Adapter {

    public List<Issue> issueList;

    public IssuesAdapter() {
        issueList = new ArrayList<Issue>();
    }

    public void updateIssues(List<Issue> issueList) {
        this.issueList = issueList;
    }

    public String TAG = IssuesAdapter.class.getSimpleName();

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
        }

        /*
        TODO: Leaving value unimplemented for now.  Will add a calculator in a later build.
         */
        //issuesViewHolder.getValueView().setText("$0.00");
        Log.d(TAG, "OnBindViewHolder: done");
    }

    @Override
    public int getItemCount() {
        return issueList.size();
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
    }
}
