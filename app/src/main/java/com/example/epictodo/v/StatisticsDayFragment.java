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

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

/**
 * StatisticsDayFragment
 *
 * @author 31112
 * @date 2024/11/21
 */
public class StatisticsDayFragment extends Fragment implements DatePickerBottomSheet.DateSelectedListener {
    private FocusProportionCard focusProportionCard;
    private BasicDataCard basicDataCard;
    private FocusSessionRepository repository;
    private TextView dayTitle;
    private TextView leftButton;
    private TextView rightButton;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_day, container, false);

        focusProportionCard = view.findViewById(R.id.day_focusProportionCard);

        basicDataCard = view.findViewById(R.id.day_basicDataCard);
        basicDataCard.setLifecycleOwner(getViewLifecycleOwner());

        dayTitle = view.findViewById(R.id.day_title);
        leftButton = view.findViewById(R.id.day_button_left);
        rightButton = view.findViewById(R.id.day_button_right);

        repository = new FocusSessionRepository(requireActivity().getApplication());

        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINESE);

        setupNavigation();
        updateDateDisplay();
        calculateDailyTimeRange();

        // Observe focus sessions
        repository.getAllFocusSession().observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });


        Button add = view.findViewById(R.id.day_add);
        Button delete = view.findViewById(R.id.day_delete);

        add.setOnClickListener(v -> addRandomSession());
        delete.setOnClickListener(v -> deleteSessionById());

        dayTitle.setOnClickListener(v -> showDatePicker());

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
        updateDateDisplay();
        calculateDailyTimeRange();
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

    private void calculateDailyTimeRange() {
        Calendar startCalendar = (Calendar) currentDate.clone();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = startCalendar.getTimeInMillis();

        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = endCalendar.getTimeInMillis() - 1;

        focusProportionCard.setTimeRange(startOfDay, endOfDay);
        basicDataCard.setTimeRange(startOfDay, endOfDay);

        // 观察当日的数据
        repository.getFocusSessionsForDay(startOfDay, endOfDay).observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });

    }

    private void setupNavigation() {
        leftButton.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, -1);
            updateDateDisplay();
            calculateDailyTimeRange();
        });

        rightButton.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            updateDateDisplay();
            calculateDailyTimeRange();
        });
    }

    private void updateDateDisplay() {
        dayTitle.setText(dateFormat.format(currentDate.getTime()));
    }

}