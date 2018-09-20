package edu.gatech.seclass.sdpcryptogram.service;

import android.content.Context;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;

/**
 * Created by Anton on 10.07.2017.
 */

public class PlayerService {

    private final CryptogramRepository cryptogramRepository;
    private final PlayerRepository playerRepository;

    private static PlayerService instance;

    //Test-only method
    public PlayerService(PlayerRepository playerRepository, CryptogramRepository cryptogramRepository) {
        this.playerRepository = playerRepository;
        this.cryptogramRepository = cryptogramRepository;
    }


    public static PlayerService getInstance(Context context) {
        if(instance == null) {
            PlayerRepository playerRepo = PlayerRepository.getPlayerRepository(context);
            CryptogramRepository cryptoRepo = CryptogramRepository.getCryptogramRepository(context);
            instance = new PlayerService(playerRepo, cryptoRepo);
        }

        return instance;
    }

    public long addPlayer(final Player player) {
        return playerRepository.addPlayer(player);
    }

    /**
     * Seems like test-only under current implementation of cryptogram attempt
     * Initiates new empty cryptogram attempt
     * @param cryptogram
     * @return
     */
    public CryptogramAttempt attemptCryptogram(final Player player, final Cryptogram cryptogram){
        return playerRepository.attemptCryptogram(player, cryptogram);
    }

    /**
     * Seems like test-only under current implementation of cryptogram attempt
     * Submits cryptogram attempt and verifies the solution
     * @param cryptogramAttempt
     * @return True if solution is correct
     */
    public boolean submitCryptogramAttempt(final CryptogramAttempt cryptogramAttempt){
        // get cryptogram
        Cryptogram crytogram = cryptogramRepository.getCryptogram(cryptogramAttempt.getCryptogramId());
        // verify proposed solution
        boolean isSolutionCorrect = crytogram.verifySolution(cryptogramAttempt.getMostRecentSubmission());

        // update accordingly
        cryptogramAttempt.setSolved(isSolutionCorrect);
        if(!isSolutionCorrect) {
            int currentIncorrectSubmissions = cryptogramAttempt.getIncorrectSubmissionCount();
            cryptogramAttempt.setIncorrectSubmissionCount(currentIncorrectSubmissions++);
        }

        cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt);
        return isSolutionCorrect;
    }

    /**
     * Seems like test-only under current implementation of cryptogram attempt
     * Saves cryptogram attempt w/o solution verification
     * @param cryptogramAttempt
     */
    public void saveCryptogramAttempt(final CryptogramAttempt cryptogramAttempt){
        // just persist whatever we got
        cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt);
    }

    /**
     * Seems like test-only under current implementation of cryptogram attempt
     * Returns cryptogramAttempt for <Player, Cryptogram> pair
     * @param player
     * @param cryptogram
     * @return CryptogramAttempt
     */
    public CryptogramAttempt getCryptogramAttemptForPlayer(Player player, Cryptogram cryptogram){
        return cryptogramRepository.getCryptogramAttemptForPlayer(player.getPlayerId(), cryptogram.getCryptogramId());
    }
}
