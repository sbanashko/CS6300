package edu.gatech.seclass.sdpcryptogram.service;

import android.content.Context;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.struct.CryptogramListItem;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Cryptogram service is responsible for talking to XXXRepository for local db data access
 * as well as to EWSService for additional cryptograms (if REQUEST_CRYPTOGRAMS_FROM_EWS = true)
 */

public class CryptogramService {
    private final static boolean REQUEST_CRYPTOGRAMS_FROM_EWS = true;
    private EWSService ewsService;

    private final CryptogramRepository cryptogramRepository;

    private static CryptogramService instance;

    protected CryptogramService(CryptogramRepository cryptogramRepository, EWSService externalWebService) {
        this.ewsService = externalWebService;
        this.cryptogramRepository = cryptogramRepository;
    }

    public static CryptogramService getInstance(Context context) {
        if (instance == null) {
            CryptogramRepository cryptoRepo = CryptogramRepository.getCryptogramRepository(context);
            EWSService ews =  EWSService.getInstance();
            instance = new CryptogramService(cryptoRepo, ews);
        }

        return instance;
    }

    /**
     * Get all cryptograms
     * (simplified implementation)
     * @return
     */
    public List<Cryptogram> getCryptograms() {
        // if need to request cryptograms from EWS
        if(REQUEST_CRYPTOGRAMS_FROM_EWS){
            mergeCryptograms();
        }
        return cryptogramRepository.getCryptograms();
    }

    /**
     * Get cryptogram items with stats calculated ready to display
     * @param player
     * @return
     */
    public List<CryptogramListItem> getCryptogramListItemsBy(Player player){
        // if need to request cryptograms from EWS
        if(REQUEST_CRYPTOGRAMS_FROM_EWS){
            mergeCryptograms();
        }
        return cryptogramRepository.getCryptogramListItemsBy(player);
    }

    public CryptogramAttempt attemptCryptogramForPlayer(Long cryptogramId, Long playerId) {
        CryptogramAttempt cryptogramAttempt = cryptogramRepository.getCryptogramAttemptForPlayer(playerId, cryptogramId);

        if(cryptogramAttempt == null) {
            cryptogramAttempt = new CryptogramAttempt();
            cryptogramAttempt.setCryptogramId(cryptogramId);
            cryptogramAttempt.setPlayerId(playerId);
            cryptogramAttempt.setSolved(false);
            cryptogramAttempt.setMostRecentSubmission("");
            cryptogramAttempt.setIncorrectSubmissionCount(0);
            cryptogramAttempt = saveCryptogramAttempt(cryptogramAttempt); //Persist it to the database.
        }

        return cryptogramAttempt;
    }


    public CryptogramAttempt saveCryptogramAttempt(CryptogramAttempt cryptogramAttempt) {

        Long id = cryptogramRepository.saveOrUpdateCryptogramAttempt(cryptogramAttempt);
        if(id > 0) {
            cryptogramAttempt.setId(id);
        } else {
            throw new RuntimeException("An error occurred while saving!");
        }

        return cryptogramAttempt;
    }

    public CryptogramAttempt submitSolution(Cryptogram cryptogram, String attemptedSolution, CryptogramAttempt cryptogramAttempt, Player player) {
        if(cryptogram.getSolutionPhrase().equals(attemptedSolution)) {
            return succeededAttempt(cryptogramAttempt, player, attemptedSolution);
        } else {
            return failedAttempt(cryptogramAttempt, player, attemptedSolution);
        }
    }

    private CryptogramAttempt succeededAttempt(CryptogramAttempt cryptogramAttempt, Player player, String attemptedSolution) {
        cryptogramAttempt.setSolved(true);
        cryptogramAttempt.setMostRecentSubmission(attemptedSolution);
        CryptogramAttempt updatedAttempt = saveCryptogramAttempt(cryptogramAttempt);
        updateEWS(player);
        return updatedAttempt;
    }

    private CryptogramAttempt failedAttempt(CryptogramAttempt cryptogramAttempt, Player player, String attemptedSolution) {
        cryptogramAttempt.setSolved(false);
        cryptogramAttempt.setMostRecentSubmission(attemptedSolution);
        cryptogramAttempt.setIncorrectSubmissionCount(cryptogramAttempt.getIncorrectSubmissionCount()+1);
        CryptogramAttempt updatedAttempt = saveCryptogramAttempt(cryptogramAttempt);
        updateEWS(player);
        return updatedAttempt;
    }


    public void updateEWS(Player player) {
        Rating playerRating = cryptogramRepository.getPlayerRating(player);
        ewsService.updateRatingService(playerRating);
    }

    /**
     * Helper: merges cryptogram list from EWS into local db
     * @return Number of cryptograms added to local db
     */
    private long mergeCryptograms(){
        // fetch list from EWS
        final List<Cryptogram> ewsCryptograms = ewsService.syncCryptogramService();
        // pass it to local db via CryptogramRepository allowing it to take care of syncing
        return cryptogramRepository.addCryptograms(ewsCryptograms);
    }
}
