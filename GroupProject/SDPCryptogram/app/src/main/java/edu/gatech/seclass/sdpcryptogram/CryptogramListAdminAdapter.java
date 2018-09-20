package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.gatech.seclass.sdpcryptogram.entity.Cryptogram;

/**
 * Displays Cryptogram List in Admin Dashboard
 */

public class CryptogramListAdminAdapter extends RecyclerView.Adapter<CryptogramListAdminAdapter.CryptogramViewHolder>{

    // lenght of captions to show, trim the rest
    private static final int CRYPTOGRAM_CAPTION_LENGTH = 50;
    private List<Cryptogram> mCryptogramList;
    private Context mContext;

    /**
     * Constructor using the context and the db cursor
     * @param context
     * @param cryptograms
     */
    public CryptogramListAdminAdapter(Context context, final List<Cryptogram> cryptograms) {
        this.mContext = context;
        this.mCryptogramList = cryptograms;
    }

    @Override
    public CryptogramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the cryptogram_list_admin_item to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.cryptogram_list_admin_item, parent, false);
        return new CryptogramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CryptogramViewHolder holder, int position) {
        if(mCryptogramList == null || mCryptogramList.size() == 0)
            return;

        // set view holder views
        holder.itemView.setTag(mCryptogramList.get(position).getCryptogramId());
        holder.mCryptogramId.setText(String.valueOf(mCryptogramList.get(position).getCryptogramUID()));
        holder.mCryptogramCaption.setText(mCryptogramList.get(position).getSolutionPhrase(CRYPTOGRAM_CAPTION_LENGTH));

    }

    @Override
    public int getItemCount() {
        if(mCryptogramList == null){
            return 0;
        }
        return mCryptogramList.size();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor that is passed in.
     */
    public void swapCursor(final List<Cryptogram> cryptograms) {
        // check if this cursor is the same as the previous cursor
        if (mCryptogramList == cryptograms) {
            return; // nothing has changed
        }

        //check if valid and update the cursor
        if (cryptograms != null) {
            mCryptogramList = cryptograms;
            this.notifyDataSetChanged();
        }
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class CryptogramViewHolder extends RecyclerView.ViewHolder {

        TextView mCryptogramId;
        TextView mCryptogramCaption;

        /**
         * ViewHolder constructor
         * @param itemView inflated in onCreateViewHolder
         *
         */

        public CryptogramViewHolder(View itemView) {
            super(itemView);

            mCryptogramId = (TextView) itemView.findViewById(R.id.cryptogram_id_text_view);
            mCryptogramCaption = (TextView) itemView.findViewById(R.id.cryptogram_caption_text_view);
        }

    }
}
