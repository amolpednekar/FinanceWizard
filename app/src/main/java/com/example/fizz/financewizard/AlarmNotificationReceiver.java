package com.example.fizz.financewizard;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Shreekumar on 22-02-2016.
 */
public class AlarmNotificationReceiver extends BroadcastReceiver {

    int keyIndex = 0;
    private ArrayList<String> keyId = new ArrayList<String>();
    private ArrayList<String> goalTitle = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> day = new ArrayList<String>();
    private ArrayList<String> month = new ArrayList<String>();
    private ArrayList<String> year = new ArrayList<String>();
    private ArrayList<String> daysLeftGoal = new ArrayList<String>();

    private ArrayList<Integer> notifyLastDay = new ArrayList<Integer>();
    private ArrayList<String> notifyTitle = new ArrayList<String>();
    private ArrayList<String> notifyId = new ArrayList<String>();

    PendingIntent resultPendingIntent, deletePendingIntent, viewPendingIntent, extendPendingIntent, savePendingIntent;
    private DbHelperGoal gHelper;
    private SQLiteDatabase dataBase;
    private NotificationManager myGoalNotifyMgr;
    NotificationCompat.Builder gBuilder;

    //days_of_months(Jan,Feb,Mar,April,May,June,July,Aug,Sept,Oct,Nov,Dec)
    ArrayList<Integer> mon = new ArrayList<Integer>(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));
    ArrayList<String> monStr = new ArrayList<String>(Arrays.asList("Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"));

    // calculates days from months
    int calMonthDay(int m,int y){//calMonthDay(month,year)
        int x=0,c;
        for(c = 1; c < m; c++) {// Jan to less than the month 'm' as 'm' we are not taking the the whole days of that month
            if(c == 2) {//if Feb
                if(y%4 == 0)//checks if year is leap or not
                    x += 29;
                else
                    x += 28;
            }
            else
                x += mon.get(c-1);
        }
        return(x);
    }

    //calculates no. of months from current month & year to goal month & year
    int calDateMonth(int mC,int yC,int mG,int yG){//(current-month, current-year, goal-month, goal-year)
        int x = 0,i,countM=0;
        if(yC<=yG){
            for(i = yC; i < yG; i++)
                countM += 12;
        }

        countM -= mC;
        countM += mG;
        return (countM);
    }

    //calculates no. of weeks from current month & year to goal month & year
    int calDateWeek(int mC,int yC,int mG,int yG){
        int x = 0,i,countW=0;
        if(yC<=yG){
            for(i = yC; i < yG; i++)
                countW+=52;
        }

        countW -= mC;
        countW += mG;
        countW *= 4;
        return (countW);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Tag:", "Alarm Notification received");
        //Toast.makeText(context,"Alarm created2",Toast.LENGTH_LONG).show();

//        gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Fizz");
//        gBuilder.setStyle(new NotificationCompat.InboxStyle().addLine("Time's up"));
//        gBuilder.setGroup("My Goals");
//        gBuilder.setGroupSummary(true);
//        gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 22);//set the alarm time
//        calendar.set(Calendar.MINUTE, 53);
//        calendar.set(Calendar.SECOND, 00);
//        gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.WHITE, 0, 1);
//            //gBuilder.setWhen(calendar.getTimeInMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.WHITE, 0, 1);
//
//            // opens the resultPendingIntent
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            // open the activity every 24 hours
//            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, resultPendingIntent);
//
//            gBuilder.setAutoCancel(true);
//            int mNotificationId = 10;
//            keyIndex = 10;
//            // Gets an instance of the NotificationManager service
//            myGoalNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            // Builds the notification and issues it.
//            myGoalNotifyMgr.notify(mNotificationId, gBuilder.build());

        gHelper = new DbHelperGoal(context);
        dataBase = gHelper.getWritableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelperGoal.TABLE_NAME, null);

        keyId.clear();
        goalTitle.clear();
        date.clear();
        daysLeftGoal.clear();

        notifyTitle.clear();
        notifyId.clear();
        notifyLastDay.clear();

        myGoalNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        myGoalNotifyMgr.cancelAll();

        if (mCursor.moveToFirst()) {
            do {
                keyId.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                goalTitle.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)));
                day.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY)));
                month.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.MONTH)));
                year.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR)));

                //display date using day/month/year
                date.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.DAY)) + "-" + monStr.get(mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.MONTH)) - 1) + "-" + mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.YEAR)));
                final Calendar c = Calendar.getInstance();
                int curYear = c.get(Calendar.YEAR), curMonth = c.get(Calendar.MONTH)+1, curDay = c.get(Calendar.DAY_OF_MONTH);
                int goalYear = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.YEAR)), goalMonth = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.MONTH)), goalDay = mCursor.getInt(mCursor.getColumnIndex(DbHelperGoal.DAY));
                int calYear = 0,calMonth = 0,calDay = 0,calDayGoal = 0;
                float dailyAmount = 0;

                //Get current date
                String curDate = String.valueOf(curDay)+"/"+String.valueOf(curMonth)+"/"+String.valueOf(curYear);

                String goalDate = String.valueOf(goalDay)+"/"+String.valueOf(goalMonth)+"/"+String.valueOf(goalYear);
                int count = -1;

                //Fetches the date and Time from system, hence not used
                if(curYear < goalYear || (goalYear==curYear && goalMonth>curMonth)||(goalYear==curYear && goalMonth==curMonth && goalDay>curDay)) {
                    count = 0;
                    int i;
                    for (i = curYear; i < goalYear; i++) {
                        if (i % 4 == 0) {
                            count += 366;//Leap year
                        } else {
                            count += 365;// Non leap year
                        }
                    }
                    //calculating the no of days left from current date to goal date
                    count -= calMonthDay(curMonth, curYear);
                    count -= curDay;
                    count += calMonthDay(goalMonth, goalYear);
                    count += goalDay;
                    if (count < 0) {
                        count *= -1;
                    }

                    if(count != 1)
                        daysLeftGoal.add(String.valueOf(count)+" days left");
                    else
                        daysLeftGoal.add(String.valueOf(count)+" day left");
                } else {// current year exceeds goal year
                    daysLeftGoal.add("Times up");
                }

                // For notification
                // check if goal date is less than or equals 2 days
                String tempNotif = mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.NOTIFICATION_DATE));
                int notifCnt = Integer.MAX_VALUE;
                if(tempNotif == "daily")
                    notifCnt = 1;
                else if(tempNotif == "weekly")
                    notifCnt = 7;
                else if (tempNotif == "monthly")
                    notifCnt = 30;

                if(count <= 2 || count % notifCnt == 0){
                    notifyLastDay.add(count);
                    notifyTitle.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.GOAL_TITLE)));
                    notifyId.add(mCursor.getString(mCursor.getColumnIndex(DbHelperGoal.KEY_ID)));
                }
            } while (mCursor.moveToNext());

            Intent viewIntent = new Intent(context, GoalDisActivity.class);
            viewIntent.putExtra("update", true);
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


            if(Build.VERSION.SDK_INT > 15) {
                TaskStackBuilder viewStackBuilder = TaskStackBuilder.create(context);
                viewStackBuilder.addParentStack(MainActivity.class);
                viewStackBuilder.addNextIntent(viewIntent);
                viewPendingIntent =  viewStackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT);
            }else{
                viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if(notifyLastDay.size() == 1){ // for 1 notification
                // display goal info
                Intent resultIntent = new Intent(context, GoalInfoActivity.class);
                resultIntent.putExtra("ID", notifyId.get(0));
                resultIntent.putExtra("update", true);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // provide delete option
                Intent deleteIntent = new Intent(context, GoalDisActivity.class);
                //Intent deleteIntent = new Intent();
                deleteIntent.setAction("Delete");
                deleteIntent.putExtra("ID", notifyId.get(0));
                deleteIntent.putExtra("update", true);
                deleteIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //provide goal date extension
                Intent extendIntent = new Intent(context, GoalDisActivity.class);
                extendIntent.setAction("Extend");
                extendIntent.putExtra("ID", notifyId.get(0));
                extendIntent.putExtra("update", true);
                extendIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //provide payment contribution
                Intent saveIntent = new Intent(context, GoalDisActivity.class);
                saveIntent.setAction("Pay");
                saveIntent.putExtra("ID", notifyId.get(0));
                saveIntent.putExtra("update", true);
                saveIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if(Build.VERSION.SDK_INT > 15) {
                    //TaskStackBuilder.create(this) -> TaskStackBuilder.create(context) as this is not an Activity class, hence used context
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(GoalDisActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    resultPendingIntent =  stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);//0);

                    TaskStackBuilder delStackBuilder = TaskStackBuilder.create(context);
                    delStackBuilder.addParentStack(MainActivity.class);
                    delStackBuilder.addNextIntent(deleteIntent);
                    deletePendingIntent =  delStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);//0);

                    TaskStackBuilder extStackBuilder = TaskStackBuilder.create(context);
                    extStackBuilder.addParentStack(MainActivity.class);
                    extStackBuilder.addNextIntent(extendIntent);
                    extendPendingIntent =  extStackBuilder.getPendingIntent(2,PendingIntent.FLAG_UPDATE_CURRENT);//0);

                    TaskStackBuilder saveStackBuilder = TaskStackBuilder.create(context);
                    saveStackBuilder.addParentStack(MainActivity.class);
                    saveStackBuilder.addNextIntent(saveIntent);
                    savePendingIntent =  saveStackBuilder.getPendingIntent(3,PendingIntent.FLAG_UPDATE_CURRENT);//0);
                } else {
                    resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);// 0);
                    deletePendingIntent = PendingIntent.getActivity(context, 1, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);// PendingIntent.FLAG_UPDATE_CURRENT);
                    extendPendingIntent = PendingIntent.getActivity(context, 2, extendIntent, PendingIntent.FLAG_UPDATE_CURRENT);// 0);
                    savePendingIntent = PendingIntent.getActivity(context, 3, saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);// 0);
                }

                // check if goal date is exceeded, if 2 or less days left, then
                if(notifyLastDay.get(0) >= 0){// && notifyLastDay.get(0) <= 2) {
                    String remContent = "";
                    if(notifyLastDay.get(0) == 1)
                        remContent = String.valueOf(notifyLastDay.get(0)) + " day left";
                    else if(notifyLastDay.get(0) == -1)
                        remContent = String.valueOf("Time's up");
                    else
                        remContent = String.valueOf(notifyLastDay.get(0)) + " days left";
                    gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Fizz").setContentText("Goals: " + notifyTitle.get(0)).addAction(R.mipmap.money_transfer, "Contribute", savePendingIntent).addAction(R.mipmap.delete_w, "Delete", deletePendingIntent);
                    gBuilder.setContentIntent(resultPendingIntent);
                    gBuilder.setStyle(new NotificationCompat.InboxStyle().setBigContentTitle(notifyTitle.get(0)).addLine(remContent));
                }else if(notifyLastDay.get(0) == -1){ // Goals that are Timed out
                    String remContent = notifyTitle.get(0);
                    int cnt = notifyLastDay.size();
                    gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Fizz").setContentText("Goals : " + remContent).addAction(R.mipmap.tear_calendar, "Extend", extendPendingIntent).addAction(R.mipmap.delete_w, "Delete", deletePendingIntent);
                    //gBuilder.setLargeIcon();
                    gBuilder.setContentIntent(resultPendingIntent);
                    gBuilder.setStyle(new NotificationCompat.InboxStyle().setBigContentTitle(remContent).addLine("Time's up"));
                }

                gBuilder.setGroup("My Goals");
                gBuilder.setGroupSummary(true);
                gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 22);//set the alarm time
                calendar.set(Calendar.MINUTE, 00);
                calendar.set(Calendar.SECOND, 0);
                gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.WHITE, 0, 1);
                //gBuilder.setWhen(calendar.getTimeInMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.WHITE, 0, 1);

                // opens the resultPendingIntent
                //getSystemService(NOTIFICATION_SERVICE) -> context.getSystemService(Context.NOTIFICATION_SERVICE) coz it is not an activity class, & context is directed from Activity class
                //AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                // open the activity every 24 hours
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, resultPendingIntent);

                gBuilder.setAutoCancel(true);
                int mNotificationId = Integer.valueOf(notifyId.get(0));
                keyIndex = Integer.valueOf(notifyId.get(0));
                // Gets an instance of the NotificationManager service
                //getSystemService(NOTIFICATION_SERVICE) -> context.getSystemService(Context.NOTIFICATION_SERVICE) coz it is not an activity class, & context is directed from Activity class
                myGoalNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                int color = Color.rgb(131,158,46);
                gBuilder.setColor(color);

                myGoalNotifyMgr.notify(mNotificationId, gBuilder.build());
            } else if(notifyLastDay.size() > 1) { //many
                int cnt = notifyLastDay.size();
                Toast.makeText(context, String.valueOf(cnt), Toast.LENGTH_LONG).show();
                //NotificationCompat.Builder(this) -> NotificationCompat.Builder(context) as this is not an Activity class
                gBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Fizz").setContentText(String.valueOf(cnt) + "goals");
                gBuilder.setContentIntent(viewPendingIntent);
                String summary = String.valueOf(cnt) + " goals";

                //gBuilder.setStyle(new NotificationCompat.InboxStyle().addLine(notifyTitle.get(0) + " - " + notifyLastDay.get(0) + " days left").addLine(notifyTitle.get(1) + " - " + notifyLastDay.get(1) + " days left").setSummaryText(summary));
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                if (cnt <= 7)// if only 7 near date goals, show all
                    for (int i = 0; i < cnt; i++) {
                        if(Integer.valueOf(notifyLastDay.get(i)) == 1)
                            inboxStyle.addLine(notifyTitle.get(i) + " - " + notifyLastDay.get(i) + " day left");
                        else if(Integer.valueOf(notifyLastDay.get(i)) == -1)
                            inboxStyle.addLine(notifyTitle.get(i) + " - Time's up");
                        else
                            inboxStyle.addLine(notifyTitle.get(i) + " - " + notifyLastDay.get(i) + " days left");
                    }
                else// show only 7 of 'n'
                    for (int i = cnt - 7; i < cnt; i++) {
                        if(Integer.valueOf(notifyLastDay.get(i)) == 1)
                            inboxStyle.addLine(notifyTitle.get(i) + " - " + notifyLastDay.get(i) + " day left");
                        else if(Integer.valueOf(notifyLastDay.get(i)) == -1)
                            inboxStyle.addLine(notifyTitle.get(i) + " - Time's up");
                        else
                            inboxStyle.addLine(notifyTitle.get(i) + " - " + notifyLastDay.get(i) + " days left");
                    }
                inboxStyle.setSummaryText(summary);
                gBuilder.setStyle(inboxStyle);

                gBuilder.setGroup("My Goals");
                gBuilder.setGroupSummary(true);
                gBuilder.setPriority(Notification.PRIORITY_HIGH);// [-2,2]->[PRIORITY_MIN,PRIORITY_MAX]

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 14);//set the alarm time
                calendar.set(Calendar.MINUTE, 57);
                calendar.set(Calendar.SECOND, 0);
                gBuilder.setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.WHITE, 0, 1);

                // opens the resultPendingIntent
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                // open the activity every 24 hours
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3 * 24 * 60 * 60 * 1000 , viewPendingIntent);
                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , viewPendingIntent);

                gBuilder.setAutoCancel(true);
                int mNotificationId = 10;
                // Builds the notification and issues it.
                int color = 0xff123456;
                gBuilder.setColor(color);
                myGoalNotifyMgr.notify(mNotificationId, gBuilder.build());
            }
        }
        mCursor.close();
    }
}
