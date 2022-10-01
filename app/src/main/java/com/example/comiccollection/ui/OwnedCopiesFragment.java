package com.example.comiccollection.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.viewmodel.CopiesViewModel;
import com.example.comiccollection.viewmodel.CopiesViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

public class OwnedCopiesFragment extends Fragment {

    /*
    This Fragment contains only a banner and a recycler view showing all owned copies.
     */
    private TextView ownedCopiesBanner;
    private RecyclerView mOwnedCopiesListView;

    @Inject
    public CopiesViewModelFactory copiesViewModelFactory;

    /*
    This Fragment should share the CopiesViewModel with the CopiesActivity that owns it.
     */
    private CopiesViewModel copiesViewModel;

    /*
    We are working with a single Issue object, which may have a list of OwnedCopies attached.
     */
    private ArrayList<OwnedCopy> ownedCopies;

    /*
    Use an OwnedCopiesAdapter to hold the list of owned copies for the RecyclerView.
     */
    private OwnedCopiesAdapter ownedCopiesAdapter;

    private final String TAG = OwnedCopiesFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.owned_copy_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Adding OwnedCopyFragment: OnViewCreated");

        /*
        Get the same CopiesViewModel instance that the Activity is using.
         */
        copiesViewModel =
                new ViewModelProvider(Objects.requireNonNull(getActivity()), copiesViewModelFactory)
                        .get(CopiesViewModel.class);

        /*
        Pull out the list of OwnedCopies from the Issue represented by the ViewModel.
         */
        ownedCopies = new ArrayList<>();
        ownedCopiesAdapter = new OwnedCopiesAdapter(ownedCopies);

        copiesViewModel.getIssue().observe(getActivity(), new Observer<Issue>() {
            @Override
            public void onChanged(Issue issue) {
                ownedCopies = issue.getOwnedCopies();
                ownedCopiesAdapter.updateOwnedCopiesList(ownedCopies);
                ownedCopiesAdapter.notifyDataSetChanged();
            }
        });

        /*
        Set up the banner so that clicking on it will show or hide the list of copies.
         */
        ownedCopiesBanner = view.findViewById(R.id.owned_copy_banner_text);
        ownedCopiesBanner.setOnClickListener((v) -> {
                    if (mOwnedCopiesListView.getVisibility() == View.GONE) {
                        mOwnedCopiesListView.setVisibility(View.VISIBLE);
                    } else {
                        mOwnedCopiesListView.setVisibility(View.GONE);
                    }
                }
        );

        /*
        Set up the RecyclerView to show the copies.
         */
        mOwnedCopiesListView = view.findViewById(R.id.owned_copies_list);
        mOwnedCopiesListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOwnedCopiesListView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mOwnedCopiesListView
                .setAdapter(ownedCopiesAdapter);

        /*
        Add a divider between each copy.
         */
        mOwnedCopiesListView.addItemDecoration(new DividerItemDecoration(mOwnedCopiesListView.getContext(),
                DividerItemDecoration.VERTICAL));

        //ownedCopies = Objects.requireNonNull(copiesViewModel.getIssue().getValue()).getOwnedCopies();

    }


}
