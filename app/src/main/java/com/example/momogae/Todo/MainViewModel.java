package com.example.momogae.Todo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(@NonNull Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        tasks = database.taskDao().loadAllTasks();
    }

    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }
}
