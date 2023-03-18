package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;

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
    public void deleteIssuesByRange(Title title, int firstIssue, int lastIssue,
                                    IssuesDeletionListener listener);

    public void addCopyOfIssue(Copy ownedCopy, Issue issue, CopiesListener copiesListener);
    public void getCollectionStats(CollectionStatsListener collectionStatsListener);
}
