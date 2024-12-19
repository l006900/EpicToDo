package com.example.epictodo.find.m;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * FindDatabase
 *
 * @author 31112
 * @date 2024/12/18
 */
@Database(entities = {FindEntity.class}, version = 2, exportSchema = false)
public abstract class FindDatabase extends RoomDatabase {

    public abstract FindDao findDao();

    private static volatile FindDatabase INSTANCE;

    public static FindDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FindDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FindDatabase.class, "find")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
