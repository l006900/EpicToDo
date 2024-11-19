package com.example.epictodo.m;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * FocusDatabaseHelper
 *
 * @author 31112
 * @date 2024/11/18
 */
public class FocusDatabaseHelper extends SQLiteOpenHelper {

    //表名
    public static final String TABLE_NAME = "FocusSessions";
    //列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_DURATION = "duration";

    public FocusDatabaseHelper(@Nullable Context context) {
        super(context, "focus.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_START_TIME + " LONG, " +
            COLUMN_END_TIME + " LONG, " +
            COLUMN_TAG + " TEXT, " +
            COLUMN_DURATION + " INTEGER); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addFocusSession(FocusSession session) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_START_TIME, session.getStartTime());
        values.put(COLUMN_END_TIME, session.getEndTime());
        values.put(COLUMN_TAG, session.getTag());
        values.put(COLUMN_DURATION, session.getDuration());
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(session.getId())};
        return db.update(TABLE_NAME, values, selection, selectionArgs);
    }

    public int deleteFocusSession(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    public List<FocusSession> getAllFocusSessions() {
        FocusSession session;
        List<FocusSession> list = new ArrayList<>();

        String sql1 = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql1,null);

        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
        int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
        int tagIndex = cursor.getColumnIndex(COLUMN_TAG);
        int durationIndex = cursor.getColumnIndex(COLUMN_DURATION);

        while (cursor.moveToNext()) {
            session = new FocusSession(cursor.getInt(idIndex), cursor.getInt(startTimeIndex), cursor.getInt(endTimeIndex), cursor.getString(tagIndex), cursor.getInt(durationIndex));
            list.add(session);
        }
        cursor.close();
        return list;
    }


}
