package com.example.comiccollection.data.map;

import android.util.Log;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.OwnedCopy;

public class FirestoreTypeUtils {
    static String TAG = FirestoreTypeUtils.class.getSimpleName();

    /*
    Firestore stores numeric values with type 'string' instead of 'number' for easy
    readability.  May want to revisit that decision, but as long as it's true, this method
    will help convert those strings into numeric values for the Copy class.
    */
    public static double handleDoubleAsString(String value, Copy copy, String fieldName) {
        double fieldAsDouble;
        try {
            fieldAsDouble = Double.parseDouble(value);
        } catch( NumberFormatException e ) {
            Log.w(TAG, copy.getTitle() + " " + copy.getIssue()
                    + " has invalid " + fieldName + " in DB : " + value);

            fieldAsDouble = 0.0;
        }

        return fieldAsDouble;
    }

}
