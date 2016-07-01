package com.example.fizz.financewizard;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

    protected Button mbank;
    public TextView cib;
    DatabaseHandler dbHand;
    SQLiteDatabase db;
    private DbHelperGoal tHelper;
    private DbHelperCategory cHelper;
    private SQLiteDatabase tDataBase, cDataBase, chDataBase;
    private AlertDialog.Builder build;
    TextView tGoals, tSavings, tCategoryNo, cashHandAmount;
    private RelativeLayout totalG;
    EditText transAmount, PayValue;

    AlertDialog alert;
    Spinner spinnerCat;
    String categoryG;
    Button cashAdd, cashSpent, alertOKButton;
    String[] defaultCat = {"Lifestyle", "Entertainment", "Food & Drinks", "Misc."};
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //sharath add database variables <- already declared on Top


    private static final int REQUEST_APP_SETTINGS = 168;
    Button handCashC;
    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            //Manifest.permission.WRITE_GSERVICES,
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
            /* ETC.. */
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashflow_main);
        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
            Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_LONG).show();
            goToSettings();
        }
        //sharath add check for enabled and disabled
        final ContentValues valuesPer = new ContentValues();
        cHelper = new DbHelperCategory(this);
        cDataBase = cHelper.getWritableDatabase();

        final Cursor cur = cDataBase.rawQuery("SELECT * FROM "+ DbHelperCategory.TABLE_PERMISSION /*+ " WHERE " +DbHelperCategory.TYPE_PER + "=" + "SMS"*/, null);
        if(cur.getCount() <= 0){// verifies if the SMS-permission is present in the database
            valuesPer.put(DbHelperCategory.TYPE_PER, "SMS");
            valuesPer.put(DbHelperCategory.STATUS_PER, "enabled");
            cDataBase.insert(DbHelperCategory.TABLE_PERMISSION, null, valuesPer);
        }else{
            ;
        }

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Finance Wizard";//string


        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mbank = (Button) findViewById(R.id.cardview_bank);
        cib = (TextView) findViewById(R.id.bank_amount);

        mbank.setOnClickListener(new AdapterView.OnClickListener() { // Cash in Bank
            @Override
            public void onClick(View view) {
                // this is for cash in bank SMS permission
                final Cursor curS = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_PERMISSION + " WHERE " + DbHelperCategory.TYPE_PER + "=" + "'SMS'", null);

                if (curS.getCount() > 0) {
                    if (curS.moveToFirst()) {
                        do {
                            if (curS.getString(curS.getColumnIndex(DbHelperCategory.STATUS_PER)).equals("enabled")) {
                                Intent i = new Intent(getApplicationContext(), SmsActivity.class);
                                startActivity(i);
                                break;
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enable the SMS permission from App Settings", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } while (curS.moveToNext());
                    }
                }
            }

        });


        countTotal();

        final Cursor curS = cDataBase.rawQuery("SELECT * FROM "+ DbHelperCategory.TABLE_PERMISSION + " WHERE " +DbHelperCategory.TYPE_PER + "=" + "'SMS'", null);

        if(curS.getCount() > 0){
            if (curS.moveToFirst()) {
                do {
                    if (curS.getString(curS.getColumnIndex(DbHelperCategory.STATUS_PER)).equals("disabled")) {
                        cib.setText("-");
                        break;
                    }
                } while (curS.moveToNext());
            }
        }


        Cursor gCursor;
        gCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME, null);
        String dbData = null;
        int catgyFlag = 0;

        if (gCursor.getCount() > 0) {
            catgyFlag = 1;
        }

        if (catgyFlag == 1) {

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
        handCashC = (Button) findViewById(R.id.viewCashHandSlot);
        cashAdd = (Button) findViewById(R.id.buttonAdd);
        cashSpent = (Button) findViewById(R.id.buttonSpend);
        // sharath add condition if disabled to disable card view <- This is cash in Hand
        cashAdd.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
                build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Cash Collected");
                build.setMessage("Enter Amount");
                build.setView(promptsPaymentView);
                PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
                //PayValue.isFocused();
                PayValue.setFocusableInTouchMode(true);
                PayValue.setFocusable(true);
                PayValue.requestFocus();
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.showSoftInput(PayValue, InputMethodManager.SHOW_IMPLICIT);

                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!PayValue.getText().toString().isEmpty()) {
                            cDataBase = cHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();

                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(calendar.YEAR), month = calendar.get(calendar.MONTH) + 1, date = calendar.get(calendar.DATE);
                            String currentDate = String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
                            values.put(DbHelperCategory.DATE, currentDate);
                            values.put(DbHelperCategory.AMOUNT, Float.valueOf(PayValue.getText().toString()));
                            values.put(DbHelperCategory.STATUS, "Credit");

                            cDataBase.insert(DbHelperCategory.TABLE_NAMECASH, null, values);
                            cDataBase.close();
                            displayData();
                            dialog.cancel();
                        } else {
                            //Toast.makeText(getApplicationContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert = build.create();
                alert.show();
                alertOKButton = alert.getButton(AlertDialog.BUTTON1);
                alertOKButton.setEnabled(false);
                PayValue.addTextChangedListener(textWatcher);
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
                build.setMessage("Enter Amount");
                build.setView(promptsPaymentView);
                PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
                PayValue.setFocusableInTouchMode(true);
                PayValue.setFocusable(true);
                PayValue.requestFocus();
                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!PayValue.getText().toString().isEmpty()) {
                            cDataBase = cHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(calendar.YEAR), month = calendar.get(calendar.MONTH) + 1, date = calendar.get(calendar.DATE);
                            String currentDate = String.valueOf(date) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
                            values.put(DbHelperCategory.DATE, currentDate);
                            values.put(DbHelperCategory.AMOUNT, Float.valueOf(PayValue.getText().toString()));
                            values.put(DbHelperCategory.STATUS, "Debit");

                            cDataBase.insert(DbHelperCategory.TABLE_NAMECASH, null, values);
                            cDataBase.close();
                           // Toast.makeText(getBaseContext(), "New amount saved successfully", Toast.LENGTH_SHORT).show();
                            displayData();
                            dialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                alert = build.create();
                alert.show();
                alertOKButton = alert.getButton(AlertDialog.BUTTON1);
                alertOKButton.setEnabled(false);
                PayValue.addTextChangedListener(textWatcher);
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

    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    // checks if textBox is empty or not & Enables/Disables the button
    private  void checkFieldsForEmptyValues(){
        String s1 = PayValue.getText().toString();

        if(s1.equals("")) {
            alertOKButton.setEnabled(false);
        }
        else {
            alertOKButton.setEnabled(true);
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    // display cash in hand amount
    public void displayData() {
        ArrayList<String> categoryList = new ArrayList<String>();
        ArrayList<Integer> categoryCnt = new ArrayList<Integer>();
        // Cash In hand Display part
        cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();
        Cursor cCursor = cDataBase.rawQuery("SELECT  * FROM " + DbHelperCategory.TABLE_NAMECASH, null);
        float total = 0, credit = 0, debit = 0;
        if (cCursor.moveToFirst()) {
            do {
                if (cCursor.getString(cCursor.getColumnIndex(DbHelperCategory.STATUS)).equals("Credit"))
                    total += cCursor.getFloat(cCursor.getColumnIndex(DbHelperCategory.AMOUNT));
                else
                    total -= cCursor.getFloat(cCursor.getColumnIndex(DbHelperCategory.AMOUNT));
            } while (cCursor.moveToNext());
        }
        cCursor.close();
        cashHandAmount.setText("₹ " + String.valueOf(total));
    }

    //cash in hand history
    public void tableCashInHand() {
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
        tv2.setText("Date");
        tv2.setTextSize(12);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(Color.BLACK);
        tv2.setGravity(Gravity.CENTER);
        tbrow0.addView(tv2);
        cht.addView(tbrow0);
        cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();

        Cursor cCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAMECASH + " ORDER BY " + DbHelperCategory.DATE + " DESC", null);
        if (cCursor.moveToFirst()) {
            do {
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
            } while (cCursor.moveToNext());
        }
        cCursor.close();

        build.setNegativeButton("Close ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert = build.create();
        alert.show();

    }

    // cash in bank total amount
    public void countTotal() {
        dbHand = new DatabaseHandler(this.getBaseContext());
        db = dbHand.getWritableDatabase();
        Cursor mcursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_NAME3, null);
        float total = 0;
        if (mcursor.moveToFirst()) {
            do {
                total += Float.valueOf(mcursor.getString(mcursor.getColumnIndex(DatabaseHandler.TOTAL)));
            } while (mcursor.moveToNext());
        }
        mcursor.close();

        cHelper = new DbHelperCategory(this.getBaseContext());
        cDataBase = cHelper.getWritableDatabase();

        mcursor = cDataBase.rawQuery("SELECT * FROM "+ DbHelperCategory.TABLE_PERMISSION, null);
        if (mcursor.moveToFirst()) {
            do {
                if(mcursor.getString(mcursor.getColumnIndex(DbHelperCategory.STATUS_PER)).equals("enabled")) {
                    cib.setText("₹ " + String.valueOf(total));
                    break;
                }else{
                    cib.setText("-");
                }
            } while (mcursor.moveToNext());
        }
        mcursor.close();
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        countTotal();
        displayData();
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    //to prevent user from exiting the app on single Back click
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

    protected void openActivity(int position) {// navbar drawer list contents
        mDrawerLayout.closeDrawer(mDrawerList);
        MainActivity.position = position; //Setting currently selected position in this field so that it will be available in our child activities.
        switch (position) {
            case 0:
                startActivity(new Intent(this, MainActivity.class));
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

    // NavBar Side Drawer
    class myAdapter extends BaseAdapter {
        private Context context;
        String NavListCategories[];
        int[] images = {R.drawable.cash_flow, R.drawable.rss, R.drawable.goals_targets, R.drawable.trends, R.drawable.cam, R.drawable.map};

        public myAdapter(Context context) {
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
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.custom_row, parent, false);
            } else {
                row = convertView;
            }

            TextView titleTextView = (TextView) row.findViewById(R.id.textViewRow1);
            ImageView titleImageView = (ImageView) row.findViewById(R.id.imageViewRow1);
            titleTextView.setText(NavListCategories[position]);
            titleImageView.setImageResource(images[position]);
            return row;
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
                return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_APP_SETTINGS) {
            if (hasPermissions(requiredPermissions)) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.calculator) {

            startActivity(new Intent(this, Calc.class));
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