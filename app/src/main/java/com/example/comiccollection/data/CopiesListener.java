package com.example.comiccollection.data;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;

public interface CopiesListener {
    /*
    Each copy of an issue is sui generis, and there is not a use case at present for adding them
    in bulk.
     */
    public void onCopyReady(Copy copy, Issue issue);

    public void onCopyChange(Copy copy);
    public void onCopyChangeFailed(String message);
}
