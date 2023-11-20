package com.example.comiccollection;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Grade;
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
    Issue ff200;
    Issue ff201;
    Issue ff202;
    Issue ff203;
    Issue ff204;
    Issue ff205;
    Issue ff450;

    @Before
    public void setup() {
        issues = new ArrayList<Issue>();

        /*
        Add some issues that are in my collection.  (200-202)
         */
        ff200 = new Issue();
        ff200.setTitle("Fantastic Four");
        ff200.setIssueNumber("200");
        ff200.setWanted(false);

        Copy ownedFF200 = new Copy();
        ownedFF200.setTitle("Fantastic Four");
        ownedFF200.setIssue("200");

        ArrayList<Copy> ownedCopyList200 = new ArrayList<Copy>();
        ownedCopyList200.add(ownedFF200);
        ff200.setOwnedCopies(ownedCopyList200);

        ff201 = new Issue();
        ff201.setTitle("Fantastic Four");
        ff201.setIssueNumber("201");
        ff201.setWanted(false);

        Copy ownedFF201_1 = new Copy();
        ownedFF201_1.setTitle("Fantastic Four");
        ownedFF201_1.setIssue("201");
        ownedFF201_1.setGrade(Grade.VF);

        Copy ownedFF201_2 = new Copy();
        ownedFF201_2.setTitle("Fantastic Four");
        ownedFF201_2.setIssue("201");
        ownedFF201_2.setGrade(Grade.VGF);

        ArrayList<Copy> ownedCopyList201 = new ArrayList<Copy>();
        ownedCopyList201.add(ownedFF201_1);
        ownedCopyList201.add(ownedFF201_2);

        ff201.setOwnedCopies(ownedCopyList201);

        ff202 = new Issue();
        ff202.setTitle("Fantastic Four");
        ff202.setIssueNumber("202");
        ff202.setWanted(false);

        Copy ownedFF202 = new Copy();
        ownedFF202.setTitle("Fantastic Four");
        ownedFF202.setIssue("202");

        ArrayList<Copy> ownedCopyList202 = new ArrayList<Copy>();
        ownedCopyList202.add(ownedFF202);
        ff202.setOwnedCopies(ownedCopyList202);

        /*
        Add an issue in my collection, but I'd like an upgrade. (203)
         */
        ff203 = new Issue();
        ff203.setTitle("Fantastic Four");
        ff203.setIssueNumber("203");
        ff203.setWanted(true);

        Copy ownedFF203 = new Copy();
        ownedFF203.setTitle("Fantastic Four");
        ownedFF203.setIssue("203");

        ArrayList<Copy> ownedCopyList203 = new ArrayList<Copy>();
        ownedCopyList203.add(ownedFF203);
        ff203.setOwnedCopies(ownedCopyList203);

        /*
        Add some issues I don't own, but that are on my want list. (204-205)
         */
        ff204 = new Issue();
        ff204.setTitle("Fantastic Four");
        ff204.setIssueNumber("204");
        ff204.setWanted(true);

        ff205 = new Issue();
        ff205.setTitle("Fantastic Four");
        ff205.setIssueNumber("205");
        ff205.setWanted(true);

        /*
        Add an issue I don't own or care about (450)
         */
        ff450 = new Issue();
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
        assertTrue(filteredIssueList.contains(ff200));
        assertTrue(filteredIssueList.contains(ff201));
        assertTrue(filteredIssueList.contains(ff202));
        assertTrue(filteredIssueList.contains(ff203));
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
        assertTrue(filteredIssueList.contains(ff204));
        assertTrue(filteredIssueList.contains(ff205));
        assertTrue(filteredIssueList.contains(ff203));
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
        assertTrue(filteredIssueList.contains(ff450));
    }

}
