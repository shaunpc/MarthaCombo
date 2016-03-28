package com.spc.marthacombo;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


// Dialog Fragment that enables single text field to be entered.
// Assumes called from a fragment, and returns the text entered


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()

public class EditTextDialog extends DialogFragment
        implements TextView.OnEditorActionListener {

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        void onFinishEditDialog(String inputText);
    }

    // 2. Defines the fragment parameters
    private static final String ARG_DIALOG_TITLE = "title";
    private String mTitle;

    // 3. Define key variables
    private final static String TAG = "EditTextDialog";
    private OnFragmentInteractionListener mListener;
    private EditText mEditText;

    // 4. Required empty public constructor
    public EditTextDialog() {
    }

    // 5. Factory method to create new instance with parameter
    public static EditTextDialog newInstance(String title) {
        Log.i(TAG, "newInstance(" + title + ")");
        EditTextDialog fragment = new EditTextDialog();
        Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    // 6.Default onCreate to retrieve any parameters passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_DIALOG_TITLE);
        }
    }

    // 7. onCreateView to get the dialog ready for input
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_text_dialog, container, false);
        mEditText = (EditText) view.findViewById(R.id.fragment_edit_text_value);
        // Set the dialog title from the parameter passed
        getDialog().setTitle(mTitle);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // Setup a callback when the "Done" button is pressed on keyboard
        mEditText.setOnEditorActionListener(this);

        return view;
    }

    // 8. default onAttach to check method implemented in caller, and set the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach()");

        // Check if the calling ACTIVITY has the interaction listener implemented
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // Check if the calling FRAGMENT has the interaction listener implemented
            Fragment owner = getTargetFragment();
            if (owner instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) owner;
            } else {
                throw new RuntimeException(context.toString() + "/" + owner.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }


    // 9. default onDetach to zap stuff
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach()");
        mListener = null;
    }

    // 10. This is the hook back to the calling fragment/activity in mListener
    // Fires whenever the text field has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            mListener.onFinishEditDialog(mEditText.getText().toString());
            // Close the dialog and return back to the parent activity/fragment
            dismiss();
            return true;
        }
        return false;
    }

}
