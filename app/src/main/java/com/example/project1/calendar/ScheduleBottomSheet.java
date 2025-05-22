package com.example.project1.calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.Serializable;
import java.util.List;

public class ScheduleBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_EVENTS = "arg_events";

    public static ScheduleBottomSheet newInstance(String selectedDate, List<EventModel> events) {
        ScheduleBottomSheet fragment = new ScheduleBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, selectedDate);
        args.putSerializable(ARG_EVENTS, (Serializable) events);
        fragment.setArguments(args);
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

    static class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
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
            EventModel event = events.get(position);
            holder.titleTextView.setText(event.getTitle());
            holder.timeTextView.setText(event.getEventTime());
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, timeTextView;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);
            }
        }
    }
}
