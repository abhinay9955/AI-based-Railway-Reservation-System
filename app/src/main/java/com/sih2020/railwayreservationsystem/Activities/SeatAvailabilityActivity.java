package com.sih2020.railwayreservationsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sih2020.railwayreservationsystem.Adapters.TrainAdapter;
import com.sih2020.railwayreservationsystem.R;
import com.sih2020.railwayreservationsystem.Utils.AppConstants;
import com.sih2020.railwayreservationsystem.Utils.GenerateBackground;
import com.sih2020.railwayreservationsystem.Utils.NetworkRequests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

public class SeatAvailabilityActivity extends AppCompatActivity {

    private String mUrl;
    private NetworkRequests networkRequests;
    private CoordinatorLayout mCL;
    private static final String LOG_TAG = "SeatAvailability";
    private LinearLayout mToolbar;
    private RelativeLayout mMain, mProgress;
    public TrainAdapter mAdapter;
    private ListView mList;
    private TextView mDate, mTravelClass;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_availability);
        init();
        receiveClicks();
    }

    private void receiveClicks() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mUrl = getIntent().getStringExtra("url");
        mCL = findViewById(R.id.cl);
        mToolbar = findViewById(R.id.app_bar);
        mMain = findViewById(R.id.main_rl);
        mProgress = findViewById(R.id.progress_rl);
        mList = findViewById(R.id.trains_list);
        mDate = findViewById(R.id.date_tv);
        mTravelClass = findViewById(R.id.travel_class_tv);
        mBack = findViewById(R.id.back_iv);
        mAdapter = new TrainAdapter(SeatAvailabilityActivity.this, AppConstants.mTrainList);
        mList.setAdapter(mAdapter);

        networkRequests = new NetworkRequests(mCL, this, SeatAvailabilityActivity.this);
        networkRequests.fetchTrainsFromUrl(mUrl);
        mToolbar.setBackground(GenerateBackground.generateBackground());
        mMain.setAlpha(0.2f);
        String dateToShow = (String) DateFormat.format("MMM", AppConstants.mDate) + " " + (String) DateFormat.format("dd", AppConstants.mDate) + "," + (String) DateFormat.format("yyyy", AppConstants.mDate);
        mDate.setText(dateToShow);
        String tc = AppConstants.mClass.getmAbbreviation() + " - " + AppConstants.mClass.getmFullForm();
        mTravelClass.setText(tc);
    }

    public void trainListFetchComplete() {
        mMain.setAlpha(1.0f);
        mProgress.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        for (int i = 0; i < AppConstants.mTrainList.size(); i++) {
            ArrayList<String> route = AppConstants.mTrainList.get(i).getmCodedRoutes();
            int pos = 0;
            for (int j = 0; j < route.size(); j++) {
                if (route.get(i).trim().equalsIgnoreCase(AppConstants.mSourceStation.getmStationCode().toString())) {
                    pos = j;
                    break;
                }
            }
            String dayNo = AppConstants.mTrainList.get(i).getmDepartureTime().get(pos);
            int x = 0;
            for (int j = 0; j < dayNo.length(); j++) {
                try {
                    x = (int) dayNo.charAt(j) - 48;
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
            x = x - 1;
            Date date = AppConstants.mDate;
            String doj = (String) DateFormat.format("yyyy", date) + "-" + (String) DateFormat.format("MM", date) + "-" + (String) DateFormat.format("dd", date);
            String temp = AppConstants.mUrl + "/seats/" + AppConstants.mTrainList.get(i).getmTrainNo().trim() + "/" + AppConstants.mSourceStation.getmStationCode().trim() + "/" + AppConstants.mDestinationStation.getmStationCode() + "/" + AppConstants.mClass.getmAbbreviation().trim() + "/" + doj + "/" + AppConstants.mQuota.getmAbbreviation().trim();
            networkRequests.fetchSeatsData(AppConstants.mTrainList.get(i).getmTrainNo().trim(), temp);
        }
    }

}
