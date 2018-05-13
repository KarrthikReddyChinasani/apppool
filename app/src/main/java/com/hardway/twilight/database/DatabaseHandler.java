package com.hardway.twilight.database;

/**
 * Created by karth on 1/26/2018.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ApplicationList";
    private static final String TABLE_CONTACTS = "applications";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PACKAGE = "package";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("  + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PACKAGE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

   public void addContact(ApplicationSqlite applicationSqlite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, applicationSqlite.getAppName());
        values.put(KEY_PACKAGE, applicationSqlite.getPackagename());
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    // Getting single contact
    public boolean getContact(String id) {
        boolean data = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS+" WHERE "+KEY_PACKAGE+" = '"+id+"'";
         Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
                        KEY_NAME, KEY_PACKAGE }, KEY_PACKAGE + "='"+id+"'",
                null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        int a = cursor.getCount();
        Log.e("count",a+"");
        if (a == 1) {
            data = true;
            Log.e("data", data+"");
        }
        if (a == 0) {
            data = false;
            Log.e("data", data+"");
        }
        return data;
    }

    public List<ApplicationSqlite> getAllContacts() {
        List<ApplicationSqlite> contactList = new ArrayList<ApplicationSqlite>();
        Log.e("fetched","");
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.e("data", cursor.getString(1));
                ApplicationSqlite contact = new ApplicationSqlite();
                contact.setAppName(cursor.getString(1));
                contact.setPackagename(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        return contactList;
    }

    public void deleteContact(ApplicationSqlite contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_PACKAGE + " = ?",
                new String[] { String.valueOf(contact.getPackagename()) });
        db.close();
    }

}