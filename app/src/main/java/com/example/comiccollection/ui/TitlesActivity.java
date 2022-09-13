package com.example.comiccollection.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.application.ComicCollectionApplication;
import com.example.comiccollection.data.CollectionStats;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesViewModel;
import com.example.comiccollection.viewmodel.TitlesViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

public class TitlesActivity extends AppCompatActivity
        implements AddTitleDialogFragment.AddTitleDialogListener,
        EditTitleDialogFragment.EditTitleDialogListener,
        TitlesAdapter.TitleClickListener {

    private RecyclerView mTitlesListView;
    private HorizontalScrollView mAlphaSelectView;

    @Inject TitlesViewModelFactory mTitlesViewModelFactory;
    private TitlesViewModel mTitlesViewModel;
    private TitlesAdapter mTitlesAdapter;

    ArrayList<Title> mTitlesList;

    /*
    Keep a CollectionStats object.  I will set an observable on the LiveData in the
    ViewModel and keep this object up to date in case the user asks to see the stats in
    the menu.
     */
    CollectionStats collectionStats;

    private final String TAG = TitlesActivity.class.getSimpleName();

    /*************************************************************************************
     * Activity lifecycle methods.
     *************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles);

        mTitlesListView = (RecyclerView) findViewById(R.id.titles_list);
        mTitlesAdapter = new TitlesAdapter(this);

        mAlphaSelectView = (HorizontalScrollView) findViewById(R.id.scroll_first_letters);

        /*
        Instantiate a ViewModel.  The container will build me one with the dependencies it needs.
         */
        ((ComicCollectionApplication)getApplication()).getAppComponent().inject(this);
        mTitlesViewModel = new ViewModelProvider(this, mTitlesViewModelFactory)
                .get(TitlesViewModel.class);

        /*
        Create an Observer to update the UI.  The Observer is observing the LiveData in the
        ViewModel, representing the current list of titles in the database.
         */
        final Observer<ArrayList<Title>> titlesObserver = new Observer< ArrayList<Title>>() {

            @Override
            public void onChanged(ArrayList<Title> titles) {
                Log.d(TAG, "Updating titles list to TitlesActivity, "
                        + titles.size() + " titles found.");
                mTitlesList = titles;
                mTitlesAdapter.updateTitles(titles);
                mTitlesAdapter.notifyDataSetChanged();

                /*
                If the list of titles has changed, the set of valid start letters for the
                scroll menu may have as well.
                 */
                buildAlphaSelectView();
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
        Add a divider between each title.
         */
        mTitlesListView.addItemDecoration(new DividerItemDecoration(mTitlesListView.getContext(),
                DividerItemDecoration.VERTICAL));

        /*
        Kick off the initial titles load.
         */
        mTitlesViewModel.loadTitles();

        /*
        Make the initial request for CollectionStats and set an observable to keep track of
        the object.
         */
        mTitlesViewModel.getCollectionStats()
                .observe(this, (cs) -> collectionStats = cs);

        /*
        Add the floating action button to add a title.
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
    protected void onResume() {
        super.onResume();

        /*
        Initialize the alpha menu that allows the user to scroll to titles beginning with a
        certain letter.  The menu is built programmatically to only show letters that actually
        have titles that start with them.
         */
        buildAlphaSelectView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************************************************************************
     * Options Menu handling
     *************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.titles_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.collection_stats_item ) {
            showCollectionStats();
            return true;
        }

        return false;
    }

    private void showCollectionStats() {
    /*
    The user wants to see collection statistics.

    In the unlikely case that the initial load of the stats from the data store (in onCreate())
    hasn't completed before the user chooses the menu option, the Activity's CollectionStats
    object will still be null.  In that case, throw an error cleanly.
     */
        if( collectionStats == null ) {
            Snackbar.make(mTitlesListView, R.string.stats_error, Snackbar.LENGTH_LONG).show();
            return;
        }
        try {
             /*
             Pop up a dialog to show the current statistics.
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog collectionStatsDialog = builder
                    .setView(R.layout.collection_stats_layout)
                    .setTitle(R.string.collection_stats_title)
                    .setNegativeButton(R.string.dismiss_stats_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();

            WebView totalIssuesView = collectionStatsDialog.findViewById(R.id.wvTotalIssues);
            WebView totalValueView = collectionStatsDialog.findViewById(R.id.wvTotalValue);

            String totalIssuesHtml = getResources()
                    .getQuantityString(R.plurals.stats_total_issues,
                            collectionStats.getTotalIssues(),
                            collectionStats.getTotalIssues());

            String totalValueHtml = getResources().getString(R.string.stats_total_value,
                    collectionStats.getTotalValue());

            totalIssuesView.loadData(totalIssuesHtml, "text/html", "utf-8");

            totalValueView.loadData(totalValueHtml, "text/html", "utf-8");

        } catch (Exception e) {
            Snackbar.make(mTitlesListView, R.string.stats_error, Snackbar.LENGTH_LONG).show();

            e.printStackTrace();
        }
    }  /* showCollectionStats() */

    /*************************************************************************************
     * TitlesAdapter listener interface method implementations.
     *************************************************************************************/

    /*
      This is the click handler for the TitlesAdapter for the list of titles.

      When I click on a title, I want to launch the list of issues for that title.
     */
    @Override
    public void onClick(View view, String titleName) {

        Log.d(TAG, "User clicked on view for title: " + titleName);

        Intent intent = new Intent(this, IssuesActivity.class);
        intent.putExtra("Title", titleName);

        startActivity(intent);
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

                A non-numeric issue number will throw a NumberFormatException and fall into the
                catch block.
                 */
                int firstIssue = Integer.parseInt(title.getFirstIssue());
                int lastIssue = Integer.parseInt(title.getLastIssue());

                /*
                Some more error checking.

                Disallow last issue < first issue.

                Disallow duplicate title names.
                 */
                if( lastIssue < firstIssue ) {
                    fragment.dismiss();
                    fragment.setErrorText(getResources().getString(R.string.title_error_last_issue_before_first_issue));
                    fragment.show(getSupportFragmentManager(), null);
                } else if ( mTitlesViewModel.titleNameExists(title) ) {
                    fragment.dismiss();
                    fragment.setErrorText(getResources().getString(R.string.title_error_duplicate_title));
                    fragment.show(getSupportFragmentManager(), null);
                }
                else {
                    /*
                    Data entered looks clean.  Add the new title to the ViewModel.
                     */
                    Log.i(TAG, "Adding title " + title.toString());
                    mTitlesViewModel.addTitle(title);

                    mTitlesViewModel.addIssuesToTitle(title,
                            Integer.parseInt(title.getFirstIssue()),
                            Integer.parseInt(title.getLastIssue()));
                }
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
        Title newTitle = fragment.getNewTitle();
        Title currentTitle = fragment.getCurrentTitle();
        if( newTitle != null && newTitle.getName().length() > 0 ) {
            try {
                /*
                Just double check that the user did not enter a character in one of the issue
                numbers.  The first and last issues are used for comparison purposes.

                A non-numeric issue number will throw a NumberFormatException and fall into the
                catch block.
                 */
                int firstIssue = Integer.parseInt(newTitle.getFirstIssue());
                int lastIssue = Integer.parseInt(newTitle.getLastIssue());

                /*
                Some more error checking.

                Disallow last issue < first issue.
                 */
                if( lastIssue < firstIssue ) {
                    fragment.dismiss();
                    fragment.setErrorText(getResources().getString(R.string.title_error_last_issue_before_first_issue));
                    fragment.show(getSupportFragmentManager(), null);
                } else {
                    /*
                    Data entered looks clean.
                     */
                    Log.i(TAG, "Modifying title " + newTitle.toString());
                    mTitlesViewModel.modifyTitle(newTitle);

                    /*
                    If the user has expanded the issues on the want list, add the new ones to
                    the database.
                     */
                    int currentFirstIssue = Integer.parseInt(currentTitle.getFirstIssue());
                    int currentLastIssue = Integer.parseInt(currentTitle.getLastIssue());

                    if( firstIssue < currentFirstIssue ) {
                        mTitlesViewModel.addIssuesToTitle(newTitle,
                                firstIssue, currentFirstIssue - 1);
                    }

                    if( lastIssue > currentLastIssue ) {
                        mTitlesViewModel.addIssuesToTitle(newTitle,
                                currentLastIssue + 1, lastIssue);
                    }

                    /*
                    If the user has removed issues from the want list, remove them from
                    the database.
                     */
                    if( firstIssue > currentFirstIssue ) {
                        mTitlesViewModel.deleteIssuesFromTitle(newTitle, currentFirstIssue,
                                firstIssue - 1);
                    }

                    if( lastIssue < currentLastIssue ) {
                        mTitlesViewModel.deleteIssuesFromTitle(newTitle, lastIssue + 1,
                                currentLastIssue);
                    }

                    fragment.dismiss();
                }
            } catch( NumberFormatException e ) {
                Log.i(TAG, "User entered a non-numeric issue number.");
            }
        }
    }

    /*
    Build the scroller at the bottom of the screen that allows you to pick the starting letter
    for titles at the top of the app, in other words, selecting 'D' gives you the titles
    starting with 'D' up at the top.
     */
    private void buildAlphaSelectView() {
        /*
        Get from the ViewModel the current list of (letter, list position) pairs.
         */
        Map<String, Integer> listPositionByStartLetter = mTitlesViewModel.getListPositionByStartLetter();
        if( listPositionByStartLetter == null ) {
            /*
            Could be that the data hasn't initialized yet.
             */
            return;
        }

        /*
        Create a TextView for each currently valid letter (with which at least one title
        in the list starts).
         */
        LinearLayout alphaSelectLayout = findViewById(R.id.layout_first_letters);
        alphaSelectLayout.removeAllViews();

        Iterator<Map.Entry<String, Integer>> it = listPositionByStartLetter.entrySet().iterator();
        while( it.hasNext() ) {
            TextView letterView = new TextView(alphaSelectLayout.getContext(), null, R.attr.scrollListItemStyle);
            letterView.setText(it.next().getKey());
            letterView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //letterView.setTextColor(ContextCompat.getColor(this, R.color.colorAlphaSelect));
            letterView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.alpha_menu_padding),
                    getResources().getDimensionPixelSize(R.dimen.alpha_menu_padding),
                    getResources().getDimensionPixelSize(R.dimen.alpha_menu_padding));

            letterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView thisLetterView = (TextView) view;
                    String firstLetter = thisLetterView.getText().toString();
                    Log.d(TAG,"User clicked on letter option " + firstLetter);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) mTitlesListView.getLayoutManager();

                    if( firstLetter.length() == 1 ) {
                        layoutManager.scrollToPositionWithOffset(mTitlesViewModel.getListPositionByStartLetter().get(firstLetter.toUpperCase()), 0);

                    } else {
                        // I don't know WHAT was just clicked on.
                        Log.e(TAG, "Found string " + firstLetter + " in the alpha menu.");
                    }
                }
            });

            alphaSelectLayout.addView(letterView);
        }
    } // buildAlphaSelectView()

}