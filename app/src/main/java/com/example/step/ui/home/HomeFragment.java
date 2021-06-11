package com.example.step.ui.home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.step.MainActivity;
import com.example.step.R;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    Button days;
    Button weeks;
    Button months;
    ProgressBar progressBar;
    TextView progGoal;
    Button setGoal;
    Button changeGoal;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        days = (Button)root.findViewById(R.id.b_days);
        weeks = (Button)root.findViewById(R.id.b_weeks);
        months = (Button)root.findViewById(R.id.b_months);
        progressBar = (ProgressBar)root.findViewById(R.id.progress_bar);
        progGoal = (TextView)root.findViewById(R.id.prog_goal);
        setGoal = (Button)root.findViewById(R.id.setGoal);
        changeGoal = (Button)root.findViewById(R.id.changeButton);

        days.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                float dailyGoal = ((MainActivity)getActivity()).getDailyGoal();
                float dailyKilos = ((MainActivity)getActivity()).getTodaysKilometers();
                String goalProgress;
                days.setTextColor(getResources().getColor(R.color.colorAccent));
                weeks.setTextColor(getResources().getColor(R.color.colorBlack));
                months.setTextColor(getResources().getColor(R.color.colorBlack));
                if(dailyGoal==0){
                    progGoal.setText("Set a new daily goal!");
                    progGoal.setTextSize(20);
                    progressBar.setProgress(0);
                    changeGoal.setAlpha(0.0f);
                    setGoal.setAlpha(1.0f);
                    setGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Daily Goal");


                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setDailyGoal(Float.parseFloat(input.getText().toString()));
                                    days.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });

                }
                else {
                    setGoal.setAlpha(0.0f);
                    progressBar.setProgress((int)(dailyKilos*100/dailyGoal));
                    progGoal.setTextSize(40);
                    goalProgress=String.format("%.2f",dailyKilos);
                    goalProgress+="/"+String.format("%.2f",dailyGoal)+ "km";
                    progGoal.setText(goalProgress);
                    changeGoal.setAlpha(1.0f);
                    changeGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Daily Goal");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setDailyGoal(Float.parseFloat(input.getText().toString()));
                                    days.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            }
        });
        weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float weeklyGoal = ((MainActivity)getActivity()).getWeeklyGoal();
                float weeklyKilos = ((MainActivity)getActivity()).getWeeksKilometers();
                String goalProgress;
                days.setTextColor(getResources().getColor(R.color.colorBlack));
                weeks.setTextColor(getResources().getColor(R.color.colorAccent));
                months.setTextColor(getResources().getColor(R.color.colorBlack));
                if(weeklyGoal==0){
                    progGoal.setText("Set a new weekly goal!");
                    progGoal.setTextSize(20);
                    progressBar.setProgress(0);
                    changeGoal.setAlpha(0.0f);
                    setGoal.setAlpha(1.0f);
                    setGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Weekly Goal");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setWeeklyGoal(Float.parseFloat(input.getText().toString()));
                                    weeks.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });

                }
                else {
                    setGoal.setAlpha(0.0f);
                    progressBar.setProgress((int)(weeklyKilos*100/weeklyGoal));
                    progGoal.setTextSize(40);
                    goalProgress=String.format("%.1f",weeklyKilos);
                    goalProgress+="/"+String.format("%.1f",weeklyGoal)+ "km";
                    progGoal.setText(goalProgress);
                    changeGoal.setAlpha(1.0f);
                    changeGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Daily Goal");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setWeeklyGoal(Float.parseFloat(input.getText().toString()));
                                    weeks.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                }

            }
        });
        months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float monthlyGoal = ((MainActivity)getActivity()).getMonthlyGoal();
                float monthlyKilos = ((MainActivity)getActivity()).getMonthsKilometers();
                String goalProgress;
                days.setTextColor(getResources().getColor(R.color.colorBlack));
                weeks.setTextColor(getResources().getColor(R.color.colorBlack));
                months.setTextColor(getResources().getColor(R.color.colorAccent));

                if(monthlyGoal==0){
                    progGoal.setText("Set a new monthly goal!");
                    progGoal.setTextSize(20);
                    progressBar.setProgress(0);
                    changeGoal.setAlpha(0.0f);
                    setGoal.setAlpha(1.0f);
                    setGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Monthly Goal");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setMonthlyGoal(Float.parseFloat(input.getText().toString()));
                                    months.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });

                }
                else {
                    setGoal.setAlpha(0.0f);
                    progressBar.setProgress((int)(monthlyKilos*100/monthlyGoal));
                    progGoal.setTextSize(40);
                    goalProgress=String.format("%.0f",monthlyKilos);
                    goalProgress+="/"+String.format("%.0f",monthlyGoal)+ "km";
                    progGoal.setText(goalProgress);
                    changeGoal.setAlpha(1.0f);
                    changeGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Monthly Goal");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)getActivity()).setMonthlyGoal(Float.parseFloat(input.getText().toString()));
                                    months.performClick();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                }

            }
        });
        days.performClick();

        return root;
    }
}