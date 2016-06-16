package com.example.fizz.financewizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Simeon on 30/04/2016.
 */
public class settings_main extends AppCompatActivity {

    public TextView about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
       // about=(TextView)findViewById(R.id.about);
        /*about.setOnClickListener((View.OnClickListener) this);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    public void about_msg(View v){
        setContentView(R.layout.about);
    }
    public void faq(View v){
        setContentView(R.layout.settings_faq);
    }
    public void contact(View v){
        setContentView(R.layout.settings_contact_us);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

    }*/
}
