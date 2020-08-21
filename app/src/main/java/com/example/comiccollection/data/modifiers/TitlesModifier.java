package com.example.comiccollection.data.modifiers;

import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;
import java.util.List;

public interface TitlesModifier {
    List<Title> modify(ArrayList<Title> titles);
}
