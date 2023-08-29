package com.example.comiccollection.ui.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UiUtils {
    /*
    Translate a string from the date field in the format mm-dd-yyyy, into a Date object for
    the datastore.  I realize Date is sort-of deprecated, but I support Firestore for now
    and Firestore still uses it.
     */
    public static Date dateFromDateField(String dateFieldText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Date returnDate;
        try {
            returnDate = dateFormat.parse(dateFieldText);
        } catch( ParseException e) {
            /*
            If the date field doesn't parse right (which shouldn't happen because we're using a
            date picker), return the current date.
             */
            returnDate = new Date();
        }

        return returnDate;
    }

}
