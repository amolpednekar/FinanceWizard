package com.example.fizz.financewizard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GoalAdapter  extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> id;
    private ArrayList<String> goal;
    private ArrayList<String> date;
    private ArrayList<String> day;
    private ArrayList<String> month;
    private ArrayList<String> year;
    private ArrayList<String> amount;
    //private ArrayList<String> breakdownDay;
    //private ArrayList<String> breakdownWeek;
    //private ArrayList<String> breakdownMonth;
    private ArrayList<String> daysLeftGoal;
    private ArrayList<Integer> progressValue;
    private ShapeDrawable pgDrawable;

    // receives values from GoalDisActivity.java
    public GoalAdapter(Context c, ArrayList<String> gId,ArrayList<String> gGoal, ArrayList<String> gDay, ArrayList<String> gMonth, ArrayList<String> gYear, ArrayList<String> gDate, ArrayList<String> gAmount, ArrayList<String> gDaysLeft,ArrayList<Integer> gProgressValue) {// ArrayList<String> gBreakDay, ArrayList<String> gBreakWeek, ArrayList<String> gBreakMonth, ArrayList<String> gDaysLeft) {
        this.mContext = c;

        this.id = gId;
        this.goal = gGoal;
        this.day = gDay;
        this.month = gMonth;
        this.year = gYear;
        this.date = gDate;
        this.amount = gAmount;
        //this.breakdownDay = gBreakDay;
        //this.breakdownWeek = gBreakWeek;
        //this.breakdownMonth = gBreakMonth;
        this.daysLeftGoal = gDaysLeft;
        this.progressValue = gProgressValue;
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
        return id.size();
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
            child = layoutInflater.inflate(R.layout.layout, null);
            mHolder = new Holder();
            //mHolder.g_Id = (TextView) child.findViewById(R.id.listid);
            mHolder.g_goal = (TextView) child.findViewById(R.id.goalView1);
            //mHolder.g_date = (TextView) child.findViewById(R.id.dateView2);
            mHolder.g_date = (TextView) child.findViewById(R.id.dateView2);
            mHolder.g_amount = (TextView) child.findViewById(R.id.amountView2);
            //mHolder.g_daybreak = (TextView) child.findViewById(R.id.dailyView2);
            //mHolder.g_weekbreak = (TextView) child.findViewById(R.id.weekView2);
            //mHolder.g_monthbreak = (TextView) child.findViewById(R.id.monthView2);
            mHolder.g_daysLeft = (TextView) child.findViewById(R.id.daysLeft);
            mHolder.progressGoal = (ProgressBar) child.findViewById(R.id.progressBarGoal);
            child.setTag(mHolder);
        } else {
            mHolder = (Holder) child.getTag();
        }
        //mHolder.g_Id.setText(id.get(pos));
        mHolder.g_goal.setText(goal.get(pos));
        mHolder.g_amount.setText(amount.get(pos));
        mHolder.g_date.setText(date.get(pos));
        mHolder.g_daysLeft.setText(daysLeftGoal.get(pos));
        //setting prgress value
        /**Drawable bckgrndDr = new ColorDrawable(Color.RED);
        Drawable secProgressDr = new ColorDrawable(Color.GRAY);
        Drawable progressDr = new ScaleDrawable(new ColorDrawable(Color.BLUE), Gravity.LEFT, 1, -1);
        LayerDrawable resultDr = new LayerDrawable(new Drawable[] { bckgrndDr, secProgressDr, progressDr });
        //setting ids is important
        resultDr.setId(0, android.R.id.background);
        resultDr.setId(1, android.R.id.secondaryProgress);
        resultDr.setId(2, android.R.id.progress);*/
        mHolder.progressGoal.setProgress(progressValue.get(pos));
        return child;
    }


    public class Holder {
        TextView g_Id;
        TextView g_date;
        TextView g_day;
        TextView g_month;
        TextView g_year;
        TextView g_goal;
        TextView g_amount;
        //TextView g_daybreak;
        //TextView g_weekbreak;
        //TextView g_monthbreak;
        TextView g_daysLeft;
        Button paymentBtn;
        Button expenseBtn;
        ProgressBar progressGoal;
    }
}
