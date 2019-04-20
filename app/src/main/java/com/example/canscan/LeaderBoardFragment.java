package com.example.canscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.canscan.User.User;
import com.example.canscan.User.UserLab;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderBoardFragment extends Fragment implements View.OnClickListener{

    private LeaderBoardAdapter mLeaderBoardAdapter;
    private RecyclerView mLeaderBoardRecyclerView;

    private ImageButton mBackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.leader_board, container, false);

        UserLab.get().createLeaderBoardList();

        mLeaderBoardRecyclerView = view.findViewById(R.id.leader_board_recyclerView);
        mLeaderBoardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBackButton = view.findViewById(R.id.leader_board_back_btn);
        mBackButton.setOnClickListener(this);

        updateUI();

        return view;
    }

    private void updateUI() {
        if (mLeaderBoardAdapter == null) {
            mLeaderBoardAdapter = new LeaderBoardAdapter();
            mLeaderBoardRecyclerView.setAdapter(mLeaderBoardAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leader_board_back_btn:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    class LeaderBoardHolder extends RecyclerView.ViewHolder {

        private TextView mPositionTextView;
        private TextView mUsernameTextView;
        private TextView mPointsTextView;

        LeaderBoardHolder(@NonNull LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.leader_board_list_item, container, false));

            mPositionTextView = itemView.findViewById(R.id.leader_board_position_textView);
            mUsernameTextView = itemView.findViewById(R.id.leader_board_username_textView);
            mPointsTextView = itemView.findViewById(R.id.leader_board_points_textView);
        }

        void bind(User user, int position) {
            mPositionTextView.setText(String.valueOf(position + 1));
            mUsernameTextView.setText(user.getUsername());
            mPointsTextView.setText(String.valueOf(user.getTotalScore()));

            try {
                colorRanking(position + 1);
            }
            catch (NullPointerException exception) {
                exception.printStackTrace();
            }
        }

        void colorRanking(int position) throws NullPointerException {
            switch (position) {
                case 1:
                    mPositionTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.gold));
                    mUsernameTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.gold));
                    mPointsTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.gold));
                    break;
                case 2:
                    mPositionTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.silver));
                    mUsernameTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.silver));
                    mPointsTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.silver));
                    break;
                case 3:
                    mPositionTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.bronze));
                    mUsernameTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.bronze));
                    mPointsTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.bronze));
                    break;
                default:
                    mPositionTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.white));
                    mUsernameTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.white));
                    mPointsTextView.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.white));
                    break;
            }
        }
    }

    private class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardHolder> {

        ArrayList<User> mLeaderBoardUsers;

        LeaderBoardAdapter() {
            mLeaderBoardUsers = UserLab.get().getLeaderBoardUserList();
        }

        @NonNull
        @Override
        public LeaderBoardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new LeaderBoardHolder(LayoutInflater.from(getActivity()), viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull LeaderBoardHolder leaderBoardHolder, int i) {
            leaderBoardHolder.bind(mLeaderBoardUsers.get(i), i);
        }

        @Override
        public int getItemCount() {
            return mLeaderBoardUsers.size();
        }
    }
}
