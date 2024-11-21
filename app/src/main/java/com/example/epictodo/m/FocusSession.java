package com.example.epictodo.m;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * FocusSession
 *
 * @author 31112
 * @date 2024/11/21
 */
@Entity(tableName = "focus_session")
public class FocusSession {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long startTime;
    private long endTime;
    private String tag;
    private int duration;
    private long dailyTotal;
    private long weeklyTotal;
    private long monthlyTotal;
    private long yearlyTotal;

    public FocusSession(long dailyTotal, int duration, long endTime, long id, long monthlyTotal, long startTime, String tag, long weeklyTotal, long yearlyTotal) {
        this.dailyTotal = dailyTotal;
        this.duration = duration;
        this.endTime = endTime;
        this.id = id;
        this.monthlyTotal = monthlyTotal;
        this.startTime = startTime;
        this.tag = tag;
        this.weeklyTotal = weeklyTotal;
        this.yearlyTotal = yearlyTotal;
    }

    public long getDailyTotal() {
        return dailyTotal;
    }

    public void setDailyTotal(long dailyTotal) {
        this.dailyTotal = dailyTotal;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMonthlyTotal() {
        return monthlyTotal;
    }

    public void setMonthlyTotal(long monthlyTotal) {
        this.monthlyTotal = monthlyTotal;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getWeeklyTotal() {
        return weeklyTotal;
    }

    public void setWeeklyTotal(long weeklyTotal) {
        this.weeklyTotal = weeklyTotal;
    }

    public long getYearlyTotal() {
        return yearlyTotal;
    }

    public void setYearlyTotal(long yearlyTotal) {
        this.yearlyTotal = yearlyTotal;
    }
}
