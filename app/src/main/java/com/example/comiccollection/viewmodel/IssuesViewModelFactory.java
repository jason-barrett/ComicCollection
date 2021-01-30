package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.FirestoreComicRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class IssuesViewModelFactory implements ViewModelProvider.Factory {

    public FirestoreComicRepository comicRepository;

    public IssuesViewModelFactory(FirestoreComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new IssuesViewModel(comicRepository);
    }
}
