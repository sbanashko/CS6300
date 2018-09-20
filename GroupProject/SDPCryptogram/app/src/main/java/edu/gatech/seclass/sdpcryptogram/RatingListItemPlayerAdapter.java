package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.struct.Rating;

/**
 * Displays Player Ratings List in Player Dashboard
 */

public class RatingListItemPlayerAdapter extends RecyclerView.Adapter<RatingListItemPlayerAdapter.RatingViewHolder> {

    private Context mContext;
    private List<Rating> mRatingList;

    /**
     * Constructor
     * @param context the calling context/activity
     * @param ratings the db cursor with data to display
     */
    public RatingListItemPlayerAdapter(Context context, final List<Rating> ratings){
        this.mContext = context;
        this.mRatingList = ratings;
    }

    @Override
    public RatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.player_rating_list_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RatingViewHolder holder, int position) {
        // move to the position of list item to be displayed
        if (mRatingList == null || mRatingList.size() == 0)
            return;

        // set view holder views
        holder.userfnameTextView.setText(mRatingList.get(position).firstname);
        holder.userlnameTextView.setText(mRatingList.get(position).lastname);
        holder.attemptedCountTextView.setText(String.valueOf(mRatingList.get(position).attemptedCryptogramsCount));
        holder.incorrectSubmissionsCountTextView.setText(String.valueOf(mRatingList.get(position).incorrectSubmissionsCount));
        holder.solvedCountTextView.setText(String.valueOf(mRatingList.get(position).solvedCryptogramsCount));
    }

    @Override
    public int getItemCount() {
        if(mRatingList == null){
            return 0;
        }
        return mRatingList.size();
    }


    /**
     * Inner class to hold the views needed to display a single item in the recycler view
     */
    class RatingViewHolder extends RecyclerView.ViewHolder {

        // first name
        public TextView userfnameTextView;
        // last name
        public TextView userlnameTextView;
        // attempted#
        public TextView attemptedCountTextView;
        // incorrect submissions#
        public TextView incorrectSubmissionsCountTextView;
        // solved#
        public TextView solvedCountTextView;


        /**
         * ViewHolder constructor
         * @param itemView inflated in onCreateViewHolder
         *
         */

        public RatingViewHolder(View itemView) {
            super(itemView);
            userfnameTextView = (TextView) itemView.findViewById(R.id.rating_fname_text_view);
            userlnameTextView = (TextView) itemView.findViewById(R.id.rating_lname_text_view);
            attemptedCountTextView = (TextView) itemView.findViewById(R.id.rating_attempted_count_text_view);
            incorrectSubmissionsCountTextView = (TextView) itemView.findViewById(R.id.rating_incorrect_count_text_view);
            solvedCountTextView = (TextView) itemView.findViewById(R.id.rating_solved_count_text_view);
        }

    }
}
