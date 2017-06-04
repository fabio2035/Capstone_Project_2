package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.List;

/**
 * Created by FBrigati on 11/05/2017.
 */

public class BudgetItemArrayAdapter extends ArrayAdapter<Budget> {

    public final String LOG_TAG = BudgetItemArrayAdapter.class.getSimpleName();

    public BudgetItemArrayAdapter(Context context, List<Budget> budgetObjectList) {
        super(context, 0, budgetObjectList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Budget item = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_budget_detail, parent, false);

            holder = new ViewHolder();

            holder.checked = (TextView) convertView.findViewById(R.id.budgetTitle);
            holder.budgetset = (TextView) convertView.findViewById(R.id.budgetset);
            holder.budgetspent = (TextView) convertView.findViewById(R.id.budgetspent);
            holder.img = (ImageView) convertView.findViewById(R.id.budgeticon);
            holder.mProgress = (ProgressBar) convertView.findViewById(R.id.progbudget);
            convertView.setTag(holder);
            }else{
            holder = (ViewHolder) convertView.getTag();
            }

            switch (position) {
                case 0:
                    Log.v("Fabio", "Setting icon for leisure.. " + 20 + " : " + item.getIcon());
                    holder.checked.setText(item.getDescription());
                    holder.img.setImageResource(R.drawable.leisuresmall);
                    holder.budgetset.setText(Float.toString(item.getAmount()));
                    holder.budgetspent.setText(Float.toString(item.getSpentAmountTemp()));
                    holder.mProgress.setProgress(20);
                    holder.checked.setTextColor(Color.parseColor("#CD0067"));
                    break;
                case 1:
                    Log.v("Fabio", "Setting icon for Bills.." + 35 + " : " + item.getIcon());
                    holder.checked.setText(item.getDescription());
                    holder.checked.setTextColor(Color.parseColor("#0070C0"));
                    holder.img.setImageResource(R.drawable.billssmall);
                    holder.mProgress.setProgress(35);
                    holder.budgetset.setText(Float.toString(item.getAmount()));
                    holder.budgetspent.setText(Float.toString(item.getSpentAmountTemp()));
                    break;
                case 2:
                    Log.v("Fabio", "Setting icon for Transport.." + 40 + " : " + item.getIcon());
                    holder.checked.setText(item.getDescription());
                    holder.checked.setTextColor(Color.parseColor("#669900"));
                    holder.img.setImageResource(R.drawable.transportsmall);
                    holder.mProgress.setProgress(40);
                    holder.budgetset.setText(Float.toString(item.getAmount()));
                    holder.budgetspent.setText(Float.toString(item.getSpentAmountTemp()));
                    break;
                case 3:
                    Log.v("Fabio", "Setting icon for Reports..");
                    holder.checked.setText(item.getDescription());
                    holder.checked.setTextColor(Color.parseColor("#D38206"));
                    holder.img.setImageResource(R.drawable.loco_main);
                    holder.mProgress.setProgress(50);
                    holder.budgetset.setText(Float.toString(item.getAmount()));
                    holder.budgetspent.setText(Float.toString(item.getSpentAmountTemp()));
            }//switch

        return convertView;
    }


    static class ViewHolder {
        TextView checked;
        TextView budgetset;
        TextView budgetspent;
        ImageView img;
        ProgressBar mProgress;
    }


}
