package edu.gatech.seclass.sdpcryptogram.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;
import edu.gatech.seclass.utilities.ExternalWebService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Nick on 7/11/2017.
 */

public class CryptogramServiceTest {

    @Mock
    CryptogramRepository cryptogramRepository;

    @Mock
    EWSService externalWebService;

    @Mock
    Rating rating;


    CryptogramService cryptogramService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cryptogramService = new CryptogramService(cryptogramRepository, externalWebService);
    }

    @After
    public void tearDown() {
        Mockito.reset(cryptogramRepository, externalWebService);
    }

    @Test
    public void testUID() {
        Cryptogram cryptogram = new Cryptogram("aaaa", "bbbb", 1L, "UID");

        assertEquals(cryptogram.getCryptogramUID(), "UID");
    }

    @Test
    public void testSuccessfulSubmit() {

        Long playerId = 2L;
        Long caId = 1L;
        Long cId = 5L;
        CryptogramAttempt cryptogramAttempt = new CryptogramAttempt();
        cryptogramAttempt.setCryptogramId(cId);
        cryptogramAttempt.setId(caId);
        cryptogramAttempt.setIncorrectSubmissionCount(1);
        cryptogramAttempt.setMostRecentSubmission("abc");
        cryptogramAttempt.setPlayerId(playerId);
        cryptogramAttempt.setSolved(false);

        Cryptogram cryptogram = new Cryptogram("aaaa", "zzzz", cId, "UID");

        Player player = new Player();
        player.setPlayerId(playerId);

        when(cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt)).thenReturn(caId);
        when(externalWebService.updateRatingService(any(Rating.class))).thenReturn(true);
        when(cryptogramRepository.getPlayerRating(player)).thenReturn(rating);

        CryptogramAttempt attemptResult = cryptogramService.submitSolution(cryptogram, "zzzz", cryptogramAttempt, player);

        verify(cryptogramRepository).saveOrUpdateCryptogramAttempt(eq(attemptResult));
        verify(externalWebService).updateRatingService(rating);

        assertEquals(attemptResult.getSolved(), true);
        assertEquals(attemptResult.getIncorrectSubmissionCount(), new Integer(1));
        assertEquals(attemptResult.getMostRecentSubmission(), "zzzz");
    }


    @Test
    public void testFailedSubmit() {

        Long playerId = 2L;
        Long caId = 1L;
        Long cId = 5L;
        CryptogramAttempt cryptogramAttempt = new CryptogramAttempt();
        cryptogramAttempt.setCryptogramId(cId);
        cryptogramAttempt.setId(caId);
        cryptogramAttempt.setIncorrectSubmissionCount(1);
        cryptogramAttempt.setMostRecentSubmission("abc");
        cryptogramAttempt.setPlayerId(playerId);
        cryptogramAttempt.setSolved(false);

        Cryptogram cryptogram = new Cryptogram("aaaa", "zzzz", cId, "UID");

        Player player = new Player();
        player.setPlayerId(playerId);

        when(cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt)).thenReturn(caId);
        when(externalWebService.updateRatingService(any(Rating.class))).thenReturn(true);
        when(cryptogramRepository.getPlayerRating(player)).thenReturn(rating);

        CryptogramAttempt attemptResult = cryptogramService.submitSolution(cryptogram, "bbbb", cryptogramAttempt, player);

        verify(cryptogramRepository).saveOrUpdateCryptogramAttempt(eq(attemptResult));
        verify(externalWebService).updateRatingService(rating);

        assertEquals(attemptResult.getSolved(), false);
        assertEquals(attemptResult.getIncorrectSubmissionCount(), new Integer(2));
        assertEquals(attemptResult.getMostRecentSubmission(), "bbbb");
    }
    @Test
    public void testNullSubmit() {

        Long playerId = 2L;
        Long caId = 1L;
        Long cId = 5L;
        CryptogramAttempt cryptogramAttempt = new CryptogramAttempt();
        cryptogramAttempt.setCryptogramId(cId);
        cryptogramAttempt.setId(caId);
        cryptogramAttempt.setIncorrectSubmissionCount(1);
        cryptogramAttempt.setMostRecentSubmission("abc");
        cryptogramAttempt.setPlayerId(playerId);
        cryptogramAttempt.setSolved(false);

        Cryptogram cryptogram = new Cryptogram("aaaa", "zzzz", cId, "UID");

        Player player = new Player();
        player.setPlayerId(playerId);

        when(cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt)).thenReturn(caId);
        when(externalWebService.updateRatingService(any(Rating.class))).thenReturn(true);
        when(cryptogramRepository.getPlayerRating(player)).thenReturn(rating);

        CryptogramAttempt attemptResult = cryptogramService.submitSolution(cryptogram, null, cryptogramAttempt, player);

        verify(cryptogramRepository).saveOrUpdateCryptogramAttempt(eq(attemptResult));
        verify(externalWebService).updateRatingService(rating);

        assertEquals(attemptResult.getSolved(), false);
        assertEquals(attemptResult.getIncorrectSubmissionCount(), new Integer(2));
        assertEquals(attemptResult.getMostRecentSubmission(), null);
    }
}
