package com.example.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.adapters.BudgetItemArrayAdapter;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetFragment extends Fragment {

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";

    private String[] temp_items = new String[]{"Leisure", "Bills", "Transportation"};
    private int[] temp_items_values = new int[]{20, 50, 55};
    private ArrayList<Budget> itemList;

    public Budget[] budgetItem = {
      new Budget("2017","05","Transport",15000f,123456,"Transport Desc",154.00f)
    };

    BudgetItemArrayAdapter budgetAdapter;


    public BudgetFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState == null || !savedInstanceState.containsKey(ID_MESSAGE)) {
            itemList = new ArrayList<Budget>(Arrays.asList(budgetItem));
        }else{
            itemList = savedInstanceState.getParcelableArrayList(ID_MESSAGE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ID_MESSAGE, itemList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        budgetAdapter = new BudgetItemArrayAdapter(getActivity(), itemList);

        //Get reference to the listView and attach adapter to it
        ListView budgetLV = (ListView) rootView.findViewById(R.id.budget_list);

        budgetLV.setAdapter(budgetAdapter);

    return rootView;
    }

   /* private void temp_data() {
        for(int j = 0; j < this.temp_items.length; ++j) {
            Budget_item itm = new Budget_item();
            itm.setMaxValue(100);
            itm.setSpentValue(this.temp_items_values[j]);
            itm.setLabelValue(this.temp_items[j]);
            itm.setIconValue(this.icon_val[j]);
            itm.setProgressValue(this.temp_items_values[j]);
            this.item.add(itm);
        }

    }
    */



}
