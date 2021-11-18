package com.example.momogae.Diary.decorators;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.DayOfWeek;

public class HighlightWeekendsDecorator implements DayViewDecorator {

    public HighlightWeekendsDecorator() {
    }

    @Override
    public boolean shouldDecorate(final CalendarDay day) {
        final DayOfWeek weekDay = day.getDate().getDayOfWeek();
        return weekDay == DayOfWeek.SATURDAY || weekDay == DayOfWeek.SUNDAY; //토요일, 일요일 컬러 빨강
    }

    @Override
    public void decorate(final DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}

