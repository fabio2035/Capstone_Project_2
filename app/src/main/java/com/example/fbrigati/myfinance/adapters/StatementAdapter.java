package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by FBrigati on 04/05/2017.
 */

public class StatementAdapter extends CursorAdapter {

    public final String LOG_TAG = StatementAdapter.class.getSimpleName();

    private final DecimalFormat currencyFormatWithPlus;
    private final DecimalFormat currencyFormatWithMinus;
    private int trxType;


    public StatementAdapter(Context context, Cursor c, int flags) {
        super(context,c,flags);
        currencyFormatWithMinus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatWithPlus.setPositivePrefix("+");
        currencyFormatWithMinus.setPositivePrefix("-");
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

        String dateRaw = cursor.getString(DataContract.StatementEntry.COL_DATE);

        StringBuilder dateBuild = new StringBuilder().append(dateRaw.substring(6,8)).append("/").append(dateRaw.substring(4,6)).append("/").append("'").append(dateRaw.substring(2,4));

        viewHolder.textDate.setText(dateBuild.toString());

        viewHolder.textDescription.setText(cursor.getString(DataContract.StatementEntry.COL_DESCRIPTION_USER));

        trxType = cursor.getInt(DataContract.StatementEntry.COL_TRANSACTION_CODE);

        Double amount = Double.valueOf(cursor.getString(DataContract.StatementEntry.COL_AMOUNT));

        if (trxType < 5){
            String PositiveValue = currencyFormatWithPlus.format(amount);
            viewHolder.textAmount.setText(PositiveValue);
            viewHolder.textAmount.setTextColor(Color.GREEN);
        }else{
            String NegativeValue = currencyFormatWithMinus.format(amount);
            viewHolder.textAmount.setText(NegativeValue);
            viewHolder.textAmount.setTextColor(Color.YELLOW);
        }

    }


    public static class ViewHolder {
        TextView textDate;
        TextView textDescription;
        TextView textAmount;

        public ViewHolder(View view){
            textDate = (TextView) view.findViewById(R.id.row_date);
            textDescription = (TextView) view.findViewById(R.id.row_description);
            textAmount = (TextView) view.findViewById(R.id.row_amt);
        }

    }

}
