package com.example.comiccollection;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.comiccollection.data.IssuesListener;
import com.example.comiccollection.data.TitlesListener;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.Issue;
import com.example.comiccollection.data.entities.Title;
import com.example.comiccollection.data.test.FakeComicRepository;
import com.example.comiccollection.ui.EditTitleDialogFragment;
import com.example.comiccollection.ui.TitlesActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class ModifyTitleTest {
    /*
    I need a TitlesActivity that Dagger knows is a test activity, to be built with the fake
    repository as a dependency.

    TitlesActivity calls getApplication() to get the application that owns it.  The dependency
    graph is built from there.  That application's getAppComponent() has to return a
    TestAppComponent.

    The 'real' TitlesActivity's owning application IS a ComicCollectionApplication, because it's
    set to be so in the AndroidManifest.

    InstrumentationTestRunner, which is set in my build.gradle, is supposed to create an
    application instance when it initializes.  I have extended AndroidJUnitRunner and overridden its
    newApplication() method to use a TestComicCollectionApplication, then specified that in
    the build.gradle.
     */

    private View mTitlesListView;

    /*
    The singleton (fake) repository.  Since this is a singleton, the instance we get for setup
    should be the same instance that Dagger will provide to the test activity.
     */
    private static FakeComicRepository fakeComicRepository;

    /*
    Titles are modified through the EditTitleDialogFragment.  Mock up one to return the
    modifications I want to test.
     */
    @Mock
    private static EditTitleDialogFragment mockEditTitleDialogFragment;

    /*
     Define the titles and issues I'm going to test with (will be instantiated in the setup()
     logic).
    */
    private static Title actionComicsTitle;
    private static Issue actionComics300;
    private static Issue actionComics400;
    private static Issue actionComics500;
    private static Issue actionComics600;

    private static Title batmanTitle;
    private static Issue batman313;

    private static Title firestormTitle;
    private static Issue firestorm1;
    private static Issue firestorm2;
    private static Issue firestorm3;
    private static Issue firestorm4;
    private static Issue firestorm5;

    private static Title talesOfSuspenseTitle;

    @BeforeClass
    public static void setup() {
        /*
        Get an instance of the fake repository, which should be empty at this point.
         */
        fakeComicRepository = FakeComicRepository.getInstance();

        /*
        Seed the repository with some titles that will be used in the tests below.
         */
        actionComicsTitle = new Title();
        actionComicsTitle.setName("Action Comics");
        actionComicsTitle.setFirstIssue("300");
        actionComicsTitle.setLastIssue("600");
        fakeComicRepository.addTitle(actionComicsTitle);

        actionComics300 = new Issue();
        actionComics300.setTitle("Action Comics");
        actionComics300.setIssueNumber("300");
        actionComics300.setWanted(true);
        fakeComicRepository.addIssue(actionComics300);

        actionComics400 = new Issue();
        actionComics400.setTitle("Action Comics");
        actionComics400.setIssueNumber("400");
        actionComics400.setWanted(false);
        ArrayList<Copy> actionComics400Copies = new ArrayList<>();
        actionComics400Copies.add(new Copy("Action Comics", "400"));
        actionComics400.setOwnedCopies(actionComics400Copies);
        fakeComicRepository.addIssue(actionComics400);

        actionComics500 = new Issue();
        actionComics500.setTitle("Action Comics");
        actionComics500.setIssueNumber("500");
        actionComics500.setWanted(true);
        fakeComicRepository.addIssue(actionComics500);

        actionComics600 = new Issue();
        actionComics600.setTitle("Action Comics");
        actionComics600.setIssueNumber("600");
        actionComics600.setWanted(false);
        ArrayList<Copy> actionComics600Copies = new ArrayList<>();
        actionComics600Copies.add(new Copy("Action Comics", "600"));
        actionComics600Copies.add(new Copy("Action Comics", "600"));
        actionComics600.setOwnedCopies(actionComics600Copies);
        fakeComicRepository.addIssue(actionComics600);


        batmanTitle = new Title();
        batmanTitle.setName("Batman");
        batmanTitle.setFirstIssue("183");
        batmanTitle.setLastIssue("391");
        fakeComicRepository.addTitle(batmanTitle);

        batman313 = new Issue();
        batman313.setTitle("Batman");
        batman313.setIssueNumber("313");
        batman313.setWanted(true);
        fakeComicRepository.addIssue(batman313);


        firestormTitle = new Title();
        firestormTitle.setName("Firestorm (1978)");
        firestormTitle.setFirstIssue("1");
        firestormTitle.setLastIssue("5");
        fakeComicRepository.addTitle(firestormTitle);

        firestorm1 = new Issue();
        firestorm1.setTitle("Firestorm (1978)");
        firestorm1.setIssueNumber("1");
        firestorm1.setWanted(true);
        fakeComicRepository.addIssue(firestorm1);

        firestorm2 = new Issue();
        firestorm2.setTitle("Firestorm (1978)");
        firestorm2.setIssueNumber("2");
        firestorm2.setWanted(false);
        ArrayList<Copy> firestorm2OwnedCopies = new ArrayList<>();
        firestorm2OwnedCopies.add(new Copy("Firestorm (1978)", "2"));
        firestorm2.setOwnedCopies(firestorm2OwnedCopies);
        fakeComicRepository.addIssue(firestorm2);

        firestorm3 = new Issue();
        firestorm3.setTitle("Firestorm (1978)");
        firestorm3.setIssueNumber("3");
        firestorm3.setWanted(true);
        fakeComicRepository.addIssue(firestorm3);

        firestorm4 = new Issue();
        firestorm4.setTitle("Firestorm (1978)");
        firestorm4.setIssueNumber("4");
        firestorm4.setWanted(false);
        ArrayList<Copy> firestorm4OwnedCopies = new ArrayList<>();
        firestorm4OwnedCopies.add(new Copy("Firestorm (1978)", "4"));
        firestorm4.setOwnedCopies(firestorm4OwnedCopies);
        fakeComicRepository.addIssue(firestorm4);

        firestorm5 = new Issue();
        firestorm5.setTitle("Firestorm (1978)");
        firestorm5.setIssueNumber("5");
        firestorm5.setWanted(false);
        ArrayList<Copy> firestorm5OwnedCopies = new ArrayList<>();
        firestorm5OwnedCopies.add(new Copy("Firestorm (1978)", "5"));
        firestorm5.setOwnedCopies(firestorm5OwnedCopies);
        fakeComicRepository.addIssue(firestorm5);


        talesOfSuspenseTitle = new Title();
        talesOfSuspenseTitle.setName("Tales of Suspense");
        talesOfSuspenseTitle.setFirstIssue("39");
        talesOfSuspenseTitle.setLastIssue("99");
        fakeComicRepository.addTitle(talesOfSuspenseTitle);

    }

    @Before
    public void initialize() {
        /*
        Initialize mock objects.
         */
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void listViewIsPresent() {
        try( ActivityScenario<TitlesActivity> activityScenario
                     = ActivityScenario.launch(TitlesActivity.class) )
        {
            activityScenario.onActivity(activity -> {
                mTitlesListView = (RecyclerView) activity.findViewById(R.id.titles_list);
                assertTrue(mTitlesListView.isLaidOut());
            });
        }
    }

    @Test
    public void onDialogClickEdit_ReduceRangeOnBothEnds_ReflectedInRepository() {

        /*
        Reduce the range on both ends.  The title in the repository should reflect the
        change.  Also, the issues outside the new range should be deleted.
         */
        try( ActivityScenario<TitlesActivity> activityScenario
                     = ActivityScenario.launch(TitlesActivity.class) )
        {
            /*
            actionComicsTitle is a reference to the Title in the repository and will be modified
            in the course of the call to the object under test.  We need a copy of it to
            return from the mock fragment.
             */
            Title newActionComicsTitle = new Title(actionComicsTitle);
            when(mockEditTitleDialogFragment.getCurrentTitle()).thenReturn(newActionComicsTitle);

            Title modifiedActionComics = new Title();
            modifiedActionComics.setName("Action Comics");
            modifiedActionComics.setDocumentId(actionComicsTitle.getDocumentId());
            modifiedActionComics.setFirstIssue("400");
            modifiedActionComics.setLastIssue("500");
            when(mockEditTitleDialogFragment.getNewTitle()).thenReturn(modifiedActionComics);

            activityScenario.onActivity(activity -> {
                activity.onDialogClickEdit(mockEditTitleDialogFragment);

                /*
                Check that the title's first and last issue has changed, and that the deleted
                issues are gone.
                 */
                fakeComicRepository.loadAndListenForTitles(new TitlesListener() {
                    @Override
                    public void onTitlesReady(List<Title> titles) {
                        assertNotNull(titles);
                        assertEquals(4, titles.size());

                        Title newActionTitle = titles.stream()
                                .filter((t) -> t.getName().equals("Action Comics"))
                                .findFirst()
                                .get();
                        assertNotNull(newActionTitle);
                        assertEquals("400", newActionTitle.getFirstIssue());
                        assertEquals("500", newActionTitle.getLastIssue());

                        fakeComicRepository.getIssuesByTitleOnce("Action Comics",
                                new IssuesListener() {
                                    @Override
                                    public void onIssuesReady(List<Issue> issues) {
                                        assertNotNull(issues);
                                        assertEquals(2, issues.size());

                                        Issue actionComics400 = issues.stream()
                                                .filter((i) -> i.getIssueNumber().equals("400"))
                                                .findFirst()
                                                .get();
                                        assertNotNull(actionComics400);

                                        Issue actionComics500 = issues.stream()
                                                .filter((i) -> i.getIssueNumber().equals("500"))
                                                .findFirst()
                                                .get();
                                        assertNotNull(actionComics500);
                                    }

                                    @Override
                                    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {
                                    }

                                    @Override
                                    public void onIssueLoadFailed() {
                                    }

                                });
                    }

                    @Override
                    public void onTitleLoadFailed() {
                    }
                });

            });
        }
    }  /* onDialogClickEdit_ReduceRangeOnBothEnds_ReflectedInRepository */

    @Test
    public void onDialogClickEdit_IncreaseRangeOnBothEnds_ReflectedInRepository() {

        /*
        Increase the range on both ends.  The title in the repository should reflect the
        change.
         */
        try( ActivityScenario<TitlesActivity> activityScenario
                     = ActivityScenario.launch(TitlesActivity.class) )
        {
            /*
            batmanTitle is a reference to the Title in the repository and will be modified
            in the course of the call to the object under test.  We need a copy of it to
            return from the mock fragment.
             */
            Title newBatmanTitle = new Title(batmanTitle);
            when(mockEditTitleDialogFragment.getCurrentTitle()).thenReturn(newBatmanTitle);

            Title modifiedBatman = new Title();
            modifiedBatman.setName("Batman");
            modifiedBatman.setDocumentId(batmanTitle.getDocumentId());
            modifiedBatman.setFirstIssue("171");
            modifiedBatman.setLastIssue("400");
            when(mockEditTitleDialogFragment.getNewTitle()).thenReturn(modifiedBatman);

            activityScenario.onActivity(activity -> {
                activity.onDialogClickEdit(mockEditTitleDialogFragment);

                /*
                Check that the title's first and last issue has changed, and that the deleted
                issues are gone.
                 */
                fakeComicRepository.loadAndListenForTitles(new TitlesListener() {
                    @Override
                    public void onTitlesReady(List<Title> titles) {
                        assertNotNull(titles);
                        assertEquals(4, titles.size());

                        Title newBatmanTitle = titles.stream()
                                .filter((t) -> t.getName().equals("Batman"))
                                .findFirst()
                                .get();
                        assertNotNull(newBatmanTitle);
                        assertEquals("171", newBatmanTitle.getFirstIssue());
                        assertEquals("400", newBatmanTitle.getLastIssue());

                        fakeComicRepository.getIssuesByTitleOnce("Batman",
                                new IssuesListener() {
                                    @Override
                                    public void onIssuesReady(List<Issue> issues) {
                                        /*
                                        I set up the list with a Batman #313.  The original
                                        bounds were issues 183-391, so the expansion should
                                        have added issues 171-182 (12) and 392-400 (9).  So
                                        there should be 22 total.
                                         */
                                        assertNotNull(issues);
                                        assertEquals(22, issues.size());

                                        Issue batman313 = issues.stream()
                                                .filter((i) -> i.getIssueNumber().equals("313"))
                                                .findFirst()
                                                .get();
                                        assertNotNull(batman313);
                                    }

                                    @Override
                                    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {
                                    }

                                    @Override
                                    public void onIssueLoadFailed() {
                                    }

                                });
                    }

                    @Override
                    public void onTitleLoadFailed() {
                    }
                });

            });
        }
    }  /* onDialogClickEdit_IncreaseRangeOnBothEnds_ReflectedInRepository */

    @Test
    public void onDialogClickEdit_LeaveRangeSame_NoChangeInRepository() {

        /*
        Simulate hitting the Edit button without making any changes to the range.
         */
        try( ActivityScenario<TitlesActivity> activityScenario
                     = ActivityScenario.launch(TitlesActivity.class) ) {

            /*
            firestormTitle is a reference to the Title in the repository and will be modified
            in the course of the call to the object under test.  We need a copy of it to
            return from the mock fragment.
             */
            Title newFirestormTitle = new Title(firestormTitle);
            when(mockEditTitleDialogFragment.getCurrentTitle()).thenReturn(newFirestormTitle);

            /*
            The fragment will have access to a title object storing the particulars entered
            in the dialog.  In this test, they are the same as in the repository.
             */
            Title modifiedFirestormTitle = new Title();
            modifiedFirestormTitle.setName("Firestorm (1978)");
            modifiedFirestormTitle.setFirstIssue("1");
            modifiedFirestormTitle.setLastIssue("5");
            modifiedFirestormTitle.setDocumentId(firestormTitle.getDocumentId());
            when(mockEditTitleDialogFragment.getNewTitle()).thenReturn(modifiedFirestormTitle);

            activityScenario.onActivity( activity -> {
                activity.onDialogClickEdit(mockEditTitleDialogFragment);

                /*
                Make sure that the repository still has the same issue bounds for the title,
                the same issues (2, 4, and 5), and that it has not lost my owned copies
                for those issues.
                 */
                fakeComicRepository.loadAndListenForTitles(new TitlesListener() {
                    @Override
                    public void onTitlesReady(List<Title> titles) {
                        assertNotNull(titles);
                        assertEquals(4, titles.size());

                        fakeComicRepository.getIssuesByTitleOnce("Firestorm (1978)",
                                new IssuesListener() {
                                    @Override
                                    public void onIssuesReady(List<Issue> issues) {
                                        assertNotNull(issues);
                                        assertEquals(5, issues.size());

                                        String ownedIssues = issues.stream()
                                                .map(Issue::getIssueNumber)
                                                .map(Integer::parseInt)
                                                .sorted()
                                                .map(String::valueOf)
                                                .reduce("", (s1, s2) -> s1.isEmpty() ? s2 : s1 + "-" + s2);
                                        assertEquals("1-2-3-4-5", ownedIssues);

                                        ArrayList<Copy> myCopies =
                                                (ArrayList<Copy>) issues.stream()
                                                .flatMap(i -> i.getOwnedCopies().stream())
                                                .collect(Collectors.toList());
                                        assertEquals(3, myCopies.size());
                                    }

                                    @Override
                                    public void onIssueChangesReady(List<Issue> issuesToAddOrReplace, List<Issue> issuesToRemove) {
                                    }

                                    @Override
                                    public void onIssueLoadFailed() {
                                    }
                                });
                    }

                    @Override
                    public void onTitleLoadFailed() {
                    }
                });
            });
        }

    }  /* onDialogClickEdit_LeaveRangeSame_NoChangeInRepository */

    @Test
    public void onDialogClickEdit_ChangeTitleName_ReflectedInRepository() {

        /*
         talesOfSuspenseTitle is a reference to the Title in the repository and will be modified
         in the course of the call to the object under test.  We need a copy of it to
         return from the mock fragment.
         */
        Title newTalesOfSuspenseTitle = new Title(talesOfSuspenseTitle);
        String tosDocumentID = talesOfSuspenseTitle.getDocumentId();

        Title talesToAstonishTitle = new Title(talesOfSuspenseTitle);
        talesToAstonishTitle.setName("Tales to Astonish");

        when(mockEditTitleDialogFragment.getCurrentTitle()).thenReturn(newTalesOfSuspenseTitle);
        when(mockEditTitleDialogFragment.getNewTitle()).thenReturn(talesToAstonishTitle);

        try( ActivityScenario<TitlesActivity> activityScenario
                     = ActivityScenario.launch(TitlesActivity.class) ) {

            activityScenario.onActivity(activity -> {
                activity.onDialogClickEdit(mockEditTitleDialogFragment);

                fakeComicRepository.loadAndListenForTitles(new TitlesListener() {
                    @Override
                    public void onTitlesReady(List<Title> titles) {
                        assertNotNull(titles);
                        assertEquals(4, titles.size());

                        /*
                        The document from the original "Tales of Suspense" title should
                        now have the name "Tales to Astonish".
                         */
                        String titleName = titles.stream()
                                .filter((t) -> t.getDocumentId().equals(tosDocumentID))
                                .findFirst()
                                .get()
                                .getName();

                        assertEquals("Tales to Astonish", titleName);
                    }

                    @Override
                    public void onTitleLoadFailed() {
                    }
                });
            });
        }

    }  /* onDialogClickEdit_ChangeTitleName_ReflectedInRepository */

}  /* class ModifyTitleTest */
