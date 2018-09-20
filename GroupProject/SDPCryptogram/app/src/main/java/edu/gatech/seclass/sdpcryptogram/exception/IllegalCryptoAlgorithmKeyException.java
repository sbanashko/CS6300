package edu.gatech.seclass.sdpcryptogram.exception;

/**
 * Thrown when encryption key is out of alphabet bounds
 * (see Req.#7)
 */

public class IllegalCryptoAlgorithmKeyException extends IllegalArgumentException {

    public IllegalCryptoAlgorithmKeyException(String message) {
        super(message);
    }

    public IllegalCryptoAlgorithmKeyException() {
        super();
    }
}
