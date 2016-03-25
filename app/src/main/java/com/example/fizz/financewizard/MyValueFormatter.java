package com.example.fizz.financewizard;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Simeon on 18/03/2016.
 */
public class MyValueFormatter implements ValueFormatter {
    private DecimalFormat mFormat;
    public MyValueFormatter(){
        mFormat=new DecimalFormat("###,###,##0");

    }
    @Override
    public String getFormattedValue(float value,Entry entry,int dataSetIndex,ViewPortHandler viewPortHandler){
        return mFormat.format(value);
    }
}
