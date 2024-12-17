package com.example.epictodo.login.phone;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.epictodo.login.phone.m.LoginPhoneDao;
import com.example.epictodo.login.phone.m.LoginPhoneDatabase;
import com.example.epictodo.login.phone.m.LoginPhoneEntity;

import java.util.List;

/**
 * LoginNumberViewModel
 *
 * @author 31112
 * @date 2024/12/5
 */
public class LoginNumberViewModel extends AndroidViewModel {
    private LoginPhoneDatabase database;
    private LoginPhoneDao loginPhoneDao;
    private LiveData<List<LoginPhoneEntity>> recentNumbers;

    public LoginNumberViewModel(@NonNull Application application) {
        super(application);
        database = LoginPhoneDatabase.getInstance(application);
        loginPhoneDao = database.loginPhoneDao();
        recentNumbers = loginPhoneDao.getPhoneHistory();
    }

    public LiveData<List<LoginPhoneEntity>> getRecentNumbers() {
        return recentNumbers;
    }

    public void insert(LoginPhoneEntity entity) {
        new Thread(() -> {
            loginPhoneDao.insert(entity);
        }).start();
    }

    public void delete(LoginPhoneEntity entity) {
        new Thread(() -> {
            loginPhoneDao.delete(entity);
        }).start();
    }

}
