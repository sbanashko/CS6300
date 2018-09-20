package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.struct.CryptogramListItem;

/**
 * Displays Cryptogram List in Player Dashboard
 * (to choose from for an attempt)
 */

public class CryptogramListItemPlayerAdapter extends RecyclerView.Adapter<CryptogramListItemPlayerAdapter.CryptogramListItemViewHolder>{

    private List<CryptogramListItem> mCryptogramListItems;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    /**
     * Interface to handle onClick events on ViewHolder
     */
    public interface ListItemClickListener{
        void onListItemClick(CryptogramListItem clickedCryptogramListItem);
    }

    /**
     * Constructor using the context and the db cursor
     * @param context
     * @param cryptogramListItems
     * @param clickListener external click handler
     */
    public CryptogramListItemPlayerAdapter(Context context,
                                           final List<CryptogramListItem> cryptogramListItems,
                                           ListItemClickListener clickListener) {
        this.mContext = context;
        this.mCryptogramListItems = cryptogramListItems;
        this.mOnClickListener = clickListener;
    }

    @Override
    public CryptogramListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the cryptogram_list_player_item to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.cryptogram_list_player_item, parent, false);
        return new CryptogramListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CryptogramListItemViewHolder holder, int position) {
        if(mCryptogramListItems == null || mCryptogramListItems.size() == 0)
            return;

        // set view holder views

        holder.itemView.setTag(mCryptogramListItems.get(position).getCryptogramId());
        holder.mCryptogramUID.setText(String.valueOf(mCryptogramListItems.get(position).getCryptogramUID()));
        holder.mIsSolved.setImageResource(mCryptogramListItems.get(position).isSolved() ?
                android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        int incorrectCount = mCryptogramListItems.get(position).getIncorrectSubmissionsCount();
        holder.mIncorrectSubmissionsCount.setText(String.valueOf(incorrectCount));
        holder.mIncorrectSubmissionsCount.setTextColor(incorrectCount > 0 ?
                Color.rgb(244, 66, 66) : Color.rgb(0, 0, 0)); // reddish or black
        holder.mCipherText.setText(mCryptogramListItems.get(position).getCipherPhrase());
    }

    @Override
    public int getItemCount() {
        if(mCryptogramListItems == null)
            return 0;
        return mCryptogramListItems.size();
    }

    public void swapCursor(final List<CryptogramListItem> cryptogramListItems){
        if(mCryptogramListItems == cryptogramListItems)
            return;

        if(cryptogramListItems != null){
            mCryptogramListItems = cryptogramListItems;
            this.notifyDataSetChanged();
        }
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class CryptogramListItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mCryptogramUID;
        ImageView mIsSolved;
        TextView mIncorrectSubmissionsCount;
        TextView mCipherText;

        /**
         * ViewHolder constructor
         * @param itemView inflated in onCreateViewHolder
         *
         */

        public CryptogramListItemViewHolder(View itemView) {
            super(itemView);

            mCryptogramUID = (TextView) itemView.findViewById(R.id.cryptogram_uid_text_view);
            mIsSolved = (ImageView) itemView.findViewById(R.id.cryptogram_is_solved_image_view);
            mIncorrectSubmissionsCount = (TextView) itemView.findViewById(R.id.cryptogram_incorrect_submissions_text_view);
            mCipherText = (TextView) itemView.findViewById(R.id.cryptogram_puzzle_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedItemPosition = getAdapterPosition();
            // make PlayerActivity aware of clicked cryptogramId by passing clicked list item
            mOnClickListener.onListItemClick(mCryptogramListItems.get(clickedItemPosition));
        }
    }
}
