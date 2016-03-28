package com.spc.marthacombo;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

// Main Activity
//
//

public class MainActivity extends AppCompatActivity
        implements MainChoiceFragment.OnFragmentInteractionListener,
        MathsChoiceFragment.OnFragmentInteractionListener,
        MathsTestFragment.OnFragmentInteractionListener,
        SpellingChoiceFragment.OnFragmentInteractionListener,
        ItemFragment.OnListFragmentInteractionListener,
        SpellingTestFragment.OnFragmentInteractionListener {

    // 3. Define key variables
    private final static String TAG = "MainActivity";
    private MeTimeFragment meTimeFragment;

    // Text-to-Speech helper class
    private final int CHECK_CODE = 0x1;
    private Speaker speaker;
    String salutation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        // For debug/etc
        logDeviceInfo();

        // initiate the Test-to-Speech Helper to get engine running...
        checkTTS();
        salutation = getResources().getString(R.string.tvMainTitle);

        setAppBarTitle(getResources().getString(R.string.app_name));
        if (getSupportActionBar() != null) getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        // ensure we have the MeTime fragment (really should check for null if not there!)
        meTimeFragment = (MeTimeFragment) getSupportFragmentManager().findFragmentById(R.id.meTimeFragment);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // However, if we're being restored from a previous state, then we don't need to do
            // anything and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainChoiceFragment firstFragment = new MainChoiceFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

    }

    // method to handle the response from the from MainChoice fragment
    public void onMainChoice(Choices choice) {
        Log.i(TAG, "Received MainChoice=" + choice.toString());
        switch (choice) {
            case SPELLING:
                if (speaker.isReady()) {
                    speaker.speak("Let's try spelling!");
                }
                SpellingChoiceFragment newSpellingChoiceFragment = new SpellingChoiceFragment();
                FragmentTransaction transactionSpelling = getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                transactionSpelling.replace(R.id.fragment_container, newSpellingChoiceFragment);
                // and add the transaction to the back stack so the user can navigate back
                transactionSpelling.addToBackStack(null);
                transactionSpelling.commit();
                break;
            case MATHS:
                if (speaker.isReady()) {
                    speaker.speak("Let's try maths!");
                }
                MathsChoiceFragment newMathsChoiceFragment = new MathsChoiceFragment();
                FragmentTransaction transactionMaths = getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                transactionMaths.replace(R.id.fragment_container, newMathsChoiceFragment);
                // and add the transaction to the back stack so the user can navigate back
                transactionMaths.addToBackStack(null);
                transactionMaths.commit();
                break;
            default:
                break;
        }
    }

    //  Method available from fragment callbacks to SPEAK the passed string...
    public void speakThis(String sayThis) {
        Log.i(TAG, "speakThis(" + sayThis + ")");
        if (speaker.isReady()) {
            speaker.pause(250);
            speaker.speak(sayThis);
        }
    }

    //  Method available from fragment callbacks to SPELL the passed string...
    public void spellThis(String spellThis) {
        Log.i(TAG, "spellThis(" + spellThis + ")");
        if (speaker.isReady()) {
            speaker.pause(500);
            String[] letters = spellThis.split("");
            for (String letter : letters) {
                speaker.speak(letter);
            }
        }
    }

    //  Method available to fragments, to set activity AppBar title
    public void setAppBarTitle(String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    // method to handle the response from the from MathsChoice fragment, and fire up Test Fragment
    // STILL NOT CLEAR WHY THIS COMMUNICATION NEEDS TO GO FRAGMENT>ACTIVITY>FRAGMENT,
    // and not just FRAGMENT>FRAGMENT... but hey ho...
    public void onMathsChoice(CalcType mCalc, TestType mTestType, int mParam) {
        Log.i(TAG, "Received MathsChoice=" + mCalc.toString() + "/" + mTestType.toString() + "/" + mParam);
        // Initiate new fragment with chosen parameters...
        MathsTestFragment newMathsTestFragment = MathsTestFragment.newInstance(mCalc, mTestType, mParam);
        FragmentTransaction transactionMaths = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transactionMaths.replace(R.id.fragment_container, newMathsTestFragment);
        transactionMaths.addToBackStack(null);
        // Commit the transaction
        transactionMaths.commit();
    }


    // method to handle the Maths Test fragment response
    public void onMathsTestResult(int value) {
        Log.i(TAG, "onMathsTestResult(" + value + ")");
        meTimeFragment.increase(value);
    }

    // method to handle the Maths Test fragment response
    public void onSpellingTestResult(int value) {
        Log.i(TAG, "onSpellingTestResult(" + value + ")");
        meTimeFragment.increase(value);
    }

    // method to handle the response from the from SpellingChoice fragment
    public void onSpellingChoice(Choices choice) {
        Log.i(TAG, "Received onSpellingChoice(" + choice.toString() + ") to handle response from SpellingChoiceFragment");
        switch (choice) {
            case SPELLING_SETUP:
                if (speaker.isReady()) {
                    speaker.speak("Let's setup the spelling test words!");
                }
                // Get a new instance, passing #columns needed, and type of behaviour [SETUP]
                ItemFragment newItemFragment1 = ItemFragment.newInstance(1, choice);
                FragmentTransaction transactionItem1 = getSupportFragmentManager().beginTransaction();
                transactionItem1.replace(R.id.fragment_container, newItemFragment1);
                transactionItem1.addToBackStack(null);
                transactionItem1.commit();
                break;
            case SPELLING_TEST:
                if (speaker.isReady()) {
                    speaker.speak("Let's take the spelling test!");
                }
                //Replace whatever is in the fragment_container view with this Spelling Test fragment,
                SpellingTestFragment newSpellingTestFragment = new SpellingTestFragment();
                FragmentTransaction transactionSpellingTest = getSupportFragmentManager().beginTransaction();
                transactionSpellingTest.replace(R.id.fragment_container, newSpellingTestFragment);
                transactionSpellingTest.addToBackStack(null);
                transactionSpellingTest.commit();
                break;
            case SPELLING_REVIEW:
                if (speaker.isReady()) {
                    speaker.speak("Let's review how you did!");
                }
                // Get a new instance, passing #columns needed, and type of behaviour [REVIEW]
                ItemFragment newItemFragment2 = ItemFragment.newInstance(1, choice);
                FragmentTransaction transactionItem2 = getSupportFragmentManager().beginTransaction();
                transactionItem2.replace(R.id.fragment_container, newItemFragment2);
                transactionItem2.addToBackStack(null);
                transactionItem2.commit();
                break;
            default:
                break;
        }

    }

    // Performs the binding to the default Text-to-Speech engine
    private void checkTTS() {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // STREAM_MUSIC is the default for Text-to-Speech
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "Media volume - MUSIC - " + currentVolume);
        if (currentVolume < 2) {
            Toast toast = Toast.makeText(this, "You need to turn the volume up a bit for the Spelling Test features",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        if (speaker == null) {
            Log.i(TAG, "Speaker/ Creating activity intent to bind to a TTS Engine");
            Intent check = new Intent();
            check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(check, CHECK_CODE);
        } else {
            Log.i(TAG, "Speaker/ TTS Engine already initialised / isReady=" + speaker.isReady());
        }
    }

    // Checks the result of the TTS binding activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Speaker/ Binding activity returned request=" + requestCode + "/result=" + resultCode);
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Log.i(TAG, "Speaker/ Binding result passed, creating Speaker instance");
                speaker = new Speaker(this);
                // ok - let's try it...
                speaker.speak(salutation);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy(); // ensure we stop any utterance in progress and return all resources
    }

    /**
     * Common functions - one day I'll figure out how to get into a common library
     **/
    private void logDeviceInfo() {
        // Make a log entry of device and readable display details.
        Log.i(TAG, Build.MANUFACTURER + "/" + Build.MODEL + "/" + Build.VERSION.SDK_INT + "/" + Build.VERSION.RELEASE);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Log.i(TAG, display.toString());


    }

}

