package com.example.step.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.step.*;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {
    Button days;
    Button weeks;
    Button  months;
    TextView AveragePer;
    TextView AverageVal;
    TextView TotalPer;
    TextView TotalVal;
    BarChart barChart;


    FirebaseDatabaseHelper fdb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        fdb = ((MainActivity)getActivity()).getFdb();
        days = (Button)root.findViewById(R.id.b_days);
        weeks = (Button)root.findViewById(R.id.b_weeks);
        months= (Button)root.findViewById(R.id.b_months);
        AveragePer = (TextView)root.findViewById(R.id.AveragePer);
        AverageVal = (TextView)root.findViewById(R.id.AverageVal);
        TotalPer = (TextView)root.findViewById(R.id.TotalPer);
        TotalVal = (TextView)root.findViewById(R.id.TotalVal);
        barChart = (BarChart)root.findViewById(R.id.barGraph);


        days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDailyClick();
            }
        });
        weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnWeeklyClick();
            }
        });
        months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMonthlyClick();
            }
        });
        days.performClick();

        return root;
    }

    public void OnDailyClick(){
        days.setTextColor(getResources().getColor(R.color.colorAccent));
        weeks.setTextColor(getResources().getColor(R.color.colorBlack));
        months.setTextColor(getResources().getColor(R.color.colorBlack));

        float total=((MainActivity)getActivity()).getTodaysKilometers();
        float average=0;
        barChart.removeAllViews();

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int i=0;
        ArrayList<Day> days = ((MainActivity)getActivity()).getDailyStats();
        for (Day day : days) {
            barEntries.add(new BarEntry(i, day.getKilometers()));
            average+=day.getKilometers();
            i++;
        }
        if(i>0){
            average=average/i;
        }
        while(i<7){
            barEntries.add(new BarEntry(i,0));
            i++;
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"kilometers");


        BarData dt = new BarData();
        dt.addDataSet(barDataSet);
        //BarData data = new BarData(theDates,barDataSet);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setData(dt);
        AveragePer.setText("Average per day: ");
        AverageVal.setText(String.format("%.2f",average)+" km");
        TotalPer.setText("Total kilometers today: ");
        TotalVal.setText(String.format("%.2f",total)+" km");


    }
    public void OnWeeklyClick(){
        days.setTextColor(getResources().getColor( R.color.colorBlack));
        weeks.setTextColor(getResources().getColor( R.color.colorAccent));
        months.setTextColor(getResources().getColor( R.color.colorBlack));
        float total=((MainActivity)getActivity()).getWeeksKilometers();
        float average=0;


        barChart.removeAllViews();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        //otan ftiaksw thn vash xrhsimopoiw auto,twra dinw dikes mou times
        int i=0;
        ArrayList<Week> weeks = ((MainActivity)getActivity()).getWeeklyStats();
        for (Week week : weeks) {
            barEntries.add(new BarEntry(i, week.getKilometers()));
            i++;
            average+=week.getKilometers();
        }
        if(i>0){
            average=average/i;
        }
        while(i<4){
            barEntries.add(new BarEntry(i,0));
            i++;
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"kilometers");

        BarData dt = new BarData(barDataSet);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setData(dt);

        AveragePer.setText("Average per week: ");
        TotalPer.setText("Total kilometers this week: ");
        AverageVal.setText(String.format("%.2f",average)+" km");
        TotalVal.setText(String.format("%.2f",total)+" km");

    }
    public void OnMonthlyClick(){
        float total=((MainActivity)getActivity()).getWeeksKilometers();
        float average=0;

        days.setTextColor(getResources().getColor(R.color.colorBlack));
        weeks.setTextColor(getResources().getColor(R.color.colorBlack));
        months.setTextColor(getResources().getColor(R.color.colorAccent));

        barChart.removeAllViews();

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        //otan ftiaksw thn vash xrhsimopoiw auto,twra dinw dikes mou times
        int i=0;
        ArrayList<Month> months = ((MainActivity)getActivity()).getMonthlyStats();
        for (Month month : months) {
            barEntries.add(new BarEntry(i, month.getKilometers()));
            i++;
            average+=month.getKilometers();
        }
        if(i>0){
            average=average/i;
        }
        while(i<12){
            barEntries.add(new BarEntry(i,0));
            i++;
        }
        IBarDataSet barDataSet = new BarDataSet(barEntries,"kilometers");

        BarData dt = new BarData(barDataSet);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setData(dt);
        AveragePer.setText("Average per month: ");
        TotalPer.setText("Total kilometers this year: ");
        AverageVal.setText(String.format("%.2f",average)+" km");
        TotalVal.setText(String.format("%.2f",total)+" km");

    }

}