package com.prod.fbrigati.myfinance.ui;

import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.prod.fbrigati.myfinance.R;

import com.prod.fbrigati.myfinance.Utility;
import com.prod.fbrigati.myfinance.data.DataContract;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.prod.fbrigati.myfinance.Utility.getStatsCategory;
import static com.prod.fbrigati.myfinance.Utility.getStatsTrimester;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class StatsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SeekBar.OnSeekBarChangeListener{

    final static String LOG_TAG = StatsFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.prod.fbrigati.myfinance.ui.StatsFragment.MESSAGE";

    public static final int PIECHART_LOADER = 6;

    public static final int LINECHART_LOADER = 0;

    public static final int CATEGORY_LOADER = 7;

    private ArrayList<Integer> colors;

    private ArrayList<String> categories;

    private Cursor mCategoryCursor;

    private int loadedCategories;

    private Map<Integer, String> catMap;

    private Map<String, Integer> colorMap;

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

    private int[] mColors = new int[] {
            ColorTemplate.MATERIAL_COLORS[0],
            ColorTemplate.MATERIAL_COLORS[1],
            ColorTemplate.MATERIAL_COLORS[2],
            ColorTemplate.MATERIAL_COLORS[3],
            ColorTemplate.PASTEL_COLORS[0],
            ColorTemplate.PASTEL_COLORS[1],
            ColorTemplate.PASTEL_COLORS[2],
            ColorTemplate.PASTEL_COLORS[3],
            ColorTemplate.PASTEL_COLORS[4],
    };



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

        //seekBarLineChart = (SeekBar) rootView.findViewById(R.id.seekBarLine);

        //mSeekLinLineChart = (LinearLayout) rootView.findViewById(R.id.seekBarLabelLineLayout);

        setupPieChartSeekBar();

        setupPieChart();

        toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_stats_title);

        setupLineChart();

        catMap = new HashMap<Integer, String>();

        setInitialTrimester();

        return rootView;
    }

    private void setupLineChartSeekBar(Cursor data) {

        categories = new ArrayList<String>();

        String tempCat = "";

        if(data.moveToFirst()) {
            while(!data.isLast()) {
                tempCat = data.getString(0);
                categories.add(tempCat.trim());
                while (tempCat.trim().equals(data.getString(0)) && !data.isLast()) {
                    if (data.isLast()) break;
                    data.moveToNext();
                }
            }
        }


        //seekBarLineChart.setOnSeekBarChangeListener(this);

        //seekBarLineChart.setMax(categories.size() + 1);

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
                categories = null;
                break;
            //case CATEGORY_LOADER:
            //    mCategoryCursor = null;
            //    break;
        }
    }

    private void setInitialTrimester(){
        final Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);

        if(month >0 && month <=2){
            Utility.setStatsPieTrimester(getActivity(), 1);
            seekBarPieChart.setProgress(0);
        }else if (month >=3 && month <=5){
            Utility.setStatsPieTrimester(getActivity(), 2);
            seekBarPieChart.setProgress(1);
        }else if (month >=6 && month <=8){
            Utility.setStatsPieTrimester(getActivity(), 3);
            seekBarPieChart.setProgress(2);
        }else if (month >=9 && month <=11){
            Utility.setStatsPieTrimester(getActivity(), 4);
            seekBarPieChart.setProgress(3);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()){
            case R.id.seekBarpie:
            {
                Utility.setStatsPieTrimester(getActivity(), seekBar.getProgress()+1);
                getLoaderManager().restartLoader(PIECHART_LOADER, null, this);
                //restart line graph aswell..
                getLoaderManager().restartLoader(LINECHART_LOADER, null, this);
                mlineChart.invalidate();

                break;}
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

        //reset seek bar labale
        mSeekLinLineChart.removeAllViews();

        for (int count = 0; count < categories.size(); count++) {
            TextView textView = new TextView(getContext());
            if(count == 0) {
                //first value is all categories in same line graph
                textView.setText("All");
                catMap.put(count, "All");
            }else{
                textView.setText(categories.get(count).substring(0, 4));
                catMap.put(count, categories.get(count));
            }

            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setRotation(45);
            textView.setPadding(0,10,0,0);
            mSeekLinLineChart.addView(textView);
            textView.setLayoutParams((count == categories.size() - 1) ? getLayoutParams(0.0f) : getLayoutParams(1.0f));
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
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        //pie chart colours
        colors = new ArrayList<Integer>();
        for(int c : mColors) colors.add(c);

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

        XAxis xAxis = mlineChart.getXAxis();

        xAxis.setValueFormatter(new DateAxisValueFormatter(null));

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    private void setPieData(Cursor data) {

        Context ctx = getContext();

        mpieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        Double totalAmount = 0.0;

        //first get the total value of spendings..
        for(int i=0; i< data.getCount(); i++){
        totalAmount = totalAmount + data.getDouble(1);
            data.moveToNext();
        }


        data.moveToFirst();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < data.getCount() ; i++) {
            entries.add(new PieEntry((float) (data.getDouble(1) / totalAmount),
                    Utility.getTranslation(ctx,"cat",data.getString(0)) ));
            data.moveToNext();
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData piedata = new PieData(dataSet);
        piedata.setValueFormatter(new PercentFormatter());
        piedata.setValueTextSize(11f);
        piedata.setValueTextColor(Color.WHITE);
        mpieChart.setData(piedata);

        // undo all highlights
        mpieChart.highlightValues(null);

        mpieChart.invalidate();

        data.close();

    }

    private void setLineCharData(Cursor data) {

        Calendar cal = new GregorianCalendar();

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        int Catcounter = 0;

        String dateRaw = "";

        String category = "";

        mlineChart.animateX(2500);

        SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        int i = 0;

        if(data.moveToFirst()) {
            while(!data.isAfterLast()) {

                ArrayList<Entry> values = new ArrayList<Entry>();
                category = data.getString(0);

                //Category loop
                do{
                    if(!category.equals(data.getString(0))) break;

                    //For every category group values into datasets
                    float amtRaw = (float) data.getDouble(2);
                    //get date and format
                    dateRaw = String.valueOf(data.getInt(1));
                    //format date from integer back to date
                    //and then pass it to date format to get the formatted date
                    StringBuilder dateBuild = new StringBuilder().append(dateRaw.substring(6, 8)).append("/").append(dateRaw.substring(4, 6)).append("/").append(dateRaw.substring(0, 4));

                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateBuild.toString());
                        cal.setTime(date);
                        Long timeinmillis = cal.getTimeInMillis();
                        values.add(new Entry(timeinmillis, amtRaw));
                    } catch (ParseException e) {
                        //  Log.v(LOG_TAG, "error parsing date..");
                    }

                    }while(data.moveToNext());

                //configure the line for the particular category
                LineDataSet d = new LineDataSet(values, category);
                d.setLineWidth(2.5f);
                d.setCircleRadius(4f);
                //set colors
                d.setColor(colors.get(Catcounter));
                d.setCircleColor(colors.get(Catcounter));

                dataSets.add(d);
                Catcounter++;

            }
        }

        LineData linedata = new LineData(dataSets);

        mlineChart.setData(linedata);
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

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LINECHART_LOADER:

                //Log.v(LOG_TAG, "onLoadFinish for Line Chart loader called. data with: " + data.getCount());

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    // Load available categories
                    //setupLineChartSeekBar(data);
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
        }
    }


    class DateAxisValueFormatter implements IAxisValueFormatter{

        private String[] mValues;
        SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM");

        public DateAxisValueFormatter(String[] values) {
            this.mValues = values; }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            long millisVal = Double.valueOf(value).longValue();
            //long millis = TimeUnit.DAYS.toMillis((long) value);
            //Log.v(LOG_TAG, "formatted date (millis): " + millisVal);
            //Log.v(LOG_TAG, "formatted date: " + mFormat.format(new Date(millisVal)));
            return mFormat.format(new Date(millisVal));
        }
    }
}