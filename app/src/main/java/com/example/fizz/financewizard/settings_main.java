package com.example.fizz.financewizard;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Simeon on 30/04/2016.
 */
public class settings_main extends AppCompatActivity {
public Switch sms_per;
    DbHelperCategory per;
    SQLiteDatabase database;
    public String status;
    public TextView about;
    private boolean isUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        final ContentValues values=new ContentValues();
        sms_per=(Switch)findViewById(R.id.permission_sms);
        per=new DbHelperCategory(this);
        database=per.getWritableDatabase();
        /*values.put(DbHelperCategory.TYPE_PER,"SMS");
        values.put(DbHelperCategory.STATUS_PER, "enabled");
        database.insert(DbHelperCategory.TABLE_PERMISSION, null, values);
        */isUpdate=false;
        final Cursor cur=database.rawQuery("SELECT * FROM "+ DbHelperCategory.TABLE_PERMISSION /*+ " WHERE " +DbHelperCategory.TYPE_PER + "=" + "SMS"*/, null);
        Toast.makeText(getApplicationContext(),String.valueOf(cur.getCount()),Toast.LENGTH_SHORT).show();
        if(cur.getCount()<=0){
            Toast.makeText(getApplicationContext(),"1st" +String.valueOf(cur.getCount()),Toast.LENGTH_SHORT).show();
            values.put(DbHelperCategory.TYPE_PER, "SMS");
            values.put(DbHelperCategory.STATUS_PER, "enabled");
            //database.insert(DbHelperCategory.TABLE_PERMISSION, null, values);
            //database.insert(DbHelperCategory.TABLE_PERMISSION,null,values);
            isUpdate=false;
            if(isUpdate)
            {
                //update database with new data
                ;
            }
            else
            {
                //insert data into database
                database.insert(DbHelperCategory.TABLE_PERMISSION,null,values);
                sms_per.setChecked(true);
            }
        }else {
            if (cur.moveToFirst()) {
                do {
                    status = cur.getString(cur.getColumnIndex(DbHelperCategory.STATUS_PER));
                } while (cur.moveToNext());
            }
            if (status.equals("disabled")) {
                sms_per.setChecked(false);
            } else
                sms_per.setChecked(true);


        }

        sms_per.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Toast.makeText(getApplicationContext(),String.valueOf(cur.getCount()),Toast.LENGTH_SHORT).show();
                    values.clear();
                    values.put(DbHelperCategory.STATUS_PER, "enabled");
                    database.update(DbHelperCategory.TABLE_PERMISSION, values, DbHelperCategory.TYPE_PER + "='SMS'", null);
                    Toast.makeText(getApplicationContext(), "Enabled" + DbHelperCategory.STATUS_PER, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(),String.valueOf(cur.getCount()),Toast.LENGTH_SHORT).show();
                    values.clear();
                    values.put(DbHelperCategory.STATUS_PER, "disabled");
                    //String x = "SMS";
                    //database.rawQuery("UPDATE "+DbHelperCategory.TABLE_PERMISSION+" SET " + DbHelperCategory.STATUS_PER + "=" +  "'disabled' WHERE "+DbHelperCategory.TYPE_PER + " ='SMS' ",null);
                    database.update(DbHelperCategory.TABLE_PERMISSION, values, DbHelperCategory.TYPE_PER + "='SMS'" , null);
                    Toast.makeText(getApplicationContext(), "disabled" + DbHelperCategory.STATUS_PER, Toast.LENGTH_SHORT).show();
                }
            }

        });
       // about=(TextView)findViewById(R.id.about);
        /*about.setOnClickListener((View.OnClickListener) this);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    public void about_msg(View v){
        setContentView(R.layout.about);
    }
    /*public void faq(View v){
        setContentView(R.layout.settings_faq);
    }*/
    public void contact(View v){
        setContentView(R.layout.settings_contact_us);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

}
