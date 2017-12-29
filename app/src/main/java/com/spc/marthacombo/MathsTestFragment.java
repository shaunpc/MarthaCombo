package com.spc.marthacombo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


// Maths Test Fragment that performs the testing of the user
// any resultant 'meTime' credits based on results are passed to calling activity


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class MathsTestFragment extends Fragment {

    // 2. Defines the fragment parameters
    private static final String ARG_PARAM_CALCTYPE = "CalcType";
    private static final String ARG_PARAM_TESTTYPE = "TestType";
    private static final String ARG_PARAM_TESTVALUE = "TestValue";
    // 3. Define key variables
    private static final String MY_PREFS_FILE = "MathsPrefs";
    private static final String TAG = "MathsTestFragment";
    CalcType mCalc;
    TestType mTest;
    int mDuration, mTable;  // param#3 goes into one of these
    int maxMultiplier = 13;
    Random r;
    int rm_best, rm_from, rm_contig, rm_rate;
    int rm_percent;
    int num1, num2;     // numbers in question
    int num_right = 0, num_wrong = 0, num_tries = 0, num_contig = 0;   // running totals
    int num_percent;
    TextView tv_num1, tv_num2, tv_score, tv_sign, tv_pbar;
    EditText edittext;
    ImageView iv_tick;
    ImageView iv_martha;
    ProgressBar mProgressBar;
    CountDownTimer myCountDownTimer;
    private OnFragmentInteractionListener mListener;
    // 4. Required empty public constructor
    public MathsTestFragment() {
    }

    // 5. Factory method to create new instance with parameter
    public static MathsTestFragment newInstance(CalcType param1, TestType param2, int param3) {
        MathsTestFragment fragment = new MathsTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_CALCTYPE, param1.toString());
        args.putString(ARG_PARAM_TESTTYPE, param2.toString());
        args.putInt(ARG_PARAM_TESTVALUE, param3);
        fragment.setArguments(args);
        return fragment;
    }

    // 6.Default onCreate to retrieve any parameters passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCalc = CalcType.toCalcType(getArguments().getString(ARG_PARAM_CALCTYPE));
            mTest = TestType.toTestType(getArguments().getString(ARG_PARAM_TESTTYPE));
            if (mTest == TestType.SPEED) {
                mDuration = getArguments().getInt(ARG_PARAM_TESTVALUE);
            }
            if (mTest == TestType.PRACTICE) {
                mTable = getArguments().getInt(ARG_PARAM_TESTVALUE);
            }
        }
    }

    // 7. onCreateView to get the fragment ready for input
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maths_test, container, false);

        // SpeedTest Processing setup
        mProgressBar = view.findViewById(R.id.progressBar);
        tv_pbar = view.findViewById(R.id.pb_text);

        if (mTest == TestType.SPEED) {
            Log.i(TAG, "Georgia is epic!");
            mProgressBar.setVisibility(View.VISIBLE);
            tv_pbar.setVisibility(View.VISIBLE);
            tv_pbar.setText(convertSecondsToText(mDuration));
            tv_pbar.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), (R.color.progress_end)));
            mProgressBar.setMax(mDuration);
            mProgressBar.setProgress(mDuration);
            Log.i(TAG, "Defining the CountDownTimer");
            myCountDownTimer = new CountDownTimer(mDuration * 1000, 100) {
                public void onTick(long millisUntilFinished) {
                    //set the progress bar accordingly..
                    int progress = (int) (millisUntilFinished / 1000);
                    mProgressBar.setProgress(progress);
                    tv_pbar.setText(convertSecondsToText(progress));
                    if (progress < 20) {
                        tv_pbar.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.progress_mid));
                    }
                    if (progress < 10) {
                        tv_pbar.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.progress_start));
                        tv_pbar.setTypeface(null, Typeface.BOLD);
                    }
                }

                public void onFinish() {
                    mProgressBar.setProgress(0);
                    tv_pbar.setText(getActivity().getApplicationContext().getResources().getString(R.string.martha_timeIsUp));
                    finishedSpeedTest();
                }
            }.start();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            tv_pbar.setVisibility(View.INVISIBLE);
        }

        // Restore the high-score variables from Preferences
        getHighScores();

        Log.i(TAG, "created, now getting key view elements identifiers");
        tv_num1 = view.findViewById(R.id.textView_num1);
        tv_num2 = view.findViewById(R.id.textView_num2);
        tv_score = view.findViewById(R.id.tv_score);

        tv_sign = view.findViewById(R.id.sign);
        if (mCalc == CalcType.ADD) {
            tv_sign.setText("+");
            maxMultiplier = 50;       // And use bigger random numbers
        }
        if (mCalc == CalcType.SUBTRACT) {
            tv_sign.setText("-");
            maxMultiplier = 50;       // And use bigger random numbers
        }
        if (mCalc == CalcType.DIVIDE) {
            tv_sign.setText("÷");
        }
        if (mCalc == CalcType.MULTIPLY) {
            tv_sign.setText("x");
        }

        edittext = view.findViewById(R.id.ed_answer);
        iv_tick = view.findViewById(R.id.iv_tick);
        iv_martha = view.findViewById(R.id.full_layout);

        iv_martha.setLongClickable(true);
        iv_martha.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // clear the high score variables
                Toast.makeText(getActivity().getBaseContext(), "Resetting High Scores", Toast.LENGTH_SHORT).show();
                clearHighScores();

                // update high-scores and display
                updateScores();
                return true;
            }
        });

        // set the EditorAction listener so that keypad remains visible after DONE
        Log.i(TAG, "setting onEditorAction");
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // return=TRUE to keep keypad visible
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.i(TAG, "in onEditorAction after IME_ACTION_DONE");
                    // Perform action on key press, after checking not empty
                    String enteredText = edittext.getText().toString();
                    if (enteredText.length() != 0) checkCorrect(Integer.parseInt(enteredText));
                    return true;
                }
                return false;
            }
        });

        // put the focus on the answer field, and enable soft keyboard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edittext.setShowSoftInputOnFocus(true);
        }
        edittext.requestFocus();
        // edittext.setsetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Start the random number seed
        r = new Random();

        Log.i(TAG, "starting the number generator");
        generateTest();

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

    // default onResume, but needed to restart the testing if interrupted
    @Override
    public void onResume() {
        super.onResume();
        generateTest();

    }

    // 10. This is the hook back to the calling fragment
    // currently embedded in CheckCorrect () - adds 1 meTime every correct answer...
    // and in finishedSpeedTest() - gives bonus on #(right-wrong)

    // 11. Other stuff

    // default onStop() to save any high scores, and cancel any running timer
    @Override
    public void onStop() {
        super.onStop();
        saveHighScores();   // Save the high scores for future reference
        // Cancel the timer if we have one...
        if (mTest == TestType.SPEED && myCountDownTimer != null) myCountDownTimer.cancel();
    }

    // Get a couple of random integers, and adjust to make sense (and slightly easier)
    public void generateTest() {
        num1 = r.nextInt(maxMultiplier);
        num2 = r.nextInt(maxMultiplier);

        // If running in PRACTICE mode...
        if (mTest == TestType.PRACTICE) {
            // fix the passed base number
            if (mCalc == CalcType.MULTIPLY || mCalc == CalcType.ADD) num1 = mTable;
            if (mCalc == CalcType.DIVIDE || mCalc == CalcType.SUBTRACT) num2 = mTable;
        }

        // make it easier to keep whole number answers - for division
        if (mCalc == CalcType.DIVIDE) num1 = num1 * num2;

        // check for DIV by ZERO
        if (mCalc == CalcType.DIVIDE && num2 == 0) num2 = 1;

        // make it easier to keep positive number answers - for subtraction
        if (mCalc == CalcType.SUBTRACT && num2 > num1) {
            int tmp_int = num2;
            num2 = num1;
            num1 = tmp_int;
        }

        tv_num1.setText(String.valueOf(num1));
        tv_num2.setText(String.valueOf(num2));
    }

    // Check if the input answer to this individual test is correct
    public void checkCorrect(int answer) {

        int real_answer = 0;
        switch (mCalc) {
            case MULTIPLY:
                real_answer = num1 * num2;
                break;
            case ADD:
                real_answer = num1 + num2;
                break;
            case DIVIDE:
                real_answer = num1 / num2;
                break;
            case SUBTRACT:
                real_answer = num1 - num2;
                break;
        }

        if (answer == real_answer) {
            showCustomToast(R.drawable.green_tick);
            num_right++;
            num_contig++;    // Add one to the current contiguous correct count
            mListener.onMathsTestResult(1);  // Increment MeTime for a correct answer
        } else {
            showCustomToast(R.drawable.red_cross);
            num_wrong++;
            num_contig = 0; // Reset the contiguous correct count
        }
        num_tries++;
        num_percent = (int) ((num_right * 100.0f) / num_tries);

        // update high-scores and display
        updateScores();

        // clear the previous input and get some new numbers...
        edittext.setText("");
        generateTest();
    }

    // constructs and display a 'running commentary' of the overall score progress
    public void updateScores() {
        String tmp_str = "SCORE : " + num_right + " out of " + num_tries + " (" + num_percent + "%)";

        if (num_tries > rm_from && num_percent > rm_percent) {
            rm_percent = num_percent;  //set the % correct record
            rm_best = num_right;
            rm_from = num_tries;
            tmp_str = tmp_str + " - the highest ever!";
        } else {
            tmp_str = tmp_str + " - best ever is " + rm_percent + "%";
        }

        if (num_contig > 0) {
            tmp_str = tmp_str + "\nYou're on a run of " + num_contig + " correct answers";
            if (num_contig > rm_contig) {
                rm_contig = num_contig;  //set the contiguous record
                tmp_str = tmp_str + " - the highest ever!";
            } else {
                tmp_str = tmp_str + " - best ever is " + rm_contig;
            }
        } else {
            tmp_str = tmp_str + "\nGet more than " + rm_contig + " correct in a row to set new record";
        }

        tv_score.setText(tmp_str);
    }

    // formats number of seconds into 00:00 string format for display...
    // TODO - probably a cleaner way using String.format?
    public String convertSecondsToText(int seconds) {
        int tmp_min = seconds / 60;
        int tmp_sec = seconds - (tmp_min * 60);
        String tmp_text = "";
        if (tmp_min < 10) {
            tmp_text = tmp_text + "0";
        }
        tmp_text = tmp_text + tmp_min + ":";
        if (tmp_sec < 10) {
            tmp_text = tmp_text + "0";
        }
        tmp_text = tmp_text + tmp_sec;
        return tmp_text;
    }

    // Pull in any previous scores from the Preferences file
    public void getHighScores() {
        SharedPreferences settings = getActivity().getSharedPreferences(MY_PREFS_FILE, 0);
        switch (mTest) {
            case RANDOM:
                rm_best = settings.getInt("RM_best", 0);
                rm_from = settings.getInt("RM_from", 0);
                rm_contig = settings.getInt("RM_contig", 0);
                rm_rate = 0;
                break;

            case PRACTICE:
                rm_best = settings.getInt("PT_best", 0);
                rm_from = settings.getInt("PT_from", 0);
                rm_contig = settings.getInt("PT_contig", 0);
                rm_rate = 0;
                break;

            case SPEED:
                rm_best = settings.getInt("ST_best", 0);
                rm_from = settings.getInt("ST_from", 0);
                rm_contig = settings.getInt("ST_contig", 0);
                rm_rate = settings.getInt("ST_rate", 0);
                break;

        }

        if (rm_from == 0) {
            rm_percent = 0;
        } else {
            rm_percent = (int) ((rm_best * 100.0f) / rm_from);
        }
    }

    // reset the current high score tracker variables
    public void clearHighScores() {
        rm_from = 0;
        rm_best = 0;
        rm_contig = 0;
        rm_rate = 0;
        rm_percent = 0;     //Calc, not stored, but still need to clear

    }

    // store the current high score variables in the preferences file
    public void saveHighScores() {
        SharedPreferences settings = getActivity().getSharedPreferences(MY_PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        switch (mTest) {
            case RANDOM:
                editor.putInt("RM_best", rm_best);
                editor.putInt("RM_from", rm_from);
                editor.putInt("RM_contig", rm_contig);
                break;
            case PRACTICE:
                editor.putInt("PT_best", rm_best);
                editor.putInt("PT_from", rm_from);
                editor.putInt("PT_contig", rm_contig);
                break;
            case SPEED:
                editor.putInt("ST_best", rm_best);
                editor.putInt("ST_from", rm_from);
                editor.putInt("ST_contig", rm_contig);
                editor.putInt("ST_rate", rm_rate);
                break;
        }
        editor.apply();
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

    private void finishedSpeedTest() {

        String btn;
        String msg;

        // Construct result message/congrats
        if (num_tries == 0) {
            msg = "You didn't attempt to answer anything!";
            btn = "TRY AGAIN!";
        } else {
            if (num_right == 0) {
                msg = "You didn't get any correct at all!";
                btn = "TRY HARDER!";
            } else {
                msg = "You got " + num_right + " correct out of " + num_tries + " questions";
                switch (num_wrong) {
                    case 0:
                        msg = msg + "\n... and no answers wrong!";
                        btn = "> VERY COOL <";
                        break;
                    case 1:
                        msg = msg + "\n... that means just " + num_wrong + " was answered wrong...";
                        btn = "NOT BAD";
                        break;
                    default:
                        msg = msg + "\n... that means " + num_wrong + " were answered wrong...";
                        btn = "OK";
                }
            }
            msg = msg + " So, " + num_percent + "% correct in " + convertSecondsToText(mDuration) + " minutes";
        }

        // display the dialog so Speed Test result
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Speed Test Finished")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // simply finish this activity, and return to main menu
                        dialog.dismiss();
                        // Give some bonus MeTime
                        mListener.onMathsTestResult(num_right - num_wrong);
                        // close this maths test fragment
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        void onMathsTestResult(int value);
    }
}
