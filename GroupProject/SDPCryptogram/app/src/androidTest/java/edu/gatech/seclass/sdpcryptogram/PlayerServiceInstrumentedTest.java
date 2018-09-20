package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.SDPCryptogramContract;
import edu.gatech.seclass.sdpcryptogram.repository.SDPCryptogramDbHelper;
import edu.gatech.seclass.sdpcryptogram.service.AdministratorService;
import edu.gatech.seclass.sdpcryptogram.service.PlayerService;

import static org.junit.Assert.*;

/**
 * IPC1-IPC4 instrumentation tests (exec on an Android device)
 */

@RunWith(AndroidJUnit4.class)
public class PlayerServiceInstrumentedTest {

    // SQLiteDatabase on the device for direct querying
    private static final Class mDbHelperClass = SDPCryptogramDbHelper.class;
    // app context
    private static final Context mContext = InstrumentationRegistry.getTargetContext();

    private static PlayerService mPlayerService;
    private static AdministratorService mAdministratorService;
    private static Cryptogram mCryptogram;
    private Player mPlayer;
    private CryptogramAttempt mCryptogramAttempt;


    @BeforeClass
    public static void setUp() throws Exception{
        mPlayerService = PlayerService.getInstance(mContext);
        mAdministratorService = AdministratorService.getInstance(mContext);

        // init cryptogram once due to EWS persistence and duplicate puzzles check
        mCryptogram = mAdministratorService.addCryptogram("abc $ xyz", "zab $ wxy");
    }

    @Before
    public void populateDb(){
        // add fake player and cryptogram
        mPlayer = mAdministratorService.addPlayer("username12345", "password12345", "firstName12345", "lastName12345");
    }

    @After
    public void flushDb() throws Exception{
        dropCryptogramAttempt();
        dropPlayer();
    }

    @AfterClass
    public static void dropCryptogram() throws Exception{
        // drop cryptogram
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        try {
            database.delete(SDPCryptogramContract.Cryptogram.TABLE_NAME,
                    SDPCryptogramContract.Cryptogram._ID + "=? ",
                    new String[]{String.valueOf(mCryptogram.getCryptogramId())});
        } catch(Exception ex){
            //
        } finally{
            database.close();
        }
    }

    /**
     * Context should be correct
     * @throws Exception
     */
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("edu.gatech.seclass.sdpcryptogram", appContext.getPackageName());
    }

    /** IPC1 test from TestPlan.md
     * Verify player can attempt a cryptogram
     */
    @Test
    public void testAttemptCryptogramNewAttempt(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        // check we got valid empty cryptogram attempt
        assertTrue(mCryptogramAttempt != null);
        assertTrue(mCryptogramAttempt.getId() != -1);
        assertEquals(mPlayer.getPlayerId(), mCryptogramAttempt.getPlayerId());
        assertEquals(mCryptogram.getCryptogramId(), mCryptogramAttempt.getCryptogramId());
        assertTrue(mCryptogramAttempt.getSolved() == false);
        assertTrue(mCryptogramAttempt.getIncorrectSubmissionCount() == 0);
    }

    /** IPC1 test from TestPlan.md
     * Verify player can NOT attempt same cryptogram twice
     */
    @Test(expected = Exception.class)
    public void testAttemptCryptogramSecondAttemptAlreadyExists(){
        // attempt same cryptogram (there should be 1 attempt per unique playerId,cryptogramId pair)
        CryptogramAttempt newAttempt1 = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        CryptogramAttempt newAttempt2 = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
    }

    /**
     * IPC2 test from TestPlan.md
     * Verify player can submit an incorrect cryptogram solution
     */
    @Test
    public void testSubmitCryptogramAttemptIncorrectSolution(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        int incorrectSubmissions =  mCryptogramAttempt.getIncorrectSubmissionCount();
        String incorrectSolutionPhrase = "bla-bla-bla";
        mCryptogramAttempt.setMostRecentSubmission(incorrectSolutionPhrase);

        assertFalse(mPlayerService.submitCryptogramAttempt(mCryptogramAttempt));
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);
        assertFalse(savedCryptogramAttempt.getSolved());
        assertTrue(savedCryptogramAttempt.getIncorrectSubmissionCount() == incorrectSubmissions++);
    }

    /**
     * IPC3 test from TestPlan.md
     * Verify player can submit a correct cryptogram solution
     */
    @Test
    public void testSubmitCryptogramAttemptCorrectSolution(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        int incorrectSubmissions =  mCryptogramAttempt.getIncorrectSubmissionCount();
        String correctSolutionPhrase = "abc $ xyz";
        mCryptogramAttempt.setMostRecentSubmission(correctSolutionPhrase);

        assertTrue(mPlayerService.submitCryptogramAttempt(mCryptogramAttempt));
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);
        assertTrue(savedCryptogramAttempt.getSolved());
        assertTrue(savedCryptogramAttempt.getIncorrectSubmissionCount() == incorrectSubmissions);
    }

    /**
     * IPC2 test from TestPlan.md
     * Verify player can submit incorrect cryptogram solution (non-alphanumeric char diff)
     */
    @Test
    public void testSubmitCryptogramAttemptAlmostCorrectSolution(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        int incorrectSubmissions =  mCryptogramAttempt.getIncorrectSubmissionCount();
        String correctSolutionPhrase = "abc , xyz";
        mCryptogramAttempt.setMostRecentSubmission(correctSolutionPhrase);

        assertFalse(mPlayerService.submitCryptogramAttempt(mCryptogramAttempt));
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);
        assertFalse(savedCryptogramAttempt.getSolved());
        assertTrue(savedCryptogramAttempt.getIncorrectSubmissionCount() == incorrectSubmissions++);
    }

    /**
     * IPC4
     * Verify player can save an in-progress solution
     */
    @Test
    public void testSaveCryptogramAttemptInProgressSolutionNoIncorrectAttempts(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        String solutionPhrase = "solution in progress...";
        mCryptogramAttempt.setMostRecentSubmission(solutionPhrase);

        mPlayerService.saveCryptogramAttempt(mCryptogramAttempt);
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);

        assertEquals(mCryptogramAttempt, savedCryptogramAttempt);
    }

    /**
     * IPC4
     * Verify player can save an in-progress solution
     */
    @Test
    public void testSaveCryptogramAttemptInProgressSolutionIncorrectAttemptsExist(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        String solutionPhrase = "solution in progress...";
        mCryptogramAttempt.setMostRecentSubmission(solutionPhrase);
        mCryptogramAttempt.setIncorrectSubmissionCount(100);

        mPlayerService.saveCryptogramAttempt(mCryptogramAttempt);
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);

        assertEquals(mCryptogramAttempt, savedCryptogramAttempt);
    }

    /**
     * IPC4
     * Verify player can save an in-progress solution
     */
    @Test
    public void testSaveCryptogramAttemptInProgressSolutionSolved(){
        mCryptogramAttempt = mPlayerService.attemptCryptogram(mPlayer, mCryptogram);
        String solutionPhrase = "solution in progress...";
        mCryptogramAttempt.setMostRecentSubmission(solutionPhrase);
        mCryptogramAttempt.setSolved(true);

        mPlayerService.saveCryptogramAttempt(mCryptogramAttempt);
        CryptogramAttempt savedCryptogramAttempt = mPlayerService.getCryptogramAttemptForPlayer(mPlayer, mCryptogram);

        assertEquals(mCryptogramAttempt, savedCryptogramAttempt);
    }

    /**
     * Helper
     * @throws Exception
     */
    private void dropCryptogramAttempt() throws Exception {
        // drop cryptogram attempts
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        try {
            database.delete(SDPCryptogramContract.CryptogramAttempt.TABLE_NAME,
                    SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_PLAYER_ID + "= ? AND " +
                            SDPCryptogramContract.CryptogramAttempt.COLUMN_NAME_CRYPTOGRAM_ID + "=? ",
                    new String[]{String.valueOf(mPlayer.getPlayerId()),
                            String.valueOf(mCryptogram.getCryptogramId())});
        } catch(Exception ex) {
            //
        } finally {
            database.close();
        }
    }

    /**
     * Helper
     * @throws Exception
     */
    private void dropPlayer() throws Exception{
        // drop player
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        try {
            database.delete(SDPCryptogramContract.Player.TABLE_NAME,
                    SDPCryptogramContract.Player._ID + "=? ",
                    new String[]{String.valueOf(mPlayer.getPlayerId())});
        } catch(Exception ex){
            //
        } finally{
            database.close();
        }
    }

}
