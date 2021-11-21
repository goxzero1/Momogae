package com.example.momogae.Todo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<TaskEntity>> tasks; //TaskEntity의 데이터를 받아와 모든 할 일 set

    public MainViewModel(@NonNull Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        tasks = database.taskDao().loadAllTasks();
    }

    public LiveData<List<TaskEntity>> getTasks() {
        return tasks;
    }
}



