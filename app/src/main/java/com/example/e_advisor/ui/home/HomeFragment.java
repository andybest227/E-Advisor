package com.example.e_advisor.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.example.e_advisor.AddCourse;
import com.example.e_advisor.AddExamTip;
import com.example.e_advisor.CGPACalculator;
import com.example.e_advisor.CareerGuide;
import com.example.e_advisor.CourseRecommendation;
import com.example.e_advisor.ExamsTips;
import com.example.e_advisor.LoginActivity;
import com.example.e_advisor.Material;
import com.example.e_advisor.R;
import com.example.e_advisor.databinding.FragmentHomeBinding;
import com.example.e_advisor.utils.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GridLayout calendarGrid;
    private TextView monthYearText;
    private GestureDetector gestureDetector;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "e_advisor";
    private LocalDate selectedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarGrid = root.findViewById(R.id.calendar_grid);
        monthYearText = root.findViewById(R.id.month_year_text);
        Button btnPrev = root.findViewById(R.id.btn_prev);
        Button btnNext = root.findViewById(R.id.btn_next);
        Button examsTip = root.findViewById(R.id.examsTips);
        Button courseRecommendation = root.findViewById(R.id.courseRecommendation);
        Button careerGuide = root.findViewById(R.id.careerGuide);
        Button material_button = root.findViewById(R.id.material_button);
        Button cgpa_cal = root.findViewById(R.id.cgpa_cal);
        Button btnLogout = root.findViewById(R.id.btnLogout);
        Spinner adminSpinner = root.findViewById(R.id.custom_spinner);
        View calendarContainer = root.findViewById(R.id.cal_container);

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JWT jwt = new JWT(Token.getInstance().getToken());
        String role = jwt.getClaim("role").asString();
        if (Objects.equals(role, "admin")){
            adminSpinner.setVisibility(View.VISIBLE);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.admin_spinner_item,
                getResources().getStringArray(R.array.admin_privileges)
        );

        adapter.setDropDownViewResource(R.layout.admin_spinner_item);
        adminSpinner.setAdapter(adapter);

        examsTip.setOnClickListener(view -> {
            Intent examsTipsIntent = new Intent(requireContext(), ExamsTips.class);
            startActivity(examsTipsIntent);
        });

        courseRecommendation.setOnClickListener(view -> {
            Intent courseIntent = new Intent(requireContext(), CourseRecommendation.class);
            startActivity(courseIntent);
        });

        careerGuide.setOnClickListener(view -> {
            Intent careerIntent = new Intent(requireContext(), CareerGuide.class);
            startActivity(careerIntent);
        });

        material_button.setOnClickListener(view -> {
            Intent materialIntent = new Intent(requireContext(), Material.class);
            startActivity(materialIntent);
        });

        cgpa_cal.setOnClickListener(view -> {
            Intent cgpaIntent = new Intent(requireContext(), CGPACalculator.class);
            startActivity(cgpaIntent);
        });

        btnLogout.setOnClickListener(view -> {
            // Clear session data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Start LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);

            // Close the current activity
            requireActivity().finish();
        });

        //set on item select for admin spinner
        adminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (selectedItem.equals("Add Exam Tip")){
                    Intent examTipIntent = new Intent(getContext(), AddExamTip.class);
                    startActivity(examTipIntent);
                }
                if (selectedItem.equals("Add Course")){
                    Intent courseIntent = new Intent(getContext(), AddCourse.class);
                    startActivity(courseIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // ✅ Initialize selectedDate
        selectedDate = LocalDate.now();

        // Swipe detector
        gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener());
        calendarContainer.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        btnPrev.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            generateCalendar(selectedDate);
        });

        btnNext.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            generateCalendar(selectedDate);
        });

        // ✅ Generate initial calendar
        generateCalendar(selectedDate);


        BottomNavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateCalendar(LocalDate date) {
        calendarGrid.removeAllViews();
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)

        String monthYear = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + date.getYear();
        monthYearText.setText(monthYear);

        // Add empty cells before the first day
        for (int i = 1; i < dayOfWeek; i++) {
            calendarGrid.addView(createEmptyCell());
        }

        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayCell = new TextView(requireContext());
            dayCell.setText(String.valueOf(day));
            dayCell.setGravity(Gravity.CENTER);
            dayCell.setPadding(16, 16, 16, 16);

            LocalDate currentDate = LocalDate.of(date.getYear(), date.getMonth(), day);
            if (currentDate.isEqual(LocalDate.now())){
                dayCell.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_background, null));
                dayCell.setTextColor(Color.WHITE);
                dayCell.setTooltipText("Today");
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // Let it share space equally
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Spread evenly
            params.setMargins(8, 8, 8, 8);
            dayCell.setLayoutParams(params);

            // Optional styling
            dayCell.setTextColor(getResources().getColor(R.color.white));

            calendarGrid.addView(dayCell);
        }
    }


    private View createEmptyCell() {
        TextView emptyCell = new TextView(requireContext());
        emptyCell.setText("");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);

        emptyCell.setLayoutParams(params);
        return emptyCell;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}