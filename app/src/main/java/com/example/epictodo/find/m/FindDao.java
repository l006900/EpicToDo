package com.example.epictodo.find.m;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * FindDao
 *
 * @author 31112
 * @date 2024/12/18
 */
@Dao
public interface FindDao {

    @Query("SELECT * FROM find ORDER BY timestamp DESC")
    LiveData<List<FindEntity>> getAllItems();

    @Query("SELECT * FROM find WHERE timestamp = :timestamp LIMIT 1")
    LiveData<FindEntity> getItemByTimestamp(long timestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FindEntity findEntity);

    @Delete
    void delete(FindEntity findEntity);

}
