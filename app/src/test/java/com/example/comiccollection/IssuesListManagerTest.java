package com.example.comiccollection;

import android.util.Log;

import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.UnownedCopy;
import com.example.comiccollection.viewmodel.IssuesListManager;
import com.example.comiccollection.viewmodel.IssuesViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class IssuesListManagerTest {

    /*
    Set up a small collections of issues that will serve as the baseline for most tests.
     */
    Issue batman227;
    Issue batman232;
    Issue batman234;
    Issue batman237;
    Issue batman244;
    Issue batman254;


    @Before
    public void setup() {
        PowerMockito.mockStatic(Log.class);

        batman227 = new Issue();
        batman227.setDocumentId("11111");
        batman227.setTitle("Batman");
        batman227.setIssueNumber("227");
        batman227.setWanted(false);

        batman232 = new Issue();
        batman232.setDocumentId("22222");
        batman232.setTitle("Batman");
        batman232.setIssueNumber("232");
        batman232.setWanted(false);

        batman234 = new Issue();
        batman234.setDocumentId("33333");
        batman234.setTitle("Batman");
        batman234.setIssueNumber("234");
        batman234.setWanted(true);

        batman237 = new Issue();
        batman237.setDocumentId("44444");
        batman237.setTitle("Batman");
        batman237.setIssueNumber("237");
        batman237.setWanted(true);

        batman244 = new Issue();
        batman244.setDocumentId("55555");
        batman244.setTitle("Batman");
        batman244.setIssueNumber("244");
        batman244.setWanted(false);

        batman254 = new Issue();
        batman254.setDocumentId("66666");
        batman254.setTitle("Batman");
        batman254.setIssueNumber("254");
        batman254.setWanted(true);

    }

    @Test
    public void issuesListManager_implementIssuesListModifications_finalListCorrect() {

        IssuesListManager issuesListManager = new IssuesListManager();

        /*
        Start with a list of 6 Issues.  Add two, replace two, remove one.
         */
        List<Issue> issueList = new ArrayList<>();

        issueList.add(batman227);
        issueList.add(batman232);
        issueList.add(batman234);
        issueList.add(batman237);
        issueList.add(batman244);
        issueList.add(batman254);

        /* Issues to add. */

        Issue batman313 = new Issue();
        batman313.setDocumentId("77777");
        batman313.setTitle("Batman");
        batman313.setIssueNumber("313");
        batman313.setWanted(false);

        Issue batman357 = new Issue();
        batman357.setDocumentId("88888");
        batman357.setTitle("Batman");
        batman357.setIssueNumber("357");
        batman357.setWanted(false);

        /* Issue to replace. */
        Issue batman254_bought = new Issue();
        batman254_bought.setDocumentId("66666");
        batman254_bought.setTitle("Batman");
        batman254_bought.setIssueNumber("254");
        batman254_bought.setWanted(false);

        List<Issue> issuesToAddOrReplace = new ArrayList<>();
        issuesToAddOrReplace.add(batman313);
        issuesToAddOrReplace.add(batman357);
        issuesToAddOrReplace.add(batman254_bought);

        /* Issues to remove */
        Issue batman227_remove = new Issue();
        batman227_remove.setDocumentId("11111");
        batman227_remove.setTitle("Batman");
        batman227_remove.setIssueNumber("227");
        batman227_remove.setWanted(false);

        Issue batman232_remove = new Issue();
        batman232_remove.setDocumentId("22222");
        batman232_remove.setTitle("Batman");
        batman232_remove.setIssueNumber("232");
        batman232_remove.setWanted(false);

        List<Issue> issuesToRemove = new ArrayList<>();
        issuesToRemove.add(batman227_remove);
        issuesToRemove.add(batman232_remove);

        /*
        Setup is done, call the SUT.
         */
        List<Issue> modifiedIssues =
                issuesListManager.implementIssuesListModifications(issueList, issuesToAddOrReplace,
                        issuesToRemove);

        /*
        Check what we got back.
         */
        assertEquals(6, modifiedIssues.size());

        //Assert additions.
        assertTrue(modifiedIssues.contains(batman313));
        assertTrue(modifiedIssues.contains(batman357));

        //Assert replacement.
        assertTrue(modifiedIssues.contains(batman254_bought));
        assertFalse(modifiedIssues.contains(batman254));

        //Assert removals.
        assertFalse(modifiedIssues.contains(batman227));
        assertFalse(modifiedIssues.contains(batman232));
    }

    /*
    Test the special case where the same document ID is in the lists to be replaced and removed.

    That's a valid condition and the remove should take precedence.
     */
    @Test
    public void issuesListManager_implementIssuesListModifications_handlesReplaceAndRemove() {
        IssuesListManager issuesListManager = new IssuesListManager();

        /*
        Start with a list of 6 Issues.  Replace and remove the same document.
         */
        List<Issue> issueList = new ArrayList<>();

        issueList.add(batman227);
        issueList.add(batman232);
        issueList.add(batman234);
        issueList.add(batman237);
        issueList.add(batman244);
        issueList.add(batman254);

        /* Set up a replacement for Batman #244. */
        Issue batman244_replace = new Issue();
        batman244_replace.setDocumentId("55555");
        batman244_replace.setTitle("Batman");
        batman244_replace.setIssueNumber("244");
        batman244_replace.setWanted(true);

        List<Issue> issuesToAddOrReplace = new ArrayList<>();
        issuesToAddOrReplace.add(batman244_replace);

        /* Set up the removal of Batman #244 */
        Issue batman244_remove = new Issue();
        batman244_remove.setDocumentId("55555");
        batman244_remove.setTitle("Batman");
        batman244_remove.setIssueNumber("244");
        batman244_remove.setWanted(false);

        List<Issue> issuesToRemove = new ArrayList<>();
        issuesToRemove.add(batman244_remove);

        /*
        Setup is done, call the SUT.
         */
        List<Issue> modifiedIssues =
                issuesListManager.implementIssuesListModifications(issueList, issuesToAddOrReplace,
                        issuesToRemove);

        /*
        Check what we got back.
         */
        assertEquals(5, modifiedIssues.size());
        assertFalse(modifiedIssues.contains(batman244));
    }

    /*
    Test adding a copy and not changing the issue otherwise.

    The change should be visible in the issue in the new list.

    This test will also test an empty list of issues to remove.
    */
    @Test
    public void issuesListManager_implementIssuesListModifications_addACopy() {
        IssuesListManager issuesListManager = new IssuesListManager();

        /*
        Start with a list of 6 Issues.  We're going to find a copy of Batman #234 for sale
        online and add it to the list.
         */
        List<Issue> issueList = new ArrayList<>();

        issueList.add(batman227);
        issueList.add(batman232);
        issueList.add(batman234);
        issueList.add(batman237);
        issueList.add(batman244);
        issueList.add(batman254);

        UnownedCopy batman234Copy = new UnownedCopy();
        batman234Copy.setDocumentId("12121");
        batman234Copy.setTitle("Batman");
        batman234Copy.setIssue("234");
        batman234Copy.addOffer(125.00, new Date("2/21/2021"));
        batman234Copy.setGrade("VG+");
        batman234Copy.setDealer("Metropolis Comics");

        ArrayList<UnownedCopy> unownedCopies = new ArrayList<>();
        unownedCopies.add(batman234Copy);

        Issue batman234_withCopy = new Issue();
        batman234_withCopy.setDocumentId("33333");
        batman234_withCopy.setTitle("Batman");
        batman234_withCopy.setIssueNumber("234");
        batman234_withCopy.setWanted(true);

        batman234_withCopy.setUnownedCopies(unownedCopies);

        ArrayList<Issue> issuesToAddOrReplace = new ArrayList<>();
        issuesToAddOrReplace.add(batman234_withCopy);

        ArrayList<Issue> issuesToRemove = new ArrayList<>();

        /*
        Setup is done, call the SUT.
         */
        List<Issue> modifiedIssues =
                issuesListManager.implementIssuesListModifications(issueList, issuesToAddOrReplace,
                        issuesToRemove);

        /*
        Check what we got back.
         */
        assertEquals(6, modifiedIssues.size());

        boolean got234Copy = false;
        for (Issue issue : modifiedIssues) {
            if (issue.getIssueNumber().equals("234")) {
                if(issue.getUnownedCopies().size() == 1 ) {
                    got234Copy = true;
                }
            }
        }

        assert (got234Copy == true);
    }
}


