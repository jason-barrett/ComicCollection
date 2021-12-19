package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Issue;

public interface SingleIssueListener {
    public void onIssueReady(Issue issue);
    public void onIssueLoadFailed();
}
