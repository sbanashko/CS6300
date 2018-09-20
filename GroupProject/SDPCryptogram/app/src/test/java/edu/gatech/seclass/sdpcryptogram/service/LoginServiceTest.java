package edu.gatech.seclass.sdpcryptogram.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.exception.LoginException;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Created by Nick on 7/10/2017.
 */

public class LoginServiceTest {

    @Mock
    PlayerRepository playerRepository;

    LoginService loginService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        loginService = new LoginService(playerRepository);
    }

    @After
    public void tearDown() {
        Mockito.reset(playerRepository);
    }

    @Test
    public void testLogin() throws LoginException {
        String username = "user";
        String password = "passw";
        String fn = "firstName";
        String ln = "lastName";
        Long id = 1L;

        Player player = new Player(username, password, fn, ln, id);

        when(playerRepository.getPlayerByUsername(username)).thenReturn(player);
        when(playerRepository.checkPlayer(username)).thenReturn(1L);

        Player result = loginService.login(username, password);

        assertTrue(result.equals(player));
    }

    @Test
    public void testLoginNoUser() {
        String username = "user";
        String password = "passw";
        String fn = "firstName";
        String ln = "lastName";
        Long id = 1L;

        Player player = new Player(username, password, fn, ln, id);

        when(playerRepository.checkPlayer(username)).thenReturn(0L);
        Player result = null;
        try {
            result = loginService.login(username, password);
        } catch(LoginException le) {
            assertEquals("This username is not valid!", le.getUserError());
            assertEquals("", le.getPasswordError());
            assertNull(result);
            return;
        }
        fail();
    }

    @Test
    public void testLoginInvalidPw() {
        String username = "user";
        String password = "p";
        String fn = "firstName";
        String ln = "lastName";
        Long id = 1L;

        Player player = new Player(username, password, fn, ln, id);

        when(playerRepository.checkPlayer(username)).thenReturn(1L);
        Player result = null;
        try {
            result = loginService.login(username, password);
        } catch(LoginException le) {
            assertEquals("This password is not valid!", le.getPasswordError());
            assertEquals("", le.getUserError());
            assertNull(result);
            return;
        }
        fail();
    }

    @Test
    public void testLoginInvalidUn() {
        String username = "";
        String password = "passw";
        String fn = "firstName";
        String ln = "lastName";
        Long id = 1L;

        Player player = new Player(username, password, fn, ln, id);

        when(playerRepository.checkPlayer(username)).thenReturn(0L);
        Player result = null;
        try {
            result = loginService.login(username, password);
        } catch(LoginException le) {
            assertEquals("This username is not valid!", le.getUserError());
            assertEquals("", le.getPasswordError());
            assertNull(result);
            return;
        }
        fail();
    }

    @Test
    public void testLoginPwDontMatch() throws LoginException {
        String username = "username";
        String password = "passw";
        String fn = "firstName";
        String ln = "lastName";
        Long id = 1L;

        Player player = new Player(username, password, fn, ln, id);
        when(playerRepository.checkPlayer(username)).thenReturn(1L);
        when(playerRepository.getPlayerByUsername(username)).thenReturn(player);

        Player result = loginService.login(username, "abcde");
        assertNull(result);
    }
}
