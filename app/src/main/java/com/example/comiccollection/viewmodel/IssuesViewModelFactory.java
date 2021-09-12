package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class IssuesViewModelFactory implements ViewModelProvider.Factory {

    private final Provider<IssuesViewModel> issuesViewModelProvider;

    @Inject
    public IssuesViewModelFactory(Provider<IssuesViewModel> issuesViewModelProvider) {
        this.issuesViewModelProvider = issuesViewModelProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if( modelClass == IssuesViewModel.class ) {
            return (T) issuesViewModelProvider.get();
        } else {
            throw new RuntimeException("Unsupported ViewModel class: " + modelClass);
        }
    }
}
