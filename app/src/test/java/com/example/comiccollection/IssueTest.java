package com.example.comiccollection;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IssueTest {

    @Before
    public void setup() {
    }

    @Test
    public void noCopiesOwned_WantItFalse_UpgradeNotWanted() {
        Issue issue = new Issue();
        issue.setTitle("Captain America");
        issue.setIssueNumber("200");
        issue.setWanted(false);

        assertFalse(issue.upgradeWanted());
    }

    @Test
    public void noCopiesOwned_WantItTrue_UpgradeNotWanted() {
        Issue issue = new Issue();
        issue.setTitle("Captain America");
        issue.setIssueNumber("200");
        issue.setWanted(true);

        assertFalse(issue.upgradeWanted());
    }

    @Test
    public void copiesOwned_WantItFalse_UpgradeNotWanted() {
        Issue issue = new Issue();
        issue.setTitle("Captain America");
        issue.setIssueNumber("200");
        issue.setWanted(false);

        Copy ownedCopy = new Copy();
        ownedCopy.setDealer("Superworld Comics");
        ownedCopy.setDatePurchased(new Date());
        ownedCopy.setPurchasePrice(2.00);

        ArrayList<Copy> ownedCopyList = new ArrayList<Copy>();
        ownedCopyList.add(ownedCopy);

        issue.setOwnedCopies(ownedCopyList);

        assertFalse(issue.upgradeWanted());
    }

    @Test
    public void copiesOwned_WantItTrue_UpgradeWanted() {
        Issue issue = new Issue();
        issue.setTitle("Captain America");
        issue.setIssueNumber("200");
        issue.setWanted(true);

        Copy ownedCopy = new Copy();
        ownedCopy.setDealer("Superworld Comics");
        ownedCopy.setDatePurchased(new Date());
        ownedCopy.setPurchasePrice(2.00);

        ArrayList<Copy> ownedCopyList = new ArrayList<Copy>();
        ownedCopyList.add(ownedCopy);

        issue.setOwnedCopies(ownedCopyList);

        assertTrue(issue.upgradeWanted());
    }
}
