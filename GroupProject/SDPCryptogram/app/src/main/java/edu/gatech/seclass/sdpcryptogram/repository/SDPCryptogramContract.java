package edu.gatech.seclass.sdpcryptogram.repository;

import android.provider.BaseColumns;

/**
 * Database schema declaration
 */
public final class SDPCryptogramContract {

	public SDPCryptogramContract(){}
	public static final String DATABASE_NAME = "SDPCryptogram.db";
	
	/**
	 * Cryptogram table
	 */
	
    public static abstract class Cryptogram implements BaseColumns {
        public static final String TABLE_NAME = "Cryptogram";
        public static final String COLUMN_NAME_ENCODED_PHRASE = "encodedPhrase";
        public static final String COLUMN_NAME_SOLUTION_PHRASE = "solutionPhrase";
        public static final String COLUMN_NAME_UID = "CryptogramUID"; // EWS::addCryptogramService uuid
    }
    
    /**
     * 
     * Player table
     */
    
    public static abstract class Player implements BaseColumns {
        public static final String TABLE_NAME = "Player";
        public static final String COLUMN_NAME_FIRST_NAME = "firstName";
        public static final String COLUMN_NAME_LAST_NAME = "lastName";
        public static final String COLUMN_NAME_USERNAME = "userName";
        public static final String COLUMN_NAME_PASSWORD = "pass";
    }
    
    /**
     * 
     * CryptogramAttempt table
     */
    
    public static abstract class CryptogramAttempt implements BaseColumns {
        public static final String TABLE_NAME = "CryptogramAttempt";
        public static final String COLUMN_NAME_PLAYER_ID = "playerId"; // Player FK
        public static final String COLUMN_NAME_CRYPTOGRAM_ID = "cryptogramId"; // Cryptogram FK
        public static final String COLUMN_NAME_SOLUTION_PHRASE = "solutionPhrase";
        public static final String COLUMN_NAME_SUBMISSION_COUNT = "submissionCount";
        public static final String COLUMN_NAME_INCORRECT_SUBMISSION_COUNT = "incorrectSubmissionCount";
        public static final String COLUMN_NAME_MOST_RECENT_SUBMISSION = "mostRecentSubmission";
        public static final String COLUMN_NAME_IS_SOLVED = "isSolved";
    }
    
}
