package com.example.comiccollection.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Application;
import android.util.Log;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Issue;
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
    This activity contains controls to move through the list, and to toggle whether to show
    issues owned or issues wanted.
     */
    private Button mRewindFarButton;
    private Button mRewindNearButton;
    private Button mForwardFarButton;
    private Button mForwardNearButton;

    private ToggleButton mOwnedButton;
    private ToggleButton mWantedButton;

    /*
    This activity is showing the issues for a specific title.  This activity is always
    created by an intent, and the title is passed in the intent.
     */
    private String mTitle;

    private final String TAG = TitlesActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        /*
        Set up the ViewModel which contains the LiveData that is the canonical
        source of current data.  Become an observer of the Issues data.
         */
        AppContainer appContainer = ((ComicCollectionApplication)getApplication()).getAppContainer();
        /* appContainer.initialize() only gets called in the first activity */
        IssuesViewModelFactory issuesViewModelFactory = appContainer.issuesViewModelFactory;
        mIssuesViewModel = issuesViewModelFactory.create(IssuesViewModel.class);

        /*
        Create the RecyclerView.
         */
        mIssuesAdapter = new IssuesAdapter();

        mIssuesRecyclerView = (RecyclerView)findViewById(R.id.rvIssues);
        mIssuesRecyclerView.setAdapter(mIssuesAdapter);
        mIssuesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIssuesRecyclerView.setHasFixedSize(true);

        /*
        Initialize the member objects for other controls.
         */
        mRewindFarButton = (Button)findViewById(R.id.btnRewindFar);
        mRewindNearButton = (Button)findViewById(R.id.btnRewindNear);
        mForwardFarButton = (Button)findViewById(R.id.btnForwardFar);
        mForwardNearButton = (Button)findViewById(R.id.btnForwardNear);

        mOwnedButton = (ToggleButton)findViewById(R.id.tBtnOwned);
        mWantedButton = (ToggleButton)findViewById(R.id.tBtnWanted);

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
        Observe the issue data for this title from the ViewModel.
         */
        final Observer<ArrayList<Issue>> issueObserver = new Observer<ArrayList<Issue>>() {
            @Override
            public void onChanged(ArrayList<Issue> issueList) {
                mIssuesAdapter.updateIssues(issueList);
                mIssuesAdapter.notifyDataSetChanged();
            }
        };

        mIssuesViewModel.getIssuesList().observe(this, issueObserver);

        /*
        Perform the initial data load.
         */
        mIssuesViewModel.loadIssues(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
