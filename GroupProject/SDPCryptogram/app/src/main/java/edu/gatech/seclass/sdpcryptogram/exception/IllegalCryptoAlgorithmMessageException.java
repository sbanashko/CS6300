package edu.gatech.seclass.sdpcryptogram.exception;


/**
 *  Thrown when cryptogram text includes *only* non-alphabetic characters
 * (see Req.#7)
 */

public class IllegalCryptoAlgorithmMessageException extends IllegalArgumentException {

    public IllegalCryptoAlgorithmMessageException(String message) {
        super(message);
    }

    public IllegalCryptoAlgorithmMessageException() {
        super();
    }
}
