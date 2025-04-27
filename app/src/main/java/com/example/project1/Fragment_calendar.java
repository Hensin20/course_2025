package com.example.project1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

public class Fragment_calendar extends Fragment {

    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView); // Знаходимо календар за id
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Формуємо строку дати
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                // Створюємо і показуємо шторку
                ScheduleBottomSheet bottomSheet = ScheduleBottomSheet.newInstance(selectedDate);
                bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
            }
        });

        return view;
    }
}
