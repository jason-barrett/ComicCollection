package com.example.comiccollection.viewmodel;

import com.example.comiccollection.data.entities.Title;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/*
This class contains convenience methods for managing a List of Title objects.

This class holds no state.  The List itself is passed into each utility method.

 */
public class TitlesListManager {

    /*
    Given a list of titles, sorted alphabetically, this method provides a Map whose entries
    are:
       Key - A letter of the alphabet
       Value - The zero-based position in the master list whose Title object represents the
       first title to begin with that letter.

    For instance, if the list contains:
      Action Comics
      Adventure Comics
      Batman
      Captain America

   Then the method will provide a map as follows:
     'A' -> 0
     'B' -> 2
     'C' -> 3

     */
    public Map<String, Integer> mapListPositionByStartLetter(List<Title> titles) {
        Map<String, Integer> listPositionByStartLetter = new TreeMap<String, Integer>();

        if( !titles.isEmpty() ) {
            Character currentLetter = new Character(titles.get(0).getName().toUpperCase().charAt(0));
            listPositionByStartLetter.put(currentLetter.toString(), 0);

           /*
            The repository will return the list sorted alphabetically.
             */
            int position = 0;
            for (Title title : titles) {
            /*
            Load up the map with list position by starting letter.
             */
                if (title.getName().toUpperCase().charAt(0) != currentLetter) {
                    currentLetter = title.getName().toUpperCase().charAt(0);
                    listPositionByStartLetter.put(currentLetter.toString(), position);
                }

                position++;
            }
        }

        return listPositionByStartLetter;
    }

    /*
    This method checks whether or not a given Title object exists by name in the master list.
     */
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
