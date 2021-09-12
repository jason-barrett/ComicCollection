package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.firestore.FirestoreComicRepository;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/*
The Factory pattern is used here so that I can inject the repository into the ViewModel via
constructor.
 */
@Singleton
public class TitlesViewModelFactory implements ViewModelProvider.Factory {

    private final Provider<TitlesViewModel> titlesViewModelProvider;

    @Inject
    public TitlesViewModelFactory(Provider<TitlesViewModel> titlesViewModelProvider) {
        this.titlesViewModelProvider = titlesViewModelProvider;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if( modelClass == TitlesViewModel.class ) {
            return (T) titlesViewModelProvider.get();
        } else {
            throw new RuntimeException("Unsupported view model class:" + modelClass);
        }
    }
}
