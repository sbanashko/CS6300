package edu.gatech.seclass.sdpcryptogram.service;

import android.content.Context;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.exception.InvalidPlayerParametersException;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;
import edu.gatech.seclass.utilities.ExternalWebService;

/**
 * Created by Nick Marsh on 7/2/2017.
 */

public class AdministratorService {

    private final PlayerRepository playerRepository;

    private final CryptogramRepository cryptogramRepository;
    private final ExternalWebService externalWebService;

    private static AdministratorService instance;

    //Test-only method
    public AdministratorService(PlayerRepository playerRepository, CryptogramRepository cryptogramRepository, ExternalWebService externalWebService) {
        this.playerRepository = playerRepository;
        this.cryptogramRepository = cryptogramRepository;
        this.externalWebService = externalWebService;
    }

    //Test-only method
    protected static void setInstance(AdministratorService administratorService) {
        instance = administratorService;
    }

    public static AdministratorService getInstance(Context context) {
        if(instance == null) {
            PlayerRepository playerRepo = PlayerRepository.getPlayerRepository(context);
            CryptogramRepository cryptoRepo = CryptogramRepository.getCryptogramRepository(context);
            ExternalWebService externalWebService = ExternalWebService.getInstance();
            instance = new AdministratorService(playerRepo, cryptoRepo, externalWebService);
        }

        return instance;
    }

    public Player addPlayer(String username, String password, String firstName, String lastName) throws InvalidPlayerParametersException{
        Boolean isValid = true;
        String usernameError = "";
        String passwordError = "";

        if(username == null || username.isEmpty() || username.contains(" ") || username.contains("\"")) {
            isValid = false;
            usernameError = "This username is not valid!";
        }

        if(password == null || password.isEmpty() || password.contains(" ") || password.contains("\"") || !Player.isPasswordValid(password)) {
            isValid = false;
            passwordError = "This password is not valid!";
        }

        //Technically we don't need to do this, as the repository will barf if we try to add a duplicate UN
        //However, checkPlayer is pretty cheap, so we can do it here and have a nice pretty early exit condition.
        if(playerRepository.checkPlayer(username) > 0) {
            isValid = false;
            usernameError = "This username is not unique!"; //The previous error condition precludes this one, so don't worry about overwrite.
        }

        if(!isValid) {
            throw new InvalidPlayerParametersException("Invalid parameters!", usernameError, passwordError);
        }

        Player player = new Player();
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setUsername(username);
        player.setPassword(password);

        Long id = playerRepository.addPlayer(player);

        return playerRepository.getPlayer(id);
    }

    public Cryptogram addCryptogram(String solutionPhrase, String cipherPhrase) {

        if(solutionPhrase == null || solutionPhrase.isEmpty()) {
            throw new InvalidParameterException("Solution cannot be empty!");
        }

        Cryptogram cryptogram = new Cryptogram(solutionPhrase, cipherPhrase);
        try {
            cryptogram.setCryptogramUID(externalWebService.addCryptogramService(cipherPhrase, solutionPhrase));
        } catch(IllegalArgumentException iae) {
            System.out.println("EWS Rejected cryptogram: " + iae.getMessage());
            throw iae;
        }
        try {
            Long newId = cryptogramRepository.addCryptogram(cryptogram);
            return cryptogramRepository.getCryptogram(newId);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
