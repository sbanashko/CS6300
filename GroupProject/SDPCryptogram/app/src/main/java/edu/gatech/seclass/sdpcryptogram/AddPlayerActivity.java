package edu.gatech.seclass.sdpcryptogram;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.InvalidParameterException;

import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.exception.InvalidPlayerParametersException;
import edu.gatech.seclass.sdpcryptogram.service.AdministratorService;

/**
 * Add New Player form
 */
public class AddPlayerActivity extends AppCompatActivity {

    // UI components
    private EditText firstNameView;
    private EditText lastNameView;
    private EditText usernameView;
    private EditText passwordView;
    private Button addButton;

    // get database proxy
//    private PlayerRepository mPlayerRepo = PlayerRepository.getPlayerRepository(this);
    private AdministratorService administratorService = AdministratorService.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        firstNameView = (EditText) findViewById(R.id.player_fname_edit_view);
        lastNameView = (EditText) findViewById(R.id.player_lname_edit_view);
        usernameView = (EditText) findViewById(R.id.player_username_edit_view);
        passwordView = (EditText) findViewById(R.id.player_password_edit_view);
        addButton = (Button) findViewById(R.id.player_add_button);
    }

    /**
     * Add player button click handler
     * @param view
     */
    public void onClickAddPlayer(View view) {

        boolean cancel = false;
        View focusView = null;

        String username = usernameView.getText().toString();
        String lastname = lastNameView.getText().toString();
        String firstname = firstNameView.getText().toString();
        String password = passwordView.getText().toString();

        try {
            Player player = administratorService.addPlayer(username, password, firstname, lastname);
            // return Player object to calling activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Intent.EXTRA_RETURN_RESULT, player);
            setResult(Activity.RESULT_OK, returnIntent);
            // finish activity (this returns back to calling activity)
            finish();
        } catch(InvalidPlayerParametersException ipe) {
            if(!ipe.getUsernameError().isEmpty()) {
                usernameView.setError(ipe.getUsernameError());
            }

            if(!ipe.getPasswordError().isEmpty()) {
                passwordView.setError(ipe.getPasswordError());
            }
        }
    }
}
