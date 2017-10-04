package com.prod.fbrigati.myfinance.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.prod.fbrigati.myfinance.R;

import static android.text.TextUtils.isEmpty;

/**
 * Created by FBrigati on 05/06/2017.
 */

public class BudgetSetDialog extends DialogFragment{


    private static final String LOG_TAG = BudgetSetDialog.class.getSimpleName();
    private EditText amtText;
    private CheckBox chkbox;
    private boolean checked = false;

    public interface setBudgetGoalListener{
         void setBudget(Double amount, int month, int year, String category, long id, boolean setYear);
    }

    public BudgetSetDialog(){

    }

    public static BudgetSetDialog newInstance(String title, int month, int year, long id, boolean setYear){
        BudgetSetDialog frag = new BudgetSetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putLong("id", id);
        args.putInt("month", month);
        args.putInt("year", year);
        args.putBoolean("setyear", setYear);
        frag.setArguments(args);
        return frag;
    }

    setBudgetGoalListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DateSetListenerCustom
    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DateSetListenerCustom so we can send events to the host
            mListener = (BudgetSetDialog.setBudgetGoalListener) ctx;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(ctx.toString()
                    + " must implement setBudgetGoalListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String title = getArguments().getString("title");
        final long Budget_id = getArguments().getLong("id");
        final int month = getArguments().getInt("month");
        final int year = getArguments().getInt("year");
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View inflator = inflater.inflate(R.layout.dialog_setbudget, null);
        amtText = (EditText) inflator.findViewById(R.id.amount_id);
        chkbox = (CheckBox) inflator.findViewById(R.id.budget_checkbox);


        builder.setView(inflator)

        .setTitle(title)
                //set positive action button
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(validateInput()){
                    //check if user wants budget for whole year..
                    if(chkbox.isChecked()) checked = true;
                    mListener.setBudget(Double.valueOf(amtText.getText().toString()),month,year, title, Budget_id, checked);
                }else{
                    Toast.makeText(getActivity(),R.string.toast_amount_notvalid, Toast.LENGTH_LONG ).show();
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
        if (!isEmpty(amtText.getText())) {
        if(Double.valueOf(amtText.getText().toString()) == 0F ||
                Double.valueOf(amtText.getText().toString()) == 0){
            validate = false;
            amtText.requestFocus();
            }
        } else {
            validate = false;
            amtText.requestFocus();
        }
        return validate;
    }

}
