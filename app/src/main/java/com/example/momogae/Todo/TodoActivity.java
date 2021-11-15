package com.example.momogae.Todo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momogae.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;


public class TodoActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener, TaskAdapter.CheckBoxCheckListener {


    private static final String TAG = TodoActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private TextView mEmptyView;
    private ConstraintLayout mConstraintLayout;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        mRecyclerView = findViewById(R.id.recyclerViewTasks);
        mEmptyView = findViewById(R.id.emptyView);
        mConstraintLayout = findViewById(R.id.constraintLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TaskAdapter(this, this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mDb = AppDatabase.getInstance(getApplicationContext());


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = viewHolder.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(mAdapter.getTasks(), position_dragged, position_target);
                mAdapter.notifyItemMoved(position_dragged, position_target);
                return false;
            }


            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks =  mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);


        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addTaskIntent = new Intent(TodoActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });


        setupViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_tasks:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.taskDao().deleteAll();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(TodoActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onCheckBoxCheckListener(final TaskEntry taskEntry) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.taskDao().updateTask(taskEntry);
                Log.d("oncheckList", " "+taskEntry.isChecked() +"---");
            }
        });
    }


    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) { //runs on main thread


                if(taskEntries.isEmpty()){
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                mAdapter.setTasks(taskEntries);
            }
        });
    }

}