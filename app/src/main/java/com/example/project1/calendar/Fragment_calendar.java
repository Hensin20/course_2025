package com.example.project1.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project1.R;
import com.example.project1.ScheduleBottomSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_calendar extends Fragment {

    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);

        // Обробка вибору дати
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            fetchEvents(selectedDate);
        });

        return view;
    }

    private void fetchEvents(String date) {
        String url = "http://10.0.2.2:5000/api/events/" + date;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    List<EventModel> events = new Gson().fromJson(responseBody,
                            new TypeToken<List<EventModel>>(){}.getType());

                    requireActivity().runOnUiThread(() -> {
                        if (isAdded()) {  // Перевірка, чи фрагмент доданий до активності
                            ScheduleBottomSheet bottomSheet = ScheduleBottomSheet.newInstance(date, events);
                            bottomSheet.show(getParentFragmentManager(), "ScheduleBottomSheet");
                        }
                    });
                } else {
                    Log.e("API_ERROR", "Помилка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Мережева помилка: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Помилка завантаження подій", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}