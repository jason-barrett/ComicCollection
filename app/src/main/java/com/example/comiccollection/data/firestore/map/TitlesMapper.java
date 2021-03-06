package com.example.comiccollection.data.firestore.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Title;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
This class is responsible for mapping a QuerySnapshot object returned from the database
containing query results for a set of titles, to a list of title entities.
 */
public class TitlesMapper {

    static String TAG = TitlesMapper.class.getSimpleName();

    public static List<Title> map(QuerySnapshot value) {

        List<Title> titles = new ArrayList<Title>();

        /*
        Every document in the result is a title.  Read them into a list of
        Title entities.
        */
        for (DocumentSnapshot document : value) {
            if (document.exists()) {
                Title title = new Title();
                title.setName(document.getString(ComicDbHelper.CC_TITLE_NAME));

                String firstIssue = document.getString(ComicDbHelper.CC_TITLE_FIRST_ISSUE);
                if (firstIssue != null) {
                    title.setFirstIssue(firstIssue);
                }

                String lastIssue = document.getString(ComicDbHelper.CC_TITLE_LAST_ISSUE);
                if (lastIssue != null) {
                    title.setLastIssue(lastIssue);
                }

                title.setDocumentId(document.getId());

                titles.add(title);
                Log.d(TAG, "Added title " + title.toString());
            }
        }  // End for (documents)

        return titles;

    }  // End map();
}
