package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TitlesViewModelFactory implements ViewModelProvider.Factory {

    public FirestoreComicRepository comicRepository;

    public TitlesViewModelFactory(FirestoreComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TitlesViewModel(comicRepository);
    }
}
