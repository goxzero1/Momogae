package com.example.momogae.Todo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao{ //데이터베이스에 접근하기 위해 사용되는 메서드

    @Query("SELECT * FROM task ")
    LiveData<List<TaskEntity>> loadAllTasks();

    @Insert
    void insertTask(TaskEntity taskEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntity taskEntity);

    @Delete
    void deleteTask(TaskEntity taskEntity); //한가지 일 삭제

    @Query("DELETE FROM task ") //모든 할 일 삭제
    void deleteAll();

    @Query("SELECT * FROM task WHERE id = :id")
    LiveData<TaskEntity> loadTaskById(int id);
}
