package com.example.fizz.financewizard;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.renderer.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Goals_MainActivity extends AppCompatActivity {

    private DbHelperGoal tHelper;
    private DbHelperCategory cHelper;
    private SQLiteDatabase tDataBase, cDataBase;
    private AlertDialog.Builder build ;
    TextView tGoals, tSavings, tCategoryNo;
    private RelativeLayout totalG;

    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected ArrayAdapter<String> mAdapter;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected String mActivityTitle;
    protected static int position;

    //pie chart code variables
    private FrameLayout mainLayout;
    private PieChart mChart;
    ArrayList<Integer> ydata=new ArrayList<Integer>();
    ArrayList<String> cat=new ArrayList<String>();
    Cursor chart_cursor;
    private DbHelperCategory dbhelp;
    private SQLiteDatabase obj_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activitymain);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Finance Wizard";//string

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tGoals = (TextView)findViewById(R.id.goalNo2);// total no of goals
        tSavings = (TextView)findViewById(R.id.totalSavings2);// total savings
        totalG = (RelativeLayout)findViewById(R.id.viewCashFlowSlot);
        tCategoryNo = (TextView)findViewById(R.id.category2);

        totalG.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GoalDisActivity.class);
                i.putExtra("update", false);
                startActivity(i);
            }
        });
        pieDisp();


          }
    public void pieDisp(){
        // fetching categories from database
        dbhelp=new DbHelperCategory(this);
        obj_db=dbhelp.getReadableDatabase();
        cat.clear();
        ydata.clear();
        chart_cursor=  obj_db.rawQuery("SELECT * FROM "+dbhelp.TABLE_NAME,null);
        // fetch from DbCategory
        if(chart_cursor.moveToFirst()){
            do{
                cat.add(chart_cursor.getString(chart_cursor.getColumnIndex(DbHelperCategory.CAT_TYPE)));
                ydata.add(0);
            }while(chart_cursor.moveToNext());
        }
        chart_cursor.close();
        dbhelp.close();

        // comparing category with DbGoal
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
                    if(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.CATEGORY)).equals(cat.get(i))){
                        categoryCntTemp = ydata.get(i);//get count of category
                        ++categoryCntTemp;
                        ydata.set(i, categoryCntTemp);
                        break;
                    }
                    i++;
                }while(i < cat.size());
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        mainLayout=(FrameLayout)findViewById(R.id.mainLayout);

        mChart=new PieChart(this);
        mainLayout.addView(mChart);
        mainLayout.setBackgroundColor(Color.WHITE);

        mChart.setUsePercentValues(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(18);
        mChart.setTransparentCircleRadius(24);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //disp value if pie selected
                if (entry == null)
                    return;

                Toast.makeText(Goals_MainActivity.this, cat.get(entry.getXIndex()) + "=" + entry.getVal() + "%", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        addData();
        mChart.getLegend().setEnabled(true);
        mChart.getLegend().setWordWrapEnabled(true);
        mChart.getLegend().setTextSize(12);
    }
    private void addData(){
        ArrayList<Entry> yValsl= new ArrayList<Entry>();
        yValsl.clear();
        int i, m = 0, j;
        for (i=0;i<ydata.size();i++) {
            if(ydata.get(i) != 0) {
                yValsl.add(new Entry(ydata.get(i), m++));
            }
        }
        ArrayList<String> xVals= new ArrayList<String>();

        for (i=0,j=0;i<cat.size();i++)
            if(ydata.get(i) != 0)
                xVals.add(cat.get(i));

        //create pie data set
        PieDataSet dataSet=new PieDataSet(yValsl," ");
        dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(10);

        //add many colours

        ArrayList<Integer> colors=new ArrayList<Integer>();

        for(int c: ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c: ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for(int c: ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for(int c: ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for(int c: ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object
        PieData data= new PieData(xVals,dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.DKGRAY);

        mChart.setData(data);

        //undo all highlights
        mChart.highlightValues(null);

        //update pie chart
        mChart.invalidate();


    }

    @Override
    protected void onResume() {
        displayData();
        createAlarm();
       // pieDisp();
        super.onResume();
    }

    private void createAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,07);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        //calendar.get(Calendar.HOUR_OF_DAY);//set the alarm time
        //calendar.get(Calendar.MINUTE);
        //calendar.get(Calendar.SECOND);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mNotificationReceiverIntent = new Intent(Goals_MainActivity.this, AlarmNotificationReceiver.class);
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(Goals_MainActivity.this, 0, mNotificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis() + 5000, mNotificationReceiverPendingIntent);
        mAlarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis() + 5000, 5 * 60 * 1000, mNotificationReceiverPendingIntent);
        Log.i("MainActivity", "Alarm created");
        Toast.makeText(getApplicationContext(),"Alarm created",Toast.LENGTH_LONG).show();
    }

    public void displayData() {
        ArrayList<String> categoryList = new ArrayList<String>();
        ArrayList<Integer> categoryCnt = new ArrayList<Integer>();
        cHelper = new DbHelperCategory(this.getBaseContext());
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
        tSavings.setText(currencyType + " " + String.format("%.0f",savings));
        /*for(i = 0;i<categoryList.size();i++){
            if(i == 0)
                catContent = categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
            else
                catContent += categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
        }
        tCategoryNo.setText(catContent);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        switch(id){
            case R.id.goal:
                Intent i = new Intent(getApplicationContext(), GoalDisActivity.class);
                i.putExtra("update", false);
                startActivity(i);
                return true;
            //case R.id.currency_info:
              //  return true;
        }
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