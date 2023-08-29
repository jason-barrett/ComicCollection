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
    public void getIssuesByTitle(String titleName, IssuesListener issuesListener);
    public void addIssue(Issue issue);
    public void addIssuesBatch(ArrayList<Issue> issues);
    public void modifyIssue(Issue issue, IssuesListener issuesListener);
    public void deleteIssue(Issue issue, IssuesDeletionListener listener);
    public void deleteIssuesByTitle(Title title, IssuesDeletionListener listener);
    public void deleteIssuesByRange(Title title, int firstIssue, int lastIssue,
                                    IssuesDeletionListener listener);

    public void addCopyOfIssue(Copy copy, Issue issue, CopiesListener copiesListener);
    public void deleteCopy(Copy copy, Issue issue, CopiesDeletionListener copiesListener);
    public void modifyCopy(Copy copy, Issue issue, CopiesListener copiesListener);
    public void recordSaleOfCopy(Copy copy, Issue issue, CopiesListener copiesListener);
    public void purchaseCopy(Copy copy, Issue issue, CopiesListener copiesListener);
    public void addOfferToCopy(Copy copy, Copy.Offer newOffer, Issue issue, CopiesListener copiesListener);

    public void getCollectionStats(CollectionStatsListener collectionStatsListener);
}
