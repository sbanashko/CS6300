package edu.gatech.seclass.sdpcryptogram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.CryptogramAttempt;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.service.CryptogramService;
import edu.gatech.seclass.sdpcryptogram.service.EWSService;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Created by Nick Marsh on 7/4/2017.
 */

public class AttemptCryptogramActivity extends AppCompatActivity {

    private final CryptogramRepository cryptogramRepository = CryptogramRepository.getCryptogramRepository(this);
    private final CryptogramService cryptogramService = CryptogramService.getInstance(this);
    private final EWSService ewsService = EWSService.getInstance();

    private Cryptogram cryptogram;
    private CryptogramAttempt cryptogramAttempt;
    private Player currentPlayer;

    private TextView puzzleTextView;
    private EditText attemptedSolutionView;
    private TextView prevSubmissionView;
    private TextView incorrectSubmissionView;
    private TextView isSolvedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_cryptogram);
        //enable Back menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        Long cryptogramId = bundle.getLong("cryptogramid");
        currentPlayer = (Player) bundle.get("player");
        cryptogram = cryptogramRepository.getCryptogram(bundle.getLong("cryptogramid"));
        cryptogramAttempt = cryptogramService.attemptCryptogramForPlayer(cryptogramId, currentPlayer.getPlayerId());

        //Initialize views
        puzzleTextView = (TextView) findViewById(R.id.cipherMessage);
        attemptedSolutionView = (EditText) findViewById(R.id.attemptedSubmission);
        prevSubmissionView = (TextView) findViewById(R.id.previousSubmission);
        incorrectSubmissionView = (TextView) findViewById(R.id.incorrectSubmissions);
        isSolvedView = (TextView) findViewById(R.id.isSolved);

        if(cryptogramAttempt.getSolved() != true && !TextUtils.isEmpty(cryptogramAttempt.getMostRecentSubmission())) {
            attemptedSolutionView.setText(cryptogramAttempt.getMostRecentSubmission());
        }
        //Set initial values
        updateView();
    }

    public void submitButton(View view) {
        String attemptedSolution = attemptedSolutionView.getText().toString();
        cryptogramAttempt = cryptogramService.submitSolution(cryptogram, attemptedSolution, cryptogramAttempt, currentPlayer);
        if(cryptogramAttempt.getSolved()) {
            sucessfulAttempt();
        } else {
            failedAttempt();
        }
    }

    public void goBack(){
        String attemptedSolution = attemptedSolutionView.getText().toString();
        if(attemptedSolution != null && !attemptedSolution.isEmpty()) {
            cryptogramAttempt.setMostRecentSubmission(attemptedSolution);
            saveAttempt();
        }

        finish();
    }

    private void failedAttempt() {
        updateView();
        Toast.makeText(getBaseContext(), "Try again!", Toast.LENGTH_LONG).show();

    }

    private void sucessfulAttempt() {
        Toast.makeText(getBaseContext(), "Congratulations! You're correct!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void updateView() {
        puzzleTextView.setText(cryptogram.getCipherPhrase());
        incorrectSubmissionView.setText(cryptogramAttempt.getIncorrectSubmissionCount().toString());
        prevSubmissionView.setText(cryptogramAttempt.getMostRecentSubmission());
        isSolvedView.setText(cryptogramAttempt.getSolved() ? "Yes" : "No");
    }

    private void saveAttempt() {
        try {
            cryptogramAttempt = cryptogramService.saveCryptogramAttempt(cryptogramAttempt);
        }catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
