package com.example.epictodo.find.m;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * FindEntity
 *
 * @author 31112
 * @date 2024/12/18
 */
@Entity(tableName = "find", primaryKeys = {"userId", "timestamp"})
public class FindEntity {
    // 用户id
    @NonNull
    public String userId = "";

    @NonNull
    public String title = "";

    @NonNull
    public String description = "";

    @NonNull
    public String imageUrl = "";

    @NonNull
    public String userName = ""; // 新增字段

    @NonNull
    public String userAvatar = ""; // 新增字段

    public long timestamp = 0;

    public FindEntity() {}

    @Ignore
    public FindEntity(@NonNull String userId, @NonNull String title, @NonNull String description, @NonNull String imageUrl, @NonNull String userName, @NonNull String userAvatar) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userName = userName; // 初始化新字段
        this.userAvatar = userAvatar; // 初始化新字段
        this.timestamp = System.currentTimeMillis();
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getUserName() { // 新增 getter 方法
        return userName;
    }

    public void setUserName(@NonNull String userName) { // 新增 setter 方法
        this.userName = userName;
    }

    @NonNull
    public String getUserAvatar() { // 新增 getter 方法
        return userAvatar;
    }

    public void setUserAvatar(@NonNull String userAvatar) { // 新增 setter 方法
        this.userAvatar = userAvatar;
    }

    @NonNull
    @Override
    public String toString() {
        return "FindEntity{" +
                "userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userName='" + userName + '\'' + // 包含新字段
                ", userAvatar='" + userAvatar + '\'' + // 包含新字段
                ", timestamp='" + timestamp +
                '}';
    }
}
