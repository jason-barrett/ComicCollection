package com.example.comiccollection.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.ui.filters.IssuesFilter;
import com.example.comiccollection.viewmodel.IssuesViewModel;
import com.example.comiccollection.viewmodel.IssuesViewModelFactory;
import com.example.comiccollection.application.AppContainer;
import com.example.comiccollection.application.ComicCollectionApplication;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IssuesActivity extends AppCompatActivity {

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
        AppContainer appContainer = ((ComicCollectionApplication)getApplication()).getAppContainer();
        /* appContainer.initialize() only gets called in the first activity */
        IssuesViewModelFactory issuesViewModelFactory = appContainer.issuesViewModelFactory;
        mIssuesViewModel = issuesViewModelFactory.create(IssuesViewModel.class);

        /*
        Create and set up the RecyclerView.
         */
        mIssuesAdapter = new IssuesAdapter();

        mIssuesRecyclerView = (RecyclerView)findViewById(R.id.rvIssues);
        mIssuesRecyclerView.setAdapter(mIssuesAdapter);
        mIssuesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIssuesRecyclerView.setHasFixedSize(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.issues_pref_menu, menu);

        return true;
    }

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

}
