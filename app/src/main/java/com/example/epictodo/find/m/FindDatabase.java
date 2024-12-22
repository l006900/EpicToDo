package com.example.epictodo.find.m;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * FindDatabase
 *
 * @author 31112
 * @date 2024/12/18
 */
@Database(entities = {FindEntity.class}, version = 4, exportSchema = false)
@TypeConverters({MediaUrlConverter.class})
public abstract class FindDatabase extends RoomDatabase {

    public abstract FindDao findDao();

    private static volatile FindDatabase INSTANCE;

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // If mediaUrls column doesn't exist, add it
            database.execSQL("ALTER TABLE find ADD COLUMN mediaUrls TEXT");
        }
    };

    public static FindDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FindDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FindDatabase.class, "find")
                            .addMigrations(MIGRATION_3_4)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
