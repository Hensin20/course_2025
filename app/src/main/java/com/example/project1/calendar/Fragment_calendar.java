package com.example.project1.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project1.ApiClient;
import com.example.project1.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_calendar extends Fragment {

    private CalendarView calendarView;
    private OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(v -> showEventDialog());

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            fetchEvents(selectedDate);
        });

        return view;
    }

    private void showEventDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_event, null);
        bottomSheetDialog.setContentView(view);

        EditText editTextEventName = view.findViewById(R.id.editTextEventName);
        EditText editTextDate = view.findViewById(R.id.editTextDate);
        EditText editTextTime = view.findViewById(R.id.editTextTime);
        Button buttonSaveEvent = view.findViewById(R.id.buttonSaveEvent);

        buttonSaveEvent.setOnClickListener(v -> {
            String eventName = editTextEventName.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();

            if (eventName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveEvent(eventName, date, time);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void saveEvent(String name, String date, String time) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"title\":\"" + name + "\",\"eventDate\":\"" + date + "\",\"eventTime\":\"" + time + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL +"/api/events/add-event")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() != null && isAdded()) {
                    Log.d("Event", "Sending JSON: " + json);
                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "✅ Подія збережена!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "❌ Помилка збереження!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null && isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "⚠ Помилка підключення!", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void fetchEvents(String date) {
        String url = ApiClient.BASE_URL +"/api/events/" + date;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    List<EventModel> events = new Gson().fromJson(responseBody,
                            new TypeToken<List<EventModel>>(){}.getType());

                    if (getActivity() != null && isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            ScheduleBottomSheet bottomSheet = ScheduleBottomSheet.newInstance(date, events);
                            bottomSheet.show(getParentFragmentManager(), "ScheduleBottomSheet");
                        });
                    }
                } else {
                    Log.e("API_ERROR", "Помилка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Мережева помилка: " + e.getMessage());
                if (getActivity() != null && isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Помилка завантаження подій", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
