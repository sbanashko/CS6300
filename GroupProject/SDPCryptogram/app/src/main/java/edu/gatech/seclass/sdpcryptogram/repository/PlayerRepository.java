package edu.gatech.seclass.sdpcryptogram.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Player Repository class
 */
public class PlayerRepository {
    public static final String LOG_TAG = PlayerRepository.class.getSimpleName();
//    private static SQLiteDatabase db;
    private static PlayerRepository instance;
    private static SDPCryptogramDbHelper dbHelper;

    
    private PlayerRepository(){}

    /**
     * PlayerRepository producer
     * @param context
     * @return PlayerRepository object
     */
    public static PlayerRepository getPlayerRepository(Context context){
    	if(instance == null){
            instance = new PlayerRepository();
            dbHelper = new SDPCryptogramDbHelper(context);
        }
        return instance;
    }

    /**
     * Add new player
     * @param player
     * @return
     */
    public long addPlayer(final Player player){
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, player.getUsername());
            cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, player.getFirstName());
            cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, player.getLastName());
            cv.put(SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD, player.getPassword());
            long newId = db.insert(SDPCryptogramContract.Player.TABLE_NAME, null, cv);
            db.close();
            Log.d(LOG_TAG, String.valueOf(newId));
            return newId;
        } catch(Exception e) {
            System.out.println("Error adding cryptogram! " + e.getMessage());
            throw e;
        }
    }

    public long checkPlayer(final String username) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db,
                SDPCryptogramContract.Player.TABLE_NAME,
                SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + "=?",
                new String[] {username} );
    }

    public Player getPlayer(Long id) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectionString = SDPCryptogramContract.Player._ID + "=?";
        Cursor cursor = db.query(SDPCryptogramContract.Player.TABLE_NAME, null, selectionString, new String[]{id.toString()}, null, null, null);
        Player player = null;
        if(cursor.moveToNext()) {
            player = new Player();
            player.setUsername(cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME)));
            player.setPlayerId(cursor.getLong(cursor.getColumnIndex(SDPCryptogramContract.Player._ID)));
            player.setFirstName(cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME)));
            player.setLastName(cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME)));
            player.setPassword(cursor.getString(cursor.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD)));
        }

        cursor.close();

        return player;
    }

    public Player getPlayerByUsername(final String username){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.query(
                SDPCryptogramContract.Player.TABLE_NAME,
                null,
                SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + "=?",
                new String[]{username},
                null,
                null,
                null
        );

        Player player = null;
        if(cur.moveToNext()) {
            player = new Player();
            player.setUsername(username);
            player.setPlayerId(cur.getLong(cur.getColumnIndex(SDPCryptogramContract.Player._ID)));
            player.setFirstName(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME)));
            player.setLastName(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME)));
            player.setPassword(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD)));
        }

        // Always close the previous mCursor first
        cur.close();

        return player;
    }

    /**
     * Get all players
     * @return
     */
    public List<Player> getPlayers(){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<Player> players = new ArrayList<>();
        // get a list of all players (most recent on top)
        Cursor cur = db.query(
                SDPCryptogramContract.Player.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                SDPCryptogramContract.Player._ID + " DESC"
        );

        while(cur.moveToNext()){
           Player player = new Player();

            Long id = cur.getLong(cur.getColumnIndex(SDPCryptogramContract.Player._ID));
            String fname = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME));
            String lname = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME));
            String username = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME));
            String password = cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD));

            player.setPlayerId(id);
            player.setFirstName(fname);
            player.setLastName(lname);
            player.setUsername(username);
            player.setPassword(password);

            players.add(player);
        }
        // Always close the previous mCursor first
        if (cur != null) {
            cur.close();
        }

        return new ArrayList<>(players);
    }

    /**
     * Get ratings for all players
     * Req.#12 The list of player ratings shall display, for each player, his or her name,
     * the number of cryptograms solved, the number of cryptograms started,
     * and the total number of incorrect solutions submitted.
     * The list shall be sorted in descending order by the number of cryptograms solved
     * @return
     */
    public List<Rating> getRatings(){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<Rating> ratings = new ArrayList<>();

        // aliases for cursor
        final String solvedCount = "solvedCount";
        final String incorrectSubmissionsCount = "incorrectSubmissionsCount";
        final String attemptedCount = "attemptedCount";

        final String sql = "SELECT " +
                "p." + SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + ", " +
                "p." + SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME + ", " +
                "p." + SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME + ", " +
                "SUM(COALESCE(ca." + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED + ", 0)) AS " + solvedCount + ", " +
                "COUNT(ca." + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + ") AS " + attemptedCount + ", " +
                "SUM(COALESCE(ca." + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT + ", 0)) AS " + incorrectSubmissionsCount + " " +
                "FROM " + SDPCryptogramContract.Player.TABLE_NAME + " AS p " +
                "LEFT JOIN " + SDPCryptogramContract.CryptogramAttempt.TABLE_NAME + " AS ca " +
                "ON p." + SDPCryptogramContract.Player._ID + "=" +
                "ca." + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + " " +
                "WHERE " + "p." + SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + " <> ? " +
                "GROUP BY " + "p." + SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + " " +
                "ORDER BY " + solvedCount + " DESC";

        Cursor cur = db.rawQuery(
                sql,
                new String[] {SDPCryptogramDbHelper.ADMIN_NAME});
        while(cur.moveToNext()){
            Rating listItem = new Rating();

            listItem.setUsername(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME)));
            listItem.setFirstname(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME)));
            listItem.setLastname(cur.getString(cur.getColumnIndex(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME)));
            listItem.setAttemptedCryptogramsCount(cur.getInt(cur.getColumnIndex(attemptedCount)));
            listItem.setIncorrectSubmissionsCount(cur.getInt(cur.getColumnIndex(incorrectSubmissionsCount)));
            listItem.setSolvedCryptogramsCount(cur.getInt(cur.getColumnIndex(solvedCount)));

            ratings.add(listItem);
        }

        // close cursor
        if (cur != null) {
            cur.close();
        }
        return new ArrayList<>(ratings);
    }


    public CryptogramAttempt attemptCryptogram(Player player, Cryptogram cryptogram){
        try {
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID, cryptogram.getCryptogramId());
            cv.put(SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID, player.getPlayerId());
            long newId = db.insertOrThrow(SDPCryptogramContract.CryptogramAttempt.TABLE_NAME, null, cv);
            db.close();
            Log.d(LOG_TAG, String.valueOf(newId));
            CryptogramAttempt cryptogramAttempt = new CryptogramAttempt();
            cryptogramAttempt.setId(newId);
            cryptogramAttempt.setCryptogramId(cryptogram.getCryptogramId());
            cryptogramAttempt.setPlayerId(player.getPlayerId());
            cryptogramAttempt.setSolved(false);
            cryptogramAttempt.setMostRecentSubmission("");
            cryptogramAttempt.setIncorrectSubmissionCount(0);
            return cryptogramAttempt;
        } catch(Exception e) {
            System.out.println("Error adding cryptogram attempt! " + e.getMessage());
            throw e;
        }

    }


    /**
     * Add fake players for testing
     */
    public void addFakePlayes(){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        // list of fake players
        List<ContentValues> list = new ArrayList<ContentValues>();
        // add random suffix to fill in the list quickly
        Random randomGenerator = new Random();

        ContentValues cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, dbHelper.DEFAULT_PLAYER_NAME);
        list.add(cv);

        cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, "John");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, "Ashcraft");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, "jash$craft" + String.valueOf(randomGenerator.nextInt(10)));
        list.add(cv);

        cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, "Paul");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, "Bradsher");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, "Bradsher_paul" + String.valueOf(randomGenerator.nextInt(10)));
        list.add(cv);

        cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, "Susan");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, "Schroder");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, "susy" + String.valueOf(randomGenerator.nextInt(10)));
        list.add(cv);

        cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, "Robena");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, "Costello");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, "Costello.Robena@fakedomain.com" + String.valueOf(randomGenerator.nextInt(10)));
        list.add(cv);

        cv = new ContentValues();
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME, "Leonard");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME, "Rocha");
        cv.put(SDPCryptogramContract.Player.COLUMN_NAME_USERNAME, "Rocha.Leonard" + String.valueOf(randomGenerator.nextInt(10)));
        list.add(cv);

        // insert fake players in a transaction
        try
        {
            db.beginTransaction();
            // remove playres
//            db.delete(SDPCryptogramContract.Player.TABLE_NAME,null,null);
            // add players
            for(ContentValues c: list){
                db.insert(SDPCryptogramContract.Player.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            // do nothing
        }
        finally
        {
            db.endTransaction();
        }

    }
}
