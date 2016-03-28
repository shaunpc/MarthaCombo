package com.spc.marthacombo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaun on 23/03/16 - to hold the spelling word list
 */
public class SpellingList {

    //  An array of String items that represent the spelling test words
    public List<String> SPELLING_WORD_LIST;
    public List<String> SPELLING_GUESS_LIST;
    public SharedPreferences settings;

    // 1. Define key variables
    private static final String PREFS_NAME = "SpellingPrefsFile";
    private static final String TAG = "SpellingList";

    // 2. Define constructor - needs context for the preferences file
    public SpellingList(Context context) {
        SPELLING_WORD_LIST = new ArrayList<>();
        SPELLING_GUESS_LIST = new ArrayList<>();
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // 3. method to add a new spelling test word
    public void addWord(String word) {
        Log.i(TAG, "addWord(" + word + ") called");
        SPELLING_WORD_LIST.add(word);
        SPELLING_GUESS_LIST.add("");    // always add an empty guessed word when adding
    }

    // 4. method to add a new spelling test word
    public void removeWord(int position) {
        Log.i(TAG, "removeWord(" + position + ") / WORD=" + SPELLING_WORD_LIST.get(position) +
                " / GUESS=" + SPELLING_GUESS_LIST.get(position));
        SPELLING_WORD_LIST.remove(position);
        SPELLING_GUESS_LIST.remove(position);  // always remove corresponding guessed word when removing
    }

    // 5. method to update the guessed word only.
    public void updateGuess(int position, String word) {
        Log.i(TAG, "updateGuess(" + position + ", " + word + ") / WORD=" + SPELLING_WORD_LIST.get(position) +
                " / GUESS=" + SPELLING_GUESS_LIST.get(position));
        SPELLING_GUESS_LIST.set(position, word);
        logListContents();
    }

    // 6. Pre-populates a spelling list, either from preferences file, or with "sample" word
    public void populateSpellingWordList() {
        // First clear anything from the existing arrays
        SPELLING_WORD_LIST.clear();
        SPELLING_GUESS_LIST.clear();
        // Get PREFS file with any existing test/progress info...
        int wordCount = settings.getInt("WordCount", 0);
        Log.i(TAG, "Stored word count is " + wordCount);
        if (wordCount == 0) {
            // No stored spelling test words, stick some in...
            addWord("sample");
            addWord("duff1");
            addWord("duff2");
            removeWord(1);
            removeWord(1);
        } else {
            // retrieve the stored spelling test words
            for (int i = 0; i < wordCount; i++) {
                Log.i(TAG, "Looking for Word/Guess_" + i + "...");
                SPELLING_WORD_LIST.add(settings.getString("Word_" + i, "NOT FOUND!"));
                SPELLING_GUESS_LIST.add(settings.getString("Guess_" + i, "NOT FOUND!"));
            }
        }
        logListContents();
    }

    // 7. stores the current state of spelling words and guesses in the preferences file
    public void persistSpellingWordList() {
        Log.i(TAG, "Persisting the spelling list to the preferences file");
        logListContents();
        SharedPreferences.Editor mEdit1 = settings.edit();
        mEdit1.putInt("WordCount", SPELLING_WORD_LIST.size());
        for (int i = 0; i < SPELLING_WORD_LIST.size(); i++) {
            mEdit1.putString("Word_" + i, SPELLING_WORD_LIST.get(i).trim());
            mEdit1.putString("Guess_" + i, SPELLING_GUESS_LIST.get(i).trim());
        }
        mEdit1.apply();
    }

    // 8. helps debugging... the positions need to be in sync...
    public void logListContents() {
        Log.i(TAG, "Spelling list is: " + SPELLING_WORD_LIST.toString());
        Log.i(TAG, "Guessed list is: " + SPELLING_GUESS_LIST.toString());
    }

    // 9. clears guess list
    public void clearGuessList() {
        Log.i(TAG, "Clearing the GUESS list");
        for (int i = 0; i < SPELLING_WORD_LIST.size(); i++) {   // put empty string in each guess position
            updateGuess(i, "");
        }
        logListContents();
    }
}
