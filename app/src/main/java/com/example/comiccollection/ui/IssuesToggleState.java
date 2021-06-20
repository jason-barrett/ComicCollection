package com.example.comiccollection.ui;

public class IssuesToggleState {
    /*
    Active filters are part of the global state of the activity.  These control which issues
    are shown and which are filtered out.
    */

    private boolean mShowOwned;
    private boolean mShowWanted;

    public IssuesToggleState(boolean mShowOwned, boolean mShowWanted) {
        this.mShowOwned = mShowOwned;
        this.mShowWanted = mShowWanted;
    }

    /*
    Initialize the toggle state to show everything.
     */
    public IssuesToggleState() {
        this(true, true);
    }

    public boolean getShowOwned() {
        return mShowOwned;
    }

    public void setShowOwned(boolean mShowOwned) {
        this.mShowOwned = mShowOwned;
    }

    public boolean getShowWanted() {
        return mShowWanted;
    }

    public void setShowWanted(boolean mShowWanted) {
        this.mShowWanted = mShowWanted;
    }

    public String toString() {
        return "Show owned = " + this.getShowOwned() + ", Show wanted = " + this.getShowWanted();
    }
}
