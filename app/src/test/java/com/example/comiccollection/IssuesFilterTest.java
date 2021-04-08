package com.example.comiccollection;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.example.comiccollection.ui.IssuesToggleState;
import com.example.comiccollection.ui.filters.IssuesFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class IssuesFilterTest {

    ArrayList<Issue> issues;

    @Before
    public void setup() {
        issues = new ArrayList<Issue>();

        /*
        Add some issues that are in my collection.  (200-202)
         */
        Issue ff200 = new Issue();
        ff200.setTitle("Fantastic Four");
        ff200.setIssueNumber("200");
        ff200.setWanted(false);

        OwnedCopy ownedFF200 = new OwnedCopy();
        ownedFF200.setTitle("Fantastic Four");
        ownedFF200.setIssue("200");

        ArrayList<OwnedCopy> ownedCopyList200 = new ArrayList<OwnedCopy>();
        ownedCopyList200.add(ownedFF200);
        ff200.setOwnedCopies(ownedCopyList200);

        Issue ff201 = new Issue();
        ff201.setTitle("Fantastic Four");
        ff201.setIssueNumber("201");
        ff201.setWanted(false);

        OwnedCopy ownedFF201_1 = new OwnedCopy();
        ownedFF201_1.setTitle("Fantastic Four");
        ownedFF201_1.setIssue("201");
        ownedFF201_1.setGrade("VF");

        OwnedCopy ownedFF201_2 = new OwnedCopy();
        ownedFF201_2.setTitle("Fantastic Four");
        ownedFF201_2.setIssue("201");
        ownedFF201_2.setGrade("VG/F");

        ArrayList<OwnedCopy> ownedCopyList201 = new ArrayList<OwnedCopy>();
        ownedCopyList201.add(ownedFF201_1);
        ownedCopyList201.add(ownedFF201_2);

        ff201.setOwnedCopies(ownedCopyList201);

        Issue ff202 = new Issue();
        ff202.setTitle("Fantastic Four");
        ff202.setIssueNumber("202");
        ff202.setWanted(false);

        OwnedCopy ownedFF202 = new OwnedCopy();
        ownedFF202.setTitle("Fantastic Four");
        ownedFF202.setIssue("202");

        ArrayList<OwnedCopy> ownedCopyList202 = new ArrayList<OwnedCopy>();
        ownedCopyList202.add(ownedFF202);
        ff202.setOwnedCopies(ownedCopyList202);

        /*
        Add an issue in my collection, but I'd like an upgrade. (203)
         */
        Issue ff203 = new Issue();
        ff203.setTitle("Fantastic Four");
        ff203.setIssueNumber("203");
        ff203.setWanted(true);

        OwnedCopy ownedFF203 = new OwnedCopy();
        ownedFF203.setTitle("Fantastic Four");
        ownedFF203.setIssue("203");

        ArrayList<OwnedCopy> ownedCopyList203 = new ArrayList<OwnedCopy>();
        ownedCopyList203.add(ownedFF203);
        ff203.setOwnedCopies(ownedCopyList203);

        /*
        Add some issues I don't own, but that are on my want list. (204-205)
         */
        Issue ff204 = new Issue();
        ff204.setTitle("Fantastic Four");
        ff204.setIssueNumber("204");
        ff204.setWanted(true);

        Issue ff205 = new Issue();
        ff205.setTitle("Fantastic Four");
        ff205.setIssueNumber("205");
        ff205.setWanted(true);

        /*
        Add an issue I don't own or care about (450)
         */
        Issue ff450 = new Issue();
        ff450.setTitle("Fantastic Four");
        ff450.setIssueNumber("450");
        ff450.setWanted(false);

        issues.add(ff200);
        issues.add(ff201);
        issues.add(ff202);
        issues.add(ff203);
        issues.add(ff204);
        issues.add(ff205);
        issues.add(ff450);
    }

    @Test
    public void ownedTrueWantedTrue() {
        IssuesToggleState issuesToggleState = new IssuesToggleState(true, true);

        IssuesFilter issuesFilter = new IssuesFilter();

        /*
        Call the SUT.
         */
        ArrayList<Issue> filteredIssueList = issuesFilter.getFilteredIssueData(issuesToggleState, issues);

        /*
        I expect this to return everything.
         */
        assertEquals(7, filteredIssueList.size());
    }

    @Test
    public void ownedTrueWantedFalse() {
        IssuesToggleState issuesToggleState = new IssuesToggleState(true, false);

        IssuesFilter issuesFilter = new IssuesFilter();

        /*
        Call the SUT.
         */
        ArrayList<Issue> filteredIssueList = issuesFilter.getFilteredIssueData(issuesToggleState, issues);

        /*
        I expect this to return 200-203.
         */
        assertEquals(4, filteredIssueList.size());

        boolean got200 = false;
        boolean got201 = false;
        boolean got202 = false;
        boolean got203 = false;

        for( Issue issue : filteredIssueList ) {
            if (issue.getIssueNumber().equals("200")) {
                got200 = true;
            }
            if (issue.getIssueNumber().equals("201")) {
                got201 = true;
            }
            if (issue.getIssueNumber().equals("202")) {
                got202 = true;
            }
            if (issue.getIssueNumber().equals("203")) {
                got203 = true;
            }
        }

        assertTrue(got200);
        assertTrue(got201);
        assertTrue(got202);
        assertTrue(got203);
    }

    @Test
    public void ownedFalseWantedTrue() {
        IssuesToggleState issuesToggleState = new IssuesToggleState(false, true);

        IssuesFilter issuesFilter = new IssuesFilter();

        /*
        Call the SUT.
         */
        ArrayList<Issue> filteredIssueList = issuesFilter.getFilteredIssueData(issuesToggleState, issues);

        /*
        I expect this to return 203-205 (203 because of the upgrade).
         */
        assertEquals(3, filteredIssueList.size());

        boolean got203 = false;
        boolean got204 = false;
        boolean got205 = false;

        for( Issue issue : filteredIssueList ) {
            if( issue.getIssueNumber().equals("203") ) {
                got203 = true;
            }
            if( issue.getIssueNumber().equals("204") ) {
                got204 = true;
            }
            if( issue.getIssueNumber().equals("205") ) {
                got205 = true;
            }
        }
        assertTrue(got204);
        assertTrue(got205);
        assertTrue(got203);
    }

    @Test
    public void ownedFalseWantedFalse() {
        IssuesToggleState issuesToggleState = new IssuesToggleState(false, false);

        IssuesFilter issuesFilter = new IssuesFilter();

        /*
        Call the SUT.
         */
        ArrayList<Issue> filteredIssueList = issuesFilter.getFilteredIssueData(issuesToggleState, issues);

        /*
        I expect this to return just 450.
         */
        assertEquals(1, filteredIssueList.size());

        boolean got450 = false;

        for( Issue issue : filteredIssueList ) {
            if( issue.getIssueNumber().equals("450") ) {
                got450 = true;
            }

            assertTrue(got450);
        }
    }

}
