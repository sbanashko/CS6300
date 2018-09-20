package edu.gatech.seclass.sdpcryptogram.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.exception.LoginException;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;

/**
 * Created by Nick on 7/10/2017.
 */

public class LoginService {

    private static LoginService instance;

    private PlayerRepository playerRepository;

    protected LoginService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public static LoginService getInstance(Context context) {
        if(instance == null) {
            PlayerRepository playerRepository = PlayerRepository.getPlayerRepository(context);
            instance = new LoginService(playerRepository);
        }

        return instance;
    }

    public Player login(String username, String password) throws LoginException {
        boolean loginValid = isLoginValid(username);
        boolean passwordValid = isPasswordValid(password);

        if(!loginValid || !passwordValid) {
            String loginError = loginValid ? "" : "This username is not valid!";
            String passwordError = passwordValid ? "" : "This password is not valid!";
            throw new LoginException(loginError, passwordError);
        }

        Player player = playerRepository.getPlayerByUsername(username);

        if(player != null && password.equals(player.getPassword())) {
            return player;
        }
        return null;
    }

    private boolean isLoginValid(String login) {
        return ((login != null) && (!login.contains(" ")) &&
                (!login.contains("\"")) &&
                (playerRepository.checkPlayer(login) > 0));
    }

    private boolean isPasswordValid (String password) {
        return (isStringEmpty(password) || Player.isPasswordValid(password));
    }

    /**
     * Copied from TextUtils to make this service testable
     * See {@link http://tools.android.com/tech-docs/unit-testing-support#TOC-Method-...-not-mocked.-}
     */
    private boolean isStringEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
