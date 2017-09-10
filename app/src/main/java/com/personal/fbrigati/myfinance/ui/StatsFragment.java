package com.personal.fbrigati.myfinance.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.personal.fbrigati.myfinance.R;

import com.personal.fbrigati.myfinance.Utility;
import com.personal.fbrigati.myfinance.data.DataContract;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.personal.fbrigati.myfinance.Utility.getStatsCategory;
import static com.personal.fbrigati.myfinance.Utility.getStatsTrimester;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class StatsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SeekBar.OnSeekBarChangeListener{

    final static String LOG_TAG = StatsFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.personal.fbrigati.myfinance.ui.StatsFragment.MESSAGE";

    public static final int PIECHART_LOADER = 6;

    public static final int LINECHART_LOADER = 0;

    public static final int CATEGORY_LOADER = 7;

    private Cursor mCategoryCursor;

    private Map<Integer, String> catMap;

    int maxCount;

    private int mTrimester;

    private Uri mUri;

    private AdView mAdView;
    private PieChart mpieChart;
    private LineChart mlineChart;
    private Toolbar toolbarView;
    private GridLayout masterGrid;
    private SeekBar seekBarPieChart;
    private SeekBar seekBarLineChart;
    LinearLayout mSeekLinPieChart;
    LinearLayout mSeekLinLineChart;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(PIECHART_LOADER, null, this);
        getLoaderManager().initLoader(LINECHART_LOADER, null, this);
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(ID_MESSAGE);
        }

        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        //Get the pieschart view
        mpieChart = (PieChart) rootView.findViewById(R.id.piechart);

        seekBarPieChart = (SeekBar) rootView.findViewById(R.id.seekBarpie);

        mSeekLinPieChart = (LinearLayout) rootView.findViewById(R.id.seekBarLabelLayout);

        //Get the linechart view
        mlineChart = (LineChart) rootView.findViewById(R.id.linechart);

        seekBarLineChart = (SeekBar) rootView.findViewById(R.id.seekBarLine);

        mSeekLinLineChart = (LinearLayout) rootView.findViewById(R.id.seekBarLabelLineLayout);

        setupPieChartSeekBar();

        setupPieChart();

        toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_stats_title);

        setupLineChart();

        catMap = new HashMap<Integer, String>();

        //masterGrid = (GridLayout) rootView.findViewById(R.id.master_grid);

        return rootView;
    }

    private void setupLineChartSeekBar() {

        seekBarLineChart.setOnSeekBarChangeListener(this);

        seekBarLineChart.setMax(mCategoryCursor.getCount() - 1);

        mSeekLinLineChart.setOrientation(LinearLayout.HORIZONTAL);

        mSeekLinLineChart.setPadding(10, 0, 10, 10);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(35, 0, 35, 0);

        params.height = 400;

        mSeekLinLineChart.setLayoutParams(params);

        addLabelsBelowLineSeekBar();
    }

    private void setupPieChartSeekBar() {
        seekBarPieChart.setOnSeekBarChangeListener(this);

        maxCount = 4;

        seekBarPieChart.setMax(maxCount - 1);

        mSeekLinPieChart.setOrientation(LinearLayout.HORIZONTAL);

        mSeekLinPieChart.setPadding(10, 0, 10, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(35, 0, 35, 0);

        mSeekLinPieChart.setLayoutParams(params);

        addLabelsBelowPieChartSeekBar();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()){
            case PIECHART_LOADER:
                loader = null;
                break;
            case LINECHART_LOADER:
                loader = null;
                break;
            case CATEGORY_LOADER:
                mCategoryCursor = null;
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()){
            case R.id.seekBarpie:
            {
                Log.v(LOG_TAG, "Pie Data selected: " + seekBar.getProgress());
                Utility.setStatsPieTrimester(getActivity(), seekBar.getProgress()+1);
                getLoaderManager().restartLoader(PIECHART_LOADER, null, this);
                //restart line graph aswell..
                getLoaderManager().restartLoader(LINECHART_LOADER, null, this);
                mlineChart.invalidate();

                break;}
            case R.id.seekBarLine:
            {
                Log.v(LOG_TAG, "Line Data selected: " + seekBar.getProgress());
                Utility.setStatsCategory(getActivity(), catMap.get(seekBar.getProgress()));
                getLoaderManager().restartLoader(LINECHART_LOADER, null, this);
                //restart line graph aswell..
                break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void addLabelsBelowPieChartSeekBar() {
        String[] trimester = getResources().getStringArray(R.array.trimesters);
        for (int count = 0; count < maxCount; count++) {
            TextView textView = new TextView(getContext());
            textView.setText(trimester[count]);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setGravity(Gravity.LEFT);
            mSeekLinPieChart.addView(textView);
            textView.setLayoutParams((count == maxCount - 1) ? getLayoutParams(0.0f) : getLayoutParams(1.0f));
        }
    }

    private void addLabelsBelowLineSeekBar() {

        for (int count = 0; count < mCategoryCursor.getCount(); count++) {
            TextView textView = new TextView(getContext());
            textView.setText(mCategoryCursor.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT).substring(0,4));
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setRotation(45);
            textView.setPadding(0,10,0,0);
            mSeekLinLineChart.addView(textView);
            textView.setLayoutParams((count == maxCount - 1) ? getLayoutParams(0.0f) : getLayoutParams(1.0f));
            //add to catMap for query retrieval..
            catMap.put(count, mCategoryCursor.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT));
            mCategoryCursor.moveToNext();
        }
    }

    LinearLayout.LayoutParams getLayoutParams(float weight) {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight);
    }

    private void setupPieChart() {

        //Pie Chart setup
        mpieChart.setUsePercentValues(true);
        mpieChart.setExtraOffsets(5, 10, 5, 5);

        mpieChart.setDragDecelerationFrictionCoef(0.95f);

        mpieChart.setDrawHoleEnabled(true);
        mpieChart.setHoleColor(Color.WHITE);

        mpieChart.setTransparentCircleColor(Color.WHITE);
        mpieChart.setTransparentCircleAlpha(110);

        mpieChart.setHoleRadius(38f);
        mpieChart.setTransparentCircleRadius(51f);

        mpieChart.setDrawCenterText(true);

        mpieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mpieChart.setRotationEnabled(true);
        mpieChart.setHighlightPerTapEnabled(true);

        Description desc = new Description();
        desc.setText(getResources().getString(R.string.piechartLegedTitle));

        mpieChart.setDescription(desc);

        Legend l = mpieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

    }

    private void setupLineChart() {

        mlineChart.setDrawGridBackground(false);
        // enable touch gestures
        //mlineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mlineChart.setDragEnabled(true);
        mlineChart.setScaleEnabled(true);

        mlineChart.setPinchZoom(true);

        mlineChart.getAxisRight().setEnabled(false);

        mlineChart.setBackgroundColor(Color.TRANSPARENT);

        Description desc = new Description();
        desc.setText(getResources().getString(R.string.linechartLegedTitle));

        mlineChart.setDescription(desc);

        // get the legend (only possible after setting data)
        Legend l = mlineChart.getLegend();
        l.setEnabled(false);
    }

    private void setPieData(Cursor data) {

        mpieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        Double totalAmount = 0.0;

        //first get the total value of spendings..
        for(int i=0; i< data.getCount(); i++){
        totalAmount = totalAmount + data.getDouble(1);
            data.moveToNext();
        }

        //Log.v(LOG_TAG, "Total category amounts: " + totalAmount);

        data.moveToFirst();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < data.getCount() ; i++) {
            entries.add(new PieEntry((float) (data.getDouble(1) / totalAmount), data.getString(0) ));
            data.moveToNext();
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c); */

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData piedata = new PieData(dataSet);
        piedata.setValueFormatter(new PercentFormatter());
        piedata.setValueTextSize(11f);
        piedata.setValueTextColor(Color.WHITE);
        mpieChart.setData(piedata);

        // undo all highlights
        mpieChart.highlightValues(null);

        mpieChart.notifyDataSetChanged();
        mpieChart.invalidate();

        data.close();

    }

    private void setLineCharData(Cursor data) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        //ArrayList<String> xValues = new ArrayList<String>();

        String dateRaw = "";

        mlineChart.animateX(2500);

        for (int i = 0; i < data.getCount(); i++) {

            dateRaw = String.valueOf(data.getInt(0));

            float val = (float) data.getDouble(1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date parsedDate = new Date();
            try
            {
            parsedDate = dateFormat.parse(dateRaw.substring(0,8));
            }catch (ParseException e){
              //  Log.v(LOG_TAG, "error parsing date..");
            }

            //Log.v(LOG_TAG, "Value for i: " + i + " , " +
            //        parsedDate.getTime() + " ;xValues: " + dateRaw.substring(6,8) + "/" + dateRaw.substring(4,6) +
            //        " ;Value: " + val);
            values.add(new Entry(parsedDate.getTime(), val));
            data.moveToNext();
        }

        LineDataSet set1;

            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");

            // set the line to be drawn like this "- - - - - -"
            //set1.enableDashedLine(10f, 5f, 0f);
            //set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.TRANSPARENT);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            //set1.setFillColor(Color.GREEN);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            set1.setValues(values);

            // create a data object with the datasets
            LineData datav = new LineData(dataSets);

            // set data
            mlineChart.setData(datav);

            //IAxisValueFormatter xAxisFormatter = new DateAxisValueFormatter();

            XAxis xAxis = mlineChart.getXAxis();

            xAxis.setValueFormatter(new DateAxisValueFormatter(null));

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            mlineChart.notifyDataSetChanged();

            mlineChart.invalidate();

            data.close();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case PIECHART_LOADER:

                //Log.v(LOG_TAG, "getting piechart info for trimester: " + mTrimester);
                return new CursorLoader(
                        getActivity(),
                        DataContract.StatementEntry.buildStatsPieChartTrimUri(getStatsTrimester(getActivity())),
                        null,
                        null,
                        null,
                        null);

            case LINECHART_LOADER:

                //Log.v(LOG_TAG, "getting linechart info for trimester: " + getStatsTrimester(getActivity()));
                return new CursorLoader(
                        getActivity(),
                        DataContract.StatementEntry.buildStatsLineGraphChartTrimUri(getStatsTrimester(getActivity()),
                                getStatsCategory(getActivity())),
                        null,
                        null,
                        null,
                        null);

            case CATEGORY_LOADER: {
                //Log.v(LOG_TAG, "Category loader called");
                //Todo: make category selection
                return new CursorLoader(
                        getActivity(),
                        DataContract.CategoryEntry.CONTENT_URI,
                        DataContract.CategoryEntry.CATEGORY_COLUMNS,
                        null,
                        null,
                        null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LINECHART_LOADER:

                //Log.v(LOG_TAG, "onLoadFinish for Line Chart loader called. data with: " + data.getCount());

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    //Load piechart data
                    setLineCharData(data);
                }

                break;

            case PIECHART_LOADER:

                //Log.v(LOG_TAG, "onLoadFinish for pieChart loader called. data with: " + data.getCount());

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    //Load piechart data
                    setPieData(data);
                }

                break;
            case CATEGORY_LOADER:
                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    mCategoryCursor = data;
                    setupLineChartSeekBar();
                }
                break;
        }
    }


    class DateAxisValueFormatter implements IAxisValueFormatter{

        private String[] mValues;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

        public DateAxisValueFormatter(String[] values) {
            this.mValues = values; }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return sdf.format(new Date((long)value)); // + "/" + strDate.substring(4,6);

        }
    }

}