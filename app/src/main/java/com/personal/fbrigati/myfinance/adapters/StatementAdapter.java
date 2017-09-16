package com.personal.fbrigati.myfinance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.Utility;
import com.personal.fbrigati.myfinance.data.DataContract;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by FBrigati on 04/05/2017.
 */

public class StatementAdapter extends CursorAdapter {

    public final String LOG_TAG = StatementAdapter.class.getSimpleName();

    private final DecimalFormat currencyFormatWithPlus;
    private final DecimalFormat currencyFormatWithMinus;
    private int trxType;
    private Context ctx;

    public StatementAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        currencyFormatWithMinus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatWithPlus.setPositivePrefix("+");
        currencyFormatWithMinus.setPositivePrefix("-");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_statement_details, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        ctx = context;

        return view;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Calendar cal = new GregorianCalendar();

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String dateRaw = cursor.getString(DataContract.StatementEntry.COL_DATE);
        //20170908
        StringBuilder dateBuild = new StringBuilder().append(dateRaw.substring(6, 8)).append("/").append(dateRaw.substring(4, 6)).append("/").append(dateRaw.substring(0, 4));

        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateBuild.toString());
            cal.setTime(date);
            Log.v(LOG_TAG, "raw: " + dateBuild.toString() + " ; date: " + date + " ; formatted:" + cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US) );
            viewHolder.textDate.setText(Utility.getDayOfWeek(ctx, cal.get(Calendar.DAY_OF_WEEK)) + ", " + dateRaw.substring(6, 8));
        } catch (Exception e) {
            //if there's a parse error..
            Log.e(LOG_TAG, "error: " + e);
        }

        String category = cursor.getString(DataContract.StatementEntry.COL_CATEGORY_KEY);

        Log.v(LOG_TAG, "Category: " + category);

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

        //Description
        viewHolder.textDescription.setText(cursor.getString(DataContract.StatementEntry.COL_DESCRIPTION_USER));

        trxType = cursor.getInt(DataContract.StatementEntry.COL_TRANSACTION_CODE);

        Double amount = Double.valueOf(cursor.getString(DataContract.StatementEntry.COL_AMOUNT));

        //Double balance = 0.0;

        if (trxType < 5) {
            String PositiveValue = currencyFormatWithPlus.format(amount);
            viewHolder.textAmount.setText(PositiveValue);
            viewHolder.textAmount.setTextColor(context.getResources().getColor(R.color.positive));
          //  balance += amount;
        } else {
            String NegativeValue = currencyFormatWithMinus.format(amount);
            viewHolder.textAmount.setText(NegativeValue);
            viewHolder.textAmount.setTextColor(context.getResources().getColor(R.color.negative));
          //  balance -= amount;
        }

    }


    public static class ViewHolder {
        ImageView iconView;
        TextView textDate;
        TextView textDescription;
        TextView textAmount;

        public ViewHolder(View view) {
            textDate = (TextView) view.findViewById(R.id.row_date);
            textDescription = (TextView) view.findViewById(R.id.row_description);
            textAmount = (TextView) view.findViewById(R.id.row_amt);
            iconView = (ImageView) view.findViewById(R.id.budgeticon);
        }

    }

}