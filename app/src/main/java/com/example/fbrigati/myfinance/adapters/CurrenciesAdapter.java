package com.example.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

/**
 * Created by FBrigati on 04/05/2017.
 */

public class CurrenciesAdapter extends CursorAdapter {

    public final String LOG_TAG = CurrenciesAdapter.class.getSimpleName();


    public CurrenciesAdapter(Context context, Cursor c, int flags) {
        super(context,c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_currencies_detail,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.symbol.setText(cursor.getString(DataContract.CurrencyExEntry.COL_SYMBOL));
        viewHolder.rate.setText(cursor.getString(DataContract.CurrencyExEntry.COL_RATE));

    }


    public static class ViewHolder {
        TextView symbol;
        TextView rate;

        public ViewHolder(View view){
            symbol = (TextView) view.findViewById(R.id.symbol_value);
            rate = (TextView) view.findViewById(R.id.rate_value);
        }

    }

}
