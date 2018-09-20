package edu.gatech.seclass.sdpcryptogram.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;
import edu.gatech.seclass.utilities.ExternalWebService;

/**
 * Local EWS Proxy Service
 * Method names left intact to ease matching
 * Method return types and arguments were changed to app specific where possible
 */

public class EWSService {
    private final ExternalWebService mEWS;

    private static EWSService instance;

    public EWSService(ExternalWebService ews) {
        this.mEWS = ews;

    }

    public static EWSService getInstance() {
        if(instance == null) {
            ExternalWebService externalWebService = ExternalWebService.getInstance();
            instance = new EWSService(externalWebService);
        }

        return instance;
    }

    /**
     * Adds cryptogram to list and return string UID if successful.
     * @param solutionPhrase
     * @param cipherPhrase
     * @return cryptogram UID
     * @throws IllegalArgumentException
     */
    public String addCryptogramService(String solutionPhrase, String cipherPhrase) {
        return mEWS.addCryptogramService(cipherPhrase, solutionPhrase);
    }

    /**
     * get the list of cryptograms
     * @return
     */
    public List<Cryptogram> syncCryptogramService(){
        final List<String[]> externalCryptograms = mEWS.syncCryptogramService();
        final List<Cryptogram> localCryptograms = new ArrayList<>();
        for(String[] extCryptogram : externalCryptograms)
        {
            Cryptogram cryptogram = new Cryptogram(extCryptogram[2], extCryptogram[1]);
            cryptogram.setCryptogramUID(extCryptogram[0]);
            localCryptograms.add(cryptogram);
        }

        return new ArrayList<>(localCryptograms);
    }

    /**
     * Get the list of all player ratings,
     * sorted in descending order by the number of cryptograms solved (Req.#12)
     * @return
     */
    public List<Rating> syncRatingService(){
        final List<ExternalWebService.PlayerRating> externalRatings = mEWS.syncRatingService();
        final List<Rating> localRatings = new ArrayList<>();
        for(ExternalWebService.PlayerRating extRating : externalRatings){
            Rating rating = new Rating();
            //rating.username = extRating.NOPE; //username is NOT present in EWS.PlayerRating
            rating.firstname =  extRating.getFirstname();
            rating.lastname =  extRating.getLastname();
            rating.attemptedCryptogramsCount =  extRating.getStarted();
            rating.incorrectSubmissionsCount =  extRating.getIncorrect();
            rating.solvedCryptogramsCount =  extRating.getSolved();
            localRatings.add(rating);
        }

        Collections.sort(localRatings, new Comparator<Rating>() {
            @Override
            public int compare(Rating o1, Rating o2) {
                return new Integer(o2.solvedCryptogramsCount).compareTo(new Integer(o1.solvedCryptogramsCount));
            }
        });

        return new ArrayList<>(localRatings);
    }

    /**
     * Send to EWS player username plus parts of player rating as part of Rating struct
     * @return
     */
    public boolean updateRatingService(Rating playerRating){
        return mEWS.updateRatingService(playerRating.username,
                playerRating.firstname,
                playerRating.lastname,
                playerRating.solvedCryptogramsCount,
                playerRating.attemptedCryptogramsCount,
                playerRating.incorrectSubmissionsCount);
    }

    /**
     *
     * Get list of player usernames, unordered
     * @return
     */
    public List<String> playernameService(){
        return mEWS.playernameService();
    }

}
