
package com.example.fizz.financewizard;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trends_main);
        db = new DatabaseHandler(this);

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
        chart.setDescription("My Chart");
        chart.animateXY(2000, 3000);

        chart.invalidate();
    }

    ArrayList<String> monStr = new ArrayList<String>(Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"));

    //BAR graph code
    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<String> Val = db.Selected3(null);

        Float[] sumCredit = new Float[12], sumDebit = new Float[12];
        for(int i=0;i<12;i++)
            sumDebit[i] = sumCredit[i] = 0f;

        Toast.makeText(getApplicationContext(),"size " + String.valueOf(Val.size()),Toast.LENGTH_SHORT).show();

        for(int i=0;i<Val.size();i++){
            String[] smsInfo = Val.get(i).split(" ");
            //Toast.makeText(getApplicationContext(),"index " + String.valueOf(Val.size()),Toast.LENGTH_SHORT).show();
            if(smsInfo[5].equals("2016")){
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
            Toast.makeText(getApplicationContext(),"Debit " + String.valueOf(sumDebit[i]),Toast.LENGTH_SHORT).show();
            valueSet1.add(vd1);
            BarEntry vc1 = new BarEntry(sumCredit[i],i);
            Toast.makeText(getApplicationContext(),"Credit " + String.valueOf(sumDebit[i]),Toast.LENGTH_SHORT).show();
            valueSet2.add(vc1);
        }

        /*BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);*/

        /*ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);*/

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//end
