package com.example.comiccollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TitlesActivity extends AppCompatActivity
        implements AddTitleDialogFragment.AddTitleDialogListener,
        EditTitleDialogFragment.EditTitleDialogListener,
        TitlesAdapter.TitleClickListener {

    private RecyclerView mTitlesListView;
    private ArrayList<Title> mTitlesList;

    private TitlesViewModel mTitlesViewModel;
    private TitlesAdapter mTitlesAdapter;

    private final String TAG = TitlesActivity.class.getSimpleName();

    /*************************************************************************************
     * Activity lifecycle methods.
     *************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles);

        mTitlesListView = (ContextMenuRecyclerView) findViewById(R.id.titles_list);
        mTitlesAdapter = new TitlesAdapter(this);

        /*
        Instantiate the ViewModel and create an Observer to update the UI.  The Observer is
        observing the LiveData in the ViewModel, representing the current list of titles
        in the database.
         */
        mTitlesViewModel = new ViewModelProvider(this).get(TitlesViewModel.class);
        final Observer<ArrayList<Title>> titlesObserver = new Observer< ArrayList<Title>>() {

            @Override
            public void onChanged(ArrayList<Title> titles) {
                Log.d(TAG, "Updating titles list to TitlesActivity, "
                        + titles.size() + " titles found.");
                mTitlesList = titles;
                mTitlesAdapter.updateTitles(titles);
                mTitlesAdapter.notifyDataSetChanged();
            }
        };

        mTitlesViewModel.getTitles().observe(this, titlesObserver);

        /*
        Set up the RecyclerView to show all of the titles.
         */
        mTitlesListView.setAdapter(mTitlesAdapter);
        mTitlesListView.setLayoutManager(new LinearLayoutManager(this));
        mTitlesListView.setHasFixedSize(true);

        /*
        Kick off the initial titles load.
         */
        mTitlesViewModel.loadTitles();

        /*
        Add the floating action button to add a title
        */
        FloatingActionButton titleFab = (FloatingActionButton) findViewById(R.id.titles_fab);
        titleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTitleDialogFragment addTitleDialogFragment = new AddTitleDialogFragment();
                addTitleDialogFragment.show(getSupportFragmentManager(), null);
            }
        });

    } // end onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************************************************************************
     * TitlesAdapter listener interface method implementations.
     *************************************************************************************/

    /*
      This is the click handler for the TitlesAdapter for the list of titles.
     */
    @Override
    public void onClick(View view, Title title) {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        /*
        Following a trick I learned in this video, https://www.youtube.com/watch?v=fl5BB3I3MvQ,
        we're hiding the adapter position in the group ID for the menu item.
         */
        Title title = mTitlesAdapter.getTitleAt(item.getGroupId());

        switch ( item.getItemId() ) {

            case R.id.title_menu_option_edit:
                EditTitleDialogFragment editTitleDialogFragment = new EditTitleDialogFragment(title);
                editTitleDialogFragment.show(getSupportFragmentManager(), null);

                return true;

            case R.id.title_menu_option_delete:
                Log.i(TAG, "Deleting title " + title.toString());
                mTitlesViewModel.deleteTitle(title);

                return true;
        }

        return super.onContextItemSelected(item);
    }


    /*************************************************************************************
     * Listeners for the 'Add Title' and 'Edit Title' dialogs.
     *************************************************************************************/

    /*
    This is the handler for when the user chooses to add a title in the popup dialog.
     */

    @Override
    public void onDialogClickAdd(AddTitleDialogFragment fragment) {
        Title title = fragment.getNewTitle();
        if( title != null && title.getName().length() > 0 ) {
            try {
                /*
                Just double check that the user did not enter a character in one of the issue
                numbers.  Technically that is not illegal (e.g., the annuals), but the first and
                last issues are used for comparison purposes.
                 */
                Integer.parseInt(title.getFirstIssue());
                Integer.parseInt(title.getLastIssue());

                // TODO: Disallow last issue < first issue

                Log.i(TAG,"Adding title " + title.toString());
                mTitlesViewModel.addTitle(title);
            } catch( NumberFormatException e ) {
                Log.i(TAG, "User entered a non-numeric issue number.");
            }

        }
    }

    /*
    This is the handler for when the user chooses to edit a title in the popup dialog.
     */

    @Override
    public void onDialogClickEdit(EditTitleDialogFragment fragment) {
        Title title = fragment.getTitle();
        if( title != null && title.getName().length() > 0 ) {
            try {
                /*
                Just double check that the user did not enter a character in one of the issue
                numbers.  Technically that is not illegal (e.g., the annuals), but the first and
                last issues are used for comparison purposes.
                 */
                Integer.parseInt(title.getFirstIssue());
                Integer.parseInt(title.getLastIssue());

                // TODO: Disallow last issue < first issue

                Log.i(TAG,"Modifying title " + title.toString());
                mTitlesViewModel.modifyTitle(title);
            } catch( NumberFormatException e ) {
                Log.i(TAG, "User entered a non-numeric issue number.");
            }

        }
    }

}