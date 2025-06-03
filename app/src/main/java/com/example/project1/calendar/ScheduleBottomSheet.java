package com.example.project1.calendar;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.Serializable;
import java.util.List;

import okhttp3.OkHttpClient;

public class ScheduleBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_EVENTS = "arg_events";
    private Fragment_calendar fragmentCalendar;

    public static ScheduleBottomSheet newInstance(String selectedDate, List<EventModel> events, Fragment_calendar fragmentCalendar) {
        ScheduleBottomSheet fragment = new ScheduleBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, selectedDate);
        args.putSerializable(ARG_EVENTS, (Serializable) events);
        fragment.setArguments(args);
        fragment.fragmentCalendar = fragmentCalendar; // ✅ Зберігаємо фрагмент для виклику методів
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_schedule, container, false);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        RecyclerView recyclerView = view.findViewById(R.id.scheduleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        if (args != null) {
            dateTextView.setText(args.getString(ARG_DATE));
            List<EventModel> events = (List<EventModel>) args.getSerializable(ARG_EVENTS);
            recyclerView.setAdapter(new EventAdapter(events));
        }

        return view;
    }

    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        private final List<EventModel> events;

        EventAdapter(List<EventModel> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SharedPreferences prefs = getActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
            String userRole = prefs.getString("userRole", "user"); // ✅ Отримуємо роль
            if(!userRole.equals("admin")){
                holder.imageView_event_delete.setVisibility(View.GONE);
                holder.imageView_event_edit.setVisibility(View.GONE);
            } else {
                holder.imageView_event_delete.setVisibility(View.VISIBLE);
                holder.imageView_event_edit.setVisibility(View.VISIBLE);
            }

            EventModel event = events.get(position);
            holder.titleTextView.setText(event.getTitle());
            holder.timeTextView.setText(event.getEventTime());

            // ✅ Виклик методів з `Fragment_calendar`
            holder.imageView_event_delete.setOnClickListener(v ->
                    fragmentCalendar.deleteEvent(event.getId(), position, this, events)
            );
            holder.imageView_event_edit.setOnClickListener(v ->
                    fragmentCalendar.editEvent(event)
            );
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, timeTextView;
            ImageView imageView_event_edit, imageView_event_delete;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);
                imageView_event_edit = itemView.findViewById(R.id.imageView_edit_event);
                imageView_event_delete = itemView.findViewById(R.id.imageView_delete_event);
            }
        }
    }
}


