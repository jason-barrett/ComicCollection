package com.example.comiccollection.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.application.AppContainer;
import com.example.comiccollection.application.ComicCollectionApplication;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class TitlesActivity extends AppCompatActivity
        implements AddTitleDialogFragment.AddTitleDialogListener,
        EditTitleDialogFragment.EditTitleDialogListener,
        TitlesAdapter.TitleClickListener {

    private RecyclerView mTitlesListView;
    private HorizontalScrollView mAlphaSelectView;

    //TODO: I think I can remove this.
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

        mTitlesListView = (RecyclerView) findViewById(R.id.titles_list);
        mTitlesAdapter = new TitlesAdapter(this);

        mAlphaSelectView = (HorizontalScrollView) findViewById(R.id.scroll_first_letters);

        /*
        The app container for dependency injection needs to be explicitly initialized once.

        Ideally, it would initiate itself on applications startup.  However, the FirebaseFirestore
        dependency cannot be initialized until startup has completed because of how that class
        creates its instance.

        So we're left with doing it here when the first activity starts up.
         */
        AppContainer appContainer = ((ComicCollectionApplication)getApplication()).getAppContainer();
        appContainer.initialize();

        /*
        Instantiate a ViewModel.  The container will build me one with the dependencies it needs.
         */
        //mTitlesViewModel = new ViewModelProvider(this).get(TitlesViewModel.class)
        mTitlesViewModel = appContainer.getTitlesViewModelFactory()
            .create(TitlesViewModel.class);

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
        Add a divider between each titie.
         */
        mTitlesListView.addItemDecoration(new DividerItemDecoration(mTitlesListView.getContext(),
                DividerItemDecoration.HORIZONTAL));

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

                    /*
                    Add issues to the database for the first-last issue range specified.  Default
                    everything to the want list.
                     */
                    ArrayList<Issue> newIssues = new ArrayList<Issue>();
                    for( int issueNumber = firstIssue; issueNumber <= lastIssue; issueNumber++) {
                        Issue issue = new Issue();
                        issue.setTitle(title.getName());
                        issue.setIssueNumber(Integer.toString(issueNumber));
                        issue.setWanted(true);

                        newIssues.add(issue);
                    }
                    mTitlesViewModel.addIssuesToTitle(newIssues);

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
        Title title = fragment.getTitle();
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
                    Data entered looks clean.
                     */
                    Log.i(TAG, "Modifying title " + title.toString());
                    mTitlesViewModel.modifyTitle(title);
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