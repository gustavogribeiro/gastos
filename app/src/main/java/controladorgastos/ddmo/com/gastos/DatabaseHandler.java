package controladorgastos.ddmo.com.gastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 12/9/2014.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactManager",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_EMAIL = "email",
            KEY_ADDRESS = "address",
            KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_IMAGEURI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);
    }

    public void createContact(Gastos gastos) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, gastos.getName());
        values.put(KEY_PHONE, gastos.getPhone());
        values.put(KEY_EMAIL, gastos.getEmail());
        values.put(KEY_ADDRESS, gastos.getAddress());
        values.put(KEY_IMAGEURI, gastos.getImageURI().toString());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public Gastos getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGEURI }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        Gastos gastos = new Gastos(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return gastos;
    }

    public void deleteContact(Gastos gastos) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[] { String.valueOf(gastos.getId()) });
        db.close();
    }

    public int getContactsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateContact(Gastos gastos) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, gastos.getName());
        values.put(KEY_PHONE, gastos.getPhone());
        values.put(KEY_EMAIL, gastos.getEmail());
        values.put(KEY_ADDRESS, gastos.getAddress());
        values.put(KEY_IMAGEURI, gastos.getImageURI().toString());

        int rowsAffected = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[] { String.valueOf(gastos.getId()) });
        db.close();

        return rowsAffected;
    }

    public List<Gastos> getAllContacts() {
        List<Gastos> gastoses = new ArrayList<Gastos>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        if (cursor.moveToFirst()) {
            do {
                gastoses.add(new Gastos(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gastoses;
    }
}
