package com.example.fizz.financewizard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Simeon on 17/02/2016.
 */
public class Trends_MainActivity extends AppCompatActivity {

    private FrameLayout mainLayout;
    private PieChart mChart;
    // private float[] yDtata={5,10,15,30,10};
    ArrayList<Integer> ydata=new ArrayList<Integer>();//(Arrays.asList(5, 10, 15, 30, 10));
    //private String[] xData={"Sony","LG","Motorola","Samsung","Nexus"};
    private String[] xData2=new String[3];
    ArrayList<String> cat=new ArrayList<String>();//(Arrays.asList("Sony","LG","Motorola","Samsung","Nexus"));
    Cursor chart_cursor;
    private DbHelperCategory dbhelp;
    private DbHelperGoal tHelper;
    private SQLiteDatabase obj_db, tDataBase;
    Button abc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trends_main);
        // fetching categories from database
        //abc=(Button)findViewById(R.id.testdata);
        dbhelp=new DbHelperCategory(this);

        /*abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cont = new ContentValues();
                obj_db = dbhelp.getWritableDatabase();
                cont.put(DbHelperCategory.CAT_TYPE, "Car");
                obj_db.insert(DbHelperCategory.TABLE_NAME, null, cont);
                obj_db.close();
            }
        });*/
        obj_db=dbhelp.getReadableDatabase();
        chart_cursor=  obj_db.rawQuery("SELECT * FROM "+dbhelp.TABLE_NAME,null);

        // fetch from DbCategory
        if(chart_cursor.moveToFirst()){
            do{
                cat.add(chart_cursor.getString(chart_cursor.getColumnIndex(DbHelperCategory.CAT_TYPE)));
                Toast.makeText(getApplicationContext(), chart_cursor.getString(chart_cursor.getColumnIndex(DbHelperCategory.CAT_TYPE)), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),String.valueOf(xData2.length), Toast.LENGTH_SHORT).show();
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

        /*for (i=0;i<cat.size();i++){
            Toast.makeText(getApplicationContext(),cat.get(i), Toast.LENGTH_SHORT).show();
        }*/

        mainLayout=(FrameLayout)findViewById(R.id.mainLayout);

        mChart=new PieChart(this);
        mainLayout.addView(mChart);
        mainLayout.setBackgroundColor(Color.WHITE);

        mChart.setUsePercentValues(true);
        mChart.setDescription("Smartphone Market shares");

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(10);
        mChart.setTransparentCircleRadius(15);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //disp value if pie selected
                if (entry == null)
                    return;

                Toast.makeText(Trends_MainActivity.this, cat.get(entry.getXIndex()) + "=" + entry.getVal() + "%", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        addData();
        Legend l=mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData(){
        ArrayList<Entry> yValsl= new ArrayList<Entry>();

        int i, m = 0, j;
        for (i=0;i<ydata.size();i++) {
            if(ydata.get(i) != 0) {
                Toast.makeText(getBaseContext(),"Ydata:"+String.valueOf(ydata.get(i)),Toast.LENGTH_LONG).show();
                yValsl.add(new Entry(ydata.get(i), m++));
            }
        }
        ArrayList<String> xVals= new ArrayList<String>();

        for (i=0,j=0;i<cat.size();i++)
            if(ydata.get(i) != 0)
                xVals.add(cat.get(i));

        //create pie data set
        PieDataSet dataSet=new PieDataSet(yValsl,"Market Shares");
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
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.DKGRAY);

        mChart.setData(data);

        //undo all highlights
        mChart.highlightValues(null);

        //update pie chart
        mChart.invalidate();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        return super.onOptionsItemSelected(item);
    }
}
//end