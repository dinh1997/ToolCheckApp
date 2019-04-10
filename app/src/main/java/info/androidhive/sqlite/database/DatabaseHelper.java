package info.androidhive.sqlite.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.database.model.Note;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "notes_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        onCreate(db);
    }

    public long insertNote(String note, String keyword, String country, int status) {
        SQLiteDatabase db = this.getWritableDatabase();        // get writable database as we want to write data

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_PACKAGE_APP, note);        // `id` and `timestamp` will be inserted automatically.no need to add them
        values.put(Note.COLUMN_KEYWORD, keyword);
        values.put(Note.COLUMN_COUNTRY, country);
        values.put(Note.COLUMN_STATUS, status);

        long id = db.insert(Note.TABLE_NAME, null, values);        // insert row
        db.close();
        return id;        // return newly inserted row id
    }

    public Note getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();        // get readable database as we are not inserting anything

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_PACKAGE_APP, Note.COLUMN_KEYWORD, Note.COLUMN_COUNTRY, Note.COLUMN_STATUS, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        assert cursor != null;
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_PACKAGE_APP)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_KEYWORD)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_COUNTRY)),
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_STATUS)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        cursor.close();        // close the db connection
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setPacKage(cursor.getString(cursor.getColumnIndex(Note.COLUMN_PACKAGE_APP)));
                note.setKeyword(cursor.getString(cursor.getColumnIndex(Note.COLUMN_KEYWORD)));
                note.setCountry(cursor.getString(cursor.getColumnIndex(Note.COLUMN_COUNTRY)));
                note.setImg_check(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_COUNTRY)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        db.close();
        return notes;        // return notes list
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_PACKAGE_APP, note.getPacKage());
        values.put(Note.COLUMN_KEYWORD, note.getKeyword());
        values.put(Note.COLUMN_COUNTRY, note.getCountry());
        values.put(Note.COLUMN_STATUS, note.getImg_check());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
