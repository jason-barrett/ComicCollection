package com.example.comiccollection.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
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
import com.example.comiccollection.data.entities.CopyType;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.viewmodel.CopiesViewModel;
import com.example.comiccollection.viewmodel.CopiesViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/*
This Activity will manage a screen containing segments for owned, unowned (for sale) and
unowned (sold) copies of a given issue.  The title and issue number will be passed in
by intent.
 */
public class CopiesActivity extends AppCompatActivity implements AddCopyDialogFragment.AddCopyDialogListener,
        EditCopyDialogFragment.EditCopyDialogListener,
        RecordSaleDialogFragment.RecordSaleDialogListener{

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

    /***************************************************************************************
      Lifecycle methods.
     ***************************************************************************************/

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

        /*
        Initialize the ExpandableListView and provide it to the CopiesAdapter.  Register the view
        to implement a context menu for edits.
         */
        copiesListView = (ExpandableListView)findViewById(R.id.copies_expandable_view);
        copiesAdapter = new CopiesAdapter(getApplicationContext(), copyCategoryNamesList, copiesMap);
        copiesListView.setAdapter(copiesAdapter);
        registerForContextMenu(copiesListView);

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

    /*
    Options menu handling.
     */
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
    Context menu handling.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        /*
        The context menu should only display on a child view that represents an actual copy,
        not in a group view that represents a category of copies.
         */
        ExpandableListView.ExpandableListContextMenuInfo expandableMenuInfo =
                (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        int type = ExpandableListView.getPackedPositionType(expandableMenuInfo.packedPosition);

        if( type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            super.onCreateContextMenu(menu, view, menuInfo);
            MenuInflater inflater = getMenuInflater();

            /*
            Choose a context menu to show based on the group in which the view that was clicked on.
            This information is packed into the ExpandableListContextMenuInfo object.
            */
            int group = ExpandableListView.getPackedPositionGroup(expandableMenuInfo.packedPosition);
            switch( group ) {
                case CopyType.FORSALE_COPIES:
                    inflater.inflate(R.menu.forsale_copy_context_menu, menu);
                    break;

                default:
                    inflater.inflate(R.menu.copy_context_menu, menu);
                    break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        /*
         I need a reference to the Copy that the menu this item belongs to was attached to.

         I can get the group / child position coordinates from the MenuInfo object attached
         to the item.
         */
        ExpandableListView.ExpandableListContextMenuInfo menuInfo =
                (ExpandableListView.ExpandableListContextMenuInfo)menuItem.getMenuInfo();

        int groupPosition = ExpandableListView.getPackedPositionGroup(menuInfo.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(menuInfo.packedPosition);

        Copy copy = (Copy)copiesAdapter.getChild(groupPosition, childPosition);

        switch (menuItem.getItemId()) {
            case R.id.copy_menu_option_edit:
                EditCopyDialogFragment fragment = new EditCopyDialogFragment(copy, this);
                fragment.show(getSupportFragmentManager(), null);

                break;

            case R.id.copy_menu_option_delete:
                copiesViewModel.deleteCopy(copy);
                break;

            case R.id.copy_menu_option_record_sale:
                RecordSaleDialogFragment recordSaleDialogFragment =
                        new RecordSaleDialogFragment(copy, this);
                recordSaleDialogFragment.show(getSupportFragmentManager(), null);

                /*
                TODO: These options are to be added.
            case R.id.copy_menu_option_purchase:
            case R.id.copy_menu_option_price_change:
            case R.id.copy_menu_option_undo_sale:
                 */

            default:
                return super.onContextItemSelected(menuItem);
        }

        return super.onContextItemSelected(menuItem);
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

    @Override
    public void onDialogClickEdit(EditCopyDialogFragment fragment) {
        /*
        Propagate the changes in the edited Copy object through the ViewModel.

        Any validation we may want to do that can't be done using the controls on the
        form would go here.
         */
        Copy editCopy = fragment.getEditCopy();

        if( editCopy != null ) {
            copiesViewModel.modifyCopy(editCopy);
        }

        fragment.dismiss();
    }

    @Override
    public void onDialogClickRecordSale(RecordSaleDialogFragment fragment) {
        Copy soldCopy = fragment.getCopy();

        if( soldCopy != null ) {
            copiesViewModel.modifyCopy(soldCopy);
            copiesViewModel.recordSaleOfCopy(soldCopy);
        }

        fragment.dismiss();
    }
}
