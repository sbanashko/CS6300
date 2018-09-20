package edu.gatech.seclass.sdpcryptogram.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.security.InvalidParameterException;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.exception.InvalidPlayerParametersException;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;
import edu.gatech.seclass.utilities.ExternalWebService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Nick Marsh on 7/4/2017.
 */

public class AdministratorServiceTest {
    @Mock
    private CryptogramRepository cryptogramRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private ExternalWebService externalWebService;

    AdministratorService administratorService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        administratorService = new AdministratorService(playerRepository, cryptogramRepository, externalWebService);
        AdministratorService.setInstance(administratorService);
    }

    @After
    public void tearDown() {
        Mockito.reset(cryptogramRepository, playerRepository, externalWebService);
    }

    @Test
    public void testAddPlayerSuccessful() {
        String username = "aUser";
        String password = "passMe";
        String firstName = "firsties";
        String lastName = "lasties";
        Long id = 1L;
        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        Player expectedPlayer = new Player(username, password, firstName, lastName, id);

        when(playerRepository.addPlayer(any(Player.class))).thenReturn(id);
        when(playerRepository.getPlayer(id)).thenReturn(expectedPlayer);

        Player player = administratorService.addPlayer(username, password, firstName, lastName);
        verify(playerRepository, times(1)).addPlayer(eq(inputPlayer));
        assertTrue(expectedPlayer.equals(player));
    }

    @Test
    public void testAddPlayerBlankUsername() {
        String username = "";
        String password = "passMe";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "");
            assertEquals(ipe.getUsernameError(), "This username is not valid!");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerBlankUsernameHasSpaces() {
        String username = "A User";
        String password = "passMe";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "");
            assertEquals(ipe.getUsernameError(), "This username is not valid!");
            return;
        }

        fail();
    }

    @Test
    public void testAddPlayerBlankUsernameHasQuotes() {
        String username = "A\"User";
        String password = "passMe";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "");
            assertEquals(ipe.getUsernameError(), "This username is not valid!");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerBlankPassword() {
        String username = "user";
        String password = "";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "This password is not valid!");
            assertEquals(ipe.getUsernameError(), "");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerPasswordHasSpaces() {
        String username = "user";
        String password = "Pass Word";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "This password is not valid!");
            assertEquals(ipe.getUsernameError(), "");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerPasswordHasQuotes() {
        String username = "user";
        String password = "Pass\"Word";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "This password is not valid!");
            assertEquals(ipe.getUsernameError(), "");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerBadUnAndPass() {
        String username = "user\"name";
        String password = "Pass\"Word";
        String firstName = "firsties";
        String lastName = "lasties";

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "This password is not valid!");
            assertEquals(ipe.getUsernameError(), "This username is not valid!");
            return;
        }

        fail();
    }
    @Test
    public void testAddPlayerDuplicate() {
        String username = "username";
        String password = "PassWord";
        String firstName = "firsties";
        String lastName = "lasties";

        when(playerRepository.checkPlayer(username)).thenReturn(1L);

        Player inputPlayer = new Player(username, password, firstName, lastName, null);
        try {
            Player player = administratorService.addPlayer(username, password, firstName, lastName);
        } catch(InvalidPlayerParametersException ipe) {
            verify(playerRepository, times(0)).addPlayer(any(Player.class));
            verify(playerRepository, times(0)).getPlayer(any(Long.class));
            assertEquals(ipe.getPasswordError(), "");
            assertEquals(ipe.getUsernameError(), "This username is not unique!");
            return;
        }

        fail();
    }

    @Test
    public void testAddCryptogram() {
        String solution = "solution";
        String cipher = "cipher";
        String uidFromEws = "5";
        Long id = 1L;
        Cryptogram expectedCryptogram = new Cryptogram(cipher, solution, id, uidFromEws);
        Cryptogram addedCryptogram = new Cryptogram(cipher, solution, null, uidFromEws);

        when(externalWebService.addCryptogramService(eq(cipher), eq(solution))).thenReturn(uidFromEws);
        when(cryptogramRepository.addCryptogram(any(Cryptogram.class))).thenReturn(id);
        when(cryptogramRepository.getCryptogram(id)).thenReturn(expectedCryptogram);

        Cryptogram result = administratorService.addCryptogram(solution, cipher);

        verify(externalWebService, times(1)).addCryptogramService(cipher, solution);
        verify(cryptogramRepository, times(1)).addCryptogram(eq(addedCryptogram));
        assertTrue(result.equals(expectedCryptogram));
    }

    @Test
    public void testAddCryptogramNoSolution() {
        String solution = "";
        String cipher = "cipher";
        try {
            administratorService.addCryptogram(solution, cipher);
        } catch(InvalidParameterException ipe) {
            assertEquals(ipe.getMessage(), "Solution cannot be empty!");
            verify(externalWebService, times(0)).addCryptogramService(any(String.class), any(String.class));
            verify(cryptogramRepository, times(0)).getCryptogram(any(Long.class));
            return;
        }
        fail("Exception was not thrown!");
    }
    @Test
    public void testAddCryptogramEwsException() {
        String solution = "solution";
        String cipher = "cipher";

        when(externalWebService.addCryptogramService(cipher, solution)).thenThrow(new IllegalArgumentException("Nope"));
        try {
            administratorService.addCryptogram(solution, cipher);
        } catch(IllegalArgumentException iae) {
            assertEquals(iae.getMessage(), "Nope");
            verify(cryptogramRepository, times(0)).getCryptogram(any(Long.class));
            return;
        }
        fail("Exception was not thrown!");
    }
}
