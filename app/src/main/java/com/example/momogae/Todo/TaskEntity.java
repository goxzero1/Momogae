package com.example.momogae.Todo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "task")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true) //엔터티에는 Pk가 포함되어야 하는데 TodoActivity에는 필요성이 없기때문에 auto로 생성
    private int id;
    private String description; //할 일 내용설명
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;
    private boolean checked;

    //할일이 아직 입력되지 않은 엔터티 (id X )
    @Ignore
    public TaskEntity(String description, Date updatedAt, boolean checked) {
        this.description = description;
        this.updatedAt = updatedAt;
        this.checked = checked;
    }

    //할일이 생성된 후 id가 부여된 엔터티
    public TaskEntity(int id, String description, Date updatedAt, boolean checked) {
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
