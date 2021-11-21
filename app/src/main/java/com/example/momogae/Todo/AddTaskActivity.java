package com.example.momogae.Todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momogae.R;

import java.util.Date;


public class AddTaskActivity extends AppCompatActivity {


    public static final String EXTRA_TASK_ID = "extraTaskId";
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    private static final int DEFAULT_TASK_ID = -1;

    EditText mEditText;
    Button mButton;

    private int mTaskId = DEFAULT_TASK_ID;
    private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews(); //할 일 추가 코드

         mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent(); //TodoActiviy에서 보낸 인텐트 받아와서
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) { //만약 인텐트가 비어있지 않다면
            mButton.setText(R.string.update_button); //할 일 수정하기
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                final AddTaskViewModel viewModel = new ViewModelProvider(this, factory).get(AddTaskViewModel.class);
                viewModel.getTask().observe(this, new Observer<TaskEntity>() {
                    @Override
                    public void onChanged(TaskEntity taskEntity) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntity);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }


    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription); //해야 할 일이 무엇인가요?
        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }


    private void populateUI(TaskEntity task) {
        if (task == null ){
            return;
        }
        mEditText.setText(task.getDescription());
    }


    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        Date date = new Date();

        if(description.equals("")){ //할일을 입력하지 않은 경우
            Toast.makeText(this,"할 일을 입력하세요", Toast.LENGTH_SHORT).show();
        }else{ //할 일을 입력한 경우 할 일 추가
            final TaskEntity taskEntity = new TaskEntity(description, date, false );

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if(mTaskId == DEFAULT_TASK_ID){
                        mDb.taskDao().insertTask(taskEntity);
                    }else {
                        taskEntity.setId(mTaskId);
                        mDb.taskDao().updateTask(taskEntity);
                    }
                    finish();
                }
            });
        }
    }
}
