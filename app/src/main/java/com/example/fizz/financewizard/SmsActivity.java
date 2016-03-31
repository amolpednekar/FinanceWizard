package com.example.fizz.financewizard;

import android.app.AlertDialog;
import android.content.ContentResolver;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ArrayList<String> accounT = new ArrayList<String>();
    ArrayList<String> totaL = new ArrayList<String>();
    //ArrayList<String> smMessageAcc = new ArrayList<String>(), smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    /*DATABASE HANDLES*/
    final DatabaseHandler db = new DatabaseHandler(this);
    String smsMessage = "", smsMessageStr = "", mAmount = "", smsAccNo = "", ReadAcc = "";
    public int StartApp=0;
    /*BANK SMS ADDRESSES*/
    EditText transAmount;
    DbHelperCategory cHelper;
    AlertDialog alert;
    AlertDialog.Builder build;
    public static int NoBank = 15;
    public ListView accountList2;
    SmsInfoAdapter smsInfoAdapt;
    //public static String stringArray[] = {/*"8451043280", "VM-HDFCBK", "VM-BOIIND", "BP-SBIMBS", "BP-ATMSBI", "AM-HDFCBK", "VM-UnionB", "VM-UIICHO", "VM-CBSSBI", "VM-CorpBk", "VL-CENTBK", "VM-CENTBK", "BW-PNBSMS","BZ-ATMSBI","BP-ATMSBI","BX-ATMSBI"*/"VK-BOIIND","VM-BOIIND"};/*,"VM-CBSSBI"};/*,"",,};*/
    /*ACCOUNT NUMBER*/
    public static String stringArray[] = {/*"8451043280",*/ "VM-HDFCBK", "VM-BOIIND", "BP-SBIMBS", "AM-HDFCBK", "VM-UnionB", "VM-UIICHO", "VM-CBSSBI", "VM-CorpBk", "VL-CENTBK", "VM-CENTBK", "BW-PNBSMS","VK-BOIIND","VM-CBSSBI","VM-BOIIND","BZ-ATMSBI","VK-AxisBk"};
    public ArrayList<String> accountNumbers = new ArrayList<String>();
    public int accountI = 0,ft;
    public String PushTime;
    /*CALENDER*/
    Calendar calendar = Calendar.getInstance();


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

        smsListView = (ListView) findViewById(R.id.SMSList);
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        //smsListView.setAdapter(arrayAdapter);
        //arrayAdapter = new ArrayAdapter<String>(this, R.layout.sms_cards, smsMessagesList);
        //smsListView.setOnItemClickListener(this);
        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                String[] smsMessages = accounT.get(arg2).split("\n");
                String ClickedItem = "", Acc = "";
                int a = 0, b = 0;

                for (int i = 1; i < smsMessages.length; ++i) {
                    ClickedItem += smsMessages[i];
                }
                setContentView(R.layout.table_view);
                a = ClickedItem.indexOf(" ");
                b = ClickedItem.indexOf(" ", a + 2);
                Acc += accounT.get(arg2);
                Toast.makeText(getApplicationContext(), "Acc : |" + Acc + "|", Toast.LENGTH_SHORT).show();

                init(arg2, Acc);
                /*Intent i = new Intent(getApplicationContext(), SmsActivity.class);
                startActivity(i);*/
            }
        });

        ArrayList<String> Val = db.Selected2();
        if(Val.size()==0)
        {
            db.AddFirstDate("0000000000000");
            StartApp=1;
        }
        Val=db.Select4();
        for(int j=0;j<Val.size();j++) {
            accountNumbers.add(Val.get(j));
            accountI++;
        }
        refreshSmsInbox();
        ft=0;
        db.UpdateDate(PushTime);
        StartApp=0;
        //arrayAdapter.clear();
        //arrayAdapter.clear();

        //for (int j = 0; j < accountI; j++) {
        //   Val = db.Selected(accountNumbers.get(j));
        Val = db.getAllvalues();
        int a,b;
        for (int i = 0; i < Val.size(); i++) {
            //arrayAdapter.add(Val.get(i));
            String Temp = Val.get(i);
            a=Temp.indexOf(" ");
            b=Temp.indexOf(" ",a+2);
            String account = Temp.substring(a+1,b);
            //TextView accNew = (TextView)findViewById(R.id.smsAcc);
            accounT.add(account);

            a=Temp.indexOf(":");
            b=Temp.indexOf(" ",a+1);
            String total = Temp.substring(a+1,b);
            totaL.add(total);
            //TextView totNew = (TextView)findViewById(R.id.smsBal);
        }
        smsInfoAdapt = new SmsInfoAdapter(SmsActivity.this,accounT,totaL);
        smsListView.setAdapter(smsInfoAdapt);
    }

    public void init(int pos, String AccountSearch) {
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
        String[] smsAcc = AccountSearch.split("\n");
        Toast.makeText(getApplicationContext(),String.valueOf(pos),Toast.LENGTH_LONG).show();
        ArrayList<String> Val = db.Selected3(smsAcc[0]);
        //arrayAdapter.clear();
        //smsMessagesList.clear();
        Toast.makeText(getApplicationContext(),String.valueOf(Val.size()),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),Val.get(0),Toast.LENGTH_LONG).show();
        for (int i = 0; i < Val.size(); i++) {
            //arrayAdapter.add(Val.get(i));
            String[] smsInfo = Val.get(i).split(" ");
            TableRow tbrow = new TableRow(this);
            tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(this);
            t1v.setText(smsInfo[1]);
            t1v.setTextSize(12);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText("  " + smsInfo[2]);
            t2v.setTextSize(12);
            t2v.setGravity(Gravity.LEFT);
            tbrow.addView(t2v);
            if(smsInfo[2].equals("Debited")) {
                t2v.setTextColor(Color.rgb(176, 23, 31));
            }
            else
            {
                t2v.setTextColor(Color.rgb(139, 195, 74));
            }


            TextView t3v = new TextView(this);
            t3v.setText("Rs. " + smsInfo[3]);
            t3v.setTextSize(12);
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.LEFT);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText( smsInfo[4] + "-" + smsInfo[5] );
            t4v.setTextSize(12);
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.LEFT);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(" " + "  ");
            t5v.setTextColor(Color.BLACK);
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

    public void refreshSmsInbox() {
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
                        /*if(smsMessage.contains("balance"))
                        {
                            String Bal="";
                            a=smsMessage.indexOf("balance");
                            b=smsMessage.indexOf(" ",a+8);
                            Bal+=smsMessage.substring(a, b);
                            Toast.makeText(this, "Data : "+Bal, Toast.LENGTH_SHORT);
                        }
                        else
                        {..
                        }*/
                        db.firstAdd(smsAccNo);
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
        int i;
        for (i = 0; i <= NoBank; i++) {
            if (stringArray[i].equalsIgnoreCase(strAddress2)) {
                arrayAdapter.insert(smsMessage, 0);
                arrayAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void AddEntry(String Message,String Time) {//Detail Transaction
        int a=0,b=0;
        smsMessage = ""; smsMessageStr= ""; mAmount = ""; smsAccNo = "";

        smsMessage+=Message;

        //Status
        if(smsMessage.contains("credited")) {
            smsMessageStr +="Credited";
        }
        else if(smsMessage.contains("debited") || smsMessage.contains("withdraw")) {
            smsMessageStr +="Debited";
        }

        //AccountNumber
        if(smsMessage.contains("a/c no.")) {
            a=smsMessage.indexOf("a/c");
            b=smsMessage.indexOf(" ",a+8);
            smsAccNo  += smsMessage.substring(a+7,b);
        }
        else if(smsMessage.contains("a/c")) {
            a=smsMessage.indexOf("a/c");
            b=smsMessage.indexOf(" ",a+9);
            smsAccNo  += smsMessage.substring(a+4,b);
        }
        else if(smsMessage.contains("account number")) {
            a=smsMessage.indexOf("account");
            b=smsMessage.indexOf(" ",a+15);
            smsAccNo += smsMessage.substring(a+15,b);
        }
        else if (smsMessage.contains("account")) {
            a=smsMessage.indexOf("account");
            b=smsMessage.indexOf(" ",a+8);
            smsAccNo += smsMessage.substring(a+8,b);
        }
        else if (smsMessage.contains("ac")) {
            a = smsMessage.indexOf("ac");
            b = smsMessage.indexOf(" ", a + 3);
            smsAccNo += smsMessage.substring(a + 3, b);
        }
        String smsAccNo2=smsAccNo.replace("x","");
        smsAccNo=smsAccNo2.replace(" ","");

        //Amount
        if(smsMessage.contains("rs.")) {
            a=smsMessage.indexOf("rs.");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+4,b);
        }
        else if(smsMessage.contains("rs")) {
            a=smsMessage.indexOf("rs");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+3,b);
        }
        else if(smsMessage.contains("inr")) {
            a=smsMessage.indexOf("inr");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+4,b);
        }
        String mAmount2 = mAmount.replace(",","");
        Toast.makeText(this, "Data Contain :\n|"+smsMessageStr+"|\n|"+smsAccNo+"|\n|"+mAmount2+"|", Toast.LENGTH_SHORT).show();
        if(smsMessageStr.length()!=0 && smsAccNo.length()!=0 && mAmount2.length()!=0) {
            db.add(smsMessageStr, smsAccNo, mAmount2, Time);
            db.Bank(smsMessageStr, smsAccNo, mAmount2);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String ClickedItem="",Acc="";
            int a=0,b=0;
            for (int i = 1; i < smsMessages.length; ++i) {
                ClickedItem += smsMessages[i];
            }
            a=ClickedItem.indexOf(" ");
            b=ClickedItem.indexOf(" ",a+2);
            Acc+=ClickedItem.substring(a+1, b);
            Toast.makeText(this,"Acc : |"+Acc+"|",Toast.LENGTH_SHORT);
            ArrayList<String> Val = db.Selected3(Acc);
            arrayAdapter.clear();
            for (int i = 0; i < Val.size(); i++) {
                arrayAdapter.add(Val.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}