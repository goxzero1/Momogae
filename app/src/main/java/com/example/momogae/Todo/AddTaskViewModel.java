package com.example.momogae.Todo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

class AddTaskViewModel extends ViewModel {

    private LiveData<TaskEntity> task;


    public AddTaskViewModel(AppDatabase database, int taskId) {
        task =  database.taskDao().loadTaskById(taskId);  //데이터베이스에 있는 할 일 로드
    }

    public LiveData<TaskEntity> getTask() {
        return task;
    } //getTask(): 할일을 리턴한다
}
