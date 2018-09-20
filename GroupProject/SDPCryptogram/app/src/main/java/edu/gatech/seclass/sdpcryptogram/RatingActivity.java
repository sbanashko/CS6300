package edu.gatech.seclass.sdpcryptogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.repository.PlayerRepository;
import edu.gatech.seclass.sdpcryptogram.service.EWSService;
import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Player Ratings List
 */
public class RatingActivity extends AppCompatActivity {

    // rating list items adapter
    private RatingListItemPlayerAdapter mRatingListAdapter;
    // database proxy
    private PlayerRepository mPlayerRepo = PlayerRepository.getPlayerRepository(this);
    // EWS proxy
    private EWSService mEWSService = EWSService.getInstance();
    // UI references
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        //enable Back menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up UI references
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_ratings);
        // set layout manager for recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // get data for adapter
        List<Rating> items;
        try{
            // first request ratings from EWS
            items = mEWSService.syncRatingService();
        } catch(Exception e){
            // now try to request them from local db
            try{
                items = mPlayerRepo.getRatings();
            } catch(Exception ex){
                // no way to get ratings
                Toast.makeText(getBaseContext(), "Error occurred!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // create adapter
        mRatingListAdapter = new RatingListItemPlayerAdapter(this, items);
        // set adapter for the recycler view
        mRecyclerView.setAdapter(mRatingListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
