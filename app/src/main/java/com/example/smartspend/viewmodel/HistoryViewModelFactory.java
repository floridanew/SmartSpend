// app/src/main/java/com/example/smartspend/viewmodel/HistoryViewModelFactory.java
package com.example.smartspend.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HistoryViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int userId;

    public HistoryViewModelFactory(Application application, int userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HistoryViewModel(application, userId);
    }
}