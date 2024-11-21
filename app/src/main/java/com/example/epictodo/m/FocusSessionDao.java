package com.example.epictodo.m;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * FocusSessionDao
 *
 * @author 31112
 * @date 2024/11/21
 */
@Dao
public interface FocusSessionDao {
    @Insert
    long insert(FocusSession focusSession);

    @Update
    void update(FocusSession focusSession);

    @Delete
    void delete(FocusSession focusSession);

    @Query("SELECT * FROM focus_session")
    LiveData<List<FocusSession>> getAllFocusSessions();

    @Query("SELECT *FROM focus_session WHERE id = :id")
    FocusSession getFocusSessionById(long id);

    @Query("SELECT * FROM focus_session WHERE tag = :tag")
    LiveData<List<FocusSession>> getFocusSessionByTag(String tag);

    @Query("SELECT SUM(duration) FROM focus_session WHERE tag = :tag")
    int getTotalDurationByTag(String tag);

    @Query("SELECT DISTINCT tag FROM focus_session")
    LiveData<List<String>> getAllTags();

    @Query("SELECT SUM(duration) FROM focus_session WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    long getDailyTotal(long startOfDay, long endOfDay);

    @Query("SELECT SUM(duration) FROM focus_session WHERE startTime >= :startOfWeek AND startTime < :endOfWeek")
    long getWeeklyTotal(long startOfWeek, long endOfWeek);

    @Query("SELECT SUM(duration) FROM focus_session WHERE startTime >= :startOfMonth AND startTime < :endOfMonth")
    long getMonthlyTotal(long startOfMonth, long endOfMonth);

    @Query("SELECT SUM(duration) FROM focus_session WHERE startTime >= :startOfYear AND startTime < :endOfYear")
    long getYearlyTotal(long startOfYear, long endOfYear);
}
