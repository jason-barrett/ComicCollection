package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class IssuesViewModelFactory implements ViewModelProvider.Factory {

    public FirestoreComicRepository comicRepository;

    public IssuesViewModelFactory(FirestoreComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new IssuesViewModel(comicRepository);
    }
}
