package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Player;

/**
 * Displays Player List in Admin Dashboard
 */

public class PlayerListAdminAdapter extends RecyclerView.Adapter<PlayerListAdminAdapter.PlayerViewHolder> {

    private Context mContext;
    private List<Player> mPlayerList;

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param players the db cursor with data to display
     */
    public PlayerListAdminAdapter(Context context, final List<Player> players) {
        this.mContext = context;
        this.mPlayerList = players;
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.player_list_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        // move to the position of list item to be displayed
        if (mPlayerList == null || mPlayerList.size() == 0)
            return;

        // set view holder views
        holder.itemView.setTag(mPlayerList.get(position).getPlayerId());
        holder.idTextView.setText(String.valueOf(mPlayerList.get(position).getPlayerId()));
        holder.fnameTextView.setText(mPlayerList.get(position).getFirstName());
        holder.lnameTextView.setText(mPlayerList.get(position).getLastName());
        holder.usernameTextView.setText(mPlayerList.get(position).getUsername());
        holder.passwordTextView.setText(""); // hide pwd
    }


    @Override
    public int getItemCount() {
        if(mPlayerList == null){
            return 0;
        }
        return mPlayerList.size();
    }

    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param players the new cursor that will replace the existing one
     */
    public void swapCursor(final List<Player> players) {
        if (players != null) {
            mPlayerList = players;
            // force RecyclerView refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class PlayerViewHolder extends RecyclerView.ViewHolder {

        // player id
        public TextView idTextView;
        // player fname
        public TextView fnameTextView;
        // player lname
        public TextView lnameTextView;
        // player username
        public TextView usernameTextView;
        public TextView passwordTextView;


        /**
         * ViewHolder constructor
         * @param itemView inflated in onCreateViewHolder
         *
         */

        public PlayerViewHolder(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.player_id_text_view);
            fnameTextView = (TextView) itemView.findViewById(R.id.player_fname_text_view);
            lnameTextView = (TextView) itemView.findViewById(R.id.player_lname_text_view);
            usernameTextView = (TextView) itemView.findViewById(R.id.player_username_text_view);
            passwordTextView = (TextView) itemView.findViewById(R.id.player_pw_text_view);
        }

    }
}
