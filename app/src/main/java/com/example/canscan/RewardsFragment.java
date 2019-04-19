package com.example.canscan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.canscan.Barcode.BarcodeLab;
import com.example.canscan.User.UserLab;

public class RewardsFragment extends Fragment implements View.OnClickListener {

    private static final int NFTA_REWARD_POINTS = 1000;
    private static final int BIKE_SHARE_REWARDS_POINTS = 2500;
    private static final int BILLS_REWARDS_POINTS = 15000;
    private static final int SABRES_REWARDS_POINTS = 15000;

    private Button mNftaRewardsButton;
    private Button mBikeShareRewardsButton;
    private Button mBillsRewardsButton;
    private Button mSabresRewardsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.rewards, container, false);

        initializeViewVariables(view);
        setOnClickListeners();

        return view;
    }

    private void initializeViewVariables(View view) {
        mNftaRewardsButton = view.findViewById(R.id.metro_rewards_btn);
        mBikeShareRewardsButton = view.findViewById(R.id.bike_rewards_btn);
        mBillsRewardsButton = view.findViewById(R.id.bills_rewards_btn);
        mSabresRewardsButton = view.findViewById(R.id.sabres_rewards_btn);
    }

    private void setOnClickListeners() {
        mNftaRewardsButton.setOnClickListener(this);
        mBikeShareRewardsButton.setOnClickListener(this);
        mBillsRewardsButton.setOnClickListener(this);
        mSabresRewardsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.metro_rewards_btn:
                maybeClaimReward(NFTA_REWARD_POINTS);
                break;
            case R.id.bike_rewards_btn:
                maybeClaimReward(BIKE_SHARE_REWARDS_POINTS);
                break;
            case R.id.bills_rewards_btn:
                maybeClaimReward(BILLS_REWARDS_POINTS);
                break;
            case R.id.sabres_rewards_btn:
                maybeClaimReward(SABRES_REWARDS_POINTS);
                break;
            default:
                break;
        }
    }

    private void maybeClaimReward(int pointsWorth) {
        if (UserLab.get().getCurrentUser().getScore() >= pointsWorth) {
            switch (pointsWorth) {
                case NFTA_REWARD_POINTS:
                    Toast.makeText(getContext(), "Congrats on the free Metro Ride!", Toast.LENGTH_SHORT).show();
                    break;
                case BIKE_SHARE_REWARDS_POINTS:
                    Toast.makeText(getContext(), "Congrats on the free Bike Share!", Toast.LENGTH_SHORT).show();
                    break;
                case BILLS_REWARDS_POINTS:
                    Toast.makeText(getContext(), "Congrats on the free Ticket!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            UserLab.get().getCurrentUser().setScore(UserLab.get().getCurrentUser().getScore() - pointsWorth);
            BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
            UserLab.get().notifyObserversUserUpdated(false);
            return;
        }
        Toast.makeText(getContext(),
                "Not enough points to redeem this reward!",
                Toast.LENGTH_SHORT).show();
    }
}
