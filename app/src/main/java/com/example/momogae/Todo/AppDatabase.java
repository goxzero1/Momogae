package com.example.momogae.Todo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TaskEntity.class}, version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)

public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if(sInstance == null){
            synchronized (LOCK){

                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME).build(); //todolist 데이터베이스 생성
            }
        }

        return sInstance;
    }

    public abstract TaskDao taskDao();
}
