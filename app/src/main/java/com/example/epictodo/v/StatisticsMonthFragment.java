package com.example.epictodo.v;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.epictodo.R;
import com.example.epictodo.m.FocusSession;
import com.example.epictodo.m.FocusSessionRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * StatisticsMonthFragment
 *
 * @author 31112
 * @date 2024/11/22
 */
public class StatisticsMonthFragment extends Fragment implements DatePickerBottomSheet.DateSelectedListener {
    private FocusProportionCard focusProportionCard;
    private BasicDataCard basicDataCard;
    private FocusSessionRepository repository;
    private TextView monthTitle;
    private TextView leftButton;
    private TextView rightButton;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_month, container, false);

        focusProportionCard = view.findViewById(R.id.month_focusProportionCard);

        basicDataCard = view.findViewById(R.id.month_basicDataCard);
        basicDataCard.setLifecycleOwner(getViewLifecycleOwner());

        monthTitle = view.findViewById(R.id.month_title);
        leftButton = view.findViewById(R.id.month_button_left);
        rightButton = view.findViewById(R.id.month_button_right);

        repository = new FocusSessionRepository(requireActivity().getApplication());

        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINESE);

        setupNavigation();
        updateMonthTitle();
        calculateMonthlyTimeRange();

        // Observe focus sessions
        repository.getAllFocusSession().observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });

        Button add = view.findViewById(R.id.month_add);
        Button delete = view.findViewById(R.id.month_delete);

        add.setOnClickListener(v -> addRandomSession());
        delete.setOnClickListener(v -> deleteSessionById());

        monthTitle.setOnClickListener(v -> showDatePicker());

        return view;
    }

    private void showDatePicker() {
        DatePickerBottomSheet datePickerBottomSheet = DatePickerBottomSheet.newInstance(currentDate);
        datePickerBottomSheet.setDateSelectedListener(this);
        datePickerBottomSheet.show(getChildFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(int year, int month, int dayOfMonth) {
        currentDate.set(year, month, 1);
        updateMonthTitle();
        calculateMonthlyTimeRange();
    }

    private void addRandomSession() {
        Random random = new Random();
        long currentTime = System.currentTimeMillis();
        long duration = random.nextInt(3600) * 1000; // 随机生成0到1小时之间的持续时间
        String tag = getRandomTag();

        FocusSession session = new FocusSession(
                duration, // dailyTotal
                (int) duration, // duration
                currentTime, // endTime
                0, // id (自动生成，可以传0)
                0, // monthlyTotal
                currentTime - duration, // startTime
                tag, // tag
                0, // weeklyTotal
                0 // yearlyTotal
        );

        repository.insert(session);
        Toast.makeText(requireContext(), "已添加随机记录", Toast.LENGTH_SHORT).show();
    }


    private String getRandomTag() {
        String[] tags = {"学习", "工作", "运动", "阅读", "编程"};
        Random random = new Random();
        return tags[random.nextInt(tags.length)];
    }

    private void deleteSessionById() {
        if (repository.getAllFocusSession().getValue() != null && !repository.getAllFocusSession().getValue().isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(repository.getAllFocusSession().getValue().size());
            FocusSession sessionToDelete = repository.getAllFocusSession().getValue().get(randomIndex);
            repository.delete(sessionToDelete);
            Toast.makeText(requireContext(), "已随机删除一条记录", Toast.LENGTH_SHORT).show();

            // 由于我们直接删除了，不需要再次观察LiveData
        } else {
            Toast.makeText(requireContext(), "没有可删除的记录", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateMonthlyTimeRange() {
        Calendar startOfMonth = (Calendar) currentDate.clone();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);
        long startOfMonthMillis = startOfMonth.getTimeInMillis();

        Calendar endOfMonth = (Calendar) startOfMonth.clone();
        endOfMonth.add(Calendar.MONTH, 1);
        endOfMonth.add(Calendar.MILLISECOND, -1);
        long endOfMonthMillis = endOfMonth.getTimeInMillis();

        focusProportionCard.setTimeRange(startOfMonthMillis, endOfMonthMillis);
        basicDataCard.setTimeRange(startOfMonthMillis, endOfMonthMillis);

        repository.getFocusSessionsForMonth(startOfMonthMillis, endOfMonthMillis).observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });
    }

    private void setupNavigation() {
        leftButton.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            updateMonthTitle();
            calculateMonthlyTimeRange();
        });

        rightButton.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, 1);
            updateMonthTitle();
            calculateMonthlyTimeRange();
        });
    }

    private void updateMonthTitle() {
        monthTitle.setText(dateFormat.format(currentDate.getTime()));
    }
}
