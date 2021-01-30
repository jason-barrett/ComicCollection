package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.FirestoreComicRepository;
import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.entities.Issue;

import java.util.List;

import androidx.lifecycle.ViewModel;

public class IssuesViewModel extends ViewModel implements IssuesListener {

    private FirestoreComicRepository repository;

    public IssuesViewModel(FirestoreComicRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onIssuesReady(List<Issue> issues) {

    }

    @Override
    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {

    }

    @Override
    public void onIssueLoadFailed() {

    }
}
