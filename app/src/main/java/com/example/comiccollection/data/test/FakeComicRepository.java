package com.example.comiccollection.data.test;

import android.util.Log;

import com.example.comiccollection.data.CollectionStats;
import com.example.comiccollection.data.CollectionStatsListener;
import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.IssuesDeletionListener;
import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.SingleIssueListener;
import com.example.comiccollection.data.TitlesDeletionListener;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.data.entities.SoldCopy;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.data.entities.UnownedCopy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class FakeComicRepository implements ComicRepository {

    /*
    This fake implements the ComicRepository interface for testing, using in-memory array lists
    instead of real database collections.

    It does not mimic the push of data available through Firestore.
     */

    private final List<Title> fakeTitles;

    private final List<Issue> fakeIssues;

    /*
    This class needs to provide fake but unique document IDs for everything it adds.  It does
    not matter what the type of document is.
     */
    private static AtomicInteger currentDocumentID;

    private String TAG = this.getClass().getCanonicalName();

    private FakeComicRepository()
    {
        fakeTitles = new ArrayList<Title>();
        fakeIssues = new ArrayList<Issue>();

        /*
        Initialize the document ID.
         */
        currentDocumentID = new AtomicInteger(1);
    }

    /*
    I want this to be a singleton so I can manipulate it through the system under test as well
    as directly in the test code.
     */
    public static FakeComicRepository thisInstance;

    public static FakeComicRepository getInstance() {
        if( thisInstance == null ) {
            thisInstance = new FakeComicRepository();
        }
        return thisInstance;
    }


    @Override
    public void loadAndListenForTitles(TitlesListener titlesListener) {
        titlesListener.onTitlesReady(fakeTitles);
    }

    @Override
    public void addTitle(Title title) {
        title.setDocumentId(String.valueOf(currentDocumentID.getAndIncrement()));
        fakeTitles.add(title);
    }

    @Override
    public void modifyTitle(Title title) {
        /*
        The reduce() in this stream is making sure that I don't somehow have multiple titles with
        the same document ID.
         */
        Optional<Title> thisTitle = fakeTitles.stream()
                .filter((t) -> t.getDocumentId().equals(title.getDocumentId()))
                .reduce((a, b) -> { throw new IllegalStateException("ERROR: Multiple titles with same document ID"); });

        if( thisTitle.isPresent() ) {
            thisTitle.get().setName(title.getName());
            thisTitle.get().setFirstIssue(title.getFirstIssue());
            thisTitle.get().setLastIssue(title.getLastIssue());
        }
    }

    @Override
    public void deleteTitle(Title title, TitlesDeletionListener listener) {
        fakeTitles.removeIf((t) -> t.getDocumentId().equals(title.getDocumentId()));
    }

    @Override
    public void getIssue(String issueTitle, String issueNumber, SingleIssueListener listener) {
        Optional<Issue> thisIssue = fakeIssues.stream()
                .filter((i) -> i.getTitle().equals(issueTitle) && i.getIssueNumber().equals(issueNumber))
                .reduce((a,b) -> { throw new IllegalStateException("ERROR: Multiple issues with same title and number"); });

        if( thisIssue.isPresent() ) {
            listener.onIssueReady(thisIssue.get());
        } else {
            listener.onIssueLoadFailed();
        }
    }

    @Override
    public void getIssuesByTitleOnce(String titleName, IssuesListener issuesListener) {
        List<Issue> theseIssues = fakeIssues.stream()
                .filter((i) -> i.getTitle().equals(titleName))
                .collect(Collectors.toList());

        issuesListener.onIssuesReady(theseIssues);
    }

    @Override
    public void getIssuesByTitleAndListen(String titleName, IssuesListener issuesListener) {
        /*
        This is a fake for testing purposes.  We are not going to do any asynchronous
        pushing of new or changed issues from here.
         */
        getIssuesByTitleOnce(titleName, issuesListener);
    }

    @Override
    public void addIssue(Issue issue) {
        issue.setDocumentId(String.valueOf(currentDocumentID.getAndIncrement()));
        fakeIssues.add(issue);
    }

    @Override
    public void addIssuesBatch(ArrayList<Issue> issues) {
        for( Issue i : issues ) {
            i.setDocumentId(String.valueOf(currentDocumentID.getAndIncrement()));
        }
        fakeIssues.addAll(issues);
    }

    @Override
    public void modifyIssue(Issue issue, IssuesListener issuesListener) {
        /*
        The reduce() in this stream is making sure that I don't somehow have multiple issues with
        the same document ID.
         */
        Optional<Issue> thisIssue = fakeIssues.stream()
                .filter((i) -> i.getDocumentId().equals(issue.getDocumentId()))
                .reduce((a, b) -> { throw new IllegalStateException("ERROR: Multiple issues with same document ID"); });

        if( thisIssue.isPresent() ) {
            thisIssue.get().setTitle(issue.getTitle());
            thisIssue.get().setIssueNumber(issue.getIssueNumber());
            thisIssue.get().setWanted(issue.isWanted());
        }
    }

    @Override
    public void deleteIssue(Issue issue, IssuesDeletionListener listener) {
        fakeIssues.removeIf((i) -> i.getDocumentId().equals(issue.getDocumentId()));
    }

    @Override
    public void deleteIssuesByTitle(Title title, IssuesDeletionListener listener) {
        fakeIssues.removeIf((i) -> i.getTitle().equals(title.getName()));
    }

    @Override
    public void deleteIssuesByRange(Title title, int firstIssue, int lastIssue, IssuesDeletionListener listener) {
        Log.i(TAG, "Deleting issues between " + firstIssue + " and " + lastIssue);
        fakeIssues.removeIf((i) -> {
            Log.i(TAG, "Consider " + i.getIssueNumber() + " for removal");
            return i.getTitle().equals(title.getName()) && i.isInRange(firstIssue, lastIssue);
        });
    }

    @Override
    public void addOwnedCopyOfIssue(OwnedCopy ownedCopy, Issue issue, IssuesListener issuesListener) {

        /*
        This method will return an empty list (not null) if there are no owned copies.
         */
        ArrayList<OwnedCopy> ownedCopies = issue.getOwnedCopies();
        ownedCopies.add(ownedCopy);
        issue.setOwnedCopies(ownedCopies);
    }

    @Override
    public void getCollectionStats(CollectionStatsListener collectionStatsListener) {
        int totalIssues = (int) fakeIssues.stream()
                .flatMap((i) -> i.getOwnedCopies().stream())
                .count();

        double totalValue = fakeIssues.stream()
                .flatMap((i) -> i.getOwnedCopies().stream())
                .map(OwnedCopy::getValue)
                .reduce(0.0, Double::sum);

        collectionStatsListener.onCollectionStatsReady(new CollectionStats(totalIssues, totalValue));
    }
}
