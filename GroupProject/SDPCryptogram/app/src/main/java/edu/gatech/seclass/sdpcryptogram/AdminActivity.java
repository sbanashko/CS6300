package edu.gatech.seclass.sdpcryptogram;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;
import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;
import edu.gatech.seclass.utilities.ExternalWebService;

/**
 * Admin Dashboard
 */
public class AdminActivity extends AppCompatActivity {

    private final static String LOG_TAG = AdminActivity.class.getSimpleName();
    private final static String EWS_TAG = "EWS";
    private final static String DATABASE_TAG = "Database";
    private final static int ACTIVITY_ADD_PLAYER_TAG = 1;
    private final static int ACTIVITY_ADD_CRYPTOGRAM_TAG = 2;
    // default list of items to show in the recycler {R.id.action_cryptogram_list, R.id.action_player_list}
    private static int DEFAULT_RECYCLER_LIST_ID = R.id.action_player_list;
    // current list of items to show in the recycler
    private int current_recycler_view_id = DEFAULT_RECYCLER_LIST_ID;
    // player list adapter
    private PlayerListAdminAdapter mPlayerListAdapter;
    // cryptogram list adapter
    private CryptogramListAdminAdapter mCryptogramListAdapter;
    // database proxy
    private PlayerRepository mPlayerRepo = PlayerRepository.getPlayerRepository(this);
    private CryptogramRepository mCryptogramRepo = CryptogramRepository.getCryptogramRepository(this);
    // EWS proxy
    private ExternalWebService mEWS = ExternalWebService.getInstance();
    // UI references
    private RecyclerView mRecyclerView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTIVITY_ADD_PLAYER_TAG){
            // returned from Add Player activity
            if(resultCode == Activity.RESULT_OK){
                // unpack Player object and save into db
                // valid id returned
                Toast.makeText(getBaseContext(), "Player added!",
                        Toast.LENGTH_LONG).show();
                // refresh list of Players
                refreshRecyclerViewPlayersAdapter();
            }

        } else if(requestCode == ACTIVITY_ADD_CRYPTOGRAM_TAG){
            // returned from Add Cryptogram activity
            if(resultCode == Activity.RESULT_OK) {
                // extract cryptogram UID and show (Req.#9)
                Cryptogram cryptogram = data.getParcelableExtra(Intent.EXTRA_RETURN_RESULT);
                if(cryptogram != null) {
                    Toast.makeText(getBaseContext(), "Cryptogram #" + cryptogram.getCryptogramUID() + " added!",
                            Toast.LENGTH_LONG).show();
                } else{
                    // if we're here we most likely added the cryptogram, but didn't return it to the calling activity for some reason
                    Toast.makeText(getBaseContext(), "Error occurred!",
                            Toast.LENGTH_LONG).show();
                }
                // refresh list of Players
                refreshRecyclerViewCryptogramsAdapter();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // set up UI references
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_players);
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_recycler_view_id == R.id.action_player_list) {
                    // start Add Player activity here
                    Intent addPlayerIntent = new Intent(AdminActivity.this, AddPlayerActivity.class);
                    startActivityForResult(addPlayerIntent, ACTIVITY_ADD_PLAYER_TAG);
                } else if(current_recycler_view_id == R.id.action_cryptogram_list){
                    // start Add Cryptogram activity here
                    Intent addCryptogramIntent = new Intent(AdminActivity.this, AddCryptogramActivity.class);
                    startActivityForResult(addCryptogramIntent, ACTIVITY_ADD_CRYPTOGRAM_TAG);
                } else{
                    throw new UnsupportedOperationException();
                }
            }
        });

        // set layout manager for recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // mRecyclerView.setHasFixedSize(true);

        // set back menu
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        // insert fake players
//        mPlayerRepo.addFakePlayes();
//        // TODO: insert fake cryptograms here

        // setup default adapter to fill in the recycler view
        setupRecyclerViewCurrentListAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-query data for the current list
        setupRecyclerViewCurrentListAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();

        switch (itemClickedId) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_cryptogram_list:
                current_recycler_view_id = R.id.action_cryptogram_list;
                setupRecyclerViewCryptogramsAdapter();
                return true;
            case R.id.action_player_list:
                current_recycler_view_id = R.id.action_player_list;
                setupRecyclerViewPlayersAdapter();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup adapter for player list
     */
    private void setupRecyclerViewPlayersAdapter(){
        //mPlayerRepo.addFakePlayes();
        // get player list
        List<Player> players = mPlayerRepo.getPlayers();
        // create adapter for this list
        mPlayerListAdapter = new PlayerListAdminAdapter(this, players);
        // set adapter for the recycler view
        mRecyclerView.setAdapter(mPlayerListAdapter);
    }

    /**
     * Refresh adapter for player list
     */
    private void refreshRecyclerViewPlayersAdapter(){
        if(mRecyclerView.getAdapter() != null){
            // adapter already addached -> refresh the list with new data
            List<Player> players = mPlayerRepo.getPlayers();
            ((PlayerListAdminAdapter)mRecyclerView.getAdapter()).swapCursor(players);
        }else{
            // no adapter yet -> create one and fill with data
            setupRecyclerViewPlayersAdapter();
        }
    }

    /**
     * Setup adapter for cryptogram list
     */
    private void setupRecyclerViewCryptogramsAdapter(){
        // get cryptogram list
        List<Cryptogram> cryptograms = mCryptogramRepo.getCryptograms();
        // create adapter for this list
        mCryptogramListAdapter = new CryptogramListAdminAdapter(this, cryptograms);
        // set adapter for the recycler view
        mRecyclerView.setAdapter(mCryptogramListAdapter);
    }

    /**
     * Refresh adapter for cryptogram list
     */
    private void refreshRecyclerViewCryptogramsAdapter(){
        if(mRecyclerView.getAdapter() != null){
            // adapter already addached -> refresh the list with new data
            List<Cryptogram> cryptograms = mCryptogramRepo.getCryptograms();
            ((CryptogramListAdminAdapter)mRecyclerView.getAdapter()).swapCursor(cryptograms);
        }else{
            // no adapter yet -> create one and fill with data
            setupRecyclerViewCryptogramsAdapter();
        }
    }

    /**
     * Setup or rebind adapter to fill in the recycler view with currently selected list
     */
    private void setupRecyclerViewCurrentListAdapter() {
        // setup default adapter based on current list of things
        switch (current_recycler_view_id){
            case R.id.action_player_list:
                setupRecyclerViewPlayersAdapter();
                break;
            case R.id.action_cryptogram_list:
                setupRecyclerViewCryptogramsAdapter();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
