package com.example.comiccollection;

import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.viewmodel.TitlesListManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TitlesListManagerTest {

    @Before
    public void setup() {
    }

    @Test
    public void titlesListManager_mapListPositionByStartLetter_allListPositionsCorrect() {
        TitlesListManager titlesListManager = new TitlesListManager();

        List<Title> titlesIn = new ArrayList<Title>();

        Title titleAction = new Title();
        titleAction.setName("ActionComics");
        titleAction.setFirstIssue("300");
        titleAction.setLastIssue("600");

        Title titleAdventure = new Title();
        titleAdventure.setName("Adventure Comics");
        titleAdventure.setFirstIssue("247");
        titleAdventure.setLastIssue("490");

        Title titleAllStar = new Title();
        titleAllStar.setName("All-Star Comics");
        titleAllStar.setFirstIssue("58");
        titleAllStar.setLastIssue("74");

        Title titleBatman = new Title();
        titleBatman.setName("Batman");
        titleBatman.setFirstIssue("183");
        titleBatman.setLastIssue("391");

        Title titleBraveAndBold = new Title();
        titleBraveAndBold.setName("Brave and Bold");
        titleBraveAndBold.setFirstIssue("1");
        titleBraveAndBold.setLastIssue("200");

        Title titleDaredevil = new Title();
        titleDaredevil.setName("Daredevil");
        titleDaredevil.setFirstIssue("1");
        titleDaredevil.setLastIssue("191");

        Title titleDCComicsPresents = new Title();
        titleDCComicsPresents.setName("DC Comics Presents");
        titleDCComicsPresents.setFirstIssue("1");
        titleDCComicsPresents.setLastIssue("97");

        Title titleDefenders = new Title();
        titleDefenders.setName("Defenders");
        titleDefenders.setFirstIssue("1");
        titleDefenders.setLastIssue("152");

        Title titleJusticeLeague = new Title();
        titleJusticeLeague.setName("Justice League");
        titleJusticeLeague.setFirstIssue("1");
        titleJusticeLeague.setLastIssue("261");

        titlesIn.add(titleAction);
        titlesIn.add(titleAdventure);
        titlesIn.add(titleAllStar);
        titlesIn.add(titleBatman);
        titlesIn.add(titleBraveAndBold);
        titlesIn.add(titleDaredevil);
        titlesIn.add(titleDCComicsPresents);
        titlesIn.add(titleDefenders);
        titlesIn.add(titleJusticeLeague);

        Map<String, Integer> startLettersByPosition = titlesListManager.mapListPositionByStartLetter(titlesIn);

        assertTrue(startLettersByPosition.size() == 4 );
        assertTrue(startLettersByPosition.get("A") == 0 );
        assertTrue(startLettersByPosition.get("B") == 3 );
        assertTrue(startLettersByPosition.get("D") == 5 );
        assertTrue(startLettersByPosition.get("J") == 8 );

    }

    @Test
    public void titlesListManager_mapListPositionByStartLetter_noAAllListPositionsCorrect() {
        TitlesListManager titlesListManager = new TitlesListManager();

        List<Title> titlesIn = new ArrayList<Title>();

        Title titleAction = new Title();
        titleAction.setName("ActionComics");
        titleAction.setFirstIssue("300");
        titleAction.setLastIssue("600");

        Title titleAdventure = new Title();
        titleAdventure.setName("Adventure Comics");
        titleAdventure.setFirstIssue("247");
        titleAdventure.setLastIssue("490");

        Title titleAllStar = new Title();
        titleAllStar.setName("All-Star Comics");
        titleAllStar.setFirstIssue("58");
        titleAllStar.setLastIssue("74");

        Title titleBatman = new Title();
        titleBatman.setName("Batman");
        titleBatman.setFirstIssue("183");
        titleBatman.setLastIssue("391");

        Title titleBraveAndBold = new Title();
        titleBraveAndBold.setName("Brave and Bold");
        titleBraveAndBold.setFirstIssue("1");
        titleBraveAndBold.setLastIssue("200");

        Title titleDaredevil = new Title();
        titleDaredevil.setName("Daredevil");
        titleDaredevil.setFirstIssue("1");
        titleDaredevil.setLastIssue("191");

        Title titleDCComicsPresents = new Title();
        titleDCComicsPresents.setName("DC Comics Presents");
        titleDCComicsPresents.setFirstIssue("1");
        titleDCComicsPresents.setLastIssue("97");

        Title titleDefenders = new Title();
        titleDefenders.setName("Defenders");
        titleDefenders.setFirstIssue("1");
        titleDefenders.setLastIssue("152");

        Title titleJusticeLeague = new Title();
        titleJusticeLeague.setName("Justice League");
        titleJusticeLeague.setFirstIssue("1");
        titleJusticeLeague.setLastIssue("261");

        titlesIn.add(titleBatman);
        titlesIn.add(titleBraveAndBold);
        titlesIn.add(titleDaredevil);
        titlesIn.add(titleDCComicsPresents);
        titlesIn.add(titleDefenders);
        titlesIn.add(titleJusticeLeague);

        Map<String, Integer> startLettersByPosition = titlesListManager.mapListPositionByStartLetter(titlesIn);

        assertTrue(startLettersByPosition.size() == 3 );
        assertTrue(startLettersByPosition.get("B") == 0 );
        assertTrue(startLettersByPosition.get("D") == 2 );
        assertTrue(startLettersByPosition.get("J") == 5 );

    }

}
