package com.spc.marthacombo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


// Spelling Choice Fragment that enables selection of spelling related action
//  a. SETUP the spelling test words
//  b. take the TEST
//  c. REVIEW the results
// Output selection choice of this fragment is passed back to the calling Activity


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class SpellingChoiceFragment extends Fragment {

    // 3. Define key variables
    private final static String TAG = "SpellingChoiceFragment";

    // 2. Defines the fragment parameters
    // NONE for this fragment
    public SpellingList mySpellingList;
    private OnFragmentInteractionListener mListener;
    // 4. Required empty public constructor
    public SpellingChoiceFragment() {
    }

    // 7. onCreateView to get the dialog ready for input
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spelling_choice, container, false);

        // Call the activity to change the AppBar title
        mListener.setAppBarTitle(getResources().getString(R.string.app_name) + "   >> Spelling <<");

        // Set up the Maths choices buttons to take action onClick
        Button setupButton = view.findViewById(R.id.button_setup);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - SETUP Button");
                mListener.onSpellingChoice(Choices.SPELLING_SETUP);
            }
        });
        Button testButton = view.findViewById(R.id.button_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - TEST Button");
                mListener.onSpellingChoice(Choices.SPELLING_TEST);
            }
        });
        Button reviewButton = view.findViewById(R.id.button_review);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - REVIEW Button");
                mListener.onSpellingChoice(Choices.SPELLING_REVIEW);
            }
        });

        // Get PREFS file with any existing test/progress info...
        mySpellingList = new SpellingList(getContext());
        mySpellingList.populateSpellingWordList();
        mySpellingList.logListContents();
        Log.i(TAG, "Spelling list is: " + mySpellingList.SPELLING_WORD_LIST.toString());

        // If only the sample word, then no real test word list stored... present only SETUP button
        if ((mySpellingList.SPELLING_WORD_LIST.get(0).equals("sample")) &&
                (mySpellingList.SPELLING_WORD_LIST.size() == 1)) {
            Log.i(TAG, "Only sample word list available, force SETUP choice");
            testButton.setClickable(false);
            testButton.setAlpha((float) 0.2);
            reviewButton.setClickable(false);
            reviewButton.setAlpha((float) 0.2);
            final Animation animScale = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_scale);
            setupButton.startAnimation(animScale);
        }

        return view;
    }

    // 5. Factory method to create new instance with parameter
    // NONE for this fragment

    // 6.Default onCreate to retrieve any parameters passed
    // NONE for this fragment

    // 8. default onAttach to check method implemented in caller, and set the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach()");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // 9. default onDetach to zap stuff
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach() - clearing mListener");
        mListener = null;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        void onSpellingChoice(Choices choice);

        void setAppBarTitle(String string);
    }

    // 10. This is the hook back to the calling fragment
    // Embedded in onCreateView button onClickListener definitions

}