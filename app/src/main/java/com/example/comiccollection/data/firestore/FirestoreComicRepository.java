package com.example.comiccollection.data.firestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.ComicRepository;
import com.example.comiccollection.data.IssuesDeletionListener;
import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.TitlesDeletionListener;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.data.entities.SoldCopy;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.data.entities.UnownedCopy;
import com.example.comiccollection.data.firestore.map.IssuesMapper;
import com.example.comiccollection.data.firestore.map.OwnedCopiesMapper;
import com.example.comiccollection.data.firestore.map.SoldCopiesMapper;
import com.example.comiccollection.data.firestore.map.TitlesMapper;
import com.example.comiccollection.data.firestore.map.UnownedCopiesMapper;
import com.example.comiccollection.data.modifiers.IssuesModifier;
import com.example.comiccollection.data.modifiers.IssuesSorter;
import com.example.comiccollection.data.modifiers.TitlesModifier;
import com.example.comiccollection.data.modifiers.TitlesSorter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

public class FirestoreComicRepository implements ComicRepository {
    /*
    This class manages access to the Firestore data.
     */
    private String TAG = this.getClass().getSimpleName();

    private static FirestoreComicRepository mInstance = null;

    private FirebaseFirestore db;

    @Singleton
    private FirestoreComicRepository() {
        /*
        Firestore knows the details of the database to fetch because of the configuration
        in the google-services.json config file in the app directory.
         */
    }

    @Inject
    public FirestoreComicRepository(FirebaseFirestore db) {
        this.db = db;
    }

    /*
    Each entity (e.g., titles) may allow some modifiers containing back-end business logic to
    be applied to them after the entities have been retrieved from the database.

    For example, the titles may be sorted alphabetically or in some custom way.
     */
    private List<TitlesModifier> titlesModifiers = new ArrayList<TitlesModifier>();
    private List<IssuesModifier> issuesModifiers = new ArrayList<IssuesModifier>();

    /*
    Store the registration for the titles query that comes back from the FirebaseFirestore
    class.  We may need to recycle the query on an error.
     */
    private ListenerRegistration titlesRegistration;

    /*
    We are going to listen for issues of only one title at a given time.  That means that when
    we're asked to get issues of a title, we're going to keep track of this registration so
    we can close the previous title and open a new one.
     */
    private ListenerRegistration issuesRegistration;

    /*
    The Firestore repository is a singleton.  Return the existing instance to the requestor.

    TODO: I should no longer need this once I've tagged it as a singleton in Dagger.
     */
    public synchronized static FirestoreComicRepository getInstance() {
        if( mInstance == null ) {
            mInstance = new FirestoreComicRepository();
        }

        return mInstance;
    }

    @Override
    public ListenerRegistration loadAndListenForTitles(final TitlesListener titlesListener) {

        /*
        Register modifiers to implement the business logic.

         - Sort the titles alphabetically.
         */
        titlesModifiers.add(new TitlesSorter());

        /*
        Define a listener to be called on any changes to the collection (including the initial
        load).
         */
        titlesRegistration = db.collection(ComicDbHelper.CC_COLLECTION_TITLE).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    /*
                    Note this onEvent() handler will be scheduled and run on the UI thread.
                     */
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        /*
                        Check for exception, otherwise I can assume I got updates.
                         */
                        if( e != null ) {
                            Log.e(TAG, "Error fetching " + ComicDbHelper.CC_COLLECTION_TITLE
                                    + ": " + e);

                            titlesListener.onTitleLoadFailed();

                            return;
                        }

                        /*
                        Check for a null snapshot.
                         */
                        if( value == null ) {
                            Log.e(TAG, "Got a null query snapshot, back end connection may have failed.");
                            titlesListener.onTitleLoadFailed();

                            return;
                        }

                        /*
                        We're good.  Map the query result into a list to send back.
                         */
                        Log.i(TAG, "Fetch collection " + ComicDbHelper.CC_COLLECTION_TITLE
                                + " SUCCESSFUL");
                        List<Title> titles = TitlesMapper.map(value);

                        /*
                        Before sending the list back, apply any registered business logic methods.
                        */
                        if( titlesModifiers != null ) {
                            for (TitlesModifier m : titlesModifiers) {
                                Log.i(TAG, "Running logic in " + m.getClass().getSimpleName());
                                titles = m.modify((ArrayList<Title>) titles);
                            }
                        }

                        /*
                        The list goes back to the ViewModel layer.
                         */
                        titlesListener.onTitlesReady(titles);

                    } // on Event()
                } // new EventListener()
        ); // addSnapshotListener()

        return titlesRegistration;
    }

    @Override
    public void addTitle(Title title) {

        /*
        Specify the title in a HashMap.  Firestore will auto-generate a document ID.
         */
        Map<String, Object> newTitle = new HashMap();
        newTitle.put(ComicDbHelper.CC_TITLE_NAME, title.getName());
        newTitle.put(ComicDbHelper.CC_TITLE_FIRST_ISSUE, title.getFirstIssue());
        newTitle.put(ComicDbHelper.CC_TITLE_LAST_ISSUE, title.getLastIssue());

        db.collection(ComicDbHelper.CC_COLLECTION_TITLE).add(newTitle)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i(TAG, "Loaded new title " + title.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to load title " + title.getName() + e.toString());

                //TODO: Refactor to propagate this error back to the screen / user.
            }
        }); // new OnFailureListener
    }

    @Override
    public void modifyTitle(Title title) {
        Log.i(TAG, "Modifying title" + title.getName());

        /*
        I could probably do this with a delete-and-add.  But this way preserves the document ID
        in Firestore, which at this point is practically probably unimportant, but as the
        product evolves, could matter.
         */

        Map<String, Object> newTitle = new HashMap();
        newTitle.put(ComicDbHelper.CC_TITLE_NAME, title.getName());
        newTitle.put(ComicDbHelper.CC_TITLE_FIRST_ISSUE, title.getFirstIssue());
        newTitle.put(ComicDbHelper.CC_TITLE_LAST_ISSUE, title.getLastIssue());

        db.collection(ComicDbHelper.CC_COLLECTION_TITLE).document(title.getDocumentId()).set(newTitle)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Modified title " + title.getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to modify title " + title.getName() + e.toString());

                //TODO: Refactor to propagate this error back to the screen / user.
            }
        });
    }

    @Override
    public void deleteTitle(Title title, TitlesDeletionListener listener) {
        Log.i(TAG, "Deleting title " + title.getName());

        db.collection(ComicDbHelper.CC_COLLECTION_TITLE).document(title.getDocumentId()).delete();

        /*
        Also delete all issues belonging to that title.

        The second argument is a little syntactic sugar that replaces the IssueDeletionListener
        argument with a method reference.
         */
        deleteIssuesByTitle(title, listener::onDeleteFailed);
    }

    @Override
    public void getIssuesByTitleOnce(String titleName, final IssuesListener issuesListener) {
        /*
        This is used for an initial load of all issues, including all associated copies.

        Because Firestore only allows shallow queries, I need separate queries to fetch the
        list of issues and all different types of copies.

        For the initial render of an issues list, I want all of this information loaded and
        combined.  After that, it is OK for individual updates for issues or associated data
        to arrive one-by-one.

        The Task API is used for asynchronous method calls, like the call to get() to fetch
        results from Firestore.
         */

        /*
        Register modifiers to implement the business logic.

         - Sort the titles alphabetically.
         */
        issuesModifiers.add(new IssuesSorter());


        /*
        Set a Task to query for the issues.
         */
        Task<QuerySnapshot> issuesTask = db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                .whereEqualTo(ComicDbHelper.CC_ISSUE_TITLE, titleName)
                .get();

        /*
        Set a task to query for owned copies.
         */
        Task<QuerySnapshot> ownedCopiesTask = db.collectionGroup(ComicDbHelper.CC_ISSUE_OWNED)
                .whereEqualTo(ComicDbHelper.CC_COPY_TITLE, titleName)
                .get();

        /*
        Set a task to query for unowned copies.
         */
        Task<QuerySnapshot> unownedCopiesTask = db.collectionGroup(ComicDbHelper.CC_ISSUE_UNOWNED)
                .whereEqualTo(ComicDbHelper.CC_COPY_TITLE, titleName)
                .get();

        /*
        Set a task to query for sold copies.
         */
        Task<QuerySnapshot> soldCopiesTask = db.collectionGroup(ComicDbHelper.CC_ISSUE_SOLD)
                .whereEqualTo(ComicDbHelper.CC_COPY_TITLE, titleName)
                .get();

        /*
        Set up a listener to be called when all tasks have completed.
         */
        Tasks.whenAllComplete(issuesTask, ownedCopiesTask, unownedCopiesTask, soldCopiesTask)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        /*
                        The task will be successful only if ALL of the passed-in tasks
                        succeeded.
                         */
                        Log.i(TAG, "All copies of all issues found.");
                        if( task.isSuccessful() ) {
                            List<Task<?>> taskList = task.getResult();

                            /*
                            I *think* I can count on the completed tasks being in the same
                            order in the List as I passed them into whenAllComplete().
                             */
                            Task<?> issuesTask = taskList.get(0);
                            QuerySnapshot issuesQueryResult =
                                    (QuerySnapshot) issuesTask.getResult();

                            Task<?> ownedCopiesTask = taskList.get(1);
                            QuerySnapshot ownedCopiesQueryResult =
                                    (QuerySnapshot) ownedCopiesTask.getResult();

                            Task<?> unownedCopiesTask = taskList.get(2);
                            QuerySnapshot unownedCopiesQueryResult =
                                    (QuerySnapshot) unownedCopiesTask.getResult();

                            Task<?> soldCopiesTask = taskList.get(3);
                            QuerySnapshot soldCopiesQueryResult =
                                    (QuerySnapshot) soldCopiesTask.getResult();

                            /*
                            First map all of the issues.
                             */
                            List<Issue> issuesList = IssuesMapper.map(issuesQueryResult);

                            /*
                            Now we need to 'bolt on' the Copies information in the collection
                            group queries, to the Issues to which they belong.

                            TODO: Save some code by genericizing this or at least pulling more
                            into the mappers or other helper classes.
                             */

                            // Owned copies
                            List<OwnedCopy> ownedCopiesList =
                                    OwnedCopiesMapper.map(ownedCopiesQueryResult);

                            Map<String, List<OwnedCopy>> ownedCopiesByIssue =
                                    new HashMap<String, List<OwnedCopy>>();

                            for( OwnedCopy ownedCopy : ownedCopiesList ) {
                                Log.d(TAG, "Found an owned copy of " +
                                        ownedCopy.getTitle() + " " + ownedCopy.getIssue());

                                String issueOfCopy = ownedCopy.getIssue();

                                List<OwnedCopy> subList;
                                if( (subList = ownedCopiesByIssue.get(issueOfCopy)) == null ) {
                                    subList = new ArrayList<OwnedCopy>();
                                    ownedCopiesByIssue.put(issueOfCopy, subList);
                                }

                                subList.add(ownedCopy);
                            }

                            // Unowned copies
                            List<UnownedCopy> unownedCopiesList =
                                    UnownedCopiesMapper.map(unownedCopiesQueryResult);

                            Map<String, List<UnownedCopy>> unownedCopiesByIssue =
                                    new HashMap<String, List<UnownedCopy>>();

                            for( UnownedCopy unownedCopy : unownedCopiesList ) {
                                String issueOfCopy = unownedCopy.getIssue();

                                List<UnownedCopy> subList;
                                if( (subList = unownedCopiesByIssue.get(issueOfCopy)) == null ) {
                                    subList = new ArrayList<UnownedCopy>();
                                    unownedCopiesByIssue.put(issueOfCopy, subList);
                                }

                                subList.add(unownedCopy);
                            }

                            // Sold copies
                            List<SoldCopy> soldCopiesList =
                                    SoldCopiesMapper.map(soldCopiesQueryResult);

                            Map<String, List<SoldCopy>> soldCopiesByIssue =
                                    new HashMap<String, List<SoldCopy>>();

                            for( SoldCopy soldCopy : soldCopiesList ) {
                                String issueOfCopy = soldCopy.getIssue();

                                List<SoldCopy> subList;
                                if( (subList = soldCopiesByIssue.get(issueOfCopy)) == null ) {
                                    subList = new ArrayList<SoldCopy>();
                                    soldCopiesByIssue.put(issueOfCopy, subList);
                                }

                                subList.add(soldCopy);
                            }

                            for( Issue issue : issuesList ) {
                                issue.setOwnedCopies((ArrayList<OwnedCopy>)ownedCopiesByIssue.get(issue.getIssueNumber()));
                                issue.setUnownedCopies((ArrayList<UnownedCopy>)unownedCopiesByIssue.get(issue.getIssueNumber()));
                                issue.setSoldCopies((ArrayList<SoldCopy>)soldCopiesByIssue.get(issue.getIssueNumber()));
                            }

                            /*
                            Before sending the list back, apply any registered business logic methods.
                            */
                            if( issuesModifiers != null ) {
                                for (IssuesModifier m : issuesModifiers) {
                                    Log.i(TAG, "Running logic in " + m.getClass().getSimpleName());
                                    issuesList = m.modify((ArrayList<Issue>) issuesList);
                                }
                            }

                            issuesListener.onIssuesReady(issuesList);

                        } else {
                            Log.e(TAG, "Failed to gather issues information");
                        }
                    }
                });

    }

    @Override
    public void getIssuesByTitleAndListen(String titleName, final IssuesListener issuesListener) {

        /*
        Un-register any previously registered listener (e.g., for issues of a different title).
         */
        if( issuesRegistration != null ) {
            issuesRegistration.remove();
        }

        /*
        Get the issues for 'titleName' and register a listener for updates.
         */
        issuesRegistration = db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                .whereEqualTo(ComicDbHelper.CC_ISSUE_TITLE, titleName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        /*
                        Check for exception, otherwise I can assume I got updates.
                         */
                        if( error != null ) {
                            Log.e(TAG, "Error fetching " + ComicDbHelper.CC_COLLECTION_ISSUES
                                    + " for title " + titleName + ": " + error);

                            issuesListener.onIssueLoadFailed();
                            /*
                            The Firestore documentation (https://firebase.google.com/docs/firestore/query-data/listen)
                            says, "After an error, the listener will not receive any more events..."

                            TODO: May need to restart the listener here.
                             */
                            return;
                        }

                        /*
                        Check for a null snapshot.
                         */
                        if( value == null ) {
                            Log.e(TAG, "Got a null query snapshot, back end connection may have failed.");
                            issuesListener.onIssueLoadFailed();

                            return;
                        }

                        /*
                        We're good.  Map the query result into a list to send back.
                         */ 
                        Log.i(TAG, "Fetch collection " + ComicDbHelper.CC_COLLECTION_ISSUES
                                + " updates for title " + titleName + " SUCCESSFUL");

                        List<Issue> issuesToAddOrReplace = new ArrayList<>();
                        List<Issue> issuesToRemove = new ArrayList<>();

                        for(DocumentChange dc : value.getDocumentChanges() ) {

                            Issue issue = IssuesMapper.map(dc.getDocument());

                            switch( dc.getType() ) {
                                case ADDED:
                                case MODIFIED:
                                    if( issue != null ) {
                                        issuesToAddOrReplace.add(issue);
                                    }
                                    break;

                                case REMOVED:
                                    if( issue != null ) {
                                        issuesToRemove.add(issue);
                                    }
                                    break;
                            }
                        }  // for( each document change )

                        issuesListener.onIssueChangesReady(issuesToAddOrReplace, issuesToRemove);
                    }
                });

    } // getIssuesByTitleAndListen()

    @Override
    public void addIssue(Issue issue) {
        Map<String, Object> newIssue = new HashMap<String, Object>();

        newIssue.put(ComicDbHelper.CC_ISSUE_TITLE, issue.getTitle());
        newIssue.put(ComicDbHelper.CC_ISSUE_NUMBER, issue.getIssueNumber());
        newIssue.put(ComicDbHelper.CC_ISSUE_WANTED, issue.isWanted());

        db.collection(ComicDbHelper.CC_COLLECTION_ISSUES).add(newIssue).addOnCompleteListener(
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.i(TAG, "Successfully added issue " + issue.getIssueNumber()
                                + " of title " + issue.getTitle());
                    }
                })
                .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add issue " + issue.getIssueNumber()
                                + " of title " + issue.getTitle());

                        //TODO: Refactor to propagate this error back to the screen / user.
                    }
                }
        );
    }

    @Override
    public void addIssuesBatch(ArrayList<Issue> issues) {
        /*
        Create a batch to do a bulk add of all of the issues in the list.

        Note: Google recommends doing bulk adds using a server client library, which would allow
        me to write Java code to do parallel writes on the server.  I can do this with a Google
        Cloud account, which I think is free.  This is a potential enhancement.
         */
        WriteBatch batch = db.batch();

        /*
        Add all of the issues to the batch.  There is a limit of 500 writes per batch, which
        would be impractical anyway for this use case.
         */
        for( Issue issue : issues ) {
            DocumentReference docRef = db.collection("issues").document();

            HashMap<String, Object> thisIssue = new HashMap<String, Object>();
            thisIssue.put(ComicDbHelper.CC_ISSUE_TITLE, issue.getTitle());
            thisIssue.put(ComicDbHelper.CC_ISSUE_NUMBER, issue.getIssueNumber());
            thisIssue.put(ComicDbHelper.CC_ISSUE_WANTED, issue.isWanted());

            batch.set(docRef, thisIssue);
        }

        /*
        Commit the batch.
         */
        batch.commit().addOnCompleteListener((t) -> Log.i(TAG, "Batch add of issues completed successfully"))
                .addOnFailureListener((t) -> Log.e(TAG, "Failed to add batch of issues"));
    }

    @Override
    public void modifyIssue(Issue issue) {
        Map<String, Object> newIssue = new HashMap<String, Object>();

        newIssue.put(ComicDbHelper.CC_ISSUE_TITLE, issue.getTitle());
        newIssue.put(ComicDbHelper.CC_ISSUE_NUMBER, issue.getIssueNumber());
        newIssue.put(ComicDbHelper.CC_ISSUE_WANTED, issue.isWanted());

        db.collection(ComicDbHelper.CC_COLLECTION_ISSUES).document(issue.getDocumentId())
                .set(newIssue).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void aVoid) {
                        Log.i(TAG, "Successfully modified issue " + issue.getIssueNumber()
                                + " of title " + issue.getTitle());
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to modify issue " + issue.getIssueNumber()
                                        + " of title " + issue.getTitle());

                                //TODO: Refactor to propagate this error back to the screen / user.
                            }
                        }
                );

    }

    @Override
    public void deleteIssue(Issue issue, IssuesDeletionListener listener) {

        Log.i(TAG, "Deleting issue " + issue.getIssueNumber() + " of title " + issue.getTitle());

        /*
        Deleting a document in Firestore does not delete its sub-collections.  We have to delete
        them first.

        The right way to do this is NOT through a mobile app, but 'server-side' using a Cloud
        Function, which can be called from a mobile app or better yet, from an OnDelete trigger.

        However, Cloud Functions are not available in the free Firestore product.  :-(
        */
        List<OwnedCopy> ownedCopies;
        if( (ownedCopies = issue.getOwnedCopies()) != null ) {
            for( OwnedCopy ownedCopy : ownedCopies ) {
                db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                        .document(issue.getDocumentId())
                        .collection(ComicDbHelper.CC_ISSUE_OWNED)
                        .document(ownedCopy.getDocumentId())
                        .delete()
                        .addOnFailureListener(e -> listener.onDeleteFailed(e.getMessage())
                        );
            }
        }

        List<UnownedCopy> unownedCopies;
        if( (unownedCopies = issue.getUnownedCopies()) != null ) {
            for( UnownedCopy unownedCopy : unownedCopies ) {
                db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                        .document(issue.getDocumentId())
                        .collection(ComicDbHelper.CC_ISSUE_UNOWNED)
                        .document(unownedCopy.getDocumentId())
                        .delete()
                        .addOnFailureListener(e -> listener.onDeleteFailed(e.getMessage()));
            }
        }

        List<SoldCopy> soldCopies;
        if( (soldCopies = issue.getSoldCopies()) != null ) {
            for( SoldCopy soldCopy : soldCopies ) {
                db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                        .document(issue.getDocumentId())
                        .collection(ComicDbHelper.CC_ISSUE_SOLD)
                        .document(soldCopy.getDocumentId())
                        .delete()
                        .addOnFailureListener(e -> listener.onDeleteFailed(e.getMessage()));
            }
        }

        /*
        Now all of the sub-collections are gone, we can go ahead and delete the issue
        itself.
         */
        db.collection(ComicDbHelper.CC_COLLECTION_ISSUES)
                .document(issue.getDocumentId())
                .delete()
                .addOnFailureListener(e -> listener.onDeleteFailed(e.getMessage()));
    }

    /*
    This method deletes all issues for a given title.  It propagates an error message from
    Firestore through the IssuesDeletionListener.
     */
    public void deleteIssuesByTitle(Title title, IssuesDeletionListener listener) {
        getIssuesByTitleOnce(title.getName(), new IssuesListener() {
            @Override
            public void onIssuesReady(List<Issue> issues) {
                for( Issue issue : issues ) {
                    deleteIssue(issue, message -> {
                        listener.onDeleteFailed(message);
                    });
                }
            }

            @Override
            public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {

            }

            @Override
            public void onIssueLoadFailed() {
                listener.onDeleteFailed("Could not load issues for deletion for title " + title.getName());
            }
        });
    }

}
