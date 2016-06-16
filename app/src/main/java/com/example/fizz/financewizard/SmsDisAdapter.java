package com.example.fizz.financewizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Simeon on 14/06/2016.
 */
public class SmsDisAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> id;
    private ArrayList<String> account;
    private ArrayList<String> credit;
    private ArrayList<String> debit;
    private ArrayList<String> total;
    private ArrayList<String> bankName;

    public SmsDisAdapter(Context c,ArrayList<String> bAccount, ArrayList<String> bBankName, ArrayList<String> bTotal) {
        this.mContext = c;
        //this.id = bId;
        this.account = bAccount;
        this.bankName = bBankName;
        this.total = bTotal;
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
        return account.size();
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

            mHolder.s_Account = (TextView) child.findViewById(R.id.smsAcc);
            mHolder.s_BankName = (TextView) child.findViewById(R.id.bank_name);
            mHolder.s_Total = (TextView) child.findViewById(R.id.smsBal);
            child.setTag(mHolder);
        } else {
            mHolder = (Holder) child.getTag();
        }
        mHolder.s_Account.setText(account.get(pos));
        mHolder.s_BankName.setText(bankName.get(pos));
        mHolder.s_Total.setText(total.get(pos));

        return child;
    }

    public class Holder {
        TextView s_Id;
        TextView s_Account;
        TextView s_Total;
        TextView s_BankName;

    }
}
