package com.example.epictodo.m;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * FocusDataBase
 *
 * @author 31112
 * @date 2024/11/21
 */
@Database(entities = {FocusSession.class}, version = 1, exportSchema = false)
public abstract class FocusDataBase extends RoomDatabase {
    private static FocusDataBase INSTANCE;

    public abstract FocusSessionDao focusSessionDao();

    public static synchronized FocusDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    FocusDataBase.class, "focus_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
