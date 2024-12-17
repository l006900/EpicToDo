package com.example.epictodo.login.account.m;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.epictodo.login.phone.m.LoginPhoneDatabase;


/**
 * LoginPasswordDatabase
 *
 * @author 31112
 * @date 2024/12/9
 */
@Database(entities = {LoginPasswordEntity.class}, version = 3, exportSchema = false)
public abstract class LoginPasswordDatabase extends RoomDatabase {

    public abstract LoginPasswordDao loginPasswordDao();

    private static volatile LoginPasswordDatabase INSTANCE;

    public static LoginPasswordDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LoginPhoneDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    LoginPasswordDatabase.class, "password")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
