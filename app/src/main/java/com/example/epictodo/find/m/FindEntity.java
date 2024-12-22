package com.example.epictodo.find.m;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * FindEntity
 *
 * @author 31112
 * @date 2024/12/18
 */
@Entity(tableName = "find", primaryKeys = {"userId", "timestamp"})
@TypeConverters(MediaUrlConverter.class)
public class FindEntity {
    // 用户id
    @NonNull
    public String userId = "";

    @NonNull
    public String title = "";

    @NonNull
    public String description = "";

    @NonNull
    public List<String> mediaUrls = new ArrayList<>(); // Initialize with empty list

    @NonNull
    public String userName = ""; // 新增字段

    @NonNull
    public String userAvatar = ""; // 新增字段

    public long timestamp = 0;

    public int starCount = 0;
    public int favoriteCount = 0;
    public int commentCount = 0;

    public FindEntity() {}

    @Ignore
    public FindEntity(@NonNull String userId, @NonNull String title, @NonNull String description, @NonNull List<String> mediaUrls, @NonNull String userName, @NonNull String userAvatar) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.mediaUrls = mediaUrls != null ? mediaUrls : new ArrayList<>();
        this.userName = userName;
        this.userAvatar = userAvatar;
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
    public List<String> getMediaUrls() {
        return mediaUrls != null ? mediaUrls : new ArrayList<>();
    }

    public void setMediaUrls(@NonNull List<String> mediaUrls) {
        this.mediaUrls = mediaUrls != null ? mediaUrls : new ArrayList<>();
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

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "FindEntity{" +
                "userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", userName='" + userName + '\'' + // 包含新字段
                ", userAvatar='" + userAvatar + '\'' + // 包含新字段
                ", timestamp=" + timestamp +
                ", starCount=" + starCount +
                ", favoriteCount=" + favoriteCount +
                ", commentCount=" + commentCount +
                '}';
    }
}