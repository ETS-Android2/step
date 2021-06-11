package com.example.step.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.step.MainActivity;
import com.example.step.R;
import com.google.android.gms.maps.SupportMapFragment;


public class NotificationsFragment extends Fragment{

    TextView kilos;
    TextView velocity;
    SupportMapFragment fragment;
    Button beginEnd;
    Button clear;

    ExpandableListView directionsListView;


    final String begin = "BEGIN";
    final String end = "END";


    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        kilos = (TextView)root.findViewById(R.id.kilos);
        velocity = (TextView)root.findViewById(R.id.velocity);
        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        beginEnd = (Button)root.findViewById(R.id.beginEndButton);
        directionsListView = (ExpandableListView) root.findViewById(R.id.directions_list);
        clear = (Button) root.findViewById(R.id.clear_button);

        ((MainActivity)getActivity()).directoriesList(directionsListView);

        ((MainActivity) getActivity()).initContentMapView(kilos, velocity, fragment);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).ClearMap();
            }
        });

        Boolean checkRecord = ((MainActivity)getActivity()).getRecord();
        if (checkRecord!=null){
            if(checkRecord){
                setRunningRecord();
            }
            else{
                setNotRunningRecord();
            }
        }
        else{
            setNotRunningRecord();
        }

        beginEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(beginEnd.getText().equals(begin)){
                    ((MainActivity)getActivity()).setRecord(true);
                    ((MainActivity)getActivity()).setFirstPoint();
                    setRunningRecord();
                }
                else{
                    ((MainActivity)getActivity()).setRecord(false);
                    ((MainActivity)getActivity()).saveDirection();
                    setNotRunningRecord();
                }
            }
        });



        return root;
    }

    public void setRunningRecord(){
        beginEnd.setText(end);
        kilos.setAlpha(1.0f);
        velocity.setAlpha(1.0f);
    }

    public void setNotRunningRecord(){
        beginEnd.setText(begin);
        kilos.setAlpha(0.0f);
        velocity.setAlpha(0.0f);
        kilos.setText("0.0 m");
        velocity.setText("0 km/s");
    }

}