package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.entities.Title;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TitlesListManager {

    public Map<String, Integer> mapListPositionByStartLetter(List<Title> titles) {
        Map<String, Integer> listPositionByStartLetter = new TreeMap<String, Integer>();

        Character currentLetter = new Character(titles.get(0).getName().toUpperCase().charAt(0));
        listPositionByStartLetter.put(currentLetter.toString(), 0);

        /*
        The repository will return the list sorted alphabetically.
         */
        int position = 0;
        for( Title title : titles ) {
            /*
            Load up the map with list position by starting letter.
             */
            if( title.getName().toUpperCase().charAt(0) != currentLetter.charValue() ) {
                currentLetter = title.getName().toUpperCase().charAt(0);
                listPositionByStartLetter.put(currentLetter.toString(), position);
            }

            position++;
        }

        return listPositionByStartLetter;
    }

    public boolean titleNameExists(List<Title> titles, Title newTitle) {
        /*
        We can expect the titles to be alphabetically sorted.  A binary search would be
        a reasonable optimization here if the performance is not good.
         */
        for( Title title : titles ) {
            if( newTitle.getName().equals(title.getName()) ) {
                return true;
            }
        }

        return false;
    }

}
