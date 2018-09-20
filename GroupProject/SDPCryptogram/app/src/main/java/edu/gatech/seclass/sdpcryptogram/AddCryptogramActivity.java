package edu.gatech.seclass.sdpcryptogram;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmKeyException;
import edu.gatech.seclass.sdpcryptogram.exception.IllegalCryptoAlgorithmMessageException;
import edu.gatech.seclass.sdpcryptogram.service.AdministratorService;
import edu.gatech.seclass.sdpcryptogram.service.CipherService;
//import edu.gatech.seclass.sdpcryptogram.util.SimpleSubstitutionCipherService;

/**
 * Add New Cryptogram form
 */
public class AddCryptogramActivity extends AppCompatActivity {

    // error messages
    private static final String INVALID_MESSAGE_ERROR_TEXT = "Invalid Message";
    private static final String INVALID_SHIFT_NUMBER_ERROR_TEXT = "Invalid Shift Number";
    private static final String INVALID_ENCODING_OPTION_ERROR_TEXT = "Missing Encoding Option";
    private static final String INVALID_SOLUTION_CIPHER_MISMATCH_TEXT = "Solution - Cipher Pair " + "" +
            "Do Not Match\n Run Cipher Algorithm to fix";

    // UI elements
    private EditText etSolutionMessage, etShiftNumber, etCipherMessage;
    private RadioButton rbEncodeOption, rbDecodeOption;
    private Button btnRunCipher;
    private Button btnAddCryptogram;

    private final AdministratorService administratorService = AdministratorService.getInstance(this);
    private final CipherService cipherService = CipherService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cryptogram);

        // init UI objects
        etSolutionMessage = (EditText) findViewById(R.id.solutionMessage);
        etShiftNumber = (EditText) findViewById(R.id.shiftNumber);
        rbEncodeOption = (RadioButton) findViewById(R.id.encodeOption);
        rbDecodeOption = (RadioButton) findViewById(R.id.decodeOption);
        etCipherMessage = (EditText) findViewById(R.id.cipherMessage);
        btnRunCipher = (Button) findViewById(R.id.buttonCalculate);
        btnAddCryptogram = (Button) findViewById(R.id.cryptogram_add_button);

    }

    public void addCipher(View view) {
        // create cryptogram
        String msgSolution = etSolutionMessage.getText().toString();
        String msgEncoding = etCipherMessage.getText().toString();
        Cryptogram addedCryptogram;
        boolean valid = true;
        if(msgEncoding.isEmpty()) {
            etCipherMessage.setError("Enter a cipher!");
            valid = false;
        }

        if(msgSolution.isEmpty()) {
            etSolutionMessage.setError("Enter a solution!");
            valid = false;
        }

        if(!msgSolution.toLowerCase().matches(".*[a-z].*")){
            etSolutionMessage.setError("At least one alphabetical character is required!");
            valid = false;
        }

        // return Cryptogram object to calling activity
        Intent returnIntent = new Intent();
        if(valid) {
            try {
                addedCryptogram = administratorService.addCryptogram(msgSolution, msgEncoding);
                returnIntent.putExtra(Intent.EXTRA_RETURN_RESULT, addedCryptogram);
                setResult(Activity.RESULT_OK, returnIntent);
                // finish activity (this returns back to calling activity)
                finish();
            } catch(Exception e) {
                etSolutionMessage.setError("Invalid solution! " + e.getMessage());
            }
        }
    }

    public void runCipher(View view) {

        String shiftString = etShiftNumber.getText().toString();
        Integer shiftNumber = 0;
        String msg = etSolutionMessage.getText().toString();
        boolean isValid = true;
        if(msg == null || msg.isEmpty()) {
            etSolutionMessage.setError(INVALID_MESSAGE_ERROR_TEXT);
            isValid = false;
        }

        if(shiftString == null) {
            etShiftNumber.setError(INVALID_SHIFT_NUMBER_ERROR_TEXT);
            isValid = false;
        } else {
            try {
                shiftNumber = Integer.parseInt(shiftString);
            } catch(Exception e) {
                //OH GOD SO LAZY
                etShiftNumber.setError(INVALID_SHIFT_NUMBER_ERROR_TEXT);
                isValid = false;
            }
            if(shiftNumber == 0) {
                etShiftNumber.setError(INVALID_SHIFT_NUMBER_ERROR_TEXT);
                isValid = false;
            }
        }

        if(!isValid) {
            return;
        }
        String res;
        try {
            if (rbEncodeOption.isChecked()) {
                res = cipherService.encode(msg, shiftNumber);
            } else if (rbDecodeOption.isChecked()) {
                res = cipherService.decode(msg, shiftNumber);
            } else {
                rbDecodeOption.setError(INVALID_ENCODING_OPTION_ERROR_TEXT);
                return;
            }
        }catch(IllegalCryptoAlgorithmKeyException ex){
            etShiftNumber.setError(ex.getMessage());
            return;
        }catch(IllegalCryptoAlgorithmMessageException ex){
            etSolutionMessage.setError(ex.getMessage());
            return;
        }

        if(etCipherMessage.getError() != null)
            etCipherMessage.setError(null);
        etCipherMessage.setText(res);
    }

    /**
     * Event handler for "Encode"/"Decode" radio buttons clicks
     * @param view
     */
    public void handleEncodingSelectorClick(View view){

        if(rbDecodeOption.getError() != null){
            rbDecodeOption.setError(null);
        }

        if(rbEncodeOption.getError() != null){
            rbEncodeOption.setError(null);
        }
    }
}
