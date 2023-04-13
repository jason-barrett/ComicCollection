package com.example.comiccollection.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.comiccollection.R;
import com.example.comiccollection.application.ComicCollectionApplication;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.viewmodel.CopiesViewModel;
import com.example.comiccollection.viewmodel.CopiesViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

/*
This Activity will manage a screen containing segments for owned, unowned (for sale) and
unowned (sold) copies of a given issue.  The title and issue number will be passed in
by intent.
 */
public class CopiesActivity extends AppCompatActivity implements AddCopyDialogFragment.AddCopyDialogListener {

    /*
    TextView for display of title and issue.
     */
    private TextView mTitleAndIssueTextView;

    /*
    ExpandableListView to show the individual copies, grouped by category.
     */
    private ExpandableListView copiesListView;

    /*
    Floating Action Button to pop up a dialog for adding a copy.
     */
    private FloatingActionButton floatingActionButton;

    /*
    Adapter for the ExpandableListView to provide it with data.
     */
    private CopiesAdapter copiesAdapter;

    /*
    We need a list of names for each group in the ExpandableList.  We're grouping the copies by
    whether they are owned, for sale, or sold.
     */
    private List<String> copyCategoryNamesList;

    /*
    For each group / category, we also need to store copies of the Copy objects for this Issue
    in each group.

    This data will be acquired from the ViewModel via the repository and refreshed from the
    ViewModel should this Activity be re-created.

    We don't have the data here at Activity creation time, but let's set up the structure.
     */
    private HashMap<String, List<Copy>> copiesMap = new HashMap<>();

    /*
    This factory will be injected by Dagger and used to create the CopiesViewModel I need.
     */
    @Inject
    public CopiesViewModelFactory copiesViewModelFactory;

    /*
    CopiesViewModel to preserve state across configuration changes.
     */
    private CopiesViewModel copiesViewModel;

    private final String TAG = CopiesActivity.class.getSimpleName();

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
                copiesMap.merge(getString(R.string.owned_copy_category), issue.getOwnedCopies(),
                        (oldList, newList) -> {return newList;});

                copiesMap.merge(getString(R.string.forsale_copy_category), issue.getForSaleCopies(),
                        (oldList, newList) -> {return newList;});

                /*
                These are copies sold in the market, not copies sold by me.
                 */
                copiesMap.merge(getString(R.string.sold_copy_category), issue.getSoldCopies(),
                        (oldList, newList) -> {return newList;});

                /*
                Update the list adapter with new or changed information.
                 */
                copiesAdapter.notifyDataSetChanged();
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
        Set up the ExpandableListView with all of the copy information.
         */
        copyCategoryNamesList = new ArrayList<String>(Arrays.asList(
                getResources().getString(R.string.owned_copy_category),
                getResources().getString(R.string.forsale_copy_category),
                getResources().getString(R.string.sold_copy_category)
        ));

        /*
        Initialize the CopiesMap to a map of empty lists.
         */
        for( String name : copyCategoryNamesList ) {
            copiesMap.put(name, new ArrayList<Copy>());
        }

        copiesListView = (ExpandableListView)findViewById(R.id.copies_expandable_view);
        copiesAdapter = new CopiesAdapter(getApplicationContext(), copyCategoryNamesList, copiesMap);
        copiesListView.setAdapter(copiesAdapter);

        /*
        Set the behavior of the Floating Action Button, to launch a dialog to add a copy.
         */
        floatingActionButton = (FloatingActionButton) findViewById(R.id.copies_fab);
        floatingActionButton.setOnClickListener( (v) -> {
            AddCopyDialogFragment fragment = new AddCopyDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString("title", thisTitle);
            arguments.putString("issue", thisIssue);
            fragment.setArguments(arguments);

            fragment.show(getSupportFragmentManager(), null);
        });

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
    IssuesActivity).  Enable the back ("home") button in the ActionBar.
    */
    private void enableHomeButtonInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
    }

    /*
    Overridden from AddCopyDialogFragment.AddCopyDialogListener.  This gets called when a
    Copy has just been successfully added.
     */
    @Override
    public void onDialogClickAdd(AddCopyDialogFragment fragment) {
        /*
        Provide the new Copy to the ViewModel to add to the repository.

        Any validation we may want to do that can't be done using the controls on the
        form would go here.
         */
        Copy newCopy = fragment.getNewCopy();
        if( newCopy != null ) {
            copiesViewModel.addCopyToIssue(newCopy);
        }
    }
}
