package com.example.epictodo.m;

import android.app.Application;
import android.icu.util.Calendar;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FocusSessionRepository
 *
 * @author 31112
 * @date 2024/11/21
 */
public class FocusSessionRepository {
    private FocusSessionDao focusSessionDao;
    private LiveData<List<FocusSession>> allFocusSession;
    private ExecutorService executorService;

    public FocusSessionRepository(Application application) {
        FocusDataBase db = FocusDataBase.getDatabase(application);
        focusSessionDao = db.focusSessionDao();
        allFocusSession = focusSessionDao.getAllFocusSessions();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<FocusSession>> getAllFocusSession() {
        return allFocusSession;
    }

    public void insert(FocusSession focusSession) {
        executorService.execute(() -> {
            long id = focusSessionDao.insert(focusSession);
            focusSession.setId(id);
            updateTotals(focusSession);
        });
    }

    public void update(FocusSession focusSession) {
        executorService.execute(() -> focusSessionDao.update(focusSession));
    }

    public void delete(FocusSession focusSession) {
        executorService.execute(() -> focusSessionDao.delete(focusSession));
    }

    public LiveData<List<FocusSession>> getFocusSessionsByTag(String tag) {
        return focusSessionDao.getFocusSessionByTag(tag);
    }

    public LiveData<List<String>> getAllTags() {
        return focusSessionDao.getAllTags();
    }

    public LiveData<List<FocusSession>> getFocusSessionsForDay(long startOfDay, long endOfDay) {
        return focusSessionDao.getFocusSessionsByDateRange(startOfDay, endOfDay);
    }

    public LiveData<List<FocusSession>> getFocusSessionsForWeek(long startOfWeek, long endOfWeek) {
        return focusSessionDao.getFocusSessionsByDateRange(startOfWeek, endOfWeek);
    }

    public LiveData<List<FocusSession>> getFocusSessionsForMonth(long startOfMonth, long endOfMonth) {
        return focusSessionDao.getFocusSessionsByDateRange(startOfMonth, endOfMonth);
    }

    public LiveData<List<FocusSession>> getFocusSessionsForYear(long startOfYear, long endOfYear) {
        return focusSessionDao.getFocusSessionsByDateRange(startOfYear, endOfYear);
    }

    public LiveData<List<FocusSession>> getFocusSessionsByDateRange(long startTime, long endTime) {
        return focusSessionDao.getFocusSessionsByDateRange(startTime, endTime);
    }

    private void updateTotals(FocusSession focusSession) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(focusSession.getStartTime());

        // Set to start of day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        long endOfDay = startOfDay + 24 * 60 * 60 * 1000;

        // Set to start of week (assuming week starts on Monday)
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long startOfWeek = cal.getTimeInMillis();
        long endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000;

        // Set to start of month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long endOfMonth = cal.getTimeInMillis();

        // Set to start of year
        cal.setTimeInMillis(focusSession.getStartTime());
        cal.set(Calendar.DAY_OF_YEAR, 1);
        long startOfYear = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, 1);
        long endOfYear = cal.getTimeInMillis();

        executorService.execute(() -> {
            long dailyTotal = focusSessionDao.getDailyTotal(startOfDay, endOfDay);
            long weeklyTotal = focusSessionDao.getWeeklyTotal(startOfWeek, endOfWeek);
            long monthlyTotal = focusSessionDao.getMonthlyTotal(startOfMonth, endOfMonth);
            long yearlyTotal = focusSessionDao.getYearlyTotal(startOfYear, endOfYear);

            focusSession.setDailyTotal(dailyTotal);
            focusSession.setWeeklyTotal(weeklyTotal);
            focusSession.setMonthlyTotal(monthlyTotal);
            focusSession.setYearlyTotal(yearlyTotal);

            focusSessionDao.update(focusSession);
        });
    }
}