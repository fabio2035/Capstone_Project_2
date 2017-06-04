package com.example.fbrigati.myfinance.adapters;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;

/**
 * Created by FBrigati on 09/05/2017.
 */

public class BudgetItemAdapter extends BaseAdapter {

    private final ArrayList<Budget> items;
    private Activity act;
    private LayoutInflater inflater;


    public BudgetItemAdapter(ArrayList<Budget> groups) {
        this.items = groups;
    }

    public void init(LayoutInflater inflater, Activity act) {
        this.inflater = inflater;
        this.act = act;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        Log.v("Fabio", "Size of array in adapter:" + this.items.size());
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = this.inflater.inflate(R.layout.item_budget_detail, (ViewGroup)null);
        }

        Budget item = (Budget)this.getItem(position);
        TextView checked = (TextView)convertView.findViewById(R.id.budgetTitle);
        TextView budgetset = (TextView)convertView.findViewById(R.id.budgetset);
        TextView budgetspent = (TextView)convertView.findViewById(R.id.budgetspent);
        ImageView img = (ImageView)convertView.findViewById(R.id.budgeticon);
        ProgressBar mProgress = (ProgressBar)convertView.findViewById(R.id.progbudget);
        switch(position) {
            case 0:
                Log.v("Fabio", "Setting icon for leisure.." + item.getProgressTemp() + " : " + item.getIcon());
                checked.setText(item.getDescription());
                img.setImageResource(item.getIcon());
                budgetset.setText(" / $" + Float.toString(item.getAmount()));
                budgetspent.setText("$" + Float.toString(item.getSpentAmountTemp()));
                mProgress.setProgress(item.getProgressTemp());
                checked.setTextColor(Color.parseColor("#CD0067"));
                break;
            case 1:
                Log.v("Fabio", "Setting icon for Bills.." + item.getProgressTemp() + " : " + item.getIcon());
                checked.setText(item.getDescription());
                checked.setTextColor(Color.parseColor("#0070C0"));
                img.setImageResource(item.getIcon());
                mProgress.setProgress(item.getProgressTemp());
                budgetset.setText(" / $" + Float.toString(item.getAmount()));
                budgetspent.setText("$" + Float.toString(item.getSpentAmountTemp()));
                break;
            case 2:
                Log.v("Fabio", "Setting icon for Transport.." + item.getProgressTemp() + " : " + item.getIcon());
                checked.setText(item.getDescription());
                checked.setTextColor(Color.parseColor("#669900"));
                img.setImageResource(item.getIcon());
                mProgress.setProgress(item.getProgressTemp());
                budgetset.setText(" / $" + Float.toString(item.getAmount()));
                budgetspent.setText("$" + Float.toString(item.getSpentAmountTemp()));
                break;
            case 3:
                Log.v("Fabio", "Setting icon for Reports..");
                checked.setText(item.getDescription());
                checked.setTextColor(Color.parseColor("#D38206"));
                img.setImageResource(item.getIcon());
                mProgress.setProgress(item.getProgressTemp());
                budgetset.setText(Float.toString(item.getAmount()));
                budgetspent.setText(Float.toString(item.getSpentAmountTemp()));
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
