package com.personal.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.data.DataContract;

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

        String symbol =cursor.getString(DataContract.CurrencyExEntry.COL_SYMBOL);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.symbol.setText(symbol);
        viewHolder.rate.setText(cursor.getString(DataContract.CurrencyExEntry.COL_RATE));

        // bind flag
        switch(symbol.substring(0,3)){
            case "USD": { viewHolder.flag.setImageResource(R.drawable.flag_us); break;}
            case "ZAR": { viewHolder.flag.setImageResource(R.drawable.flag_rand); break;}
            case "TZS": { viewHolder.flag.setImageResource(R.drawable.flag_tanzania); break;}
            case "AOA": { viewHolder.flag.setImageResource(R.drawable.flag_angola); break;}
            case "EUR": { viewHolder.flag.setImageResource(R.drawable.flag_eu); break;}
            case "GHS": { viewHolder.flag.setImageResource(R.drawable.flag_ghana); break;}
            case "MZN": { viewHolder.flag.setImageResource(R.drawable.flag_mozambique); break;}
            default:viewHolder.flag.setImageResource(R.drawable.menu_icon_curex);
        }


    }


    public static class ViewHolder {
        TextView symbol;
        TextView rate;
        ImageView flag;

        public ViewHolder(View view){
            symbol = (TextView) view.findViewById(R.id.symbol_value);
            rate = (TextView) view.findViewById(R.id.rate_value);
            flag = (ImageView) view.findViewById(R.id.flag_icon);
        }

    }

}
