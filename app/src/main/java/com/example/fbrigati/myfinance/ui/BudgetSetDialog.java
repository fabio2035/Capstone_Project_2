package com.example.fbrigati.myfinance.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fbrigati.myfinance.R;

/**
 * Created by FBrigati on 05/06/2017.
 */

public class BudgetSetDialog extends DialogFragment{


    private static final String LOG_TAG = BudgetSetDialog.class.getSimpleName();
    private EditText amtText;


    public interface setBudgetGoalListener{
         void setBudget(Double amount, long id);
    }

    public BudgetSetDialog(){

    }

    public static BudgetSetDialog newInstance(String title, long id){
        BudgetSetDialog frag = new BudgetSetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putLong("id", id);
        frag.setArguments(args);
        return frag;
    }

    setBudgetGoalListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DateSetListenerCustom
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DateSetListenerCustom so we can send events to the host
            mListener = (BudgetSetDialog.setBudgetGoalListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement setBudgetGoalListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String title = getArguments().getString("title");
        final long Budget_id = getArguments().getLong("id");

        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View inflator = inflater.inflate(R.layout.dialog_setbudget, null);
        amtText = (EditText) inflator.findViewById(R.id.amount_id);


        builder.setView(inflator)

        .setTitle(title)
                //set positive action button
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(validateInput()){
                    mListener.setBudget(Double.valueOf(amtText.getText().toString()), Budget_id);
                }else{
                    Toast.makeText(getActivity(),"Amount not valid, please correct and try again", Toast.LENGTH_LONG ).show();
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

    return builder.create();
    }

    private boolean validateInput() {
        Boolean validate = true;

        //check that amount is >0
        Log.v(LOG_TAG, "value of amtText: " + amtText.getText());
        if(Double.valueOf(amtText.getText().toString()) == 0F ||
                Double.valueOf(amtText.getText().toString()) == 0)
        {validate = false;
            amtText.requestFocus();}
        return validate;
    }

}
