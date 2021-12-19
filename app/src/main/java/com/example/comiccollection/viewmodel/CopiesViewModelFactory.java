package com.example.comiccollection.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.comiccollection.data.ComicRepository;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Provider;

public class CopiesViewModelFactory implements ViewModelProvider.Factory {

    private final Provider<CopiesViewModel> copiesViewModelProvider;

    @Inject
    public CopiesViewModelFactory(Provider<CopiesViewModel> copiesViewModelProvider) {
        this.copiesViewModelProvider = copiesViewModelProvider;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if( modelClass == CopiesViewModel.class ) {
            return (T) copiesViewModelProvider.get();
        } else {
            throw new RuntimeException("Unsupported view model class: " + modelClass);
        }
    }
}
