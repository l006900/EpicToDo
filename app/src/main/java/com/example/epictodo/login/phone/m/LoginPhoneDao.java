package com.example.epictodo.login.phone.m;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
/**
 * LoginPhoneDao
 *
 * @author 31112
 * @date 2024/12/9
 */
@Dao
public interface LoginPhoneDao {

    @Query("SELECT * FROM phone ORDER BY updateTime DESC LIMIT 7")
    LiveData<List<LoginPhoneEntity>> getPhoneHistory();

    @Query("SELECT * FROM phone ORDER BY updateTime DESC")
    List<LoginPhoneEntity> getPhoneHistoryData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginPhoneEntity entity);

    @Delete
    void delete(LoginPhoneEntity entity);

    @Delete
    void delete(List<LoginPhoneEntity> entities);
}
