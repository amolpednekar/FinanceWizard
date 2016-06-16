package com.example.fizz.financewizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//API KEY AIzaSyBiht1KNsxYLPgfP73P_Gb72mULFUQV_TY
//Server key  AIzaSyCUUjovK_G1Q-ak0wV5RPTHuzyywDO5iWA

public class MainActivity extends AppCompatActivity {
    AlertDialog alert1;
    private AlertDialog.Builder build1;
    private static boolean isLaunch = true;
    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected ArrayAdapter<String> mAdapter;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected String mActivityTitle;
    protected static int position;

    protected RelativeLayout mbank;
    public TextView cib;
    DatabaseHandler dbHand;
    SQLiteDatabase db;
    private DbHelperGoal tHelper;
    private DbHelperCategory cHelper;
    private DbHelperCategory chHelper;
    private SQLiteDatabase tDataBase, cDataBase, chDataBase;
    private AlertDialog.Builder build ;
    TextView tGoals, tSavings, tCategoryNo, cashHandAmount;
    private RelativeLayout totalG, handCashC;
    EditText transAmount, PayValue;

    AlertDialog alert;
    Spinner spinnerCat;
    String categoryG;
    Button cashAdd, cashSpent;
    String[] defaultCat = {"Lifestyle","Entertainment","Food & Drinks","Misc."};
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashflow_main);
        //TextView txt = (TextView) findViewById(R.id.custom_font);
        //Typeface font = Typeface.createFromAsset(getAssets(), "BrockScript.ttf"); Add custom font
        //txt.setTypeface(font);

        /*if(checkAndRequestPermissions()){
            ;
        }else{
            Toast.makeText(this,"No permissions",Toast.LENGTH_SHORT).show();
        }*/

        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Finance Wizard";//string


        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mbank=(RelativeLayout)findViewById(R.id.cardview_bank);
        mbank.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SmsActivity.class);
                startActivity(i);
            }

        });

        cib=(TextView)findViewById(R.id.bank_amount);
        countTotal();
        cHelper = new DbHelperCategory(this);

        cDataBase = cHelper.getWritableDatabase();
        Cursor gCursor;
        gCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME, null);
        String dbData = null;
        int catgyFlag = 0;

        if (gCursor.getCount() > 0) {
            //Toast.makeText(getApplicationContext(), "Data present", Toast.LENGTH_LONG).show();
            catgyFlag = 1;
        }

        if (catgyFlag == 1) {
            //Toast.makeText(getApplicationContext(), "Sorry, this option is already present", Toast.LENGTH_LONG).show();
            gCursor.close();
            cDataBase.close();
        } else {
            ContentValues values = new ContentValues();
            for (int x = 0; x < defaultCat.length; x++) {
                values.put(DbHelperCategory.CAT_TYPE, defaultCat[x]);
                cDataBase.insert(DbHelperCategory.TABLE_NAME, null, values);
            }
            cDataBase.close();
        }

        cashHandAmount = (TextView) findViewById(R.id.handNo2);// cash in hand
        handCashC = (RelativeLayout) findViewById(R.id.viewCashHandSlot);
        cashAdd = (Button) findViewById(R.id.buttonAdd);
        cashSpent = (Button) findViewById(R.id.buttonSpend);

        cashAdd.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
                build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Cash collected");
                build.setMessage("Please Enter amount collected");
                build.setView(promptsPaymentView);
                PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
                //PayValue.isFocused();
                PayValue.setFocusableInTouchMode(true);
                PayValue.setFocusable(true);
                PayValue.requestFocus();
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.showSoftInput(PayValue, InputMethodManager.SHOW_IMPLICIT);
                build.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        cDataBase=cHelper.getWritableDatabase();
                        ContentValues values=new ContentValues();

                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(calendar.YEAR), month = calendar.get(calendar.MONTH) + 1, date = calendar.get(calendar.DATE);
                        String currentDate = String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
                        values.put(DbHelperCategory.DATE,currentDate);
                        values.put(DbHelperCategory.AMOUNT, Float.valueOf(PayValue.getText().toString()));
                        values.put(DbHelperCategory.STATUS, "Credit");

                        cDataBase.insert(DbHelperCategory.TABLE_NAMECASH, null, values);
                        cDataBase.close();
                        Toast.makeText(getBaseContext(), "Amount saved successfully", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year),Toast.LENGTH_SHORT).show();
                        displayData();
                        dialog.cancel();
                    }
                });
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(PayValue.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });
                alert = build.create();
                alert.show();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        cashSpent.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
                build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Cash Spent");
                build.setMessage("Please Enter amount spent");
                build.setView(promptsPaymentView);
                PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
                //PayValue.isFocused();
                PayValue.setFocusableInTouchMode(true);
                PayValue.setFocusable(true);
                PayValue.requestFocus();
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.showSoftInput(PayValue, InputMethodManager.SHOW_IMPLICIT);
                build.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        cDataBase=cHelper.getWritableDatabase();
                        ContentValues values=new ContentValues();

                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(calendar.YEAR), month = calendar.get(calendar.MONTH) + 1, date = calendar.get(calendar.DATE);
                        String currentDate = String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
                        values.put(DbHelperCategory.DATE,currentDate);
                        values.put(DbHelperCategory.AMOUNT, Float.valueOf(PayValue.getText().toString()));
                        values.put(DbHelperCategory.STATUS, "Debit");

                        cDataBase.insert(DbHelperCategory.TABLE_NAMECASH, null, values);
                        cDataBase.close();
                        Toast.makeText(getBaseContext(), "New amount saved successfully", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year),Toast.LENGTH_SHORT).show();
                        displayData();
                        dialog.cancel();
                    }
                });
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(PayValue.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });
                alert = build.create();
                alert.show();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        handCashC.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableCashInHand();
            }
        });
    }



    /*private boolean checkAndRequestPermissions(){

        List<String> listPermissions = new ArrayList<String>();
        listPermissions.clear();
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_SMS) !=
        PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS)){

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS},REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
            listPermissions.add(Manifest.permission.READ_SMS);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECEIVE_SMS) !=
                PackageManager.PERMISSION_GRANTED){
            *//*if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS)){

            }*//*
            listPermissions.add(Manifest.permission.RECEIVE_SMS);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.MAPS_RECEIVE) !=
                PackageManager.PERMISSION_GRANTED){
            *//*if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS)){

            }*//*
            listPermissions.add(Manifest.permission.MAPS_RECEIVE);
        }

        if(!listPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode){
            case REQUEST_ID_MULTIPLE_PERMISSIONS :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
        }*/
        //Log.d(TAG, "Permission callback called-------");
        /*switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MAPS_RECEIVE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.MAPS_RECEIVE) == PackageManager.PERMISSION_GRANTED) {
                        //Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MAPS_RECEIVE)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }*/

    //}

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void displayData() {
        ArrayList<String> categoryList = new ArrayList<String>();
        ArrayList<Integer> categoryCnt = new ArrayList<Integer>();
        /*cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();
        Cursor cCursor = cDataBase.rawQuery("SELECT  * FROM " + DbHelperCategory.TABLE_NAME, null);
        if(cCursor.moveToFirst()){
            do{
                categoryList.add(cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.CAT_TYPE)));
                categoryCnt.add(0);
                //Toast.makeText(getApplicationContext(),cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.CAT_TYPE)),Toast.LENGTH_LONG).show();
            }while(cCursor.moveToNext());
            //Toast.makeText(getApplicationContext(),String.valueOf(categoryList.size()),Toast.LENGTH_LONG).show();
        }
        tHelper = new DbHelperGoal(this.getBaseContext());
        tDataBase = tHelper.getWritableDatabase();

        Cursor mCursor = tDataBase.rawQuery("SELECT * FROM " + DbHelperGoal.TABLE_NAME, null);
        float savings = 0;
        int goalCnt = 0;
        int categoryCntTemp = 0;
        String currencyType = "";
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                i = 0;
                goalCnt += 1;
                currencyType = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CURRENCY));
                savings += (mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT)) - mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE)));
                do{
                    if(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CATEGORY)).equals(categoryList.get(i))){
                        categoryCntTemp = categoryCnt.get(i);
                        ++categoryCntTemp;
                        categoryCnt.set(i, categoryCntTemp);
                        break;
                    }
                    i++;
                }while(i < categoryList.size());
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        tGoals.setText(String.valueOf(goalCnt));
        String catContent = "";
        for(i = 0;i<categoryList.size();i++){
            if(i == 0)
                catContent = categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
            else
                catContent += categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
        }
        tSavings.setText(currencyType + " " + String.format("%.0f", savings));
        tCategoryNo.setText(catContent);


        */

        // Cash In hand Display part
        cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();
        Cursor cCursor = cDataBase.rawQuery("SELECT  * FROM " + DbHelperCategory.TABLE_NAMECASH, null);
        float total = 0, credit = 0, debit = 0;
        if(cCursor.moveToFirst()){
            do{
                if(cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.STATUS)).equals("Credit"))
                    total += cCursor.getFloat(cCursor.getColumnIndex(DbHelperCategory.AMOUNT));
                else
                    total -= cCursor.getFloat(cCursor.getColumnIndex(DbHelperCategory.AMOUNT));
            }while(cCursor.moveToNext());
        }
        cCursor.close();
        cashHandAmount.setText("₹ " + String.valueOf(total));
    }


    public void tableCashInHand(){
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsHistoryView = li.inflate(R.layout.cash_hand_layout, null);
        build = new AlertDialog.Builder(MainActivity.this);
        build.setTitle("History");
        build.setView(promptsHistoryView);
        TableLayout cht = (TableLayout) promptsHistoryView.findViewById(R.id.tableHandCash);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView tv0 = new TextView(this);
        tv0.setText("Status");
        tv0.setTextSize(12);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextColor(Color.BLACK);
        tv0.setGravity(Gravity.CENTER);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Amount");
        tv1.setTextSize(12);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(Color.BLACK);
        tv1.setGravity(Gravity.CENTER);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Trans Date");
        tv2.setTextSize(12);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(Color.BLACK);
        tv2.setGravity(Gravity.CENTER);
        tbrow0.addView(tv2);
        cht.addView(tbrow0);
        cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();

        Cursor cCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAMECASH, null);
        //Toast.makeText(getApplicationContext(), String.valueOf(cCursor.getCount()),Toast.LENGTH_SHORT).show();
        float total = 0, credit = 0, debit = 0;
        if(cCursor.moveToFirst()){
            do{
                TableRow tbrow = new TableRow(this);
                tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView t1v = new TextView(this);
                t1v.setText(cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.STATUS)));
                t1v.setTextSize(12);
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText("₹ " + String.valueOf(cCursor.getFloat(cCursor.getColumnIndex(DbHelperCategory.AMOUNT))));
                t2v.setTextSize(12);
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                TextView t3v = new TextView(this);
                t3v.setText(cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.DATE)));
                t3v.setTextSize(12);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);
                cht.addView(tbrow);
            }while(cCursor.moveToNext());
        }
        cCursor.close();

        build.setNegativeButton("Close ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                //displayData();
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(PayValue.getWindowToken(), 0);
                dialog.cancel();
            }
        });

        alert = build.create();
        alert.show();

    }


    public void countTotal(){
        dbHand=new DatabaseHandler(this.getBaseContext());
        db=dbHand.getWritableDatabase();
        Cursor mcursor = db.rawQuery("SELECT * FROM "+DatabaseHandler.TABLE_NAME3,null);
        float total=0;
        if(mcursor.moveToFirst()) {
            do {
                total += Float.valueOf(mcursor.getString(mcursor.getColumnIndex(DatabaseHandler.TOTAL)));
            } while (mcursor.moveToNext());
        }
        cib.setText(String.valueOf(total));
    }
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        countTotal();
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
    }

    private void addDrawerItems() {

        myAdapter MyAdapter = new myAdapter(this);
        mDrawerList.setAdapter(MyAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    protected void openActivity(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        MainActivity.position = position; //Setting currently selected position in this field so that it will be available in our child activities.
        switch (position) {
            case 0:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, RssMainActivity.class));
                break;
           case 2:
                startActivity(new Intent(this, Goals_MainActivity.class));
                break;
            case 3:
               startActivity(new Intent(this, Trends_MainActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, CamMainActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, MapsMainActivity.class));
            default:
                break;
        }

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    class myAdapter extends BaseAdapter {
        private Context context;
        String NavListCategories[];
        int[] images = {R.drawable.cash_flow,R.drawable.rss,R.drawable.goals_targets,R.drawable.trends,R.drawable.reminders,R.drawable.map};

        public myAdapter(Context context){
            this.context = context;
            NavListCategories = context.getResources().getStringArray(R.array.NavigationDrawerList);
        }
        @Override
        public int getCount() {
            return NavListCategories.length;
        }

        @Override
        public Object getItem(int position) {
            return NavListCategories[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.custom_row, parent, false);
            }
            else{
                row =convertView;
            }

            TextView titleTextView =(TextView) row.findViewById(R.id.textViewRow1);
            ImageView titleImageView = (ImageView) row.findViewById(R.id.imageViewRow1);
            titleTextView.setText(NavListCategories[position]);
            titleImageView.setImageResource(images[position]);
            return row;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.calculator){

            startActivity(new Intent(this,Calc.class));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, settings_main.class));
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
//end