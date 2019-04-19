package com.example.canscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.canscan.User.User;
import com.example.canscan.User.UserLab;

import java.util.ArrayList;

public class LeaderBoardFragment extends Fragment {

    private LeaderBoardAdapter mLeaderBoardAdapter;
    private RecyclerView mLeaderBoardRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.leader_board, container, false);

        UserLab.get().createLeaderBoardList();

        mLeaderBoardRecyclerView = view.findViewById(R.id.leader_board_recyclerView);
        mLeaderBoardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        if (mLeaderBoardAdapter == null) {
            mLeaderBoardAdapter = new LeaderBoardAdapter();
            mLeaderBoardRecyclerView.setAdapter(mLeaderBoardAdapter);
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
            mPointsTextView.setText(String.valueOf(user.getScore()));

            colorRanking(position + 1);
        }

        void colorRanking(int position) {
            switch (position) {
                case 1:
                    mPositionTextView.setTextColor(getContext().getColor(R.color.gold));
                    mUsernameTextView.setTextColor(getContext().getColor(R.color.gold));
                    mPointsTextView.setTextColor(getContext().getColor(R.color.gold));
                    break;
                case 2:
                    mPositionTextView.setTextColor(getContext().getColor(R.color.silver));
                    mUsernameTextView.setTextColor(getContext().getColor(R.color.silver));
                    mPointsTextView.setTextColor(getContext().getColor(R.color.silver));
                    break;
                case 3:
                    mPositionTextView.setTextColor(getContext().getColor(R.color.bronze));
                    mUsernameTextView.setTextColor(getContext().getColor(R.color.bronze));
                    mPointsTextView.setTextColor(getContext().getColor(R.color.bronze));
                    break;
                default:
                    mPositionTextView.setTextColor(getContext().getColor(R.color.white));
                    mUsernameTextView.setTextColor(getContext().getColor(R.color.white));
                    mPointsTextView.setTextColor(getContext().getColor(R.color.white));
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
