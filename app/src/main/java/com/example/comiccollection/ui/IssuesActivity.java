package com.example.comiccollection.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.application.ComicCollectionApplication;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.ui.filters.IssuesFilter;
import com.example.comiccollection.ui.selection.IssuesItemDetailsLookup;
import com.example.comiccollection.ui.selection.IssuesItemKeyProvider;
import com.example.comiccollection.viewmodel.IssuesViewModel;
import com.example.comiccollection.viewmodel.IssuesViewModelFactory;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

public class IssuesActivity extends AppCompatActivity
        implements ActionMode.Callback, IssuesAdapter.OnClickListener{

    @Inject
    IssuesViewModelFactory mIssuesViewModelFactory;

    private IssuesViewModel mIssuesViewModel;
    private RecyclerView mIssuesRecyclerView;
    private IssuesAdapter mIssuesAdapter;

    /*
    This banner at the top of the screen will show the user which set of issues is currently
    on display; i.e., all, want list, owned.
     */
    private TextView mBanner;

    /*
    This activity contains controls to move through the list, and to toggle whether to show
    issues owned or issues wanted.
     */
    private Button mRewindFarButton;
    private Button mRewindNearButton;
    private Button mForwardFarButton;
    private Button mForwardNearButton;

    /*
    This activity is showing the issues for a specific title.  This activity is always
    created by an intent, and the title is passed in the intent.
     */
    private String mTitle;

    /*
    Active filters are part of the global state of the activity.  These control which issues
    are shown and which are filtered out.
    */
    private IssuesToggleState mIssuesToggleState;
    private IssuesFilter mIssuesFilter;

    /*
    A selection tracker object (from the RecyclerView selection library) to keep the state of
    which items have been selected (for example, to mark them all owned).
     */
    private SelectionTracker<Long> selectionTracker;

    /*
    A handle to the ActionMode, which will replace the action bar with the ActionMode menu.
     */
    ActionMode actionMode;

    /*
    The number of Issues in the list to scroll forward (or back) when the near forward
    (or rewind) button on the screen is pressed.
     */
    private final int NEAR_SCROLL_ITEMS = 15;

    /*
    The number of Issues in the list to scroll forward (or back) when the far forward
    (or rewind) button on the screen is pressed.
     */
    private final int FAR_SCROLL_ITEMS = 50;


    private final String TAG = IssuesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        /*
        Set up the ViewModel which contains the LiveData that is the canonical
        source of current data.
         */
        ((ComicCollectionApplication)getApplication()).getAppComponent().inject(this);
        mIssuesViewModel = new ViewModelProvider(this, mIssuesViewModelFactory)
                .get(IssuesViewModel.class);

        /*
        Create and set up the RecyclerView and its Adapter.

        The Adapter will have stable IDs so that those IDs can be used as keys for
        multi-selection.
         */
        mIssuesAdapter = new IssuesAdapter(this);
        mIssuesAdapter.setHasStableIds(true);

        mIssuesRecyclerView = (RecyclerView)findViewById(R.id.rvIssues);
        mIssuesRecyclerView.setAdapter(mIssuesAdapter);
        mIssuesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIssuesRecyclerView.setHasFixedSize(true);

        /*
        Create a selection tracker to allow for multiple item selection.  This will be used,
        for example, when the user wants to mark issues as owned.

        The key type is Long.  We will use the adapter position as the key.
         */
        selectionTracker = new SelectionTracker.Builder<Long>(
                "IssueSelectionID",
                mIssuesRecyclerView,
                new IssuesItemKeyProvider(mIssuesRecyclerView),
                new IssuesItemDetailsLookup(mIssuesRecyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything())
                .build();

       selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
           @SuppressLint("RestrictedApi")
           @Override
           protected void onSelectionCleared() {
               super.onSelectionCleared();

               /*
               Nothing is selected, so dismiss the ActionMode.
                */
               actionMode.finish();
           }

           @Override
           public void onSelectionChanged() {
               super.onSelectionChanged();

               if( actionMode == null ) {
                   /*
                    If we are not currently showing the ActionMode, show it now.
                    */
                   actionMode = IssuesActivity.this.startSupportActionMode((ActionMode.Callback) IssuesActivity.this);
               }
           }
       });

        /*
        Now that I have the tracker, provide it to the recycler view's adapter.
         */
        mIssuesAdapter.setSelectionTracker(selectionTracker);

        /*
        Initialize the navigation buttons.
         */
        mRewindFarButton = (Button)findViewById(R.id.btnRewindFar);
        mRewindFarButton.setOnClickListener((v) -> scrollBackward(FAR_SCROLL_ITEMS));

        mRewindNearButton = (Button)findViewById(R.id.btnRewindNear);
        mRewindNearButton.setOnClickListener((v) -> scrollBackward(NEAR_SCROLL_ITEMS));

        mForwardFarButton = (Button)findViewById(R.id.btnForwardFar);
        mForwardFarButton.setOnClickListener((v) -> scrollForward(FAR_SCROLL_ITEMS));

        mForwardNearButton = (Button)findViewById(R.id.btnForwardNear);
        mForwardNearButton.setOnClickListener((v) -> scrollForward(NEAR_SCROLL_ITEMS));

        /*
        Initialize the toggle state which will be managed by the toggle buttons.
         */
        mIssuesFilter = new IssuesFilter();
        mIssuesToggleState = new IssuesToggleState();

        /*
        Set up the banner that shows what issue set is currently displayed.  The default is
        to show all issues.
         */
        mBanner = (TextView)findViewById(R.id.tvIssuesBanner);
        mBanner.setText(R.string.showing_all_issues);

        /*
        Observe the issue data for this title from the ViewModel.
         */
        final Observer<ArrayList<Issue>> issueObserver = new Observer<ArrayList<Issue>>() {
            @Override
            public void onChanged(ArrayList<Issue> issueList) {
                Log.d(TAG, "Issue list changed");
                for( Issue issue : issueList ) {
                    if( issue.getOwnedCopies() != null ) {
                        Log.d(TAG, issue.getOwnedCopies().size() + " copies owned of " + issue.getTitleAndIssueNumber());
                    }
                }

                /*
                Apply the toggle settings to filter the new data set for display.
                 */
                sendDataToAdapter();
            }
        };

        mIssuesViewModel.getIssuesList().observe(this, issueObserver);

        /*
        Which title is this Activity for?
         */
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("Title");
        if( mTitle == null || mTitle.isEmpty() ) {
            Log.e(TAG, "Started IssuesActivity with no Title");
            finish();
            return;
        }

        /*
        Perform the initial data load.
         */
        mIssuesViewModel.loadIssues(mTitle);

    }  /* onCreate() */

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    Present the user with a choice of preferences:
     - Show collection
     - Show want list
     - Show all issues
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.issues_pref_menu, menu);

        return true;
    }

    /*
    Handle the user's selection from the options menu.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch( item.getItemId() ) {
            /*
            The following three items form a group of radio buttons, which controls which
            set of issues are displayed:
               - The collection (issues owned)
               - The want list
               - All known issues, owned or not
             */
            case R.id.item_show_collection:
                mIssuesToggleState.setShowOwned(true);
                mIssuesToggleState.setShowWanted(false);

                item.setChecked(true);

                sendDataToAdapter();

                mBanner.setText(R.string.showing_collection);
                return true;

            case R.id.item_show_want_list:
                mIssuesToggleState.setShowOwned(false);
                mIssuesToggleState.setShowWanted(true);

                item.setChecked(true);

                sendDataToAdapter();

                mBanner.setText(R.string.showing_want_list);
                return true;

            case R.id.item_show_all_issues:
                mIssuesToggleState.setShowOwned(true);
                mIssuesToggleState.setShowWanted(true);

                item.setChecked(true);

                sendDataToAdapter();

                mBanner.setText(R.string.showing_all_issues);
                return true;

            default:
                Log.w(TAG, "User somehow selected an item not on the menu");
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     Prepare and send changed and/or filtered data to the adapter to be re-displayed.
     */
    private void sendDataToAdapter() {
        /*
        Filter the master list from the ViewModel, with the current toggle state.
         */
        ArrayList<Issue> filteredIssueList = mIssuesFilter.getFilteredIssueData(mIssuesToggleState,
                mIssuesViewModel.getIssuesList().getValue());

        /*
        Send the updated list to the adapter for display.
         */
        mIssuesAdapter.updateIssues(filteredIssueList);
        mIssuesAdapter.notifyDataSetChanged();
    }


    /*
    Scroll forward in the list visible on screen by the specified number of items.
     */
    private void scrollForward(int numItems) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)mIssuesRecyclerView
                .getLayoutManager();

        /*
        Make sure we're working with the current issue list, which may have been filtered.
        For example, we may be navigating through the want list only.
         */
        ArrayList<Issue> currentIssueList = (ArrayList<Issue>)mIssuesAdapter.getCurrentIssueList();

        /*
        I also want to know where the top view is *now*.  This will return me the adapter
        position, that is the index in the current data set of the current topmost view.
        */
        assert layoutManager != null;
        int firstPosition = layoutManager.findFirstVisibleItemPosition();

        /*
        Find the position we want to scroll to.  If we'd scroll off the end of the list,
        just stay where we are.  An enhancement might be to scroll so that the last item
        is at the bottom of the screen.
         */
        int positionToScrollTo = firstPosition + numItems;
        if( positionToScrollTo < currentIssueList.size() ) {
            layoutManager.scrollToPositionWithOffset(positionToScrollTo, 0);
        }
    }
    /*
    Scroll backward in the list visible on screen by the specified number of items.
     */
    private void scrollBackward(int numItems) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)mIssuesRecyclerView
                .getLayoutManager();

        /*
        I also want to know where the top view is *now*.  This will return me the adapter
        position, that is the index in the current data set of the current topmost view.
        */
        assert layoutManager != null;
        int firstPosition = layoutManager.findFirstVisibleItemPosition();

        /*
        Find the position we want to scroll to.  If the position is negative, just scroll to
        the top.
         */
        int positionToScrollTo = firstPosition - numItems;
        if( positionToScrollTo < 0 ) {
            positionToScrollTo = 0;
        }
        layoutManager.scrollToPositionWithOffset(positionToScrollTo, 0);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.issues_action_mode_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        Selection<Long> selection = selectionTracker.getSelection();
        Iterator<Long> iter = selection.iterator();

        switch( item.getItemId() ) {
            case R.id.mark_owned:
                /*
                Everything in the selection should be marked in the data store as owned.
                 */
                while(iter.hasNext()) {
                    /*
                    Get the Issue represented by this selected item.
                     */
                    Long selectionKey = iter.next();
                    Issue issue = mIssuesAdapter.getIssueAt(selectionKey.intValue());

                    /*
                    To mark the issue owned, there has to be an OwnedCopy.  We don't know
                    anything about the copy yet, but create a simple object to add to the
                    datastore to mark the fact that *a* copy is owned.

                    If there is an owned copy (or more) already, do nothing.
                     */
                    if( issue.numberOfCopiesOwned() == 0 ) {
                        ArrayList<OwnedCopy> markCopy = new ArrayList<OwnedCopy>();
                        markCopy.add(new OwnedCopy(issue.getTitle(), issue.getIssueNumber()));
                        issue.setOwnedCopies(markCopy);

                        /*
                        The copy needs to be explicitly added to the DB.
                         */
                        mIssuesViewModel.addOwnedCopyOfIssue(markCopy.get(0), issue);
                    }

                    /*
                     By default, if I own it, I don't want it.
                     */
                    issue.setWanted(false);
                    mIssuesViewModel.modifyIssue(issue);
                }

            case R.id.mark_wanted:
                /*
                Everything in the selection should be marked as 'wanted'.  If an issue is
                already on the want list, no harm.  If the issue is already owned, it will
                be interpreted as the user wants an upgrade.
                 */
                while(iter.hasNext()) {
                    /*
                    Get the Issue represented by this selected item.
                     */
                    Long selectionKey = iter.next();
                    Issue issue = mIssuesAdapter.getIssueAt(selectionKey.intValue());

                    issue.setWanted(true);
                    mIssuesViewModel.modifyIssue(issue);
                }

                return true;

            case R.id.dont_want:
                /*
                Everything in the selection should be marked as 'not wanted'.
                 */
                while(iter.hasNext()) {
                    /*
                    Get the Issue represented by this selected item.
                     */
                    Long selectionKey = iter.next();
                    Issue issue = mIssuesAdapter.getIssueAt(selectionKey.intValue());

                    issue.setWanted(false);
                    mIssuesViewModel.modifyIssue(issue);
                }
        }

        /*
        Destroy the ActionMode.  I've made a selection, so I'll get my normal screen
        mode back.
         */
        mode.finish();

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        /*
        Clear the selection when the ActionMode goes away.
         */
        selectionTracker.clearSelection();
        actionMode = null;
    }

    /*
    A click handler for items (issues) in the RecyclerView.  This will take the user to the
    copies view for that issue.
     */
    @Override
    public void onClick(String issueTitle, String issueNumber) {
        Intent intent = new Intent(this, CopiesActivity.class);
        intent.putExtra(getString(R.string.title_extra), issueTitle);
        intent.putExtra(getString(R.string.issue_extra), issueNumber);

        startActivity(intent);
    }
}
