package com.example.comiccollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesViewModel;

import java.util.ArrayList;

public class TitlesActivity extends AppCompatActivity implements RecyclerView.OnClickListener {

    private RecyclerView mTitlesListView;
    private ArrayList<Title> mTitlesList;

    private TitlesViewModel mTitlesViewModel;
    private TitlesAdapter mTitlesAdapter;

    private final String TAG = TitlesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles);

        mTitlesAdapter = new TitlesAdapter();

        /*
        Instantiate the ViewModel and create an Observer to update the UI.
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
        mTitlesListView = (RecyclerView) findViewById(R.id.titles_list);
        mTitlesListView.setAdapter(mTitlesAdapter);
        mTitlesListView.setLayoutManager(new LinearLayoutManager(this));

        /*
        Kick off the initial titles load.
         */
        mTitlesViewModel.loadTitles();
    }

    /*
    This is the click handler for the RecyclerView for the list of titles.
     */
    @Override
    public void onClick(View view) {

    }
}