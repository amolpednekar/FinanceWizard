package com.example.fizz.financewizard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelperCategory extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="trialgoal3";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="triCatgy";
    public static final String KEY_ID="_id";
    public static final String CAT_TYPE = "type";
    public static final String TABLE_NAMECASH ="triHandCash";
    public static final String KEY_IDCASH ="_id";
    public static final String STATUS = "status";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";
    public static final String TABLE_LOGIN = "triLogin";
    public static final String KEY_IDLOGIN = "_id";
    public static final String PASSWORD_LOGIN = "password";

    public static final String TABLE_PERMISSION="try_permission";
    public static final String KEY_ID_PER ="_id";
    public static final String STATUS_PER = "status";
    public static final String TYPE_PER = "type_per";


    public DbHelperCategory(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE "+ TABLE_NAME +" ("+ KEY_ID +" INTEGER PRIMARY KEY, "+  CAT_TYPE +" TEXT)";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLEC ="CREATE TABLE "+ TABLE_NAMECASH +" ("+ KEY_IDCASH +" INTEGER PRIMARY KEY, "+  AMOUNT +" FLOAT, "+ STATUS +" TEXT, "+ DATE +" TEXT)";
        db.execSQL(CREATE_TABLEC);

        String CREATE_TABLEL = "CREATE TABLE "+ TABLE_LOGIN +" ("+ KEY_IDLOGIN +" TEXT PRIMARY KEY, "+  PASSWORD_LOGIN +" TEXT)";
        db.execSQL(CREATE_TABLEL);

        String CREATE_TABLEP = "CREATE TABLE "+ TABLE_PERMISSION +" ("+ KEY_ID_PER +" INTEGER PRIMARY KEY, "+  STATUS_PER+" TEXT, " + TYPE_PER + " TEXT )";
        db.execSQL(CREATE_TABLEP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMECASH);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERMISSION);
        onCreate(db);
    }
}
