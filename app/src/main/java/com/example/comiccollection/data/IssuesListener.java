package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Issue;

import java.util.List;

public interface IssuesListener {
    public void onIssuesReady(List<Issue> issues);
    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove);
    public void onIssueLoadFailed();
}
