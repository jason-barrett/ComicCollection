package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Title;

import java.util.List;

public interface ComicRepository {
    public void loadAndListenForTitles(TitlesListener onTitlesReady);
    public void addTitle(Title title);
    public void modifyTitle(Title title);
    public void deleteTitle(Title title);
}
