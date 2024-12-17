package com.example.epictodo.epic.statistics;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.epictodo.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
/**
 * DatePickerBottomSheet
 *
 * @author 31112
 * @date 2024/11/25
 */
public class DatePickerBottomSheet extends BottomSheetDialogFragment {

    private DateSelectedListener listener;
    private Calendar selectedDate;

    public interface DateSelectedListener {
        void onDateSelected(int year, int month, int dayOfMonth);
    }

    public static DatePickerBottomSheet newInstance(Calendar initialDate) {
        DatePickerBottomSheet fragment = new DatePickerBottomSheet();
        fragment.selectedDate = initialDate;
        return fragment;
    }

    public void setDateSelectedListener(DateSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_date_picker, container, false);

        DatePicker datePicker = view.findViewById(R.id.datePicker);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        // Set initial date
        datePicker.init(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH), null);

        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateSelected(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }
}
