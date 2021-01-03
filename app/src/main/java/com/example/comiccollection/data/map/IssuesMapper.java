package com.example.comiccollection.data.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Copy;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
This class is responsible for mapping a QuerySnapshot object returned from the database
containing query results for a set of issues, typically for a specific title, to a list of
issue entities.
 */
public class IssuesMapper {

    static String TAG = IssuesMapper.class.getSimpleName();

    public static List<Issue> map(QuerySnapshot value) {

        List<Issue> issues = new ArrayList<Issue>();

        for( QueryDocumentSnapshot document : value ) {
            if( document.exists() ) {
                Issue issue = map(document);
                if( issue != null ) {
                    issues.add(issue);
                }
            }
        }

        return issues;
    }  //end (map)

    public static Issue map(QueryDocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        Issue issue = new Issue();

        try {
            /*
            Map in all of the global per-issue information.
            */
            issue.setIssueNumber(document.getString(ComicDbHelper.CC_ISSUE_NUMBER));
            issue.setTitle(document.getString(ComicDbHelper.CC_ISSUE_TITLE));

            if (document.contains(ComicDbHelper.CC_ISSUE_WANTED)) {
                issue.setWanted(document.getBoolean(ComicDbHelper.CC_ISSUE_WANTED));
            } else {
                issue.setWanted(false);
            }

            issue.setDocumentId(document.getId());

            return issue;

        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot map Issue document " + document.getId()
                    + ", may be malformed.");
        }
        return null;
    }
}
