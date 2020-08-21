package com.example.comiccollection.data.modifiers;

import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;
import java.util.List;

public class TitlesSorter implements TitlesModifier {
    @Override
    public List<Title> modify(ArrayList<Title> titles) {
        titles.sort((t0, t1) -> {return t0.getName().compareTo(t1.getName());});

        return titles;
    }
}
