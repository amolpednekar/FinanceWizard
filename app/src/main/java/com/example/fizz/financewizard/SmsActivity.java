package com.example.fizz.financewizard;

import android.app.AlertDialog;
import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SmsActivity extends AppCompatActivity implements OnItemClickListener {

    private static SmsActivity inst;
    SmsDisAdapter smsDisAdapt;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    /*DATABASE HANDLES*/
    final DatabaseHandler db = new DatabaseHandler(this);
    String smsMessage = "", smsMessageStr = "", mAmount = "", smsAccNo = "", ReadAcc = "", categoryG, textAmount = "", leftAmount = "";
    String mAmount2 = "", balanceTemp = "";
    public int StartApp=0, balA;
    /*BANK SMS ADDRESSES*/
    public static int NoBank = 6;
    public static String stringArray[] = {"BP-ATMSBI","BZ-ATMSBI","BX-ATMSBI","VK-CorpBK","VM-BOIIND","VK-BOIIND","VM-CBSSBI"};
    public static String bankNames[]={"State Bank Of India","State Bank Of India","State Bank Of India","Cooporative Bank","Bank of India","Bank of India","State Bank of India"};
    // public static String stringArray[] = {/*"8451043280", */"VM-HDFCBK", "VM-BOIIND", "BP-SBIMBS", "AM-HDFCBK", "VM-UnionB", "VM-UIICHO", "VM-CBSSBI", "VM-CorpBk", "VL-CENTBK", "VM-CENTBK", "BW-PNBSMS","VK-BOIIND","VM-CBSSBI","VM-BOIIND","BZ-ATMSBI","VK-AxisBk"};
    /*ACCOUNT NUMBER*/
    public ArrayList<String> accountNumbers = new ArrayList<String>();
    public ArrayList<String> accounT = new ArrayList<String>(), bankNameS = new ArrayList<String>(), totaL = new ArrayList<String>();
    public int accountI = 0,ft;
    public String PushTime;
    /*CALENDER*/
    Calendar calendar = Calendar.getInstance();
    //Alertbox
    AlertDialog.Builder build;
    EditText transAmount;
    Spinner spinnerCat;
    private DbHelperCategory cHelper;
    private SQLiteDatabase tDataBase, cDataBase;
    AlertDialog alert;
    String[] defaultCat = {"Lifestyle","Entertainment","Misc."};
    int catgyFlag;

    public static SmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        cHelper = new DbHelperCategory(this);
        cDataBase = cHelper.getWritableDatabase();

        // populate category Database
        Cursor gCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME, null);
        String dbData = null;

        catgyFlag = 0;

        if(gCursor.getCount() > 0)// checks if there is categories in database
            catgyFlag = 1;

        if(catgyFlag == 0){ // if empty, populate the default category lists
            ContentValues values = new ContentValues();
            for(int x = 0; x < defaultCat.length; x++){
                values.put(DbHelperCategory.CAT_TYPE, defaultCat[x]);
                cDataBase.insert(DbHelperCategory.TABLE_NAME, null, values);
            }
            cDataBase.close();
        }



            /*
            /* Button for Database
            Button But1 = (Button) findViewById(R.id.buttonDb);
            But1.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> Val = db.getAllvalues();
                    arrayAdapter.clear();
                    for (int i = 0; i < Val.size(); i++) {
                        arrayAdapter.add(Val.get(i));
                    }
                }
            });

            /* Button for Messages
            Button But2 = (Button) findViewById(R.id.messages);
            But2.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshSmsInbox();
                }
            });
            */
        /*Button But3 = (Button) findViewById(R.id.submit);
        But3.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For selected account number
                arrayAdapter.clear();
                //for (int j = 0; j < accountI; j++) {
                //  ArrayList<String> Val = db.Selected(accountNumbers.get(j));
                ArrayList<String> Val = db.getAllvalues();
                for (int i = 0; i < Val.size(); i++) {
                    String temp=Val.get(i);
                    int a=temp.indexOf("|");
                    String index="";
                    String Data="",BankName="";
                    index+=temp.substring(a+1);
                    BankName+=bankNames[Integer.parseInt(index)];
                    Data+=temp.substring(0,a);
                    String Disp=BankName+ "\n"+Data;
                    arrayAdapter.add(Disp);
                }
                //}
            }
        });
*/
        smsListView = (ListView) findViewById(R.id.SMSList);
        // default adapter
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        //smsListView.setAdapter(arrayAdapter);
        //arrayAdapter = new ArrayAdapter<String>(this, R.layout.sms_cards, smsMessagesList);
        //smsListView.setOnItemClickListener(this);

        ArrayList<String> ValMain = db.getAllvalues();
        for (int i = 0; i < ValMain.size(); i++) {
            String[] tempContent = ValMain.get(i).split("\n");
            accounT.add(tempContent[1]);

            //String[] tempContent2 = tempContent[tempContent.length - 1].split("|");
            bankNameS.add(bankNames[Integer.valueOf(tempContent[tempContent.length - 1].substring(tempContent[tempContent.length - 1].indexOf('|') + 1))]);
            totaL.add(tempContent[tempContent.length - 1].substring( 0 , tempContent[tempContent.length - 1].indexOf('|')));
        }

        //custom adapter
        smsDisAdapt = new SmsDisAdapter(SmsActivity.this, accounT, bankNameS, totaL);// format of adapter
        smsListView.setAdapter(smsDisAdapt);// set adapter to the list

        // on account list click
        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                String[] smsMessages = accounT.get(arg2).split(" ");
                String ClickedItem = "", Acc = "";
                int a = 0, b = 0;

                for (int i = 1; i < smsMessages.length; ++i) {
                    ClickedItem += smsMessages[i];
                }
                setContentView(R.layout.table_view);
                a = ClickedItem.indexOf(" ");
                b = ClickedItem.indexOf(" ", a + 2);
                Acc += smsMessages[1];
                Toast.makeText(getApplicationContext(), "Acc : |-" + Acc + "=>" + String.valueOf(arg2) + "|", Toast.LENGTH_SHORT).show();

                init(arg2, Acc);
                /*Intent i = new Intent(getApplicationContext(), SmsActivity.class);
                startActivity(i);*/
            }
        });

        ArrayList<String> Val = db.Selected2();
        if(Val.size()==0) {
            db.AddFirstDate("0000000000000");
            StartApp=1;
        }
        Val=db.Select4();
        for(int j=0;j<Val.size();j++) {
            accountNumbers.add(Val.get(j));
            accountI++;
        }

        refreshSmsInbox();// for sms parsing
        ft=0;
        db.UpdateDate(PushTime);
        StartApp=0;
        //arrayAdapter.clear();
        //for (int j = 0; j < accountI; j++) {
        //   Val = db.Selected(accountNumbers.get(j));
        Val = db.getAllvalues();
        for (int i = 0; i < Val.size(); i++) {
            String temp=Val.get(i);
            int a=temp.indexOf("|");
            String index="";
            String Data="",BankName="";
            index+=temp.substring(a+1);
            BankName+=bankNames[Integer.parseInt(index)];
            Data+=temp.substring(0,a);
            String Disp=BankName+ "\n"+Data;
            //arrayAdapter.add(Disp);
        }
        // }
    }

    //table
    public void init(int pos, String AccountSearch) { // convert to tableview & display info of that account
        TableLayout stk = (TableLayout) findViewById(R.id.tableView);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView tv0 = new TextView(this);
        tv0.setText("Sr.No");
        tv0.setTextSize(12);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Status");
        tv1.setTextSize(12);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Amount");
        tv2.setTextSize(12);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("TimeStamp");
        tv3.setTextSize(12);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText("  ");
        tv4.setTextColor(Color.BLACK);
        tbrow0.addView(tv4);
        stk.addView(tbrow0);
        //String[] smsAcc = AccountSearch.split("\n");
        //Toast.makeText(getApplicationContext(),String.valueOf(pos),Toast.LENGTH_LONG).show();
        ArrayList<String> Val = db.Selected3(AccountSearch);
        //arrayAdapter.clear();
        //smsMessagesList.clear();
        //Toast.makeText(getApplicationContext(),String.valueOf(Val.size()),Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),Val.get(0),Toast.LENGTH_LONG).show();
        for (int i = 0; i < Val.size(); i++) {
            //arrayAdapter.add(Val.get(i));
            String[] smsInfo = Val.get(i).split(" ");
            TableRow tbrow = new TableRow(this);
            tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(this);
            t1v.setText(smsInfo[1]);
            t1v.setTextSize(12);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText("  " + smsInfo[2]);
            t2v.setTextSize(12);
            t2v.setGravity(Gravity.LEFT);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            TextView t4v = new TextView(this);
            TextView t5v = new TextView(this);
            if(smsInfo[2].equals("Debited")) {
                t2v.setTextColor(Color.WHITE);
                t2v.setBackgroundColor(Color.rgb(245,69,89));
                t1v.setBackgroundColor(Color.rgb(245,69,89));
                t3v.setBackgroundColor(Color.rgb(245,69,89));
                t4v.setBackgroundColor(Color.rgb(245,69,89));
                t5v.setBackgroundColor(Color.rgb(245,69,89));
            }
            else {
                t2v.setTextColor(Color.WHITE);
                t2v.setBackgroundColor(Color.rgb(54,201,72));
                t1v.setBackgroundColor(Color.rgb(54,201,72));
                t3v.setBackgroundColor(Color.rgb(54,201,72));
                t4v.setBackgroundColor(Color.rgb(54,201,72));
                t5v.setBackgroundColor(Color.rgb(54,201,72));
            }

            t3v.setText("Rs. " + smsInfo[3]);
            t3v.setTextSize(12);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.LEFT);
            tbrow.addView(t3v);

            t4v.setText( smsInfo[4] + "-" + smsInfo[5] );
            t4v.setTextSize(12);
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.LEFT);
            tbrow.addView(t4v);

            t5v.setText(" " + "  ");
            t5v.setTextColor(Color.WHITE);
            t5v.setGravity(Gravity.LEFT);
            tbrow.addView(t5v);
            stk.addView(tbrow);
            //smsMessagesList.add(Val.get(i));
        }
        try {
            //Toast.makeText(getApplicationContext(),smsMessagesList.get(0),Toast.LENGTH_LONG).show();
            //String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String ClickedItem="",Acc="";
            String _id,amo,time,sta,temp;
            int a=0,b=0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sms parser
    public void refreshSmsInbox() { // parsing the sms
        String Balance="";
        int balA=0;
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        //arrayAdapter.clear();
        int i;
        int a, b;
        do {
            smsMessage = "";
            smsAccNo = "";
            String month="",year="";
            float DB,SMS;
            String strAddress = smsInboxCursor.getString(indexAddress);
            //String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
            //        "\n" + smsInboxCursor.getString(indexBody).toLowerCase() + "\n";
            String Time = smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"));
            Long timestamp = Long.parseLong(Time);
            calendar.setTimeInMillis(timestamp);

            Date finalDate = calendar.getTime();
            String smsDate = finalDate.toString();
            month+=smsDate.substring(4,7);
            year+=smsDate.substring(30,34);
            //str+="Date : "+smsDate+"\nTimeStamp : "+Time;

            SMS=Float.parseFloat(Time);
            smsMessage += smsInboxCursor.getString(indexBody).toLowerCase();
            if(ft==0){
                ft=1;
                PushTime=Time;
            }
            for (i = 0; i <= NoBank; i++) {
                if (stringArray[i].equalsIgnoreCase(strAddress)) {
                    ////////
                    //arrayAdapter.add(str);

                    //SearchForAccountNumber
                    if (smsMessage.contains("a/c no.")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 7, b);
                    } else if (smsMessage.contains("a/c")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a +9);
                        smsAccNo += smsMessage.substring(a + 4, b);
                    } else if (smsMessage.contains("account number")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 15);
                        smsAccNo += smsMessage.substring(a + 15, b);
                    } else if (smsMessage.contains("account")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 8, b);
                    } else if (smsMessage.contains("ac")) {
                        a = smsMessage.indexOf("ac");
                        b = smsMessage.indexOf(" ", a + 3);
                        smsAccNo += smsMessage.substring(a + 3, b);
                    }
                    String smsAccNo2=smsAccNo.replace("x","");
                    smsAccNo=smsAccNo2.replace(" ","");
                    char AA='a';
                    for(int d=0;d<26;d++)
                    {
                        smsAccNo2=smsAccNo.replace(String.valueOf(AA),"");
                        smsAccNo=smsAccNo2;
                        AA++;
                    }
                    int found = 0;
                    String Temp = "";
                    for (int j = 0; j < accountI; j++) {
                        Temp = accountNumbers.get(j);
                        if (Temp.contains(smsAccNo)) {
                            found = 1;
                            break;
                        }
                        else if(smsAccNo.contains(Temp)){
                            found = 1;
                            break;
                        }
                    }

                    if (found != 1) {
                        accountNumbers.add(smsAccNo);
                        db.firstAdd(smsAccNo,i);
                        accountI++;
                    }

                    if(StartApp==1) {
                        AddEntry(smsMessage,month+" "+year);
                    }
                    else {
                        ArrayList<String> Val = db.Selected2();
                        String S1 = Val.get(Val.size() - 1);
                        DB = Float.parseFloat(S1);
                        if (SMS > DB) {
                            AddEntry(smsMessage,month+" "+year);
                        }
                    }
                    break;
                }
            }
        } while (smsInboxCursor.moveToNext());
    }


    public void updateList(final String smsMessage, final String strAddress2) {
        /*int i;
        for (i = 0; i <= NoBank; i++) {
            if (stringArray[i].equalsIgnoreCase(strAddress2)) {

                Toast.makeText(this,"smsMessage : "+smsMessage +" add : " + strAddress2,Toast.LENGTH_SHORT);

                break;
            }
        }*/
    }

    public void AddEntry(String Message,String Time) {
        int a = 0, b = 0;
        smsMessage = "";
        smsMessageStr = "";
        String Category = "";
        mAmount = "";
        smsAccNo = "";
        String Balance = "";
        balA = 0;
        smsMessage += Message;

        //Status
        if (smsMessage.contains("credited")) {
            smsMessageStr += "Credited";
        } else if (smsMessage.contains("debited") || smsMessage.contains("withdraw") || smsMessage.contains("withdrawal") || smsMessage.contains("deducted")) {
            smsMessageStr += "Debited";
        }

        //AccountNumber
        if (smsMessage.contains("a/c no.")) {
            a = smsMessage.indexOf("a/c");
            b = smsMessage.indexOf(" ", a + 8);
            smsAccNo += smsMessage.substring(a + 7, b);
        } else if (smsMessage.contains("a/c")) {
            a = smsMessage.indexOf("a/c");
            b = smsMessage.indexOf(" ", a + 9);
            smsAccNo += smsMessage.substring(a + 4, b);
        } else if (smsMessage.contains("account number")) {
            a = smsMessage.indexOf("account");
            b = smsMessage.indexOf(" ", a + 15);
            smsAccNo += smsMessage.substring(a + 15, b);
        } else if (smsMessage.contains("account")) {
            a = smsMessage.indexOf("account");
            b = smsMessage.indexOf(" ", a + 8);
            smsAccNo += smsMessage.substring(a + 8, b);
        } else if (smsMessage.contains("ac")) {
            a = smsMessage.indexOf("ac");
            b = smsMessage.indexOf(" ", a + 3);
            smsAccNo += smsMessage.substring(a + 3, b);
        }
        String smsAccNo2=smsAccNo.replace("x","");
        smsAccNo=smsAccNo2.replace(" ","");
        char AA='a';
        for(int d=0;d<26;d++)
        {
            smsAccNo2=smsAccNo.replace(String.valueOf(AA),"");
            smsAccNo=smsAccNo2;
            AA++;
        }

        //Amount
        if (smsMessage.contains("rs.")) {
            a = smsMessage.indexOf("rs.");
            b = smsMessage.indexOf(" ", a + 4);
            mAmount += smsMessage.substring(a + 4, b);
        } else if (smsMessage.contains("rs")) {
            a = smsMessage.indexOf("rs");
            b = smsMessage.indexOf(" ", a + 4);
            if(b<0){b=smsMessage.indexOf(".",a+4);}
            mAmount += smsMessage.substring(a + 3, b);
        } else if (smsMessage.contains("inr")) {
            a = smsMessage.indexOf("inr");
            b = smsMessage.indexOf(" ", a + 4);
            mAmount += "1234";//smsMessage.substring(a + 4, b);
        }

        //Balance
        if (smsMessage.contains("balance ")) {
            a = smsMessage.indexOf("balance ");
            b = smsMessage.indexOf(".", a + 8);
            Balance += smsMessage.substring(a + 8, b + 2);
            balA = 1;
        }

        mAmount2 = mAmount.replace(",", "");
        if (smsMessageStr.length() != 0 && smsAccNo.length() != 0 && mAmount2.length() != 0) {
            /*
            ------------------1---------------------------
            */

            db.add(smsMessageStr, smsAccNo, mAmount2, Time, "Misc.");

            ArrayList<String> Val=db.Select4();
            int j;
            for(j=0;j<Val.size();j++) {
                String Temp=Val.get(j);
                if(Temp.contains(smsAccNo2)) {
                    //Toast.makeText(this,"Status : "+smsMessageStr+"\n Acc : |"+Temp+"|\n Amo : "+mAmount2,Toast.LENGTH_LONG).show();
                    db.Bank(smsMessageStr, Temp, mAmount2);
                    break;
                }
                else if(smsAccNo2.contains(Temp)) {
                    //Toast.makeText(this,"Status : "+smsMessageStr+"\n Acc : |"+Temp+"|\n Amo : "+mAmount2,Toast.LENGTH_LONG).show();
                    db.Bank(smsMessageStr, Temp, mAmount2);
                    break;
                }
            }
            if(Val.size()==j) {
                //Toast.makeText(this,"Status : "+smsMessageStr+"\n Acc : "+smsAccNo+"\n Amo : "+mAmount2,Toast.LENGTH_LONG).show();
                db.Bank(smsMessageStr, smsAccNo, mAmount2);
            }
            if (balA == 1) {
                //Toast.makeText(this, "Balance : " + Balance, Toast.LENGTH_SHORT).show();
                //  db.UpdateTotal(smsAccNo,Balance);
            }
        }
    }

    public void categoryFunc(){ //category DropDown
        ArrayList<Integer> catId = new ArrayList<Integer>();
        ArrayList<String> catCont = new ArrayList<String>();
        cDataBase = cHelper.getReadableDatabase();
        Cursor gCursor = cDataBase.rawQuery("SELECT * FROM "+ DbHelperCategory.TABLE_NAME, null);

        catId.add(-1);
        catCont.add("--Select Category--");

        if(gCursor.moveToFirst()){
            do{
                catId.add(gCursor.getInt(gCursor.getColumnIndex(DbHelperCategory.KEY_ID)));
                catCont.add(gCursor.getString(gCursor.getColumnIndex(DbHelperCategory.CAT_TYPE)));
            }while(gCursor.moveToNext());
        }
        gCursor.close();
        cDataBase.close();//close database

        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catCont);
        //SimpleCursorAdapter adapterC = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, gCursor, catCont, catId);
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapterC);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cDataBase = cHelper.getWritableDatabase();
                Cursor gCursor;
                categoryG = "";
                if (position != 0) {
                    if (Build.VERSION.SDK_INT > 15) {
                        gCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME + " WHERE " + DbHelperCategory.CAT_TYPE + "=?", new String[]{spinnerCat.getSelectedItem().toString()}, null);
                    } else {
                        gCursor = cDataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME, null);
                    }
                    //gCursor = dataBase.rawQuery("SELECT * FROM " + DbHelperCategory.TABLE_NAME + " WHERE " + DbHelperCategory.CAT_TYPE + " = " + spinnerCat.getSelectedItem().toString(), null);
                    //categoryG = "";
                    if (gCursor.moveToFirst()) {
                        do {
                            categoryG = gCursor.getString(gCursor.getColumnIndex(DbHelperCategory.CAT_TYPE));
                        } while (gCursor.moveToNext());
                    }
                    gCursor.close();
                }
                cDataBase.close();
            }
            //Spinner spinnerCat = (Spinner) findViewById(R.id.categoryDrop);

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String ClickedItem="",Acc="";
            String _id,amo,month,year,sta,temp, cat;
            int a = 0, b = 0;
            for (int i = 1; i < smsMessages.length; ++i) {
                ClickedItem += smsMessages[i];
            }
            a=ClickedItem.indexOf(" ");
            b=ClickedItem.indexOf(" ",a+2);
            Acc+=ClickedItem.substring(a+1, b);
            //Toast.makeText(this,"Acc : |"+Acc+"|",Toast.LENGTH_SHORT).show();
            ArrayList<String> Val = db.Selected3(Acc);
            arrayAdapter.clear();
            for (int i = 0; i < Val.size(); i++) {
                temp=Val.get(i);
                a=temp.indexOf(" ");
                b = temp.indexOf(" ", a + 2);
                _id = temp.substring(a + 1, b);
                a = temp.indexOf(" ", b + 2);
                sta = temp.substring(b + 1, a);
                b = temp.indexOf(" ", a + 2);
                amo = temp.substring(a + 1, b);
                a = temp.indexOf(" ", b + 2);
                month=temp.substring(b + 1, a);
                b=temp.indexOf(" ", a + 2);
                year=temp.substring(a + 1, b);
                cat=temp.substring(b+1);
                String Tm="\n|" +_id+ "|\n|" + sta + "|\n|" + amo + "|\n|" + month + "|" + year + "|\n|" + cat + "|";
                arrayAdapter.add(Tm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}