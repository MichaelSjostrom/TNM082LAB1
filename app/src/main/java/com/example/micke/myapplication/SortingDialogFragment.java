package com.example.micke.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SortingDialogFragment extends DialogFragment {

    public static final String ARG_SORT = "sort";

    private int chosenSort;
    private SharedPreferences preferences;

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("com.example.micke.myapplication", Context.MODE_PRIVATE);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_sorting)
                .setSingleChoiceItems(R.array.array_sorting,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenSort = which;
                    }
                })
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putInt(ARG_SORT, chosenSort).apply();
                        mListener.onDialogPositiveClick(SortingDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(SortingDialogFragment.this);
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialogFragment);
        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try{
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        }catch (ClassCastException e){
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + "must implement NoticeDialogListener");

        }
    }

}