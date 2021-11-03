package com.example.momogae.Todo.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "task")
public class TaskEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    private boolean checked;

    //When adding new entry to database, id is automatically generated
    @Ignore
    public TaskEntry(String description, Date updatedAt, boolean checked) {
        this.description = description;
        this.updatedAt = updatedAt;
        this.checked = checked;
    }

    //when reading from database
    public TaskEntry(int id, String description, Date updatedAt, boolean checked) {
        this.id = id;
        this.description = description;
        this.updatedAt = updatedAt;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
