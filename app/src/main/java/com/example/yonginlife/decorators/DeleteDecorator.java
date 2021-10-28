package com.example.yonginlife.decorators;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.example.yonginlife.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class DeleteDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private CalendarDay date;
    public DeleteDecorator(CalendarDay _date, Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.delete_more);
        this.date = _date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        view.addSpan(new ForegroundColorSpan(Color.BLACK));
    }
}
