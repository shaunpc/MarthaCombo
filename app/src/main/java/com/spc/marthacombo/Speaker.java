package com.spc.marthacombo;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Text-to-Speech Helper class
 * Created by shaun on 12/03/16.
 */
public class Speaker implements TextToSpeech.OnInitListener {

    // 1. Define key variables
    String TAG = "Speaker";
    private TextToSpeech tts;
    private boolean ready = false;

    // 2. Define constructor - needs the application context passed
    public Speaker(Context context) {
        tts = new TextToSpeech(context, this);
    }

    // 3. Interface for OnInitListener
    @Override
    public void onInit(int status) {
        Log.d(TAG, "Text-To-Speech - OnInit - Status [" + status + "]");
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "Setting language locale to UK");
            tts.setLanguage(Locale.UK);
            ready = true;
            speak("Let's Go!");
        } else {
            ready = false;
        }
    }


    // 4. Public Methods...
    public void speak(String text) {
        // Speak only if the TTS is ready
        Log.i(TAG, "Speak=" + text + " / Ready=" + ready);
        if (ready) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            }
        }
    }

    public void pause(long mSeconds) {
        // Speak only if the TTS is ready
        Log.i(TAG, "Pause=" + mSeconds + " / Ready=" + ready);
        if (ready) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.playSilentUtterance(mSeconds, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }


    // ... provide status
    public boolean isReady() {
        return ready;
    }

    // ... and free up resources
    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}

