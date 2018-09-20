package edu.gatech.seclass.sdpcryptogram.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database creation/upgrade
 */
public class SDPCryptogramDbHelper extends SQLiteOpenHelper {
	/**
	 * Database declarations
	 */
	public static final String LOG_TAG = SDPCryptogramDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = SDPCryptogramContract.DATABASE_NAME;
    public static final String DEFAULT_PLAYER_NAME = "test";
    public static final String DEFAULT_PLAYER_PASS = "test1";
    public static final String ADMIN_NAME = "admin";
    public static final String ADMIN_PASS = "nimda";

    /**
     * Constructor
     * @param context
     */
    public SDPCryptogramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Table declarations
     */
    private static final String SQL_CREATE_PLAYER_TABLE =
            "CREATE TABLE " + SDPCryptogramContract.Player.TABLE_NAME + " (" +
                    SDPCryptogramContract.Player._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME + " TEXT, " +
                    SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME + " TEXT, " +
                    SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD + " TEXT " +
                    ");";
    private static final String SQL_DROP_PLAYER_TABLE =
    	    "DROP TABLE IF EXISTS " + SDPCryptogramContract.Player.TABLE_NAME;


    private static final String SQL_CREATE_CRYPTOGRAM_TABLE =
            "CREATE TABLE " + SDPCryptogramContract.Cryptogram.TABLE_NAME + " (" +
                    SDPCryptogramContract.Cryptogram._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE + " TEXT NOT NULL, " +
                    SDPCryptogramContract.Cryptogram.COLUMN_NAME_SOLUTION_PHRASE + " TEXT NOT NULL, " +
                    SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID + " TEXT NOT NULL, " +
                    "UNIQUE (" + SDPCryptogramContract.Cryptogram.COLUMN_NAME_UID + ", " + // workaround against repeated EWS UIDs
                    SDPCryptogramContract.Cryptogram.COLUMN_NAME_ENCODED_PHRASE + ") " +
                    ");";
    private static final String SQL_DROP_CRYPTOGRAM_TABLE = 
    		"DROP TABLE IF EXISTS " + SDPCryptogramContract.Cryptogram.TABLE_NAME;
    
    private static final String SQL_CREATE_CRYPTOGRAM_ATTEMPT_TABLE =
            "CREATE TABLE " + SDPCryptogramContract.CryptogramAttempt.TABLE_NAME + " (" +
                    SDPCryptogramContract.CryptogramAttempt._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + " INTEGER NOT NULL, " +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID + " INTEGER NOT NULL, " +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_MOST_RECENT_SUBMISSION + " TEXT, " +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_INCORRECT_SUBMISSION_COUNT + " INTEGER DEFAULT 0, " +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_IS_SOLVED + " INTEGER DEFAULT 0, " +
                    "UNIQUE (" + SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + ", " +
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID + ") " +
            "); ";
    private static final String SQL_DROP_CRYPTOGRAM_ATTEMPT_TABLE =
    		"DROP TABLE IF EXISTS " + SDPCryptogramContract.CryptogramAttempt.TABLE_NAME;
    
    /**
     * Data (just a single DEFAULT_PLAYER_NAME player)
     */
    private static final String SQL_INSERT_PLAYER =
    		"INSERT INTO " + SDPCryptogramContract.Player.TABLE_NAME + "(" +
    		SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME + ") " +
    		"VALUES('" +
                    DEFAULT_PLAYER_NAME + "','" +
                    DEFAULT_PLAYER_NAME + "','" +
                    DEFAULT_PLAYER_PASS + "','" +
                    DEFAULT_PLAYER_NAME +
                    "')";

    /**
     * Data (just a single DEFAULT_PLAYER_NAME player)
     */
    private static final String SQL_INSERT_ADMINISTRATOR =
    		"INSERT INTO " + SDPCryptogramContract.Player.TABLE_NAME + "(" +
    		SDPCryptogramContract.Player.COLUMN_NAME_USERNAME + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_FIRST_NAME + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_PASSWORD + "," +
    		SDPCryptogramContract.Player.COLUMN_NAME_LAST_NAME + ") " +
    		"VALUES('" +
                    ADMIN_NAME + "','" +
                    ADMIN_NAME + "','" +
                    ADMIN_PASS + "','" +
                    ADMIN_NAME +
                    "')";
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create data structure
        db.execSQL(SQL_CREATE_PLAYER_TABLE);
        db.execSQL(SQL_CREATE_CRYPTOGRAM_TABLE);
        db.execSQL(SQL_CREATE_CRYPTOGRAM_ATTEMPT_TABLE);

        // insert default player
        db.execSQL(SQL_INSERT_PLAYER);
        db.execSQL(SQL_INSERT_ADMINISTRATOR);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // just drop everything and start over
        db.execSQL(SQL_DROP_CRYPTOGRAM_ATTEMPT_TABLE);
        db.execSQL(SQL_DROP_PLAYER_TABLE);
        db.execSQL(SQL_DROP_CRYPTOGRAM_TABLE);
        onCreate(db);
    }
}
