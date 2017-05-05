/*package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.elements.Statement;

import java.util.List;


 * Created by FBrigati on 03/05/2017.


public class StatementAdapter1 extends ArrayAdapter<Statement> {

    public final String LOG_TAG = StatementAdapter.class.getSimpleName();

    public StatementAdapter1(Context context, int resource, List<Statement> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Statement statementObj = getItem(position);
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_statement_details, parent, false);


            holder = new ViewHolder();
            holder.textSequence = (TextView) convertView.findViewById(R.id.row_num);
            holder.textDate = (TextView) convertView.findViewById(R.id.row_date);
            holder.textDescription = (TextView) convertView.findViewById(R.id.row_description);
            holder.textPayee = (TextView) convertView.findViewById(R.id.row_payee);
            holder.textCategory = (TextView) convertView.findViewById(R.id.row_cat);
            holder.textAmount = (TextView) convertView.findViewById(R.id.row_amt);
            holder.textBalance = (TextView) convertView.findViewById(R.id.row_bal);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textSequence.setText(statementObj.getSequence());
        holder.textSequence.setText(statementObj.getDate());
        holder.textSequence.setText(statementObj.getDesc_user());
        holder.textPayee.setText(statementObj.getAcquirer_id());
        holder.textCategory.setText(statementObj.getCategory());
        holder.textAmount.setText(Float.toString(statementObj.getAmount()));
        //Todo: create algorithm for balance calculation...
        return convertView;
    }

    static class ViewHolder {
        TextView textSequence;
        TextView textDate;
        TextView textDescription;
        TextView textPayee;
        TextView textCategory;
        TextView textAmount;
        TextView textBalance;
        int position;
    }


 } */
