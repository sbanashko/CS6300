package edu.gatech.seclass.sdpcryptogram.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.struct.CryptogramListItem;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Cryptogram Repository class
 */

public class CryptogramRepository {
    public static final String LOG_TAG = CryptogramRepository.class.getSimpleName();
//    private static SQLiteDatabase db;
    private static CryptogramRepository instance;
    private static SDPCryptogramDbHelper dbHelper;


    public CryptogramRepository(){}

    public static String test() {
        return "test";
    }

    /**
     * UserRepository producer
     * @param context
     * @return CryptogramRepository object
     */
    public static CryptogramRepository getCryptogramRepository(Context context){
        if(instance == null){
            instance = new CryptogramRepository();
            instance.dbHelper = new SDPCryptogramDbHelper(context);
        }
        return instance;
    }

    public long addCryptogram(final Cryptogram cryptogram){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SDPCryptogramContract.Cryptogram.COLUMN_NAME_SOLUTION_PHRASE, cryptogram.getSolutionPhrase());
        cv.put(SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE, cryptogram.getCipherPhrase());
        cv.put(SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID, cryptogram.getCryptogramUID());
        long newId = db.insertOrThrow(SDPCryptogramContract.Cryptogram.TABLE_NAME, null, cv);
        db.close();
        Log.d(LOG_TAG, String.valueOf(newId));
        return newId;
    }

    public Long saveOrUpdateCryptogramAttempt(CryptogramAttempt cryptogramAttempt) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Long id = null;
        ContentValues cv = new ContentValues();
        cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID, cryptogramAttempt.getCryptogramId());
        cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT, cryptogramAttempt.getIncorrectSubmissionCount());
        cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED, cryptogramAttempt.getSolved() ? 1 : 0); // better be explicit
        cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_MOST_RECENT_SUBMISSION, cryptogramAttempt.getMostRecentSubmission());
        cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID, cryptogramAttempt.getPlayerId());
        //There's vomit on his sweater already, mom's spaghetti
        if(cryptogramAttempt.getId() != null) {
            //Update
            int numUpdated = db.update(
                    SDPCryptogramContract.CryptogramAttempt.TABLE_NAME,
                    cv,
                    SDPCryptogramContract.CryptogramAttempt._ID + "=?",
                    new String[]{cryptogramAttempt.getId().toString()});
            if(numUpdated != 1) {
                throw new RuntimeException("Failed to update exactly one row by unique id " + cryptogramAttempt.getCryptogramId());
            }
            db.close();
            id = cryptogramAttempt.getId();
        } else {

            id = db.insertOrThrow(SDPCryptogramContract.CryptogramAttempt.TABLE_NAME, null, cv);
            db.close();

        }
        return id;
    }

    /**
     * Put into db an external list of cryptograms (possibly duplicates)
     * @param cryptograms
     * @return No. of cryptograms actually added
     */
    public long addCryptograms(final List<Cryptogram> cryptograms){
        long addedCount = 0;
        // dummy: just rely on UNIQUE constraint on cryptogramUID that silently disregards INSERTs
        for(Cryptogram cryptogram : cryptograms){
            try{
                long newId = addCryptogram(cryptogram);
                addedCount++;
            } catch(SQLException ex){
                // do nothing
            }
        }

        return addedCount;
    }

    public List<Cryptogram> getCryptograms(){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<Cryptogram> cryptograms = new ArrayList<>();

        // get a list of cryptograms (most recent on top)
        Cursor cur = db.query(
                SDPCryptogramContract.Cryptogram.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                SDPCryptogramContract.Cryptogram._ID + " DESC"
        );

        while(cur.moveToNext()){

            long id = cur.getLong(cur.getColumnIndex(SDPCryptogramContract.Cryptogram._ID));
            String uid = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID));
            String cipher = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE));
            String solution= cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_SOLUTION_PHRASE));

            Cryptogram cryptogram = new Cryptogram(solution, cipher);
            cryptogram.setCryptogramId(id);
            cryptogram.setCryptogramUID(uid);

            cryptograms.add(cryptogram);
        }
        // Always close the previous mCursor first
        if (cur != null) {
            cur.close();
        }

        return new ArrayList<>(cryptograms);
    }

    public Cryptogram getCryptogram(Long id) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cur = db.query(
                SDPCryptogramContract.Cryptogram.TABLE_NAME,
                null,
                SDPCryptogramContract.Cryptogram._ID + "=?",
                new String[] {id.toString()},
                null,
                null,
                null
        );

        Cryptogram cryptogram = null;
        if(cur.moveToNext()){

            String cipher = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE));
            String solution= cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_SOLUTION_PHRASE));
            String cryptogramUID = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID));
            Long cryptogramId = cur.getLong(cur.getColumnIndex(SDPCryptogramContract.Cryptogram._ID));

            cryptogram = new Cryptogram(cipher, solution, cryptogramId, cryptogramUID);
        }
        // Always close the previous mCursor first
        if (cur != null) {
            cur.close();
        }

        return cryptogram;
    }

    /**
     * Req.#11 The list of available cryptograms shall show, for each cryptogram, its identifier,
     * whether the player has solved it, and the number of incorrect solution submissions, if any.
     * @param player
     * @return
     */
    public List<CryptogramListItem> getCryptogramListItemsBy(Player player){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<CryptogramListItem> cryptogramListItems = new ArrayList<>();
        final String cryptogramIdFullName = SDPCryptogramContract.Cryptogram.TABLE_NAME + "." +
                SDPCryptogramContract.Cryptogram._ID;
        final String cryptogramUIDFullName = SDPCryptogramContract.Cryptogram.TABLE_NAME + "." +
                SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID;
        final String cryptogramCipherPhraseFullName = SDPCryptogramContract.Cryptogram.TABLE_NAME + "." +
                SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE;

        final String sql = "SELECT " +
                cryptogramIdFullName + " AS " + SDPCryptogramContract.Cryptogram._ID + ", " +
                cryptogramUIDFullName + " AS " + SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID + ", " +
                cryptogramCipherPhraseFullName + " AS " + SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE + ", " +
                "COALESCE(" + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED + ", 0) AS " +
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED + ", " +
                "COALESCE(" + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT + ", 0) AS " +
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT + " " +
                "FROM " + SDPCryptogramContract.Cryptogram.TABLE_NAME + " " +
                "LEFT JOIN " + SDPCryptogramContract.CryptogramAttempt.TABLE_NAME + " ON " +
                cryptogramIdFullName + " = " +
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID + " " +
                "AND (" + // WHERE doesn't work for some reason...
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + " IS NULL OR " +
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + " =? ) " +
                "ORDER BY " +
                SDPCryptogramContract.Cryptogram._ID + " DESC";

        Cursor cur = db.rawQuery(sql, new String[] { String.valueOf(player.getPlayerId())});

        while(cur.moveToNext()){

            long id = cur.getLong(cur.getColumnIndex(SDPCryptogramContract.Cryptogram._ID));
            String uid = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID));
            Boolean isSolved = cur.getInt(cur.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED)) > 0;
            int incorrectCount= cur.getInt(cur.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT));
            String cipherPhrase = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE));

            CryptogramListItem listItem = new CryptogramListItem(id, uid, isSolved, incorrectCount, cipherPhrase);
            cryptogramListItems.add(listItem);
        }

        // Always close the previous mCursor first
        if (cur != null) {
            cur.close();
        }

        return cryptogramListItems;
    }

    //He's nervous, but on the surface he looks calm spaghetti.
    // Instead we may want to return precomputed Rating struct for the given player
    public Rating getPlayerRating(Player player) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                SDPCryptogramContract.CryptogramAttempt.TABLE_NAME,
                null,
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + "=? ",
                new String[] {String.valueOf(player.getPlayerId())},
                null,
                null,
                null,
                null
        );

        List<CryptogramAttempt> cryptogramAttempts = new ArrayList<>();
        while(cursor.moveToNext()){
            CryptogramAttempt cryptogramAttempt;
            Long id = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt._ID));
            Integer isSolved = cursor.getInt(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED));
            String mostRecentSubmission = cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_MOST_RECENT_SUBMISSION));
            int incorrectCount = cursor.getInt(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT));
            Long storedCryptogramId = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID));
            Long storedPlayerId = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID));
            cryptogramAttempt = new CryptogramAttempt();
            cryptogramAttempt.setIncorrectSubmissionCount(incorrectCount);
            cryptogramAttempt.setMostRecentSubmission(mostRecentSubmission);
            cryptogramAttempt.setSolved(isSolved > 0);
            cryptogramAttempt.setPlayerId(storedPlayerId);
            cryptogramAttempt.setCryptogramId(storedCryptogramId);
            cryptogramAttempt.setId(id);
            cryptogramAttempts.add(cryptogramAttempt);

        }
        // Always close the previous mCursor first
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        // better do this in sql, but... just moved here code from AttemptCryptogramActivity.updateEWS
        int numAttempted = 0, numSolved = 0, numIncorrect = 0;
        for(CryptogramAttempt attempt : cryptogramAttempts) {
            // mmm... either treating unsubmitted attempts as attempts or not...
            if(attempt.getSolved() || attempt.getIncorrectSubmissionCount() > 0) {
                numAttempted++;
            }
            if(attempt.getSolved()) {
                numSolved++;
            }
            numIncorrect+= attempt.getIncorrectSubmissionCount();
        }

        Rating playerRating = new Rating(player.getFirstName(),
                player.getLastName(),
                player.getUsername(),
                numAttempted,
                numIncorrect,
                numSolved);

        return playerRating;

    }

    public CryptogramAttempt getCryptogramAttemptForPlayer(Long playerId, Long cryptogramId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                SDPCryptogramContract.CryptogramAttempt.TABLE_NAME,
                null,
                SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID + "=? AND " + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + "=?",
                new String[] {cryptogramId.toString(), playerId.toString()},
                null,
                null,
                null,
                null
        );

        CryptogramAttempt cryptogramAttempt = null;
        if(cursor.moveToNext()){
            Long id = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt._ID));
            Integer isSolved = cursor.getInt(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED));
            String mostRecentSubmission = cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_MOST_RECENT_SUBMISSION));
            int incorrectCount = cursor.getInt(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT));
            Long storedCryptogramId = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID));
            Long storedPlayerId = cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID));

            cryptogramAttempt = new CryptogramAttempt();
            cryptogramAttempt.setIncorrectSubmissionCount(incorrectCount);
            cryptogramAttempt.setMostRecentSubmission(mostRecentSubmission);
            cryptogramAttempt.setSolved((isSolved == 1));
            cryptogramAttempt.setPlayerId(storedPlayerId);
            cryptogramAttempt.setCryptogramId(storedCryptogramId);
            cryptogramAttempt.setId(id);
        }

        // Always close the previous mCursor first
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return cryptogramAttempt;
    }

}
