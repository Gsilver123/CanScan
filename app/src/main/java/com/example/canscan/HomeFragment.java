package com.example.canscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canscan.UserLab.DatabaseObserver;

public class HomeFragment extends Fragment implements View.OnClickListener, DatabaseObserver {

    private TextView mPointsEarned;
    private Button mScanCodeButton;
    private Button mLeaderBoardButton;
    private Button mRewardsButton;
    private Button mSettingsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.user_home, container, false);

        initializeViewVariables(view);
        setOnClickListeners();

        UserLab.get().registerDatabaseObserver(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserLab.get().removeDatabaseObserver(this);
    }

    private void initializeViewVariables(View view) {
        mPointsEarned = view.findViewById(R.id.earned_points_textView);
        mScanCodeButton = view.findViewById(R.id.scan_code_btn);
        mLeaderBoardButton = view.findViewById(R.id.leader_board_btn);
        mRewardsButton = view.findViewById(R.id.rewards_btn);
        mSettingsButton = view.findViewById(R.id.settings_btn);
    }

    private void setOnClickListeners() {
        mScanCodeButton.setOnClickListener(this);
        mLeaderBoardButton.setOnClickListener(this);
        mRewardsButton.setOnClickListener(this);
        mSettingsButton.setOnClickListener(this);
    }

    @Override
    public void notifyDatabaseCompletedTasks() {
        mPointsEarned.setText(String.valueOf(UserLab.get().getCurrentUser().getScore()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_code_btn:
                Toast.makeText(getContext(), "Barcode scanner", Toast.LENGTH_SHORT).show();
                break;
            case R.id.leader_board_btn:
                Toast.makeText(getContext(), "Leader Boards", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rewards_btn:
                Toast.makeText(getContext(), "Rewards", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings_btn:
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
