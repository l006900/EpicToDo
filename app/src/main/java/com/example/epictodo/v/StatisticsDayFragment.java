package com.example.epictodo.v;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.epictodo.R;
import com.example.epictodo.m.FocusSession;
import com.example.epictodo.m.FocusSessionRepository;
import java.util.Random;

/**
 * StatisticsDayFragment
 *
 * @author 31112
 * @date 2024/11/21
 */
public class StatisticsDayFragment extends Fragment {
    private FocusProportionCard focusProportionCard;
    private FocusSessionRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_day, container, false);

        focusProportionCard = view.findViewById(R.id.day_focusProportionCard);

        repository = new FocusSessionRepository(requireActivity().getApplication());

        // Observe focus sessions
        repository.getAllFocusSession().observe(getViewLifecycleOwner(), focusSessions -> {
            focusProportionCard.setFocusSessions(focusSessions);
        });


        Button add = view.findViewById(R.id.day_add);
        Button delete = view.findViewById(R.id.day_delete);

        add.setOnClickListener(v -> addRandomSession());
        delete.setOnClickListener(v -> deleteSessionById());

        return view;
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


}
