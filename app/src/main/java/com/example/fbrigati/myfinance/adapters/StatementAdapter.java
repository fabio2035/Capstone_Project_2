package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

/**
 * Created by FBrigati on 04/05/2017.
 */

public class StatementAdapter extends CursorAdapter {

    public final String LOG_TAG = StatementAdapter.class.getSimpleName();


    public StatementAdapter(Context context, Cursor c, int flags) {
        super(context,c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_statement_details,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

//        viewHolder.textSequence.setText(cursor.getString(DataContract.StatementEntry.COL_ACCOUNT_NUMBER));
        viewHolder.textDate.setText(cursor.getString(DataContract.StatementEntry.COL_DATE));
        viewHolder.textDescription.setText(cursor.getString(DataContract.StatementEntry.COL_DESCRIPTION_USER));
    //    viewHolder.textPayee.setText(cursor.getString(DataContract.StatementEntry.COL_ACQUIRER_ID));
   //     viewHolder.textCategory.setText(cursor.getString(DataContract.StatementEntry.COL_CATEGORY_KEY));
        viewHolder.textAmount.setText(cursor.getString(DataContract.StatementEntry.COL_AMOUNT));
   //     viewHolder.textBalance.setText(cursor.getString(DataContract.StatementEntry.COL_AMOUNT));
    }


    public static class ViewHolder {
        TextView textSequence;
        TextView textDate;
        TextView textDescription;
        TextView textPayee;
        TextView textCategory;
        TextView textAmount;
        TextView textBalance;
        int position;

        public ViewHolder(View view){
            //textSequence = (TextView) view.findViewById(R.id.row_num);
            textDate = (TextView) view.findViewById(R.id.row_date);
            textDescription = (TextView) view.findViewById(R.id.row_description);
            //textPayee = (TextView) view.findViewById(R.id.row_payee);
            //textCategory = (TextView) view.findViewById(R.id.row_cat);
            textAmount = (TextView) view.findViewById(R.id.row_amt);
            //textBalance = (TextView) view.findViewById(R.id.row_bal);
        }

    }

}
