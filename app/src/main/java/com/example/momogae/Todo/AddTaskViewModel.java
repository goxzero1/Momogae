package com.example.momogae.Todo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.momogae.Todo.database.AppDatabase;
import com.example.momogae.Todo.database.TaskEntry;

class AddTaskViewModel extends ViewModel {

    private LiveData<TaskEntry> task;


    public AddTaskViewModel(AppDatabase database, int taskId) {
        task =  database.taskDao().loadTaskById(taskId);
    }

    public LiveData<TaskEntry> getTask() {
        return task;
    }
}
