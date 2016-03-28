package com.spc.marthacombo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


// Spelling Test Fragment that performs the testing of the user
// any resultant 'meTime' credits based on results are passed to calling activity


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class SpellingTestFragment extends Fragment {

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        void onSpellingTestResult(int value);

        void speakThis(String sayThis);

        void spellThis(String sayThis);
    }

    // 2. Defines the fragment parameters
    // NONE REQUIRED

    // 3. Define key variables
    private static final String TAG = "SpellingTestFragment";
    private OnFragmentInteractionListener mListener;
    SpellingList mySpellingList;
    int wordCount, guessCount, correctCount;
    Button btn_play;
    TextView tv_title, tv_score;
    ImageView iv_tick, iv_martha;
    EditText ed_answer;
    String displayThis, sayThis;


    // 4. Required empty public constructor
    public SpellingTestFragment() {
    }

    // 5. Factory method to create new instance with parameter
    // NOT REQUIRED

    // 6.Default onCreate to retrieve any parameters passed
    // NONE REQUIRED

    // 7. onCreateView to get the fragment ready for input
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spelling_test, container, false);

        // Get PREFS file with any existing test/progress info...
        mySpellingList = new SpellingList(getContext());
        mySpellingList.populateSpellingWordList();
        mySpellingList.logListContents();

        // Set up the various UI component variables
        btn_play = (Button) view.findViewById(R.id.btn_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           // set the PLAY listener
                Log.i(TAG, "PLAY pressed - for word #" + guessCount);
                Log.i(TAG, "Spelling Word is: " + mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                if (null != mListener) {
                    // Get the MainActivity to speak the word
                    mListener.speakThis(mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                }
            }
        });
        btn_play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {           // set the PLAY listener
                Log.i(TAG, "PLAY long pressed - for word #" + guessCount);
                Log.i(TAG, "Spelling Word is: " + mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                if (null != mListener) {
                    // Get the MainActivity to SPELL the word, then repeat word
                    mListener.speakThis(mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                    mListener.spellThis(mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                    mListener.speakThis(mySpellingList.SPELLING_WORD_LIST.get(guessCount));
                }
                return true;
            }
        });

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_score = (TextView) view.findViewById(R.id.tv_score);
        iv_tick = (ImageView) view.findViewById(R.id.iv_tick);
        iv_tick.setVisibility(View.INVISIBLE);
        iv_martha = (ImageView) view.findViewById(R.id.iv_martha);
        ed_answer = (EditText) view.findViewById(R.id.ed_answer);

        // set the EditorAction listener so that keypad remains visible after DONE
        Log.i(TAG, "setting onEditorAction");
        ed_answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // return=TRUE to keep keypad visible
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.i(TAG, "in onEditorAction after IME_ACTION_DONE");
                    // Perform action on key press, after checking not empty
                    String enteredText = ed_answer.getText().toString();
                    if (enteredText.length() != 0) checkCorrect(enteredText.trim());
                    return true;
                }
                return false;
            }
        });


        // Figure out where we last got to....
        wordCount = mySpellingList.SPELLING_WORD_LIST.size();
        guessCount = -1;
        correctCount = 0;
        for (int i = 0; i < wordCount; i++) {

            if (mySpellingList.SPELLING_GUESS_LIST.get(i).equals("")) {
                guessCount = i;
                break; //out of the FOR loop
            }
            if (mySpellingList.SPELLING_GUESS_LIST.get(i).equalsIgnoreCase(mySpellingList.SPELLING_WORD_LIST.get(i))) {
                correctCount += 1;
            }
        }
        Log.i(TAG, "Starting test from position " + guessCount);

        if (guessCount == -1) { //no null guesses found
            mListener.speakThis("This test has been completed");
            displayThis = "This test has been completed - you scored " + correctCount + " out of " + wordCount;
            tv_score.setText(displayThis);
            finishedSpellingTest();
        } else {
            if (guessCount == 0) {
                sayThis = "Let's start the test";
                displayThis = "There are " + wordCount + " words in this test\nLet's see how well you can do!";
            } else {
                sayThis = "Let's continue the test";
                displayThis = "You have " + correctCount + " correct so far from " + (guessCount);
                if (wordCount - guessCount == 1) {
                    displayThis = displayThis + "\nThis is the last word!";
                } else {
                    if (wordCount - guessCount == 2) {
                        displayThis = displayThis + "\nJust one word remaining after this";
                    } else {
                        displayThis = displayThis + "\nStill " + (wordCount - guessCount - 1) + " words remaining after this";
                    }
                }
            }
            tv_score.setText(displayThis);
            mListener.speakThis(sayThis);
            generateTest();
        }
        return view;
    }

    // 8. default onAttach to check method implemented in caller, and set the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mListener = null;
    }

    // 10. This is the hook back to the calling fragment
    // currently embedded in CheckCorrect () - adds 1 meTime every correct answer...
    // and in finishedSpeedTest() - gives bonus on #(right-wrong)

    // 11. Other stuff

    // default onStop() to save test progress
    @Override
    public void onStop() {
        super.onStop();
        mySpellingList.persistSpellingWordList();   // Save Spelling Test progress
    }


    public void generateTest() {

        Log.i(TAG, "Generating TEST for position " + guessCount);

        String testNumber = String.valueOf(guessCount + 1);
        btn_play.setText(testNumber);
        String testText = "Word #" + testNumber;
        tv_title.setText(testText);
        mListener.speakThis("Word number " + testNumber);

        // clear any previous answer, put the focus on the answer field, and enable soft keyboard
        ed_answer.setText("");
        ed_answer.setShowSoftInputOnFocus(true);
        ed_answer.requestFocus();
        // ed_answer.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mListener.speakThis(mySpellingList.SPELLING_WORD_LIST.get(guessCount));

    }

    public void checkCorrect(String answer) {

        mySpellingList.updateGuess(guessCount, answer);

        if (answer.equalsIgnoreCase(mySpellingList.SPELLING_WORD_LIST.get(guessCount))) {
            showCustomToast(R.drawable.green_tick);
            correctCount += 1;          // Increment the #correct
            mListener.onSpellingTestResult(1);  // Increment MeTime for a correct answer
        } else {
            showCustomToast(R.drawable.red_cross);
        }

        guessCount += 1;    // Increment the guess counter
        updateScores();

    }

    //
    public void updateScores() {
        if (guessCount == wordCount) { //word Count is #words, but guess Count is the position (starts from ZERO)
            mListener.speakThis("You've completed all the words in this test");
            displayThis = "This test has been completed - you scored " + correctCount + " out of " + wordCount;
            tv_score.setText(displayThis);
            finishedSpellingTest();
        } else {
            displayThis = "You have " + correctCount + " correct so far from " + (guessCount);
            if (wordCount - guessCount == 1) {
                displayThis = displayThis + "\nThis is the last word!";
            } else {
                if (wordCount - guessCount == 2) {
                    displayThis = displayThis + "\nJust one word remaining after this";
                } else {
                    displayThis = displayThis + "\nStill " + (wordCount - guessCount - 1) + " words remaining after this";
                }
            }
            tv_score.setText(displayThis);
            generateTest();
        }
    }

    // Provides a fading tick/cross to reflect whether answer was correct
    public void showCustomToast(int resource_id) {
        // Set the new image, make it visible fade it out, then make view invisible...
        iv_tick.setImageResource(resource_id);
        iv_tick.setVisibility(View.VISIBLE);
        Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(2000);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_tick.setVisibility(View.INVISIBLE);
            }
        });
        iv_tick.startAnimation(fadeOutAnimation);
    }

    private void finishedSpellingTest() {

        String btn;
        String msg;

        if (correctCount == 0) {
            msg = "You didn't get any correct at all!";
            btn = "TRY HARDER!";
        } else {
            msg = "You correctly spelt " + correctCount + " of the " + wordCount + " words";
            switch (wordCount - correctCount) {
                case 0:
                    msg = msg + "\n... so no wrong answers!";
                    btn = "> VERY COOL <";
                    break;
                case 1:
                    msg = msg + "\n... that means just a single word was answered wrong...";
                    btn = "NOT BAD";
                    break;
                default:
                    msg = msg + "\n... that means " + (wordCount - correctCount) + " were answered wrong...";
                    btn = "Hmmmm...";
            }
            int percent = (correctCount * 100) / wordCount;
            msg = msg + " [" + percent + "%]";
        }

        // display the dialog Spelling Test result
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Spelling Test Finished")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // simply finish this activity, and return to main menu
                        dialog.dismiss();
                        // Give some bonus MeTime
                        mListener.onSpellingTestResult(correctCount);

                        // close this spelling test fragment
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        ed_answer.setText("");


    }
}
