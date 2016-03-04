package com.example.fizz.financewizard;

import android.widget.BaseAdapter;

public abstract class BaseSwipListAdapter extends BaseAdapter {

    public boolean getSwipEnableByPosition(int position){
        return true;
    }



}