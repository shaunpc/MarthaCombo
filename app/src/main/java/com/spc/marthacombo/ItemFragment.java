package com.spc.marthacombo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Item Fragment that presents the current Spelling Word List for editing
// Latest list is persisted in preferences when closing fragment


// Lifecycle of a starting fragment:              and an ending fragment
// 1. OnAttach()                                    1. OnDetach()
// 2. OnCreate()        [#2 could be bypassed]      2. OnDestroy()
// 3. OnCreateView()  <<<<<<<<<<<<<<<<<<<<<<<<<<    3. OnDestroyView()
// 4. OnActivityCreated()
// 5. OnStart()       <<<<<<<<<<<<<<<<<<<<<<<<<<    5. OnStop()
// 6. OnResume()     >>> [fragment is running] >>>  6. OnPause()
public class ItemFragment extends Fragment
        implements EditTextDialog.OnFragmentInteractionListener {

    // 1. Defines the listener interface with a method passing back data result.
    public interface OnListFragmentInteractionListener {
        void speakThis(String string);

        void spellThis(String string);
    }

    // 2. Defines the fragment parameters
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_BEHAVIOUR = "behaviour";
    private int mColumnCount = 1;
    private boolean setupMode;


    // 3. Define key variables
    private final static String TAG = "ItemFragment";
    private OnListFragmentInteractionListener mListener;
    public SpellingList mySpellingList;
    MyItemRecyclerViewAdapter myAdapter;

    // 4. Required empty public constructor
    public ItemFragment() {
    }


    // 5. Factory method to create new instance with parameter
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount, Choices choice) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_BEHAVIOUR, choice.toString());
        fragment.setArguments(args);
        return fragment;
    }

    // 6.Default onCreate to retrieve any parameters passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            String tmp = getArguments().getString(ARG_BEHAVIOUR);
            setupMode = (Choices.toChoice(tmp) == Choices.SPELLING_SETUP);
        }
    }

    // 7. onCreateView to get the dialog ready for input
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // first, inflate the whole Coordinator Layout
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // But for attaching the adapter we only want the ListView element
        View justListView = view.findViewById(R.id.list);

        // Get PREFS file with any existing test/progress info...
        mySpellingList = new SpellingList(getContext());
        mySpellingList.populateSpellingWordList();
        mySpellingList.logListContents();

        // Set the adapter on the ListView component
        if (justListView instanceof RecyclerView) {
            Context context = justListView.getContext();
            RecyclerView recyclerView = (RecyclerView) justListView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myAdapter = new MyItemRecyclerViewAdapter(mySpellingList.SPELLING_WORD_LIST,
                    mySpellingList.SPELLING_GUESS_LIST, mListener, setupMode);
            recyclerView.setAdapter(myAdapter);
        } else {
            Log.i(TAG, "View is not an instance of RecyclerView - not setting adapter");
        }

        // Define the action we want from the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (setupMode) {
            // In SETUP mode, it should enable addition of a new word to the list
            fab.setImageResource(android.R.drawable.ic_menu_add);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Call dialog to get new word, onFinishEditDialog adds to WORD LIST
                    showEditDialog("Enter Word");
                    // TODO - notify adapter that UI needs updating
                    myAdapter.notifyDataSetChanged();  // bit blunt
                    // myAdapter.notifyItemInserted(int position);   // but don't know position
                }
            });
        } else {
            // In REVIEW mode, it should clear the GUESS list
            fab.setImageResource(android.R.drawable.ic_menu_delete);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "FAB - clearing Guess List");
                    mySpellingList.clearGuessList();
                    myAdapter.notifyDataSetChanged();
                }
            });
        }

        return view;  // but return the overall coordinator view
    }


    // 8. default onAttach to check method implemented in caller, and set the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    // 9. default onDetach to zap stuff
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach() - clearing mListener & persisting WordLists");
        mListener = null;
        mySpellingList.persistSpellingWordList();
    }

    // 10. This is the hook back to the calling fragment
    // Embedded in MyItemRecyclerViewAdapter...

    // 11. Other stuff

    // These are used to launch the EditText Dialog, and to handle the resultant callback
    private void showEditDialog(String title) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditTextDialog editTextDialog = EditTextDialog.newInstance(title);
        // SETS the target fragment for use later when sending results
        editTextDialog.setTargetFragment(ItemFragment.this, 300);
        editTextDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Log.i(TAG, "onFinishedEditDialog(" + inputText + ")");
        if (inputText != null) {
            // Add this new word to the WORD LIST, removing any leading or trailing spaces
            mySpellingList.addWord(inputText.trim());
        }
    }
}
