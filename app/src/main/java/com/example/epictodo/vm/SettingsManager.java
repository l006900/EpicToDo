package com.example.epictodo.vm;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SettingManager
 * 设置页面的数据管理类
 * @author 31112
 * @date 2024/11/29
 */
public class SettingsManager {
    private static final String PREF_NAME = "EseeCloudSettings";
    private static final String KEY_HARD_ENCODING = "hard_encoding";
    private static final String KEY_PREVIEW_MODE = "preview_mode";
    private static final String KEY_NOTIFICATION_BANNER = "notification_banner";
    private static final String KEY_SCREEN_BRIGHTNESS = "screen_brightness";
    private static final String KEY_VIBRATION_EFFECT = "vibration_effect";
    private static final String KEY_PERSONALIZED_ADS = "personalized_ads";

    private SharedPreferences preferences;

    public SettingsManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setHardEncoding(boolean enabled) {
        preferences.edit().putBoolean(KEY_HARD_ENCODING, enabled).apply();
    }

    public boolean isHardEncodingEnabled() {
        return preferences.getBoolean(KEY_HARD_ENCODING, false);
    }

    public void setPreviewMode(int mode) {
        preferences.edit().putInt(KEY_PREVIEW_MODE, mode).apply();
    }

    public int getPreviewMode() {
        return preferences.getInt(KEY_PREVIEW_MODE, 0);
    }

    public void setNotificationBanner(boolean enabled) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_BANNER, enabled).apply();
    }

    public boolean isNotificationBannerEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATION_BANNER, true);
    }

    public void setScreenBrightness(boolean enabled) {
        preferences.edit().putBoolean(KEY_SCREEN_BRIGHTNESS, enabled).apply();
    }

    public boolean isScreenBrightnessEnabled() {
        return preferences.getBoolean(KEY_SCREEN_BRIGHTNESS, true);
    }

    public void setVibrationEffect(boolean enabled) {
        preferences.edit().putBoolean(KEY_VIBRATION_EFFECT, enabled).apply();
    }

    public boolean isVibrationEffectEnabled() {
        return preferences.getBoolean(KEY_VIBRATION_EFFECT, false);
    }

    public void setPersonalizedAds(boolean enabled) {
        preferences.edit().putBoolean(KEY_PERSONALIZED_ADS, enabled).apply();
    }

    public boolean isPersonalizedAdsEnabled() {
        return preferences.getBoolean(KEY_PERSONALIZED_ADS, true);
    }
}
