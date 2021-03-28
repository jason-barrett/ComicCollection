package com.example.comiccollection.ui;

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

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.IssuesViewHolder> {

    public List<Issue> issueList;

    public IssuesAdapter() {
        issueList = new ArrayList<Issue>();
    }

    public void updateIssues(List<Issue> issueList) {
        this.issueList = issueList;
    }

    @NonNull
    @Override
    public IssuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_item_layout, parent, false);

        return new IssuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesViewHolder holder, int position) {
        IssuesViewHolder issuesViewHolder = holder;

        Issue issue = issueList.get(position);

        /*
        Concatenate the title and issue number.  This is the main part of the display.
         */
        String titleAndIssue = issue.getTitle() + " " + issue.getIssueNumber();
        issuesViewHolder.getTitleAndIssueView().setText(titleAndIssue);

        /*
        I want an upgrade if I own at least one copy, and the 'want it' flag is still
        set to true.
         */
        if( issue.numberOfCopiesOwned() > 0 && issue.isWanted() ) {
            issuesViewHolder.getUpgradeView().setText(R.string.upgrade_wanted);
        }

        /*
        TODO: Leaving value unimplemented for now.  Will add a calculator in a later build.
         */
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