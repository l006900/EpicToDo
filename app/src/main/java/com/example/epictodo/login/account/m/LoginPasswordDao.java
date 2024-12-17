package com.example.epictodo.login.account.m;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * LoginPasswordDao
 *
 * @author 31112
 * @date 2024/12/10
 */
@Dao
public interface LoginPasswordDao {

    @Query("SELECT * FROM password ORDER BY updateTime DESC LIMIT 7")
    LiveData<List<LoginPasswordEntity>> getPhoneHistory();

    @Query("SELECT * FROM password WHERE phone = :phone")
    LoginPasswordEntity getPhone(String phone);

    @Query("SELECT * FROM password WHERE phone = :phone AND password = :password")
    LoginPasswordEntity getPhoneAndPassword(String phone, String password);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginPasswordEntity entity);

    @Delete
    void delete(LoginPasswordEntity entity);

    @Delete
    void delete(List<LoginPasswordEntity> entities);
}
