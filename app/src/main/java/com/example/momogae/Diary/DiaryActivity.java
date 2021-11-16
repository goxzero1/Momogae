package com.example.momogae.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DiaryActivity extends AppCompatActivity
        implements OnDateSelectedListener {

    public static ArrayList<DiaryClass> diary_data;
    String userID, petName;
    private DatabaseReference diaryDB, dateDB;
    StorageReference mStorage;
    private final com.example.momogae.Diary.decorators.OneDayDecorator oneDayDecorator = new com.example.momogae.Diary.decorators.OneDayDecorator();
    private final com.example.momogae.Diary.decorators.TodayDecorator todayDecorator = new com.example.momogae.Diary.decorators.TodayDecorator();
    public static ArrayList<CalendarDay> dates;

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        petName = intent.getStringExtra("petName");
        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");
        mStorage = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.content_my_diary);
        ButterKnife.bind(this);
        diary_data = new ArrayList<DiaryClass>();
        dates = new ArrayList<CalendarDay>();

        dateDB = FirebaseDatabase.getInstance().getReference("/pet/"+userID+"/"+petName);
        String sort_column_name="date";
        Query sortbyDate = dateDB.orderByChild(sort_column_name);
        sortbyDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dates.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key.equals("petAge")||key.equals("petGender")||key.equals("petName")||key.equals("petSpecies")||
                            key.equals("petAbout")||key.equals("petFirstDate")||key.equals("petNeutralization")||key.equals("uid")) {continue;}
                    int year = Integer.parseInt(key.split("-")[0]);
                    int month = Integer.parseInt(key.split("-")[1]);
                    int day = Integer.parseInt(key.split("-")[2]);
                    dates.add(CalendarDay.from(year, month, day));
                    widget.removeDecorators();
                    widget.addDecorators(
                            new com.example.momogae.Diary.decorators.MySelectorDecorator(DiaryActivity.this, dates),
                            new com.example.momogae.Diary.decorators.HighlightWeekendsDecorator(),
                            oneDayDecorator,
                            todayDecorator
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        final LocalDate instance = LocalDate.now();
        widget.setSelectedDate(instance);

        final LocalDate min = LocalDate.of(instance.getYear(), Month.JANUARY, 1);
        final LocalDate max = LocalDate.of(instance.getYear(), Month.DECEMBER, 31);



        widget.state().edit().setMinimumDate(min).setMaximumDate(max).commit();

        widget.invalidateDecorators();
        widget.addDecorators(
                new com.example.momogae.Diary.decorators.MySelectorDecorator(this, dates),
                new com.example.momogae.Diary.decorators.HighlightWeekendsDecorator(),
                oneDayDecorator,
                todayDecorator
        );

    }


    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {
        //If you change a decorate, you need to invalidate decorators
        int Year = date.getYear();
        int Month = date.getMonth();
        int Day = date.getDay();

        String shot_Day = Year + "-" + Month + "-" + Day;
        widget.clearSelection();

        Toast.makeText(getApplicationContext(), shot_Day + "에 무슨 일이 있었개?" , Toast.LENGTH_LONG).show();
        oneDayDecorator.setDate(date.getDate());
        widget.invalidateDecorators();

        if (!dates.contains(date)){
            Intent write_intent = new Intent(DiaryActivity.this, DiaryWriteActivity.class);
            write_intent.putExtra("petName", petName);
            write_intent.putExtra("saveDate", shot_Day);
            dates.add(date);
            startActivity(write_intent);
        }

        else {
            diaryDB = FirebaseDatabase.getInstance().getReference("/pet/" + userID + "/" + petName);
            String sort_column_name = "title";
            Query sortbyTitle = diaryDB.orderByChild(sort_column_name);
            sortbyTitle.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    diary_data.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        if (key.equals(shot_Day)) {
                            DiaryClass get = postSnapshot.getValue(DiaryClass.class);
                            diary_data.add(get);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Intent intent = new Intent(DiaryActivity.this, DiaryLargeActivity.class);
            intent.putExtra("petName", petName);
            startActivity(intent);
        }
    }


}