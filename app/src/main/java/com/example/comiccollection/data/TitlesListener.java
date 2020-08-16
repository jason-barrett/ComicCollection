package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Title;

import java.util.List;

public interface TitlesListener {
    public void onTitlesReady(List<Title> titles);
    public void onTitleLoadFailed();
}
