package com.example.comiccollection;

import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.data.modifiers.TitlesSorter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TitlesSorterTest {

    @Before
    public void setup() {
    }

    @Test
    public void titlesSorter_modify_SortsCorrectly() {
        TitlesSorter titlesSorter = new TitlesSorter();

        List<Title> titlesIn = new ArrayList<Title>();

        Title titleSuperman = new Title();
        titleSuperman.setName("Superman");
        titleSuperman.setFirstIssue("223");
        titleSuperman.setLastIssue("450");

        Title titleCaptainAmerica = new Title();
        titleCaptainAmerica.setName("Captain America");
        titleCaptainAmerica.setFirstIssue("100");
        titleCaptainAmerica.setLastIssue("410");

        Title titleNova = new Title();
        titleNova.setName("Nova");
        titleNova.setFirstIssue("1");
        titleNova.setLastIssue("25");

        titlesIn.add(titleSuperman);
        titlesIn.add(titleCaptainAmerica);
        titlesIn.add(titleNova);

        ArrayList<Title> titlesOut = (ArrayList<Title>) titlesSorter.modify((ArrayList<Title>) titlesIn);

        assertEquals(titlesOut.get(0).getName(), "Captain America");
        assertEquals(titlesOut.get(1).getName(), "Nova");
        assertEquals(titlesOut.get(2).getName(), "Superman");
    }
}
