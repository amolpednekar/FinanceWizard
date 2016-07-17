package com.example.fizz.financewizard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

//This java file is used to display a listview for Goal and also to update and delete
// This file calls "GoalAdapter.java" & "DbHelperGoal.java"
public class GoalDisActivity extends AppCompatActivity {

    ImageButton tri;
    static final int PAY_DIALOG_ID = 0, DATE_DIALOG_ID = 0;;
    private int currentYear, currentMonth, currentDay;
    int buttonPosFlag = 0, keyIndex = 0;
    float check = 0, val = 0;
    AlertDialog alert, alert2;
    String currencyG, moneyValue, dayValue, dbExpAmount, alertString = "";
    GoalAdapter goaladpt;
    private DbHelperGoal gHelper;
    private DbHelperCategory cHelper;
    private SQLiteDatabase dataBase;
    EditText PayValue, ExpValue, CatgyValue;
    Button newDate, alertOKButton;
    String dayG = "", monthG = "", yearG = "", delGoalId, catNotify;
    private NotificationManager myGoalNotifyMgr;
    View promptsDateView;

    private ArrayList<String> keyId = new ArrayList<String>();
    private ArrayList<String> goalTitle = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> day = new ArrayList<String>();
    private ArrayList<String> month = new ArrayList<String>();
    private ArrayList<String> year = new ArrayList<String>();
    private ArrayList<String> amount = new ArrayList<String>();
    private ArrayList<String> daysLeftGoal = new ArrayList<String>();
    private ArrayList<Integer> progressValue = new ArrayList<Integer>();

    private ArrayList<Integer> notifyLastDay = new ArrayList<Integer>();
    private ArrayList<String> notifyTitle = new ArrayList<String>();
    private ArrayList<String> notifyId = new ArrayList<String>();


    public ListView goalList2;

    private AlertDialog.Builder build, build2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_dis);

        //call the listview
        goalList2 = (ListView) findViewById(R.id.goalListView2);

        gHelper = new DbHelperGoal(this);//call the database
        cHelper = new DbHelperCategory(this);
        tri = (ImageButton) findViewById(R.id.imageButtonAdd);
        tri.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GoalActivity.class);
                i.putExtra("update", false);
                startActivity(i);
            }
        });

        delGoalId = getIntent().getExtras().getString("ID");

        if(delGoalId == null){
            delGoalId = String.valueOf(getIntent().getExtras().getInt("ID"));
            if(Integer.valueOf(delGoalId) > 0) {
                onReceive(delGoalId);
            }
        }else if(delGoalId != null ) {
            onReceive(delGoalId);
        }

        //go to the detail list
        goalList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                Intent i = new Intent(getApplicationContext(), GoalInfoActivity.class);
                i.putExtra("Goal", goalTitle.get(arg2));
                i.putExtra("Date", date.get(arg2));
                i.putExtra("Amount", amount.get(arg2));
                i.putExtra("DaysLeft", daysLeftGoal.get(arg2));
                i.putExtra("ID", keyId.get(arg2));
                i.putExtra("update", true);
                startActivity(i);
            }
        });

        //On List data long Click
        goalList2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                final CharSequence[] listClick = {"Change Deadline", "Contribute", "Expense", "Delete"};

                build = new AlertDialog.Builder(GoalDisActivity.this);
                build.setTitle("Options");
                build.setItems(listClick, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        switch (item) {
                            case 0://Date Modifier
                                LayoutInflater ld = LayoutInflater.from(GoalDisActivity.this);
                                promptsDateView = ld.inflate(R.layout.date_button, null);//promptsDateView declared global for later reference to DateDialogButton
                                build = new AlertDialog.Builder(GoalDisActivity.this);
                                build.setTitle("Change Deadline");
                                build.setMessage("");
                                build.setView(promptsDateView);
                                newDate = (Button) promptsDateView.findViewById(R.id.newGoalDate);
                                newDate.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        showDialog(DATE_DIALOG_ID);
                                    }
                                });

                                final Calendar c = Calendar.getInstance();
                                currentYear = c.get(Calendar.YEAR);
                                currentMonth = c.get(Calendar.MONTH);
                                currentDay = c.get(Calendar.DAY_OF_MONTH);

                                newDate.setFocusableInTouchMode(true);
                                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataBase = gHelper.getWritableDatabase();
                                        Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperGoal.TABLE_NAME + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2), null);
                                        if (mCursor.moveToFirst()) {
                                            do {
                                                dayValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY));
                                            } while (mCursor.moveToNext());
                                        }
                                        if (dayG != null && !dayG.isEmpty() && monthG != null && !monthG.isEmpty() && yearG != null && !yearG.isEmpty()) {
                                            int dayVal = Integer.valueOf(dayG), monthVal = Integer.valueOf(monthG), yearVal = Integer.valueOf(yearG);
                                            String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.DAY + "=" + String.valueOf(dayVal) + ", " + DbHelperGoal.MONTH + "=" + String.valueOf(monthVal) + ", " + DbHelperGoal.YEAR + "=" + String.valueOf(yearVal) + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2);
                                            dataBase.execSQL(strSQL);
                                            displayData();
                                            dialog.cancel();
                                        }else{
                                            displayData();
                                            Toast.makeText(getApplicationContext(),"Please select a date", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(getApplication(), "New Date Cancelled", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                                alert = build.create();
                                alert.show();
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                break;
                            case 1://Payment
                                LayoutInflater li = LayoutInflater.from(GoalDisActivity.this);
                                View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
                                build = new AlertDialog.Builder(GoalDisActivity.this);
                                build.setTitle("Contribute");
                                build.setMessage("Enter Amount");
                                build.setView(promptsPaymentView);
                                PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
                                PayValue.setFocusableInTouchMode(true);
                                PayValue.setFocusable(true);
                                PayValue.requestFocus();
                                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        check = 0;
                                        val = 0;
                                        dataBase = gHelper.getWritableDatabase();
                                        Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperGoal.TABLE_NAME + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2), null);
                                        if (mCursor.moveToFirst()) {
                                            do {
                                                moneyValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT));
                                                dbExpAmount = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT));
                                                check = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE));
                                            } while (mCursor.moveToNext());
                                        }
                                        val = Float.valueOf(PayValue.getText().toString()) + Float.valueOf(moneyValue);
                                        if (val - check <= Float.valueOf(dbExpAmount) && val - check >= 0) {// within the Target Amount
                                            String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2);
                                            dataBase.execSQL(strSQL);
                                            Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                            displayData();
                                            dialog.cancel();
                                        } else if (val - check > Float.valueOf(dbExpAmount) && val - check >= 0) {// if client collects extra amount for that goal, the Target amount extends
                                            build2 = new AlertDialog.Builder(GoalDisActivity.this);
                                            build2.setTitle("Confirmation");
                                            build2.setMessage("The Payment Amount is exceeding the Target Amount. Do you want to increment the Target amount to the new value?");
                                            build2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.AMOUNT + "=" + String.valueOf(val - check) + "," + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2);
                                                    dataBase.execSQL(strSQL);
                                                    //Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    displayData();
                                                    dialog.cancel();
                                                }
                                            });
                                            build2.setNeutralButton("No, Only Savings", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {


                                                    String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2);
                                                    dataBase.execSQL(strSQL);
                                                   // Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    displayData();
                                                    dialog.cancel();
                                                }
                                            });

                                            build2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                }
                                            });

                                            alert2 = build2.create();
                                            alert2.show();
                                            alert2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                                        } else {
                                            Toast.makeText(getApplication(), "Sorry, the amount is beyond the Target Amount", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                                alert = build.create();
                                alert.show();
                                alertOKButton = alert.getButton(AlertDialog.BUTTON1);
                                alertOKButton.setEnabled(false);
                                PayValue.addTextChangedListener(textWatcher);
                                alertString = PayValue.getText().toString();
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                break;
                            case 2://Expense
                                LayoutInflater le = LayoutInflater.from(GoalDisActivity.this);
                                View promptsExpenseView = le.inflate(R.layout.payment_layout, null);
                                build = new AlertDialog.Builder(GoalDisActivity.this);
                                build.setTitle("Expense");
                                build.setMessage("Enter Amount");
                                build.setView(promptsExpenseView);
                                ExpValue = (EditText) promptsExpenseView.findViewById(R.id.PaymentEnter1);
                                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        check = 0;
                                        val = 0;
                                        dataBase = gHelper.getWritableDatabase();
                                        Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperGoal.TABLE_NAME + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2), null);
                                        if (mCursor.moveToFirst()) {
                                            do {
                                                moneyValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE));
                                                dbExpAmount = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT));
                                                check = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT));
                                            } while (mCursor.moveToNext());
                                        }

                                        val = Float.valueOf(ExpValue.getText().toString()) + Float.valueOf(moneyValue);// TextBox + db value
                                        if (check - val <= Float.valueOf(dbExpAmount) && check - val >= 0) {
                                            String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_EXPENSE + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + keyId.get(arg2);
                                            dataBase.execSQL(strSQL);
                                            ExpValue.setText("");
                                            displayData();
                                            dialog.cancel();
                                        } else {
                                            Toast.makeText(getApplication(), "Exceeding the Target amount limit.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplication(), "Expense Cancelled", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                                alert = build.create();
                                alert.show();
                                alertOKButton = alert.getButton(AlertDialog.BUTTON1);
                                alertOKButton.setEnabled(false);
                                ExpValue.addTextChangedListener(textWatcher);
                                alertString = ExpValue.getText().toString();
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                break;
                            case 3://Delete Data
                                build = new AlertDialog.Builder(GoalDisActivity.this);
                                build.setTitle("Delete " + goalTitle.get(arg2) + " " + date.get(arg2) + " " + amount.get(arg2));
                                build.setMessage("Do you want to delete ?");
                                build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), goalTitle.get(arg2) + " " + date.get(arg2) + " " + amount.get(arg2) + " is deleted.", Toast.LENGTH_LONG).show();
                                        dataBase.delete(DbHelperGoal.TABLE_NAME, DbHelperGoal.KEY_ID + "=" + keyId.get(arg2), null);
                                        displayData();
                                        dialog.cancel();
                                    }
                                });
                                build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert = build.create();
                                alert.show();
                                break;
                            default:
                                Toast.makeText(getApplication(), "default", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alert = build.create();
                alert.show();
                return true;
            }
        });


        //hide button on slide down
        goalList2.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    tri.animate().translationX(350);
                    tri.animate().translationY(350);
                } else if (scrollState == SCROLL_STATE_FLING) {
                    tri.animate().translationX(350);
                    tri.animate().translationY(350);
                }else {
                    tri.animate().translationX(0);
                    tri.animate().translationY(0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {//(view-Object, 1st item index, no of values displayed, total items)
                //Toast.makeText(getApplication(),String.valueOf(visibleItemCount),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener reservationDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day){
            view.setMinDate(new Date().getTime());// set min date limit in calendar
            final Calendar c = Calendar.getInstance();
            int curYear = c.get(Calendar.YEAR), curMonth = c.get(Calendar.MONTH)+1, curDay = c.get(Calendar.DAY_OF_MONTH);
            newDate=(Button)promptsDateView.findViewById(R.id.newGoalDate);
            //Picks the selected date, month & year & displays on button
            if((year>curYear)||(year==curYear && month+1>curMonth)||(year==curYear && month+1==curMonth && day>curDay)) {
                dayG = Integer.toString(day);
                monthG = Integer.toString(month + 1);
                yearG = Integer.toString(year);
                newDate.setText(Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year));
                //Toast.makeText(getBaseContext(), "Your rental time is set from " + curDay + "-" + curMonth + "-" + curYear + " to " + day + "-" + (month + 1) + "-" + year + ".", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseContext(), "Please choose date after " + curDay + "-" + curMonth + "-" + curYear, Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected Dialog onCreateDialog(int id) {// create a calendar dialog
        switch(id){
            case DATE_DIALOG_ID:
                DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, reservationDate, currentYear, currentMonth, currentDay) {
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        view.setMinDate(new Date().getTime());
                    }
                };

                dialog.getDatePicker().setMinDate(new Date().getTime());// set Min date in Calendar
                return dialog;
        }
        return null;
    }


    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            alertString = charSequence.toString();
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    // checks if textBox is empty or not & Enables/Disables the button
    private  void checkFieldsForEmptyValues(){

        if(alertString.equals("")) {
            alertOKButton.setEnabled(false);
        }
        else {
            alertOKButton.setEnabled(true);
        }
    }

    //for contribution & delete goal via notification
    public void onReceive(final String delId) {
        String action = getIntent().getAction();
        if("Delete".equals(action)) {// to execute delete option
            gHelper = new DbHelperGoal(this.getBaseContext());
            dataBase = gHelper.getWritableDatabase();
            Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME + " WHERE " + DbHelperGoal.KEY_ID + "=" + delId, null);
            if (mCursor.moveToFirst()) {
                do{
                    keyIndex = Integer.valueOf(delId);
                    final String delGoal = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)), delAmount = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CURRENCY)) + " " + mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT));
                    final String delDate = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY)) + "-" + monStr.get(mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.MONTH)) - 1) + "-" + mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR));
                    build = new AlertDialog.Builder(GoalDisActivity.this);
                    build.setTitle("Delete " + delGoal + " " + delDate + "of amount " + delAmount);
                    build.setMessage("Do you want to delete ?");
                    build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), delGoal + " " + delDate + " of amount " + delAmount + " " + " is deleted.", Toast.LENGTH_LONG).show();
                            dataBase.delete(DbHelperGoal.TABLE_NAME, DbHelperGoal.KEY_ID + "=" + keyIndex, null);
                            myGoalNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            myGoalNotifyMgr.cancel(keyIndex);
                            displayData();
                            dialog.cancel();
                        }
                    });
                    build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert = build.create();
                    alert.show();
                }while (mCursor.moveToNext());
            }else{
                build = new AlertDialog.Builder(GoalDisActivity.this);
                build.setMessage("Sorry, the data might have been moved or deleted.");
                build.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert = build.create();
                alert.show();
            }
        } else if("Pay".equals(action)){
            LayoutInflater li = LayoutInflater.from(GoalDisActivity.this);
            View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
            build = new AlertDialog.Builder(GoalDisActivity.this);
            build.setTitle("Payment");
            build.setMessage("Please Enter payment amount");
            build.setView(promptsPaymentView);
            PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
            PayValue.setFocusableInTouchMode(true);
            PayValue.setFocusable(true);
            PayValue.requestFocus();
            build.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    check = 0; val = 0;
                    moneyValue = "0"; dbExpAmount = "0";

                    dataBase = gHelper.getWritableDatabase();
                    Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME+" WHERE "+DbHelperGoal.KEY_ID+" = "+ delId , null);
                    if (mCursor.moveToFirst()) {
                        do {
                            moneyValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT));
                            dbExpAmount = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT));
                            check = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE));
                        } while (mCursor.moveToNext());
                        Toast.makeText(getApplication(), "Money Paid:" + moneyValue, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplication(), "Money not Paid:" + delId, Toast.LENGTH_LONG).show();
                    }
                    val = Float.valueOf(PayValue.getText().toString())+ Float.valueOf(moneyValue);
                    if(val-check <= Float.valueOf(dbExpAmount) && val-check >= 0) {// within the Target Amount
                        String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + delId;
                        dataBase.execSQL(strSQL);
                        ContentValues values = new ContentValues();
                        values.put(DbHelperGoal.ALT_PAYMENT, String.valueOf(val));
                        Toast.makeText(getApplication(), PayValue.getText().toString() + " added", Toast.LENGTH_SHORT).show();
                        myGoalNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        myGoalNotifyMgr.cancel(keyIndex);
                        displayData();
                        dialog.cancel();
                    }else if(val-check > Float.valueOf(dbExpAmount) && val-check >= 0){// if client collects extra amount for that goal, the Target amount extends
                        build2 = new AlertDialog.Builder(GoalDisActivity.this);
                        build2.setTitle("Confirmation");
                        build2.setMessage("The contribution is exceeding the target amount. Do you want to increment the target amount to the new value?");
                        build2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.AMOUNT + "=" + String.valueOf(val - check) + "," + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + delId;
                                dataBase.execSQL(strSQL);
                                Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                myGoalNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                myGoalNotifyMgr.cancel(keyIndex);
                                displayData();
                                dialog.cancel();
                            }
                        });
                        build2.setNeutralButton("No, only Savings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + delId;
                                dataBase.execSQL(strSQL);
                                Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                myGoalNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                myGoalNotifyMgr.cancel(keyIndex);
                                displayData();
                                dialog.cancel();
                            }
                        });

                        build2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                        alert2 = build2.create();
                        alert2.show();
                        alert2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                    }else{
                        Toast.makeText(getApplication(),"Sorry, the amount is beyond the Target Amount", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });
            alert = build.create();
            alert.show();
            alertOKButton = alert.getButton(AlertDialog.BUTTON1);
            alertOKButton.setEnabled(false);
            PayValue.addTextChangedListener(textWatcher);
            alertString = PayValue.getText().toString();
            alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else if("Extend".equals(action)) {
            Toast.makeText(getApplicationContext(), "Extend", Toast.LENGTH_LONG).show();
            onUpdate(delId);
        }
    }

    // for Goal date extension
    public void onUpdate(final String updateId) {
        LayoutInflater ld = LayoutInflater.from(GoalDisActivity.this);
        View promptsDateView = ld.inflate(R.layout.date_button, null);
        build = new AlertDialog.Builder(GoalDisActivity.this);
        build.setTitle("Goal-Date");
        build.setMessage("Please Enter new Goal Date");
        build.setView(promptsDateView);
        newDate = (Button) promptsDateView.findViewById(R.id.newGoalDate);
        newDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);

        newDate.setFocusableInTouchMode(true);
        build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dataBase = gHelper.getWritableDatabase();
                Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME + " WHERE " + DbHelperGoal.KEY_ID + "=" + updateId, null);
                if (mCursor.moveToFirst()) {
                    do {
                        dayValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY));
                    } while (mCursor.moveToNext());
                }

                if (dayG != null && !dayG.isEmpty() && monthG != null && !monthG.isEmpty() && yearG != null && !yearG.isEmpty()) {
                    int dayVal = Integer.valueOf(dayG), monthVal = Integer.valueOf(monthG), yearVal = Integer.valueOf(yearG);
                    String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.DAY + "=" + String.valueOf(dayVal) + ", " + DbHelperGoal.MONTH + "=" + String.valueOf(monthVal) + ", "+ DbHelperGoal.YEAR + "=" + String.valueOf(yearVal) + " WHERE " + DbHelperGoal.KEY_ID + "=" + updateId;
                    dataBase.execSQL(strSQL);
                    displayData();
                    dialog.cancel();
                }else{
                    displayData();
                    Toast.makeText(getApplicationContext(),"Please select a date", Toast.LENGTH_SHORT).show();
                }
            }
        });
        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "New Date Cancelled", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        alert = build.create();
        alert.show();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    // for contribution to Goal
    public void onContribute(final String payId) {
        LayoutInflater li = LayoutInflater.from(GoalDisActivity.this);
        View promptsPaymentView = li.inflate(R.layout.payment_layout, null);
        build = new AlertDialog.Builder(GoalDisActivity.this);
        build.setTitle("Payment");
        build.setMessage("Please Enter payment amount");
        build.setView(promptsPaymentView);
        PayValue = (EditText) promptsPaymentView.findViewById(R.id.PaymentEnter1);
        PayValue.setFocusableInTouchMode(true);
        PayValue.setFocusable(true);
        PayValue.requestFocus();
        build.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                check = 0; val = 0;
                moneyValue = "0";
                dataBase = gHelper.getWritableDatabase();
                Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME+" WHERE "+DbHelperGoal.KEY_ID+"="+payId, null);
                if (mCursor.moveToFirst()) {
                    do {
                        moneyValue = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT));
                        dbExpAmount = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT));
                        check = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE));
                    } while (mCursor.moveToNext());
                }
                val = Float.valueOf(PayValue.getText().toString())+ Float.valueOf(moneyValue);
                if(val-check <= Float.valueOf(dbExpAmount) && val-check >= 0) {// within the Target Amount
                    String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + payId;
                    dataBase.execSQL(strSQL);
                    Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                    displayData();
                    dialog.cancel();
                }else if(val-check > Float.valueOf(dbExpAmount) && val-check >= 0){// if client collects extra amount for that goal, the Target amount extends
                    build2 = new AlertDialog.Builder(GoalDisActivity.this);
                    build2.setTitle("Confirmation");
                    build2.setMessage("The Payment Amount is exceeding the Target Amount. Do you want to increment the Target amount to the new value?");
                    build2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.AMOUNT + "=" + String.valueOf(val - check) + "," + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + payId;
                            dataBase.execSQL(strSQL);
                            Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                            displayData();
                            dialog.cancel();
                        }
                    });
                    build2.setNeutralButton("No, only Savings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String strSQL = "UPDATE " + DbHelperGoal.TABLE_NAME + " SET " + DbHelperGoal.ALT_PAYMENT + "=" + String.valueOf(val) + " WHERE " + DbHelperGoal.KEY_ID + "=" + payId;
                            dataBase.execSQL(strSQL);
                            Toast.makeText(getApplication(), PayValue.getText().toString(), Toast.LENGTH_SHORT).show();
                            displayData();
                            dialog.cancel();
                        }
                    });

                    build2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    alert2 = build2.create();
                    alert2.show();
                    alert2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                }else{
                    Toast.makeText(getApplication(),"Sorry, the amount is beyond the Target Amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        alert = build.create();
        alert.show();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    //days_of_months(Jan,Feb,Mar,April,May,June,July,Aug,Sept,Oct,Nov,Dec)
    ArrayList<Integer> mon = new ArrayList<Integer>(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));
    ArrayList<String> monStr = new ArrayList<String>(Arrays.asList("Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"));

    // calculates days from months
    int calMonthDay(int m,int y){//calMonthDay(month,year)
        int x=0,c;
        for(c = 1; c < m; c++) {// Jan to less than the month 'm' as 'm' we are not taking the the whole days of that month
            if(c == 2) {//if Feb
                if(y%4 == 0)//checks if year is leap or not
                    x += 29;
                else
                    x += 28;
            }
            else
                x += mon.get(c-1);
        }
        return(x);
    }

    //calculates no. of months from current month & year to goal month & year
    int calDateMonth(int mC,int yC,int mG,int yG){//(current-month, current-year, goal-month, goal-year)
        int x = 0,i,countM=0;
        if(yC<=yG){
            for(i = yC; i < yG; i++)
                countM += 12;
        }

        countM -= mC;
        countM += mG;
        return (countM);
    }

    //calculates no. of weeks from current month & year to goal month & year
    int calDateWeek(int mC,int yC,int mG,int yG){
        int x = 0,i,countW=0;
        if(yC<=yG){
            for(i = yC; i < yG; i++)
                countW+=52;
        }

        countW -= mC;
        countW += mG;
        countW *= 4;
        return (countW);
    }

    private void displayData() {
        dataBase = gHelper.getWritableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME, null);

        keyId.clear();
        goalTitle.clear();
        date.clear();
        daysLeftGoal.clear();
        amount.clear();
        progressValue.clear();

        // to avoid duplicate notifications
        notifyTitle.clear();
        notifyId.clear();
        notifyLastDay.clear();
        // to clear all previous notifications
        myGoalNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myGoalNotifyMgr.cancelAll();

        if (mCursor.moveToFirst()) {
            do {
                keyId.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                goalTitle.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)));
                day.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY)));
                month.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.MONTH)));
                year.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR)));

                //display date using day/month/year
                date.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY))+"-"+monStr.get(mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.MONTH)) - 1)+"-"+mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR)));

                //mProgress = (ProgressBar) findViewById(R.id.progressBarGoal);
                float addSavings = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_PAYMENT)) - mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE));//Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.ALT_EXPENSE)));
                float calSavings = Float.valueOf(addSavings) / Float.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT)))*100;
                progressValue.add((int) calSavings);

                float amountLeftPay = Float.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.AMOUNT))) - addSavings;
                //Displays goal amount && currency
                if(amountLeftPay >= 0 )
                    amount.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CURRENCY))+" "+String.format("%.0f",amountLeftPay)+" of "+ String.format("%.0f",mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.AMOUNT))));
                else {
                    amountLeftPay = Math.abs(amountLeftPay);
                    amount.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CURRENCY)) + " " + String.format("%.0f", amountLeftPay) + " extra");
                }

                //Calculate the days & amount per day/week/month
                final Calendar c = Calendar.getInstance();
                int curYear = c.get(Calendar.YEAR), curMonth = c.get(Calendar.MONTH)+1, curDay = c.get(Calendar.DAY_OF_MONTH);
                int goalYear = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.YEAR)), goalMonth = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.MONTH)), goalDay = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.DAY));
                int calYear = 0,calMonth = 0,calDay = 0,calDayGoal = 0;
                float dailyAmount = 0;

                //Get current date
                String curDate = String.valueOf(curDay)+"/"+String.valueOf(curMonth)+"/"+String.valueOf(curYear);
                //String goalDate=String.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DATE)));

                //Get goal date
                //String goalDate = String.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY)))+"/"+String.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.MONTH)))+"/"+String.valueOf(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR)));
                String goalDate = String.valueOf(goalDay)+"/"+String.valueOf(goalMonth)+"/"+String.valueOf(goalYear);
                int count = -1;

                //Fetches the date and Time from system, hence not used
                if(curYear < goalYear || (goalYear==curYear && goalMonth>curMonth)||(goalYear==curYear && goalMonth==curMonth && goalDay>curDay)) {
                    count = 0;
                    int i;
                    for (i = curYear; i < goalYear; i++) {
                        if (i % 4 == 0) {
                            count += 366;//Leap year
                        } else {
                            count += 365;// Non leap year
                        }
                    }
                    //calculating the no of days left from current date to goal date
                    count -= calMonthDay(curMonth, curYear);
                    count -= curDay;
                    count += calMonthDay(goalMonth, goalYear);
                    count += goalDay;
                    if (count < 0) {
                        count *= -1;
                    }
                    // amount divided as per date
                    dailyAmount = mCursor.getFloat(mCursor.getColumnIndex(DbHelperGoal.AMOUNT)) / count;

                    if(count != 1)
                        daysLeftGoal.add(String.valueOf(count)+" days left");
                    else
                        daysLeftGoal.add(String.valueOf(count)+" day left");
                } else {// current year exceeds goal year
                    daysLeftGoal.add("Times up");
                }

                // For notification
                // check if goal date is less than or equals 2 days
                if(count <= 7){// && count >= 0) {
                    //Toast.makeText(getApplicationContext(),mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)),Toast.LENGTH_LONG).show();
                    notifyLastDay.add(count);
                    notifyTitle.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)));
                    notifyId.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                }
            } while (mCursor.moveToNext());
        }

        goaladpt = new GoalAdapter(GoalDisActivity.this,keyId, goalTitle, day, month, year, date, amount,daysLeftGoal,progressValue);// dailyBreak,weeklyBreak,monthlyBreak,daysLeftGoal);
        goalList2.setAdapter(goaladpt);
        mCursor.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goal_dis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.calculator:
                startActivity(new Intent(this,Calc.class));
                return true;
            case R.id.categoriesNewGD:
                LayoutInflater li = LayoutInflater.from(GoalDisActivity.this);
                View promptsCategoryView = li.inflate(R.layout.category_layout, null);
                build = new AlertDialog.Builder(GoalDisActivity.this);
                build.setTitle("New Category");
                build.setMessage("Please Enter new Category");
                build.setView(promptsCategoryView);
                CatgyValue = (EditText) promptsCategoryView.findViewById(R.id.CategoryEnter1);
                CatgyValue.setFocusableInTouchMode(true);
                CatgyValue.setFocusable(true);
                CatgyValue.requestFocus();
                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (CatgyValue.getText().toString().isEmpty() || CatgyValue.getText().toString().equals(" ")) {// check is textbox is empty or Not
                            Toast.makeText(getApplicationContext(), "Enter a category and click", Toast.LENGTH_LONG).show();
                        } else {
                            dataBase = cHelper.getWritableDatabase();
                            Cursor gCursor;
                            String[] catSet = CatgyValue.getText().toString().split(" ");
                            String str = "";
                            for (int i = 0; i < catSet.length; i++) {
                                if (!catSet[i].equals(" ") && !catSet[i].equals("")) {
                                    str += Character.toUpperCase(catSet[i].charAt(0)) + catSet[i].substring(1).toLowerCase();//catSet[i].charAt(0);
                                    if (i < catSet.length - 1)
                                        str += " ";
                                }
                            }

                            if (str.contains(" And "))
                                str = str.replace(" And ", " & ");

                            if (Build.VERSION.SDK_INT > 15) {
                                gCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME + " WHERE " + DbHelperCategory.CAT_TYPE + "=?", new String[]{str}, null);
                            } else {
                                gCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME, null);
                            }
                            int catgyFlag = 0;
                            if (gCursor.getCount() > 0) {
                                catgyFlag = 1;
                            }
                            //gCursor.close();
                            //dataBase.close();
                            if (catgyFlag == 1) {
                                Toast.makeText(getApplicationContext(), "Category already present", Toast.LENGTH_LONG).show();
                                gCursor.close();
                                dataBase.close();
                            } else {
                                ContentValues values = new ContentValues();
                                values.put(DbHelperCategory.CAT_TYPE, str);
                                dataBase.insert(DbHelperCategory.TABLE_NAME, null, values);
                                dataBase.close();
                                //setContentView(R.layout.activity_goal);
                                //categoryFunc();// to update the Category Spinner
                                Toast.makeText(getApplication(), CatgyValue.getText().toString(), Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }
                    }
                });

                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "New Category Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                alert = build.create();
                alert.show();
                alertOKButton = alert.getButton(AlertDialog.BUTTON1);
                alertOKButton.setEnabled(false);
                CatgyValue.addTextChangedListener(textWatcher);
                alertString = CatgyValue.getText().toString();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}