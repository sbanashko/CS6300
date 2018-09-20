package edu.gatech.seclass.sdpcryptogram.service;

import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmKeyException;
import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmMessageException;
import edu.gatech.seclass.sdpcryptogram.util.SimpleSubstitutionCipherService;

/**
 * Created by Nick Marsh on 7/3/2017.
 */

public class CipherService {

    private static CipherService instance;

    public static CipherService getInstance() {
        if(instance == null) {
            instance = new CipherService();
        }
        return instance;
    }

    public String encode(String input, Integer shiftNumber) throws IllegalCryptoAlgorithmKeyException, IllegalCryptoAlgorithmMessageException {
        return SimpleSubstitutionCipherService.encode(input, shiftNumber);
    }

    public String decode(String input, Integer shiftNumber) throws IllegalCryptoAlgorithmKeyException, IllegalCryptoAlgorithmMessageException {
        return SimpleSubstitutionCipherService.decode(input, shiftNumber);
    }
}
