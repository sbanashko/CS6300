package edu.gatech.seclass.sdpcryptogram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Player;
import edu.gatech.seclass.sdpcryptogram.repository.CryptogramRepository;
import edu.gatech.seclass.sdpcryptogram.service.CryptogramService;
import edu.gatech.seclass.sdpcryptogram.struct.CryptogramListItem;

/**
 * Player Dashboard
 */
public class PlayerActivity extends AppCompatActivity
        implements CryptogramListItemPlayerAdapter.ListItemClickListener{

    private final static String LOG_TAG = AdminActivity.class.getSimpleName();
    // current user
    Player mCurrentUser;
//    // database proxy
//    private CryptogramRepository mCryptogramRepo = CryptogramRepository.getCryptogramRepository(this);
    // data service
    private CryptogramService mCryptogramService = CryptogramService.getInstance(this);
    // UI references
    private RecyclerView mRecyclerView;
    // cryptogram list items adapter
    private CryptogramListItemPlayerAdapter mCryptogramListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // extract current user from Intent passed from LoginActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mCurrentUser = bundle.getParcelable(Intent.EXTRA_ASSIST_CONTEXT);
        }else{
            // nothing to do here
            Toast.makeText(getBaseContext(), "Error occurred!", Toast.LENGTH_LONG).show();
            return;
        }

        if(mCurrentUser != null){
            setTitleText();
        }

        // set up UI references
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_cryptograms);
        // set layout manager for recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // get data for adapter
        List<CryptogramListItem> items = mCryptogramService.getCryptogramListItemsBy(mCurrentUser);
        // create adapter
        mCryptogramListAdapter = new CryptogramListItemPlayerAdapter(this, items, this);
        // set adapter for the recycler view
        mRecyclerView.setAdapter(mCryptogramListAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-query data for the current list
        if(mRecyclerView.getAdapter() != null){
            List<CryptogramListItem> items = mCryptogramService.getCryptogramListItemsBy(mCurrentUser);
            mCryptogramListAdapter.swapCursor(items);
        }
    }

    @Override
    public void onListItemClick(CryptogramListItem clickedCryptogramListItem) {
        Intent intent = new Intent(this, AttemptCryptogramActivity.class);
        intent.putExtra("player", mCurrentUser);
        intent.putExtra("cryptogramid", clickedCryptogramListItem.getCryptogramId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        switch (itemClickedId){
            case R.id.action_rating_list:
                Intent intent = new Intent(this, RatingActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to set F/L name as activity title
     */
    private void setTitleText(){
        // set activity title
        String newTitle = "";
        if(mCurrentUser.getLastName() != null && !mCurrentUser.getLastName().isEmpty())
            newTitle = mCurrentUser.getLastName();
        if(mCurrentUser.getFirstName() != null && !mCurrentUser.getFirstName().isEmpty())
            newTitle += newTitle.isEmpty() ? mCurrentUser.getFirstName() : ", " + mCurrentUser.getFirstName();

        if(!newTitle.isEmpty())
            setTitle(newTitle);
    }
}
