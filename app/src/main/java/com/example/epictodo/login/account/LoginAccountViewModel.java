package com.example.epictodo.login.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.epictodo.login.account.m.LoginPasswordDao;
import com.example.epictodo.login.account.m.LoginPasswordDatabase;
import com.example.epictodo.login.account.m.LoginPasswordEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AccountViewModel
 *
 * @author 31112
 * @date 2024/12/6
 */
public class LoginAccountViewModel extends AndroidViewModel {
    private LoginPasswordDatabase database;
    private LoginPasswordDao loginPasswordDao;
    private LiveData<List<LoginPasswordEntity>> recentNumbers;
    private MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> loginErrorMessage = new MutableLiveData<>();

    public LoginAccountViewModel(@NonNull Application application) {
        super(application);
        database = LoginPasswordDatabase.getInstance(application);
        loginPasswordDao = database.loginPasswordDao();
        recentNumbers = loginPasswordDao.getPhoneHistory();
    }

    public LiveData<List<LoginPasswordEntity>> getRecentAccounts() {
        return recentNumbers;
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getLoginErrorMessage() {
        return loginErrorMessage;
    }

    public void insert(LoginPasswordEntity entity) {
        new Thread(() -> {
            loginPasswordDao.insert(entity);
        }).start();
    }

    public void delete(LoginPasswordEntity entity) {
        new Thread(() -> {
            loginPasswordDao.delete(entity);
        }).start();
    }

    public void validateLogin(String phone, String password) {
        new Thread(() -> {
            LoginPasswordEntity entity = loginPasswordDao.getPhone(phone);
            if (entity == null) {
                // 账号不存在
                setLoginResult(false);
                setLoginErrorMessage("账号不存在");
            } else {
                // 账号存在，验证密码
                if (entity.getPassword().equals(password)) {
                    setLoginResult(true);
                    setLoginErrorMessage("");
                } else {
                    setLoginResult(false);
                    setLoginErrorMessage("密码错误");
                }
            }
        }).start();
    }

    public boolean loginDialog(String phone) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new Thread(() -> {
            LoginPasswordEntity entity = loginPasswordDao.getPhone(phone);
            if (entity != null) {
                // 账号存在，显示弹窗
                future.complete(true);
            } else {
                future.complete(false);
            }
        }).start();

        try {
            return future.get();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public void setLoginResult(boolean result) {
        loginResult.postValue(result);
    }

    public void setLoginErrorMessage(String message) {
        loginErrorMessage.postValue(message);
    }
}
