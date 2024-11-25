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
 * StatisticsYearFragment
 *
 * @author 31112
 * @date 2024/11/22
 */
public class StatisticsYearFragment extends Fragment implements DatePickerBottomSheet.DateSelectedListener {
    private FocusProportionCard focusProportionCard;
    private BasicDataCard basicDataCard;
    private FocusSessionRepository repository;
    private TextView yearTitle;
    private TextView leftButton;
    private TextView rightButton;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_year, container, false);

        focusProportionCard = view.findViewById(R.id.year_focusProportionCard);

        basicDataCard = view.findViewById(R.id.year_basicDataCard);
        basicDataCard.setLifecycleOwner(getViewLifecycleOwner());

        yearTitle = view.findViewById(R.id.year_title);
        leftButton = view.findViewById(R.id.year_button_left);
        rightButton = view.findViewById(R.id.year_button_right);

        repository = new FocusSessionRepository(requireActivity().getApplication());

        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年", Locale.CHINESE);

        setupNavigation();
        updateYearTitle();
        calculateYearlyTimeRange();

        // Observe focus sessions
        repository.getAllFocusSession().observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });

        Button add = view.findViewById(R.id.year_add);
        Button delete = view.findViewById(R.id.year_delete);

        add.setOnClickListener(v -> addRandomSession());
        delete.setOnClickListener(v -> deleteSessionById());

        yearTitle.setOnClickListener(v -> showDatePicker());

        return view;
    }

    private void showDatePicker() {
        DatePickerBottomSheet datePickerBottomSheet = DatePickerBottomSheet.newInstance(currentDate);
        datePickerBottomSheet.setDateSelectedListener(this);
        datePickerBottomSheet.show(getChildFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(int year, int month, int dayOfMonth) {
        currentDate.set(Calendar.YEAR, year);
        updateYearTitle();
        calculateYearlyTimeRange();
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

    private void calculateYearlyTimeRange() {
        Calendar startOfYear = (Calendar) currentDate.clone();
        startOfYear.set(Calendar.DAY_OF_YEAR, 1);
        startOfYear.set(Calendar.HOUR_OF_DAY
                , 0);
        startOfYear.set(Calendar.MINUTE, 0);
        startOfYear.set(Calendar.SECOND, 0);
        startOfYear.set(Calendar.MILLISECOND, 0);
        long startOfYearMillis = startOfYear.getTimeInMillis();

        Calendar endOfYear = (Calendar) startOfYear.clone();
        endOfYear.add(Calendar.YEAR, 1);
        endOfYear.add(Calendar.MILLISECOND, -1);
        long endOfYearMillis = endOfYear.getTimeInMillis();

        focusProportionCard.setTimeRange(startOfYearMillis, endOfYearMillis);
        basicDataCard.setTimeRange(startOfYearMillis, endOfYearMillis);

        repository.getFocusSessionsForYear(startOfYearMillis, endOfYearMillis).observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });
    }

    private void setupNavigation() {
        leftButton.setOnClickListener(v -> {
            currentDate.add(Calendar.YEAR, -1);
            updateYearTitle();
            calculateYearlyTimeRange();
        });

        rightButton.setOnClickListener(v -> {
            currentDate.add(Calendar.YEAR, 1);
            updateYearTitle();
            calculateYearlyTimeRange();
        });
    }

    private void updateYearTitle() {
        yearTitle.setText(dateFormat.format(currentDate.getTime()));
    }
}
