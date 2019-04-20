package com.example.canscan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canscan.Barcode.BarcodeLab;
import com.example.canscan.User.UserLab;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int USERNAME_UPDATE = 1;
    private static final int PASSWORD_UPDATE = 2;
    private static final int ZIPCODE_UPDATE = 3;

    private TextView mUsernameTextView;
    private TextView mTotalPointsTextView;
    private TextView mMetroTicketsTextView;
    private TextView mBikeShareTicketsTextView;
    private TextView mGameTicketsTextView;
    private TextView mCurrentZipcodeTextView;
    private TextView mRecyclePickUpDayTextView;
    private Button mUpdateUsernameButton;
    private Button mUpdatePasswordButton;
    private Button mUpdateZipcodeButton;
    private ImageButton mBackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.profile, container, false);

        initializeViewVariable(view);
        setOnClickListener();
        fillProfileTextViews();

        return view;
    }

    private void initializeViewVariable(View view) {
        mUsernameTextView = view.findViewById(R.id.profile_username_textView);
        mTotalPointsTextView = view.findViewById(R.id.profile_total_points_number_textView);
        mMetroTicketsTextView = view.findViewById(R.id.profile_metro_tickets_number_textView);
        mBikeShareTicketsTextView = view.findViewById(R.id.profile_bike_share_tickets_number_textView);
        mGameTicketsTextView = view.findViewById(R.id.profile_game_tickets_number_textView);
        mCurrentZipcodeTextView = view.findViewById(R.id.profile_current_zipcode_number_textView);
        mUpdateUsernameButton = view.findViewById(R.id.profile_update_username_btn);
        mUpdatePasswordButton = view.findViewById(R.id.profile_update_password_btn);
        mUpdateZipcodeButton = view.findViewById(R.id.profile_update_zipcode_btn);
        mRecyclePickUpDayTextView = view.findViewById(R.id.profile_recycle_pick_up_day_number_textView);
        mBackButton = view.findViewById(R.id.profile_back_btn);
    }

    private void setOnClickListener() {
        mUpdateUsernameButton.setOnClickListener(this);
        mUpdatePasswordButton.setOnClickListener(this);
        mUpdateZipcodeButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    private void fillProfileTextViews() {
        mUsernameTextView.setText(UserLab.get().getCurrentUser().getUsername());
        mTotalPointsTextView.setText(String.valueOf(UserLab.get().getCurrentUser().getTotalScore()));
        mMetroTicketsTextView.setText(String.valueOf(UserLab.get().getCurrentUser().getMetroTickets()));
        mBikeShareTicketsTextView.setText(String.valueOf(UserLab.get().getCurrentUser().getBikeTickets()));
        mGameTicketsTextView.setText(String.valueOf(UserLab.get().getCurrentUser().getGameTickets()));

        if (UserLab.get().getCurrentUser().getZipcode() == 0) {
            mCurrentZipcodeTextView.setText("N/A");
        }
        else {
            mCurrentZipcodeTextView.setText(String.valueOf(UserLab.get().getCurrentUser().getZipcode()));
        }

        fillRecycleDay();
    }

    private void fillRecycleDay() {
        if (ZipcodeLab.get().getZipcodePickUpDaysMap().containsKey(UserLab.get().getCurrentUser().getZipcode())) {
            mRecyclePickUpDayTextView.setText(ZipcodeLab.get()
                    .getZipcodePickUpDaysMap().get(UserLab.get().getCurrentUser().getZipcode()));
        }
        else {
            mRecyclePickUpDayTextView.setText("N/A");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_update_username_btn:
                showAlertDialogForProfileUpdate(USERNAME_UPDATE, "Update Username");
                break;
            case R.id.profile_update_password_btn:
                showAlertDialogForProfileUpdate(PASSWORD_UPDATE, "Update Password");
                break;
            case R.id.profile_update_zipcode_btn:
                showAlertDialogForProfileUpdate(ZIPCODE_UPDATE, "Update Zipcode");
                break;
            case R.id.profile_back_btn:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void showAlertDialogForProfileUpdate(int updateMode, String dialogTitle) {
        boolean isLoginUpdate = true;

        if (updateMode == ZIPCODE_UPDATE) {
            isLoginUpdate = false;
        }

        View v = LayoutInflater.from(getContext()).inflate(isLoginUpdate ?
                R.layout.profile_update_login_alert_dialog
                : R.layout.profile_update_zipcode_alert_dialog, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(v)
                .setNegativeButton("Cancel", (dialogInterface, which) ->
                        dialogInterface.dismiss())
                .setPositiveButton("Accept", (dialogInterface, which) -> { })
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            switch (updateMode) {
                case USERNAME_UPDATE:
                    String newUsername = ((EditText)
                            v.findViewById(R.id.update_profile_editText)).getText().toString();

                    UserLab.get().getCurrentUser().setUsername(((EditText)
                            v.findViewById(R.id.update_profile_editText)).getText().toString());
                    BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
                    mUsernameTextView.setText(newUsername);
                    dialog.dismiss();

                    break;
                case PASSWORD_UPDATE:
                    if (newPasswordIsValid(((EditText)
                            v.findViewById(R.id.update_profile_editText)).getText().toString())) {
                        confirmPasswordForChangeAlertDialog(((EditText)
                                v.findViewById(R.id.update_profile_editText)).getText().toString());
                        dialog.dismiss();
                    }

                    break;
                case ZIPCODE_UPDATE:
                    String newZipcode = ((EditText)
                            v.findViewById(R.id.update_profile_editText)).getText().toString();

                    if (newZipcode.length() == 5) {
                        UserLab.get().getCurrentUser().setZipcode(Integer.parseInt(newZipcode));
                        BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
                        mCurrentZipcodeTextView.setText(String.valueOf(newZipcode));

                        if (!ZipcodeLab.get().getZipcodePickUpDaysMap().containsKey(Integer.parseInt(newZipcode))) {
                            promptIfZipcodeIsNotInArea();
                        }
                        fillRecycleDay();
                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(getActivity(),
                                "Has to be in standard zipcode format (5 numbers)",
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private boolean newPasswordIsValid(String newPassword) {
        if (newPassword.length() < 5) {
            Toast.makeText(getActivity(), "Must be longer than 5 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.contains(" ")) {
            Toast.makeText(getActivity(), "Cannot contain spaces", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmPasswordForChangeAlertDialog(String newPassword) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.profile_update_login_alert_dialog, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Confirm Old Password")
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, which) -> { })
                .setCancelable(false)
                .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (((EditText) view.findViewById(R.id.update_profile_editText)).getText().toString()
                    .equals(UserLab.get().getCurrentUser().getPassword())) {

                UserLab.get().getCurrentUser().setPassword(newPassword);
                BarcodeLab.get().updateDatabaseWithCurrentListAndPoints();
                dialog.dismiss();
            }
            else {
                Toast.makeText(getActivity(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void promptIfZipcodeIsNotInArea() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Invalid Buffalo Zipcode")
                .setMessage("Your zipcode is not a valid city of Buffalo zipcode." +
                        " If you would like to know your curbside recycle date " +
                        "please update. Otherwise, happy recycling!")
                .setNeutralButton("Ok", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .create();

        dialog.show();
    }
}
