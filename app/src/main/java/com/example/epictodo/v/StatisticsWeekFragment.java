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
 * StatisticsWeekFragment
 *
 * @author 31112
 * @date 2024/11/22
 */
public class StatisticsWeekFragment extends Fragment implements DatePickerBottomSheet.DateSelectedListener {
    private FocusProportionCard focusProportionCard;
    private BasicDataCard basicDataCard;
    private FocusSessionRepository repository;
    private TextView weekTitle;
    private TextView leftButton;
    private TextView rightButton;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_week, container, false);

        focusProportionCard = view.findViewById(R.id.week_focusProportionCard);

        basicDataCard = view.findViewById(R.id.week_basicDataCard);
        basicDataCard.setLifecycleOwner(getViewLifecycleOwner());

        weekTitle = view.findViewById(R.id.week_title);
        leftButton = view.findViewById(R.id.week_button_left);
        rightButton = view.findViewById(R.id.week_button_right);

        repository = new FocusSessionRepository(requireActivity().getApplication());

        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM月dd日", Locale.CHINESE);

        setupNavigation();
        updateWeekTitle();
        calculateWeeklyTimeRange();

        // Observe focus sessions
        repository.getAllFocusSession().observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });


        Button add = view.findViewById(R.id.week_add);
        Button delete = view.findViewById(R.id.week_delete);

        add.setOnClickListener(v -> addRandomSession());
        delete.setOnClickListener(v -> deleteSessionById());

        weekTitle.setOnClickListener(v -> showDatePicker());

        return view;
    }

    private void showDatePicker() {
        DatePickerBottomSheet datePickerBottomSheet = DatePickerBottomSheet.newInstance(currentDate);
        datePickerBottomSheet.setDateSelectedListener(this);
        datePickerBottomSheet.show(getChildFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(int year, int month, int dayOfMonth) {
        currentDate.set(year, month, dayOfMonth);
        updateWeekTitle();
        calculateWeeklyTimeRange();
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

    private void calculateWeeklyTimeRange() {
        Calendar startOfWeek = (Calendar) currentDate.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        startOfWeek.set(Calendar.HOUR_OF_DAY, 0);
        startOfWeek.set(Calendar.MINUTE, 0);
        startOfWeek.set(Calendar.SECOND, 0);
        startOfWeek.set(Calendar.MILLISECOND, 0);
        long startOfWeekMillis = startOfWeek.getTimeInMillis();

        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6);
        endOfWeek.set(Calendar.HOUR_OF_DAY, 23);
        endOfWeek.set(Calendar.MINUTE, 59);
        endOfWeek.set(Calendar.SECOND, 59);
        endOfWeek.set(Calendar.MILLISECOND, 999);
        long endOfWeekMillis = endOfWeek.getTimeInMillis();

        focusProportionCard.setTimeRange(startOfWeekMillis, endOfWeekMillis);
        basicDataCard.setTimeRange(startOfWeekMillis, endOfWeekMillis);

        repository.getFocusSessionsForWeek(startOfWeekMillis, endOfWeekMillis).observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });
    }

    private void setupNavigation() {
        leftButton.setOnClickListener(v -> {
            currentDate.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekTitle();
            calculateWeeklyTimeRange();
        });

        rightButton.setOnClickListener(v -> {
            currentDate.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekTitle();
            calculateWeeklyTimeRange();
        });
    }

    private void updateWeekTitle() {
        Calendar startOfWeek = (Calendar) currentDate.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        Calendar endOfWeek = (Calendar) startOfWeek.clone();
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6);

        String weekRange = dateFormat.format(startOfWeek.getTime()) + "-" + dateFormat.format(endOfWeek.getTime());
        weekTitle.setText(weekRange);
    }
}
