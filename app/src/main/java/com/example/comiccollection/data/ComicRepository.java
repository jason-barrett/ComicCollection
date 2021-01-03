package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public interface ComicRepository {
    public ListenerRegistration loadAndListenForTitles(TitlesListener onTitlesReady);
    public void addTitle(Title title);
    public void modifyTitle(Title title);
    public void deleteTitle(Title title);


    public void getIssuesByTitleOnce(String titleName);
    public void getIssuesByTitleAndListen(String titleName, IssuesListener onIssuesReady);
    public void addIssue(Issue issue);
    public void modifyIssue(Issue issue);
    public void deleteIssue(Issue issue);
}
