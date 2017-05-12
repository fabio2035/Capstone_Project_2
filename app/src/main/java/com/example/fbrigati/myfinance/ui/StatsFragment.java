package com.example.fbrigati.myfinance.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbrigati.myfinance.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class StatsFragment extends Fragment {

    final static String LOG_TAG = StatsFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatsFragment.MESSAGE";

    private Uri mUri;

    public StatsFragment(){}


    private LineChart mlineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Bundle arguments = getArguments();
        if( arguments != null){
            mUri = arguments.getParcelable(ID_MESSAGE);
        }

        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);


        //Get the linechart view
        mlineChart = (LineChart) rootView.findViewById(R.id.linechart);

        createDummyChartData();

        return rootView;
    }

    private void createDummyChartData() {

        int i = 0;
        String historyString = "13/05/2017,1500\n12/05/2017,1400\n11/05/2017,1300\n";

        List<Entry> entries = new ArrayList<Entry>();

        final Map<Integer, String> valueMap = new HashMap<Integer, String>();

        //calculate quarterly values for compressed chart diagram...
        for (String retVal : historyString.split("\n") ){
            Log.v(LOG_TAG, "value for i=" + i + " is: " + retVal);
            i++;
            String[] something = retVal.split(", ");

            valueMap.put(i, something[0]);

            float xValue = (float) i;
            float yValue = (float) Double.parseDouble(something[1]);
            entries.add(new Entry(xValue,yValue));
        }

        Collections.sort(entries, new EntryXComparator());
        LineDataSet dataSet = new LineDataSet(entries, "Quote");
        LineData lineData = new LineData(dataSet);
        mlineChart.setData(lineData);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.v(LOG_TAG, "Getting date value for: " + (Float) value);
                return valueMap.get((int) value).toString();
            }
        };

        XAxis xAxis = mlineChart.getXAxis();
        xAxis.setGranularity(10);
        xAxis.setValueFormatter(formatter);
        mlineChart.setPinchZoom(true);
        mlineChart.fitScreen();

        //  mChart.setVisibleXRangeMaximum(30);
        mlineChart.invalidate(); //refresh
    }

}
