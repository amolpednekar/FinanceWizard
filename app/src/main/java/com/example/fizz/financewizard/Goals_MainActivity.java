package com.example.fizz.financewizard;

/**
 * Created by Simeon on 03/01/2016.
 */
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;


public class Goals_MainActivity extends AppCompatActivity {

    private DbHelperGoal tHelper;
    private DbHelperCategory cHelper;
    private SQLiteDatabase tDataBase, cDataBase;
    private AlertDialog.Builder build ;
    TextView tGoals, tSavings, tCategoryNo;
    private RelativeLayout totalG;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activitymain);

        frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);//@ activity main
        mActivityTitle = "Goals and Targets";//string

        addDrawerItems();
        setupDrawer();

        tGoals = (TextView)findViewById(R.id.goalNo2);
        tSavings = (TextView)findViewById(R.id.totalSavings2);
        totalG = (RelativeLayout)findViewById(R.id.viewCashFlowSlot);
        tCategoryNo = (TextView)findViewById(R.id.category2);

        totalG.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(), GoalDisActivity.class);
                i.putExtra("update", false);
                startActivity(i);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


         tGoals = (TextView)findViewById(R.id.goalNo2);
         tSavings = (TextView)findViewById(R.id.totalSavings2);
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
           /* case 3:
                startActivity(new Intent(this, Item4Activity.class));
                break;
            case 4:
                startActivity(new Intent(this, Item5Activity.class));
                break;*/

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
    protected void onResume() {
        displayData();
        super.onResume();
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
        for(i = 0;i<categoryList.size();i++){
            if(i == 0)
                catContent = categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
            else
                catContent += categoryList.get(i) + ":" + String.valueOf(categoryCnt.get(i)) + "\n";
        }
        tSavings.setText(currencyType + " " + String.format("%.0f",savings));
        tCategoryNo.setText(catContent);
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
        int[] images = {R.drawable.cash_flow,R.drawable.rss,R.drawable.goals_targets,R.drawable.trends,R.drawable.reminders};

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
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id==R.id.calculator){

            startActivity(new Intent(this,Calc.class));
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//end