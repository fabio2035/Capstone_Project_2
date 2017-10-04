package com.prod.fbrigati.myfinance.ui;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.prod.fbrigati.myfinance.R;
import com.prod.fbrigati.myfinance.Utility;

/**
 * Created by FBrigati on 24/08/2017.
 */

public class InstructionsDialog extends DialogFragment {

    private CheckBox chkbox;

    public InstructionsDialog (){
    }

    public static InstructionsDialog newIstance(String title, String text, int ID){
        InstructionsDialog diag = new InstructionsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putInt("ID", ID);
        diag.setArguments(args);
        return diag;
    }

   /* @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    } */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String text = getArguments().getString("text");
        final int ID = getArguments().getInt("ID");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_instructions, null);
        chkbox = (CheckBox) inflator.findViewById(R.id.dialog_instr_chkbx);

        builder.setView(inflator)
                .setMessage(text)
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //check if user said to not show message again
                        if(chkbox.isChecked()){
                            Utility.setInstructionStat(getActivity(), ID, false);
                        }
                        dismiss();
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}
