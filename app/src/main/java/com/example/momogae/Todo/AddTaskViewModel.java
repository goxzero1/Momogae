package com.example.momogae.Todo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

class AddTaskViewModel extends ViewModel {

    private LiveData<TaskEntry> task;


    public AddTaskViewModel(AppDatabase database, int taskId) {
        task =  database.taskDao().loadTaskById(taskId);
    }

    public LiveData<TaskEntry> getTask() {
        return task;
    }
}
