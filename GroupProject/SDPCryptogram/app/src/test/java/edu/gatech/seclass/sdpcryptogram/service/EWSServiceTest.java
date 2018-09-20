package edu.gatech.seclass.sdpcryptogram.service;

import edu.gatech.seclass.sdpcryptogram.struct.Rating;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by Anton on 05.07.2017.
 */

public class EWSServiceTest {
    private static EWSService ewss;

    @BeforeClass
    public static void setUpBeforeTest() {
        ewss = EWSService.getInstance();
    }

    /* Test updateRatingService valid username ratings */
    @Test
    public void testUpdateRatingService1() {
        Rating newRating = new Rating();
        newRating.username = "UniqueUsername#435*342$#$%^&*&*";
        newRating.firstname = "First Name";
        newRating.lastname= "Last Name";
        newRating.solvedCryptogramsCount = 1;
        newRating.attemptedCryptogramsCount = 2;
        newRating.incorrectSubmissionsCount = 3;
        int ewssistingRatingsCount =  ewss.syncRatingService().size();

        // can add
        assertTrue(ewss.updateRatingService(newRating));
        assertEquals(ewssistingRatingsCount + 1, ewss.syncRatingService().size());

    }

    /* Test updateRatingService null username ratings */
    @Test
    public void testUpdateRatingService2() {
        Rating newRating = new Rating();
        newRating.firstname = "First Name";
        newRating.lastname= "Last Name";
        newRating.solvedCryptogramsCount = 1;
        newRating.attemptedCryptogramsCount = 2;
        newRating.incorrectSubmissionsCount = 3;

        int ewssistingRatingsCount = ewss.syncRatingService().size();

        // can't add
        assertFalse(ewss.updateRatingService(newRating));
        assertEquals(ewssistingRatingsCount, ewss.syncRatingService().size());

    }

    /* Test updateRatingService empty username ratings */
    @Test
    public void testUpdateRatingService3() {
        Rating newRating = new Rating();
        newRating.username = "";
        newRating.firstname = "First Name";
        newRating.lastname= "Last Name";
        newRating.solvedCryptogramsCount = 1;
        newRating.attemptedCryptogramsCount = 2;
        newRating.incorrectSubmissionsCount = 3;

        int ewssistingRatingsCount = ewss.syncRatingService().size();

        // can't add
        assertFalse(ewss.updateRatingService(newRating));
        assertEquals(ewssistingRatingsCount, ewss.syncRatingService().size());

    }

    /* Test syncRatingService sorting by solved# DESC */
    @Test
    public void testSyncRatingService1() {
        Rating newRating1 = new Rating();
        newRating1.username = "UniqueUsername1";
        newRating1.firstname = "First Name";
        newRating1.lastname= "Last Name";
        newRating1.solvedCryptogramsCount = Integer.MAX_VALUE;
        newRating1.attemptedCryptogramsCount = 2;
        newRating1.incorrectSubmissionsCount = 3;

        Rating newRating2 = new Rating();
        newRating2.username = "UniqueUsername2";
        newRating2.firstname = "First Name";
        newRating2.lastname= "Last Name";
        newRating2.solvedCryptogramsCount = -1;
        newRating2.attemptedCryptogramsCount = 2;
        newRating2.incorrectSubmissionsCount = 3;

        assertTrue(ewss.updateRatingService(newRating1));
        assertTrue(ewss.updateRatingService(newRating2));
        // added
        assertTrue(ewss.syncRatingService().size() >= 2);

        List<Rating> fetchedRatings = ewss.syncRatingService();
        for(int i = 0; i < fetchedRatings.size()-1; i++){ //size at least 2
            assertTrue(fetchedRatings.get(i).solvedCryptogramsCount >=
                    fetchedRatings.get(i+1).solvedCryptogramsCount);
        }

    }

    /* Test syncRatingService sorting by solved# DESC */
    @Test
    public void testSyncRatingService2() {
        Rating newRating1 = new Rating();
        newRating1.username = "UniqueUsername1";
        newRating1.firstname = "First Name";
        newRating1.lastname= "Last Name";
        newRating1.solvedCryptogramsCount = -1;
        newRating1.attemptedCryptogramsCount = 2;
        newRating1.incorrectSubmissionsCount = 3;

        Rating newRating2 = new Rating();
        newRating2.username = "UniqueUsername2";
        newRating2.firstname = "First Name";
        newRating2.lastname= "Last Name";
        newRating2.solvedCryptogramsCount = Integer.MAX_VALUE;
        newRating2.attemptedCryptogramsCount = 2;
        newRating2.incorrectSubmissionsCount = 3;

        assertTrue(ewss.updateRatingService(newRating1));
        assertTrue(ewss.updateRatingService(newRating2));
        // updated solved#
        assertTrue(ewss.syncRatingService().size() >= 2);

        List<Rating> fetchedRatings = ewss.syncRatingService();
        for(int i = 0; i < fetchedRatings.size()-1; i++){ //size at least 2
            assertTrue(fetchedRatings.get(i).solvedCryptogramsCount >=
                    fetchedRatings.get(i+1).solvedCryptogramsCount);
        }

    }

    /* Test syncRatingService sorting by solved# DESC */
    @Test
    public void testSyncRatingService3() {
        Rating newRating1 = new Rating();
        newRating1.username = "UniqueUsername3";
        newRating1.firstname = "First Name3";
        newRating1.lastname= "Last Name3";
        newRating1.solvedCryptogramsCount = Integer.MIN_VALUE;
        newRating1.attemptedCryptogramsCount = 2;
        newRating1.incorrectSubmissionsCount = 3;

        assertTrue(ewss.updateRatingService(newRating1));
        // added
        assertTrue(ewss.syncRatingService().size() >= 2);

        List<Rating> fetchedRatings = ewss.syncRatingService();
        for(int i = 0; i < fetchedRatings.size()-1; i++){ //size at least 2
            assertTrue(fetchedRatings.get(i).solvedCryptogramsCount >=
                    fetchedRatings.get(i+1).solvedCryptogramsCount);
        }

    }

}
