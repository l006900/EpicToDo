package com.example.epictodo.login.phone.m;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * LoginPhoneDatabase
 *
 * @author 31112
 * @date 2024/12/9
 */
@Database(entities = {LoginPhoneEntity.class}, version = 1, exportSchema = false)
public abstract class LoginPhoneDatabase extends RoomDatabase {

    public abstract LoginPhoneDao loginPhoneDao();

    private static volatile LoginPhoneDatabase INSTANCE;

    public static LoginPhoneDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LoginPhoneDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LoginPhoneDatabase.class, "phone")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
