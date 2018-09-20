package edu.gatech.seclass.sdpcryptogram.util;

import java.util.regex.Pattern;

import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmKeyException;
import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmMessageException;

/**
 * Simple Substitution Cipher algorithm
 */
public final class SimpleSubstitutionCipherService {
    private static final String INVALID_MESSAGE_ERROR_TEXT = "Invalid Message";
    private static final String INVALID_SHIFT_NUMBER_ERROR_TEXT = "Invalid Shift Number";

    private static final String ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHABET_LOWER = ALPHABET_UPPER.toLowerCase();
    private static final int ALPHABET_LENGTH = ALPHABET_UPPER.length();
    private static final int MIN_KEY = 1;
    private static final int MAX_KEY = 25;

    /**
     * Utility class, non-instantiable
     */
    private SimpleSubstitutionCipherService() { }

    /**
     * Encodes a message by shifting forward in the alphabet
     * @param msg Message to be encoded
     * @param key Cipher key (#chars to shift the alphabet)
     * @return Encoded message
     */
    public static String encode(final String msg, final int key){

        //fail fast
        validateKey(key);
        validateMessage(msg);

        StringBuilder resultMsg = new StringBuilder();

        for(int i = 0; i < msg.length(); i++){

            char curSymbol = msg.charAt(i);
            int curSymbolIndex = -1;

            curSymbolIndex = ALPHABET_UPPER.indexOf(curSymbol);
            if(curSymbolIndex != -1){
                resultMsg.append(ALPHABET_UPPER.charAt((curSymbolIndex + key) % ALPHABET_LENGTH));
                continue;
            }

            curSymbolIndex = ALPHABET_LOWER.indexOf(curSymbol);
            if(curSymbolIndex != -1){
                resultMsg.append(ALPHABET_LOWER.charAt((curSymbolIndex + key) % ALPHABET_LENGTH));
                continue;
            }

            resultMsg.append(curSymbol);

        }

        return resultMsg.toString();
    }

    /**
     * Decodes a message by shifting backward in the alphabet
     * @param msg Message to be decoded
     * @param key Cipher key (#chars to shift the alphabet)
     * @return Decoded message
     */
    public static String decode(final String msg, final int key){

        //fail fast
        validateKey(key);
        validateMessage(msg);

        StringBuilder resultMsg = new StringBuilder();

        for(int i = 0; i < msg.length(); i++){

            char curSymbol = msg.charAt(i);
            int curSymbolIndex = -1;

            curSymbolIndex = ALPHABET_UPPER.indexOf(curSymbol);
            if(curSymbolIndex != -1){
                resultMsg.append(ALPHABET_UPPER.charAt((curSymbolIndex - key + ALPHABET_LENGTH) % ALPHABET_LENGTH));
                continue;
            }

            curSymbolIndex = ALPHABET_LOWER.indexOf(curSymbol);
            if(curSymbolIndex != -1){
                resultMsg.append(ALPHABET_LOWER.charAt((curSymbolIndex - key + ALPHABET_LENGTH) % ALPHABET_LENGTH));
                continue;
            }

            resultMsg.append(curSymbol);

        }

        return resultMsg.toString();
    }

    /**
     * Cipher key validator helper method
     * key must be an integer between 1 and 25, inclusive
     *
     * @param key cipher key (#chars to shift the alphabet)
     */
    public static void validateKey(final int key){

        // check cipher key validity
        if(key < MIN_KEY || key > MAX_KEY){
            throw new IllegalCryptoAlgorithmKeyException(INVALID_SHIFT_NUMBER_ERROR_TEXT); //"Invalid Shift Number"
        }

    }

    /**
     * Message validator helper method
     * message must be not empty, must contain some letters
     *
     * @param msg Message to be encoded/decoded
     */
    public static void validateMessage(final String msg){

        // check message is not empty, must contain some letters
        Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*", Pattern.DOTALL);
        if(msg == null || msg.isEmpty() || !pattern.matcher(msg).matches()){ //!msg.matches(".*[a-zA-Z]+.*")
            throw new IllegalCryptoAlgorithmMessageException(INVALID_MESSAGE_ERROR_TEXT); //"Invalid Message"
        }
    }

}
