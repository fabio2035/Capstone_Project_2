package com.prod.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.prod.fbrigati.myfinance.R;
import com.prod.fbrigati.myfinance.Utility;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class CurrenciesActivity extends AppCompatActivity{


    public final static String ID_INSTRUCTIONS = "com.prod.fbrigati.myfinance.ui.CurrenciesActivity.INSTRUCTIONS";
    private boolean show_instrucions = false;
    final static int INST_ID = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(CurrenciesFragment.ID_MESSAGE, getIntent().getData());

            CurrenciesFragment fragment = new CurrenciesFragment();

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_currency_main,fragment)
                    .commit();

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (show_instrucions){
            //check if instructions should be shown
            if (Utility.getInstructionStat(this, INST_ID)) {
                DialogFragment instruction = InstructionsDialog.newIstance(
                        getResources().getString(R.string.dialog_instruction_title_currencies),
                        getResources().getString(R.string.dialog_instruction_text_currencies),
                        INST_ID);
                instruction.show(getSupportFragmentManager(), "instruction");
            }
        }
    }


}
