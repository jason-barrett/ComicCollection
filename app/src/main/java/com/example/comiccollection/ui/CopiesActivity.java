package com.example.comiccollection.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.comiccollection.R;
import com.example.comiccollection.application.ComicCollectionApplication;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.viewmodel.CopiesViewModel;
import com.example.comiccollection.viewmodel.CopiesViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

/*
This Activity will manage a screen containing Fragments for owned, unowned (for sale) and
unowned (sold) copies of a given issue.  The title and issue number will be passed in
by intent.
 */
public class CopiesActivity extends AppCompatActivity {

    /*
    TextView for display of title and issue.
     */
    private TextView mTitleAndIssueTextView;

    @Inject
    public CopiesViewModelFactory copiesViewModelFactory;

    private CopiesViewModel copiesViewModel;

    private String TAG = CopiesActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Set up the layout.
         */
        setContentView(R.layout.activity_copies);

        /*
        Get the issue information that was passed in by intent.
         */
        final String thisTitle = getIntent().getStringExtra(getString(R.string.title_extra));
        final String thisIssue = getIntent().getStringExtra(getString(R.string.issue_extra));

        /*
        Get a CopiesViewModel from Dagger.  Observe its LiveData for this Issue.

        Under the hood, this is calling create() on the CopiesViewModelFactory instance injected
        above.  In turn, the factory is calling to Dagger's Provider class for CopiesViewModel
        to create the ViewModel with the repository dependency, which it knows to do based on the
        @Inject annotation to the CopiesViewModel constructor.
         */
        ((ComicCollectionApplication)getApplicationContext()).getAppComponent().inject(this);

        copiesViewModel = new ViewModelProvider(this, copiesViewModelFactory)
                .get(CopiesViewModel.class);
        copiesViewModel.setIssueDetails(thisTitle, thisIssue);


        copiesViewModel.getIssue().observe(this, new Observer<Issue>() {
            @Override
            public void onChanged(Issue issue) {
                /*
                I get back information on whether this issue is on the want list, and on all
                known copies.
                 */
            }
        });

        /*
        Set up the title and issue string at the top of the page.
         */
        mTitleAndIssueTextView = (TextView)findViewById(R.id.copy_issue_and_title);
        mTitleAndIssueTextView
                .setText(String.format(getResources().getString(R.string.title_and_issue),
                        thisTitle, thisIssue));

        /*
        Enable the home (back) button in the ActionBar, which will allow the user to return
        to the previous activity (the issues screen).
         */
        enableHomeButtonInActionBar();

    }  /* onCreate() */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch( item.getItemId() ) {

            /*
            The user pressed the home (back) button.  Pop this activity off the stack and
            go back to the last one.
             */
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        The back button in the ActionBar will take us back to the previous activity (the
        TitlesActivity).  Enable the back ("home") button in the ActionBar.
        */
    private void enableHomeButtonInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
    }


}
