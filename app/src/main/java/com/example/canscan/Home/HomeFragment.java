package com.example.canscan.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canscan.Barcode.BarcodeCameraActivity;
import com.example.canscan.LeaderBoardFragment;
import com.example.canscan.ProfileFragment;
import com.example.canscan.R;
import com.example.canscan.RewardsFragment;
import com.example.canscan.User.UserLab;
import com.example.canscan.User.UserLab.DatabaseObserver;

import java.util.Objects;

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

        try {
            mPointsEarned.setText(String.valueOf(UserLab.get().getCurrentUser().getScore()));
        }
        catch (NullPointerException exception) {
            exception.printStackTrace();
        }

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
        mSettingsButton = view.findViewById(R.id.profile_btn);
    }

    private void setOnClickListeners() {
        mScanCodeButton.setOnClickListener(this);
        mLeaderBoardButton.setOnClickListener(this);
        mRewardsButton.setOnClickListener(this);
        mSettingsButton.setOnClickListener(this);
    }

    @Override
    public void notifyObserverUserCreatedFromDatabase(boolean shouldPullFromDatabase) {
        mPointsEarned.setText(String.valueOf(UserLab.get().getCurrentUser().getScore()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_code_btn:
                startCameraActivity();
                break;
            case R.id.leader_board_btn:
                startFragment(new LeaderBoardFragment());
                break;
            case R.id.rewards_btn:
                startFragment(new RewardsFragment());
                break;
            case R.id.profile_btn:
                startFragment(new ProfileFragment());
                break;
            default:
                Toast.makeText(getContext(), "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(getContext(), BarcodeCameraActivity.class);
        startActivity(intent);
    }

    private void startFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
