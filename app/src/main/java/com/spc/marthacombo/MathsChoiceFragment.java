package com.spc.marthacombo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

// Maths Choice Fragment that presents the different maths test options
// the choice is passed back to calling ACTIVITY to handle


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class MathsChoiceFragment extends Fragment {

    // 3. Define key variables
    private static final String MY_PREFS_FILE = "MathsPrefs";

    // 2. Defines the fragment parameters
    // NOT REQUIRED FOR THIS FRAGMENT
    private static final String TAG = "MathsChoiceFragment";
    NumberPicker np, np_min, np_sec;
    CalcType mCalc;
    ImageButton btn_multiply, btn_add, btn_divide, btn_subtract;
    Button maths_random_btn, maths_speedtest_btn, maths_practice_btn;
    private OnFragmentInteractionListener mListener;
    // 4. Required empty public constructor
    public MathsChoiceFragment() {
    }

    // 7. onCreateView to get the fragment ready for input
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maths_choice, container, false);

        // Call the activity to change the AppBar title
        mListener.setAppBarTitle(getResources().getString(R.string.app_name) + "   >> Maths <<");


        // set up the SIGN buttons
        btn_multiply = view.findViewById(R.id.btn_multiply);
        btn_multiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - Multiply Button");
                mCalc = CalcType.MULTIPLY;
                resetOperandButtons();
            }
        });
        btn_add = view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - Add Button");
                mCalc = CalcType.ADD;
                resetOperandButtons();
            }
        });
        btn_divide = view.findViewById(R.id.btn_divide);
        btn_divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - Divide Button");
                mCalc = CalcType.DIVIDE;
                resetOperandButtons();
            }
        });
        btn_subtract = view.findViewById(R.id.btn_subtract);
        btn_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - Subtract Button");
                mCalc = CalcType.SUBTRACT;
                resetOperandButtons();
            }
        });

        // Determine if a CalcType stored previously; default to "MULTIPLY" if not found
        SharedPreferences settings = this.getActivity().getSharedPreferences(MY_PREFS_FILE, 0);
        String CalcTypeString = settings.getString("CalcType", CalcType.MULTIPLY.toString());
        mCalc = CalcType.toCalcType(CalcTypeString);
        resetOperandButtons();

        // Set up the Practice Tables choice spinner - use previously saved value if available
        int lastPracticeValue = settings.getInt("LastPracticeValue", 6);
        np = view.findViewById(R.id.numberPicker);
        np.setMaxValue(12);     // max value 12
        np.setMinValue(1);      // min value 1-by georgia (amazing!!!)
        np.setValue(lastPracticeValue);

        // Set up the SpeedTest MINUTE choice spinner - use previously saved value if available
        int lastRandomMinuteValue = settings.getInt("LastRandomMinuteValue", 10);
        np_min = view.findViewById(R.id.numberPicker_min);
        np_min.setMaxValue(10);     // max value 10
        np_min.setMinValue(0);      // min value 0
        np_min.setValue(lastRandomMinuteValue);

        // Set up the SpeedTest SECOND choice spinner - use previously saved value if available
        int lastRandomSecondValue = settings.getInt("LastRandomSecondValue", 30);
        np_sec = view.findViewById(R.id.numberPicker_sec);
        np_sec.setMaxValue(59);     // max value 59
        np_sec.setMinValue(0);      // min value 0
        np_sec.setValue(lastRandomSecondValue);

        // Set up the Maths choices buttons to take action
        maths_random_btn = view.findViewById(R.id.maths_random_btn);
        maths_random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - RANDOM Button");
                mListener.onMathsChoice(mCalc, TestType.RANDOM, 0);
            }
        });
        maths_speedtest_btn = view.findViewById(R.id.maths_speedtest_btn);
        maths_speedtest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - SPEEDTEST Button");
                int param = np_min.getValue() * 60 + np_sec.getValue();
                mListener.onMathsChoice(mCalc, TestType.SPEED, param);
            }
        });
        maths_practice_btn = view.findViewById(R.id.maths_practice_btn);
        maths_practice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick - PRACTICE Button");
                int param = np.getValue();
                mListener.onMathsChoice(mCalc, TestType.PRACTICE, param);
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

    // highlights selected operand image
    public void resetOperandButtons() {
        Log.i(TAG, "Resetting operand button color filters");
        // first, deselect all, 0.0=invisible; 1.0=normal
        btn_add.setAlpha((float) 0.3);
        btn_multiply.setAlpha((float) 0.3);
        btn_divide.setAlpha((float) 0.3);
        btn_subtract.setAlpha((float) 0.3);

        switch (mCalc) {
            case ADD:
                btn_add.setAlpha((float) 1.0);
                break;
            case MULTIPLY:
                btn_multiply.setAlpha((float) 1.0);
                break;
            case DIVIDE:
                btn_divide.setAlpha((float) 1.0);
                break;
            case SUBTRACT:
                btn_subtract.setAlpha((float) 1.0);
                break;
        }
    }

    // 10. This is the hook back to the calling fragment
    // Embedded in onCreateView button listeners

    // 11. Other stuff

    // default onStop - where we persist the Maths Test choice selections
    @Override
    public void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getActivity().getSharedPreferences(MY_PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CalcType", mCalc.toString());
        editor.putInt("LastPracticeValue", np.getValue());
        editor.putInt("LastRandomMinuteValue", np_min.getValue());
        editor.putInt("LastRandomSecondValue", np_sec.getValue());
        Log.i(TAG, "storing settings: " + mCalc.toString());
        // Commit the edits!  Actually 'applies' to let it work in background...
        editor.apply();

    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnFragmentInteractionListener {
        // Defines the interface and params needed to enable activity/fragment communication
        void onMathsChoice(CalcType mCalc, TestType mTestType, int mParam);

        void setAppBarTitle(String string);
    }
}
