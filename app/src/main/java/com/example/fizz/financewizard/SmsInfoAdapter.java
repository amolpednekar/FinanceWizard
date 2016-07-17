package com.example.fizz.financewizard;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SmsInfoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> account;
    private ArrayList<String> total;
    private ShapeDrawable pgDrawable;

    public SmsInfoAdapter(Context c, ArrayList<String> aAcc,ArrayList<String> aTotal){
        this.mContext = c;
        this.account = aAcc;
        this.total = aTotal;
    }

    public customButtonListener customListener;

    public interface customButtonListener {
        public void onButtonClickListener(int position,String value);
    }

    public void setCustomButtonListener(customButtonListener listener) {
        this.customListener = listener;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return account.size();//no of values in the ArrayList
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int pos, View child, ViewGroup parent) {
        Holder mHolder;
        LayoutInflater layoutInflater;
        if (child == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child = layoutInflater.inflate(R.layout.sms_cards, null);
            mHolder = new Holder();
            //mHolder.g_Id = (TextView) child.findViewById(R.id.listid);
            mHolder.a_Acc = (TextView) child.findViewById(R.id.smsAcc);
            //mHolder.g_date = (TextView) child.findViewById(R.id.dateView2);
            mHolder.a_Total = (TextView) child.findViewById(R.id.smsBal);
            child.setTag(mHolder);
        } else {
            mHolder = (Holder) child.getTag();
        }
        //mHolder.g_Id.setText(id.get(pos));
        mHolder.a_Acc.setText(account.get(pos));
        mHolder.a_Total.setText(total.get(pos));

        return child;
    }

    public class Holder {
        TextView a_Id;
        TextView a_Acc;
        TextView a_Total;
    }
}
