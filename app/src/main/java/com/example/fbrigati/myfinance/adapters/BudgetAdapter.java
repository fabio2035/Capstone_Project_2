package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by FBrigati on 03/06/2017.
 */

public class BudgetAdapter extends CursorAdapter {


    private final DecimalFormat percentageFormat;

    public BudgetAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);

        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());

    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget_detail,parent,false);

        BudgetAdapter.ViewHolder viewHolder = new BudgetAdapter.ViewHolder(view);

        view.setTag(viewHolder);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String category = cursor.getString(0);

        Double spent = cursor.getDouble(1);

        Double goal = cursor.getDouble(2);

        Double percentage = spent/goal*100;

        //Load icon resources
        switch (category){
            case "Transportation" : {
                viewHolder.iconView.setImageResource(R.drawable.transportation_icon);
                break;
            }
            case "Food" : {
                viewHolder.iconView.setImageResource(R.drawable.food_icon);
                break;
            }
            case "Leisure" : {
                viewHolder.iconView.setImageResource(R.drawable.leisure_icon);
                break;
            }
            case "Education" : {
                viewHolder.iconView.setImageResource(R.drawable.education_icon);
                break;
            }
            case "HealthCare" : {
                viewHolder.iconView.setImageResource(R.drawable.healthcare_icon);
                break;
            }
            case "Groceries" : {
                viewHolder.iconView.setImageResource(R.drawable.groceries_icon);
                break;
            }
            case "Rent" : {
                viewHolder.iconView.setImageResource(R.drawable.rent_icon);
                break;
            }
        }

        viewHolder.iconView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

        viewHolder.textTitle.setText(category);

        viewHolder.textBudgetGoal.setText(String.valueOf(goal));

        viewHolder.textBudgetSpent.setText(String.valueOf(spent));

        viewHolder.progressBar.setProgress(percentage.intValue());

        viewHolder.textPercentage.setText(percentageFormat.format(percentage/100));

    }


    public static class ViewHolder {
        ImageView iconView;
        TextView textTitle;
        TextView textBudgetGoal;
        TextView textBudgetSpent;
        ProgressBar progressBar;
        TextView textPercentage;


        public ViewHolder(View view){
            iconView = (ImageView) view.findViewById(R.id.budgeticon);
            textTitle = (TextView) view.findViewById(R.id.budgetTitle);
            textBudgetGoal = (TextView) view.findViewById(R.id.budgetset);
            textBudgetSpent = (TextView) view.findViewById(R.id.budgetspent);
            progressBar = (ProgressBar) view.findViewById(R.id.progbudget);
            textPercentage = (TextView) view.findViewById(R.id.budget_percent);
        }

    }


}
