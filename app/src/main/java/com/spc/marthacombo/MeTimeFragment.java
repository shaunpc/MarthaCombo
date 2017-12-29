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
import android.widget.TextView;
import android.widget.Toast;


//
// MeTime fragment. Utilised to keep track of credit earned across a number of tasks
//

// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class MeTimeFragment extends Fragment {

    // 1. Defines the listener interface with a method passing back data result.
    // NOTHING PASSED BACK

    // 2. Defines the fragment parameters
    // NO PARAMETERS REQUIRED

    // 3. Define key variables
    private static final String MY_PREFS_FILE = "MeTimePrefs";
    private static final String TAG = "MeTimeFragment";
    TextView meTimeCredit;
    SharedPreferences sharedpreferences;
    private int meTime;

    // 4. Required empty public constructor   --   TODO - seemed to work without this??
    public MeTimeFragment() {
    }

    // 5. Factory method to create new instance with parameter
    // NO PARAMETERS REQUIRED

    // 6.Default onCreate to retrieve any parameters passed
    // NO PARAMETERS REQUIRED

    // 7. onCreateView to get the fragment ready for input
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me_time, container, false);

        // Find button in view and set long/short click listeners
        Button meTimeButton = view.findViewById(R.id.meTimeButton);
        meTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "MeTime button short-click called");
                // reduce MeTime Credits by 15
                meTime -= 15;
                if (meTime < 0) {
                    meTime = 0;
                }
                meTimeCredit.setText(String.valueOf(meTime));
            }
        });
        meTimeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Log.d(TAG, "MeTime button long-click called");
                // reset MeTime credits to ZERO
                meTime = 0;
                meTimeCredit.setText(String.valueOf(meTime));
                Toast.makeText(getActivity().getApplicationContext(), "MeTime Credits reset to ZERO", Toast.LENGTH_SHORT).show();
                return true;    // indicates no further processing needed
            }
        });

        // Open Prefs File and find MeTime, will default minutes to ZERO if not found
        sharedpreferences = this.getActivity().getSharedPreferences(MY_PREFS_FILE, Context.MODE_PRIVATE);
        meTime = sharedpreferences.getInt("MeTime", 0);

        // Find the text view, and populate with what we find in Preference file
        meTimeCredit = view.findViewById(R.id.meTimeCredit);
        meTimeCredit.setText(String.valueOf(meTime));

        return view;
    }

    // 8. default onAttach to check method implemented in caller, and set the listener
    // NOT REQUIRED as activity doesn't need responses from this fragment

    // 9. default onDetach to zap stuff
    // NOT REQUIRED as no specific activity listener to remove

    // 10. This is the hook back to the calling fragment
    // NOT REQUIRED as nothing passed back

    // 11. Other stuff

    // As the fragment pauses, then store the current MeTime credit in the preferences file
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "MeTime fragment is pausing. saving MeTime:" + meTime);
        // stores the integer in the preferences file
        sharedpreferences.edit().putInt("MeTime", meTime).apply();
    }

    // this method is called by the ACTIVITY on it's MeTime object
    public void increase(int credit) {
        Log.d(TAG, "MeTime increase() called with " + credit);
        meTime += credit;
        meTimeCredit.setText(String.valueOf(meTime));
        // Toast.makeText(getActivity().getApplicationContext(), "MeTime Credits incremented", Toast.LENGTH_SHORT).show();
    }
}
