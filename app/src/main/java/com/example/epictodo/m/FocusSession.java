package com.example.epictodo.m;

/**
 * FocusSession
 *
 * @author 31112
 * @date 2024/11/18
 */
public class FocusSession {
    private long id;
    private long startTime;
    private long endTime;
    private String tag;
    private int duration;

    public FocusSession(long id, long startTime, long endTime, String tag, int duration) {
        this.duration = duration;
        this.endTime = endTime;
        this.id = id;
        this.startTime = startTime;
        this.tag = tag;
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
}


