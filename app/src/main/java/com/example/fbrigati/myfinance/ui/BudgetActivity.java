package com.example.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.fbrigati.myfinance.R;


/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_budget);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(BudgetFragment.ID_MESSAGE, getIntent().getData());

            BudgetFragment fragment = new BudgetFragment();

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_budget_detail,fragment)
                    .commit();

        }
    }

}
