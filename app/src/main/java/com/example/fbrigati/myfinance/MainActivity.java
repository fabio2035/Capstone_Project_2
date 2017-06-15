/*package com.example.fbrigati.myfinance;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Statement;
import com.example.fbrigati.myfinance.ui.StatementFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by FBrigati on 30/04/2017.
 */
/*
public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    //Firebase variables
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStatementDatabaseReference;
    private ChildEventListener mChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        //Firebase stuff...
        //initialize object variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsername = ANONYMOUS;
        mStatementDatabaseReference = mFirebaseDatabase.getReference().child("statement");
        attachDatabaseReadListener();

        //todo: Add two-pane view check and execution..
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_frag_container, new MainFragment())
                    .commit();
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void attachDatabaseReadListener() {
        if(mChildListener == null){
            mChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Statement statementMessage = dataSnapshot.getValue(Statement.class);
                    UpdateStatement(statementMessage);
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
                public void onChildRemoved(DataSnapshot dataSnapshot) { }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                public void onCancelled(DatabaseError databaseError) { }
            };

            mStatementDatabaseReference.addChildEventListener(mChildListener);}
    }

    private void UpdateStatement(Statement statementMessage) {

        //Check origin of statement data
        if(statementMessage.getAcquirer_id().equals("0")){
            //If its an update from local device do noting..
            Log.v(LOG_TAG, "update from local device.. do nothing");
        }else{
            Log.v(LOG_TAG, "update not from local device.. add data");
            //It's not an update from local device, add data..
            ContentValues cv = new ContentValues();

            cv.put(DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER, statementMessage.getAccountNumber());
            cv.put(DataContract.StatementEntry.COLUMN_DATE, statementMessage.getDate());
            cv.put(DataContract.StatementEntry.COLUMN_TIME, statementMessage.getTime());
            cv.put(DataContract.StatementEntry.COLUMN_SEQUENCE, statementMessage.getSequence());
            cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_ORIGIN, statementMessage.getDescription());
            cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_USER, statementMessage.getDesc_user());
            cv.put(DataContract.StatementEntry.COLUMN_AMOUNT, statementMessage.getAmount());
            cv.put(DataContract.StatementEntry.COLUMN_TRANSACTION_CODE, statementMessage.getTransactionCode());
            cv.put(DataContract.StatementEntry.COLUMN_ACQUIRER_ID, statementMessage.getAcquirer_id());
            cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, statementMessage.getCategory());

            getContentResolver().insert(DataContract.StatementEntry.CONTENT_URI, cv);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }





}

*/