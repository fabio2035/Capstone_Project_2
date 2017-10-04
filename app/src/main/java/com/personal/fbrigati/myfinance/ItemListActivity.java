package com.personal.fbrigati.myfinance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.personal.fbrigati.myfinance.dummy.MenuContent;
import com.personal.fbrigati.myfinance.ui.BudgetActivity;
import com.personal.fbrigati.myfinance.ui.CurrenciesActivity;
import com.personal.fbrigati.myfinance.ui.CurrenciesFragment;
import com.personal.fbrigati.myfinance.ui.StatementActivity;
import com.personal.fbrigati.myfinance.ui.StatementFragment;
import com.personal.fbrigati.myfinance.ui.StatsActivity;
import com.personal.fbrigati.myfinance.ui.StatsFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;


public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private AdView mAdView;

    private boolean showInstr_Statement = false;
    private boolean showInstr_Budget = false;
    private boolean showInstr_Stats = false;
    private boolean showInstr_Currencies = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_logo);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_logo);
        //toolbar.setTitle(getTitle());


        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("53F4B94474E00A7E14FD516F7AD2ACDF")  // My Galaxy Nexus test phone
        //        .build();
        mAdView.loadAd(adRequest);


        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        ((RecyclerView) recyclerView).setHasFixedSize(true);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(MenuContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<MenuContent.MenuItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<MenuContent.MenuItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(getResources().getString(mValues.get(position).content));
            holder.mContentView.setContentDescription(getResources().getString(mValues.get(position).content));
            holder.mIcon.setImageResource(mValues.get(position).icon);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        //Todo: add layouts for twopane
                    } else {
                        Context context = v.getContext();
                        //Log.v("Choosing_Menu", "Chose menu item: " + holder.mItem.id);
                        Intent intent;
                        switch (holder.mItem.id){
                            case "0":
                                intent = new Intent(context, StatementActivity.class);
                                intent.putExtra(StatementFragment.ID_MESSAGE, holder.mItem.id);
                                intent.putExtra(StatementActivity.ID_INSTRUCTIONS, showInstr_Statement);
                                context.startActivity(intent);
                                showInstr_Statement = false;
                                break;
                            case "1":
                                intent = new Intent(context, BudgetActivity.class);
                                //intent.putExtra(BudgetFragment.ID_MESSAGE, holder.mItem.id);
                                intent.putExtra(BudgetActivity.ID_INSTRUCTIONS, showInstr_Budget);
                                context.startActivity(intent);
                                showInstr_Budget = false;
                                break;
                            case "2":
                                intent = new Intent(context, StatsActivity.class);
                                intent.putExtra(StatsFragment.ID_MESSAGE, holder.mItem.id);
                                intent.putExtra(StatsActivity.ID_INSTRUCTIONS, showInstr_Stats);
                                context.startActivity(intent);
                                showInstr_Stats = false;
                                break;
                            case "3":
                                intent = new Intent(context, CurrenciesActivity.class);
                                intent.putExtra(CurrenciesFragment.ID_MESSAGE, holder.mItem.id);
                                intent.putExtra(CurrenciesActivity.ID_INSTRUCTIONS, showInstr_Currencies);
                                context.startActivity(intent);
                                showInstr_Currencies = false;
                                break;
                            default:
                                Toast.makeText(getApplicationContext(),R.string.toast_functionality_notimplemented, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public MenuContent.MenuItem mItem;
            public final ImageView mIcon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mIcon = (ImageView) view.findViewById(R.id.menu_icon);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
