package com.spc.marthacombo;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

// Main Choice Fragment that presents the two sub-app choices:
//    MATHS
//    SPELLING
// the choice is passed back to calling ACTIVITY to handle


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class MainChoiceFragment extends Fragment
        implements EditTextDialog.OnFragmentInteractionListener {

    // 3. Define key variables
    private final static String TAG = "MainChoiceFragment";

    // 2. Defines the fragment parameters
    // NOT REQUIRED FOR THIS FRAGMENT
    ImageButton ibMathsButton;
    ImageButton ibSpellingButton;
    Animation animScale, animRotate, animTranslate, animAlpha;
    private OnFragmentInteractionListener mListener;
    // 4. Required empty public constructor
    public MainChoiceFragment() {
    }

    // 7. onCreateView to get the dialog ready for input
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        // Inflate the layout for this fragment, and get context for animations
        View view = inflater.inflate(R.layout.fragment_main_choice, container, false);
        Context context = getActivity().getApplicationContext();

        // Call the activity to change the AppBar title
        mListener.setAppBarTitle(getResources().getString(R.string.app_name));

        // Load the main choice button animations
        animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);
        animRotate = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        animScale = AnimationUtils.loadAnimation(context, R.anim.anim_scale);
        animTranslate = AnimationUtils.loadAnimation(context, R.anim.anim_translate);

        ibMathsButton = view.findViewById(R.id.ibMathsButton);
        ibMathsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - MathsButton");
                // Send the event to the host activity
                mListener.onMainChoice(Choices.MATHS);
            }
        });
        ibSpellingButton = view.findViewById(R.id.ibSpellingButton);
        ibSpellingButton.startAnimation(animScale);
        ibSpellingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - SpellingButton");
                // Send the event to the host activity
                mListener.onMainChoice(Choices.SPELLING);
            }
        });

        // Set the long-click on the main image to be capture a word/phrase in dialog...
        ImageView ivMainImage = view.findViewById(R.id.ivMainImage);
        ivMainImage.setLongClickable(true);
        ivMainImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v(TAG, "onLongClick - Martha's Easter Egg");
                // Initiate a Dialog to capture some text
                showEditDialog("Enter something funny...");
                return true;
            }
        });

        return view;

    }

    // 5. Factory method to create new instance with parameter
    // NOT REQUIRED FOR THIS FRAGMENT

    // 6.Default onCreate to retrieve any parameters passed
    // NOT REQUIRED FOR THIS FRAGMENT

    // 8. default onAttach to check method implemented in caller, and set the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
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
        Log.i(TAG, "onDetach");
        mListener = null;
    }

    // These are used to launch the EditText Dialog fragment, and to handle the resultant callback
    private void showEditDialog(String title) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditTextDialog editTextDialog = EditTextDialog.newInstance(title);
        // SETS the target fragment for use later when sending results
        editTextDialog.setTargetFragment(MainChoiceFragment.this, 300);
        editTextDialog.show(fm, "fragment_edit_name");
    }

    // 10. This is the hook back to the calling fragment
    // Embedded in onCreateView button listeners

    // 11. Other stuff

    @Override
    public void onFinishEditDialog(String inputText) {
        Log.i(TAG, "onFinishedEditDialog(" + inputText + ")");
        if (inputText != null) {
            // Tell the activity to run the speakThis method
            mListener.speakThis(inputText);
        }
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        void onMainChoice(Choices choice);

        void speakThis(String string);

        void setAppBarTitle(String string);
    }

}
