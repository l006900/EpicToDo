package com.example.epictodo.login.account.m;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
/**
 * LoginPhoneEntity
 *
 * @author 31112
 * @date 2024/12/9
 */
@Entity(tableName = "password", primaryKeys = {"area", "phone"})
public class LoginPasswordEntity {
    // 手机区号
    @NonNull
    public String area = "";

    // 手机号
    @NonNull
    public String phone = "";

    // 密码
    @NonNull
    @ColumnInfo
    public String password = "";

    // 更新时间
    @ColumnInfo
    public long updateTime;

    public LoginPasswordEntity() {
    }

    @Ignore
    public LoginPasswordEntity(@NonNull String area, @NonNull String phone, @NonNull String password) {
        this.area = TextUtils.isEmpty(area) ? "" : area;
        this.phone = TextUtils.isEmpty(phone) ? "" : phone;
        this.password = TextUtils.isEmpty(password) ? "" : password;
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "LoginPhoneEntity{" +
                "area='" + area + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    public String getPassword() {
        return password;
    }
}
