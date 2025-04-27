package com.example.project1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ScheduleBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_SCHEDULE = "arg_schedule";

    public static ScheduleBottomSheet newInstance(String selectedDate) {
        ScheduleBottomSheet fragment = new ScheduleBottomSheet();
        Bundle args = new Bundle();
        args.putString("selectedDate", selectedDate);
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
        TextView scheduleTextView = view.findViewById(R.id.scheduleTextView);

        Bundle args = getArguments();
        if (args != null) {
            dateTextView.setText(args.getString(ARG_DATE));
            scheduleTextView.setText(args.getString(ARG_SCHEDULE));
        }

        return view;
    }
}
