package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.data.entities.Title;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public interface ComicRepository {
    public void loadAndListenForTitles(TitlesListener titlesListener);
    public void addTitle(Title title);
    public void modifyTitle(Title title);
    public void deleteTitle(Title title, TitlesDeletionListener listener);

    public void getIssue(String issueTitle, String issueNumber, SingleIssueListener listener);
    public void getIssuesByTitleOnce(String titleName, IssuesListener issuesListener);
    public void getIssuesByTitleAndListen(String titleName, IssuesListener issuesListener);
    public void addIssue(Issue issue);
    public void addIssuesBatch(ArrayList<Issue> issues);
    public void modifyIssue(Issue issue, IssuesListener issuesListener);
    public void deleteIssue(Issue issue, IssuesDeletionListener listener);
    public void deleteIssuesByTitle(Title title, IssuesDeletionListener listener);

    public void addOwnedCopyOfIssue(OwnedCopy ownedCopy, Issue issue, IssuesListener issuesListener);
}
