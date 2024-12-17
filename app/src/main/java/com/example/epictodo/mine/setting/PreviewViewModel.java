package com.example.epictodo.mine.setting;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * PreviewViewModel
 * 预览弹窗的ViewModel
 * @author 31112
 * @date 2024/11/29
 */
public class PreviewViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> selected = new MutableLiveData<>();
    private SettingsManager settingsManager;

    public PreviewViewModel(Application application) {
        super(application);
        settingsManager = new SettingsManager(application);
        selected.setValue(settingsManager.getPreviewMode());
    }

    public LiveData<Integer> getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected.setValue(selected);
        settingsManager.setPreviewMode(selected);
    }
}