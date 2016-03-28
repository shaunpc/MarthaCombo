package com.spc.marthacombo;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spc.marthacombo.ItemFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {RecyclerView.Adapter} that can display a STRING and makes a call to the
 * specified OnListFragmentInteractionListener}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValuesWords;
    private final List<String> mValuesGuesses;
    private final OnListFragmentInteractionListener mListener;
    private final boolean mSetupMode;
    private final static String TAG = "MyItemAdapter";

    public MyItemRecyclerViewAdapter(List<String> words, List<String> guesses, OnListFragmentInteractionListener listener, boolean setupMode) {
        mValuesWords = words;
        mValuesGuesses = guesses;
        mListener = listener;
        mSetupMode = setupMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "onBindHolder with int=" + position);
        holder.mPosition = position;                //store this list array position
        holder.mItemWord = mValuesWords.get(position);       //get the WORD at this position
        holder.mItemGuess = mValuesGuesses.get(position);       //get the GUESS at this position
        holder.mIdView.setText(String.valueOf(position + 1));   // set the #word on screen (1..n)
        holder.mIdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // set the PLAY listener
                Log.i(TAG, "Item #" + holder.mPosition + " - PLAY pressed");
                Log.i(TAG, "Word Content is " + holder.mItemWord);
                if (null != mListener) {
                    // Get the MainActivity to speak the word
                    mListener.speakThis(holder.mItemWord);
                }
            }
        });
        if (mSetupMode) {
            // We're in SETUP mode, show actual WORD, and offer the DELETE word option
            holder.mContentView.setText(holder.mItemWord);  // set the actual word on screen
            holder.mDelete.setBackgroundResource(android.R.drawable.ic_menu_delete);
            holder.mDelete.setColorFilter(Color.RED);   // make the delete button red
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   // set the DELETE listener
                    Log.i(TAG, "Item #" + holder.mPosition + " - DELETE pressed");
                    Log.i(TAG, "Word Content is " + holder.mItemWord);
                    removeItem(holder.mPosition);

                }
            });
        } else {
            // We're in REVIEW mode, so change the 'DELETE' option to marking result image
            if (holder.mItemGuess.equals("")) { // Hasn't been guessed yet...
                holder.mContentView.setText("-");
                holder.mContentView.setTextColor(Color.LTGRAY);
                holder.mDelete.setBackgroundResource(R.drawable.box_empty);
            } else {
                holder.mContentView.setText(holder.mItemGuess);     // show the guessed word
                if (holder.mItemGuess.equals(holder.mItemWord)) {   // guessed correctly
                    holder.mContentView.setTextColor(Color.GREEN);
                    holder.mDelete.setBackgroundResource(R.drawable.box_green_tick);
                } else {    // guessed incorrectly
                    holder.mContentView.setTextColor(Color.RED);
                    holder.mDelete.setBackgroundResource(R.drawable.box_red_cross);
                    holder.mDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {   // set the DELETE listener
                            Log.i(TAG, "Item #" + holder.mPosition + " - INCORRECT pressed");
                            Log.i(TAG, "Word=" + holder.mItemWord + "/Guess=" + holder.mItemGuess);
                            if (null != mListener) {
                                // Get the MainActivity to speak the word
                                mListener.spellThis(holder.mItemWord);
                            }
                        }
                    });
                }
            }

        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Log.i(TAG, "List Row#" + holder.mItemWord + " pressed");
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // mListener.onListFragmentInteraction(holder.mItemWord);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValuesWords.size();
    }

    public void removeItem(int position) {
        mValuesWords.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Button mIdView;
        public final TextView mContentView;
        public final ImageButton mDelete;
        int mPosition;
        String mItemWord;
        String mItemGuess;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (Button) view.findViewById(R.id.list_item_btn_play);
            mContentView = (TextView) view.findViewById(R.id.list_item_word);
            mDelete = (ImageButton) view.findViewById(R.id.list_item_btn_delete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
