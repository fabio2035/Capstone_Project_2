package com.example.fbrigati.myfinance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.dummy.DummyContent;
import com.example.fbrigati.myfinance.elements.Budget;
import com.example.fbrigati.myfinance.sync.MFSyncJob;
import com.example.fbrigati.myfinance.ui.BudgetActivity;
import com.example.fbrigati.myfinance.ui.CurrenciesActivity;
import com.example.fbrigati.myfinance.ui.CurrenciesFragment;
import com.example.fbrigati.myfinance.ui.StatementActivity;
import com.example.fbrigati.myfinance.ui.StatementFragment;
import com.example.fbrigati.myfinance.ui.StatsActivity;
import com.example.fbrigati.myfinance.ui.StatsFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("53F4B94474E00A7E14FD516F7AD2ACDF")  // My Galaxy Nexus test phone
                .build();
        mAdView.loadAd(adRequest);


        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
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
            holder.mContentView.setText(mValues.get(position).content);
            holder.mContentView.setContentDescription(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Log.v("Choosing_Menu", "Chose menu item: " + holder.mItem.id);
                        Intent intent;
                        switch (holder.mItem.id){
                            case "0":
                                intent = new Intent(context, StatementActivity.class);
                                intent.putExtra(StatementFragment.ID_MESSAGE, holder.mItem.id);
                                context.startActivity(intent);
                                break;
                            case "1":
                                intent = new Intent(context, BudgetActivity.class);
                                //intent.putExtra(BudgetFragment.ID_MESSAGE, holder.mItem.id);
                                context.startActivity(intent);
                                break;
                            case "2":
                                intent = new Intent(context, StatsActivity.class);
                                intent.putExtra(StatsFragment.ID_MESSAGE, holder.mItem.id);
                                context.startActivity(intent);
                                break;
                            case "3":
                                intent = new Intent(context, CurrenciesActivity.class);
                                intent.putExtra(CurrenciesFragment.ID_MESSAGE, holder.mItem.id);
                                context.startActivity(intent);
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
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
               //mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
