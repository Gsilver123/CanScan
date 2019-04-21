package com.example.canscan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.canscan.Barcode.BarcodeLab;
import com.example.canscan.User.UserLab;

import java.util.Objects;

public class RewardsFragment extends Fragment implements View.OnClickListener {

    private static final int NFTA_REWARD_POINTS = 1000;
    private static final int BIKE_SHARE_REWARDS_POINTS = 2500;
    private static final int GAME_TICKET_REWARDS_POINTS = 15000;


    private Button mNftaRewardsButton;
    private Button mBikeShareRewardsButton;
    private Button mBillsRewardsButton;
    private Button mSabresRewardsButton;
    private ImageButton mBackButton;

    private static Toast mToast;

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
        mBackButton = view.findViewById(R.id.rewards_back_btn);
    }

    private void setOnClickListeners() {
        mNftaRewardsButton.setOnClickListener(this);
        mBikeShareRewardsButton.setOnClickListener(this);
        mBillsRewardsButton.setOnClickListener(this);
        mSabresRewardsButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
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
                maybeClaimReward(GAME_TICKET_REWARDS_POINTS);
                break;
            case R.id.sabres_rewards_btn:
                maybeClaimReward(GAME_TICKET_REWARDS_POINTS);
                break;
            case R.id.rewards_back_btn:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void maybeClaimReward(int pointsWorth) {
        if (UserLab.get().getCurrentUser().getScore() >= pointsWorth) {
            switch (pointsWorth) {
                case NFTA_REWARD_POINTS:
                    maybeShowToast("Congrats on the free Metro Ride!");
                    UserLab.get().getCurrentUser()
                            .setMetroTickets(UserLab.get().getCurrentUser().getMetroTickets() + 1);
                    break;
                case BIKE_SHARE_REWARDS_POINTS:
                    maybeShowToast("Congrats on the free Bike Share!");
                    UserLab.get().getCurrentUser()
                            .setBikeTickets(UserLab.get().getCurrentUser().getBikeTickets() + 1);
                    break;
                case GAME_TICKET_REWARDS_POINTS:
                    maybeShowToast("Congrats on the free Ticket!");
                    UserLab.get().getCurrentUser()
                            .setGameTickets(UserLab.get().getCurrentUser().getGameTickets() + 1);
                    break;
                default:
                    break;
            }

            UserLab.get().getCurrentUser()
                    .setScore(UserLab.get().getCurrentUser().getScore() - pointsWorth);
            BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
            UserLab.get().notifyDatabaseObserversUserUpdated(false);

            return;
        }
        maybeShowToast("Not enough points to redeem this reward!");
    }

    private void maybeShowToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
