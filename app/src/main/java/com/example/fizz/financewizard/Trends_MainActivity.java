
package com.example.fizz.financewizard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;



/**
 * Created by Simeon on 17/02/2016.
 */




public class Trends_MainActivity extends AppCompatActivity {

    AlertDialog alert;
    String yearSms;
    private AlertDialog.Builder build;
    Spinner yearDrop;
    String yearTrends = "2016";
    private SQLiteDatabase dataBase;
    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected ArrayAdapter<String> mAdapter;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected String mActivityTitle;
    protected static int position;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DatabaseHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trends_main);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Trends";//string
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        BarChart chart = (BarChart) findViewById(R.id.chart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.animateXY(2000, 3000);

        chart.invalidate();
    }

    ArrayList<String> monStr = new ArrayList<String>(Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"));

    //BAR graph code
    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<String> Val = db.Selected3("");

        Float[] sumCredit = new Float[12], sumDebit = new Float[12];
        for(int i=0;i<12;i++)
            sumDebit[i] = sumCredit[i] = 0f;

        //Toast.makeText(getApplicationContext(),"size " + String.valueOf(Val.size()),Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(),"getYear " + yearTrends,Toast.LENGTH_SHORT).show();
        for(int i=0;i<Val.size();i++){
            String[] smsInfo = Val.get(i).split(" ");
            //Toast.makeText(getApplicationContext(),"index " + String.valueOf(Val.size()),Toast.LENGTH_SHORT).show();
            if(smsInfo[5].equals(yearTrends)){
                int index = monStr.indexOf(smsInfo[4]);
                if(monStr.get(index).equals(smsInfo[4])){
                    if(smsInfo[2].equals("Debited"))
                        sumDebit[index] += Float.valueOf(smsInfo[3]);
                    else if(smsInfo[2].equals("Credited"))
                        sumCredit[index] += Float.valueOf(smsInfo[3]);
                    //BarEntry v1e1 = new BarEntry(110.000f, index); // Jan
                }
            }
        }

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();

        for(int i = 0;i < 12;i++){
            BarEntry vd1 = new BarEntry(sumDebit[i],i);
            //Toast.makeText(getApplicationContext(),"Debit " + String.valueOf(sumDebit[i]),Toast.LENGTH_SHORT).show();
            valueSet1.add(vd1);
            BarEntry vc1 = new BarEntry(sumCredit[i],i);
            //Toast.makeText(getApplicationContext(),"Credit " + String.valueOf(sumDebit[i]),Toast.LENGTH_SHORT).show();
            valueSet2.add(vc1);
        }


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Debited");
        barDataSet1.setColor(Color.rgb(176, 23, 31));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Credited");
        barDataSet2.setColor(Color.rgb(139, 195, 74));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JULY");
        xAxis.add("AUG");
        xAxis.add("SEPT");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;
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

            /** Called when a drawer has settled in a completely closed stsate. */
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
                break;

            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        getMenuInflater().inflate(R.menu.menu_trends, menu);
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

        if(id == R.id.listYearMenu){

            LayoutInflater li = LayoutInflater.from(Trends_MainActivity.this);
            View promptsCategoryView = li.inflate(R.layout.dropdown, null);
            AlertDialog.Builder build = new AlertDialog.Builder(Trends_MainActivity.this);
            build.setTitle("New Category");
            build.setMessage("Please Enter new Category");
            build.setView(promptsCategoryView);

            yearDrop = (Spinner) promptsCategoryView.findViewById(R.id.year_spinner);
            //PayValue.isFocused();
            yearDrop.setFocusableInTouchMode(true);
            yearDrop.setFocusable(true);
            yearDrop.requestFocus();

            ArrayList<String> yearCont = new ArrayList<String>();
            ArrayList<Integer> yearId = new ArrayList<Integer>();
            yearSms = null;
            db = new DatabaseHandler(this);
            dataBase = db.getWritableDatabase();
            Cursor gCursor = dataBase.rawQuery("SELECT DISTINCT " + db.YEAR +" FROM " + db.TABLE_NAME , null);

            yearId.add(-1);
            yearCont.add("--Select Category--");

            int catgyFlag = 0;
            if (gCursor.getCount() > 0) {
                gCursor.moveToFirst();
                do{
                    yearId.add(Integer.valueOf(gCursor.getString(gCursor.getColumnIndex(db.YEAR))));
                    yearCont.add(gCursor.getString(gCursor.getColumnIndex(db.YEAR)));
                }while(gCursor.moveToNext());
            }

            Toast.makeText(getApplicationContext(), "Count of year " + String.valueOf(gCursor.getCount()), Toast.LENGTH_LONG).show();
            gCursor.close();
            dataBase.close();//close database

            ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearCont);
            //SimpleCursorAdapter adapterC = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, gCursor, catCont, catId);
            adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearDrop.setAdapter(adapterC);
            yearDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    yearSms = yearDrop.getSelectedItem().toString();
                }
                //Spinner spinnerCat = (Spinner) findViewById(R.id.categoryDrop);

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //
                }
            });
            build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    yearTrends = yearSms;
                    Toast.makeText(getApplicationContext(), yearTrends, Toast.LENGTH_SHORT).show();
                    //getDataSet();
                    //onResume();
                    BarChart chart = (BarChart) findViewById(R.id.chart);

                    BarData data = new BarData(getXAxisValues(), getDataSet());
                    chart.setData(data);
                    chart.setDescription("My Chart");
                    chart.animateXY(2000, 3000);

                    chart.invalidate();
                    dialog.cancel();
                }
            });

            build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert = build.create();
            alert.show();
            alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, settings_main.class));
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(id==R.id.calculator){

            startActivity(new Intent(this,Calc.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
//end
