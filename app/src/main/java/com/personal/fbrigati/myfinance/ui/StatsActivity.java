package com.personal.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.Utility;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class StatsActivity extends AppCompatActivity {

    public final static String ID_INSTRUCTIONS = "com.personal.fbrigati.myfinance.ui.StatsActivity.INSTRUCTIONS";
    private boolean show_instrucions = false;
    final static int INST_ID = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(StatsFragment.ID_MESSAGE, getIntent().getData());

            StatsFragment fragment = new StatsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_stats_detail, fragment)
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
                        getResources().getString(R.string.dialog_instruction_title_stats),
                        getResources().getString(R.string.dialog_instruction_text_stats),
                        INST_ID);
                instruction.show(getSupportFragmentManager(), "instruction");
            }
        }
    }


}
