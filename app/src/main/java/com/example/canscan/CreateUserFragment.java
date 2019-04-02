package com.example.canscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserFragment extends Fragment implements View.OnClickListener {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mCancelButton;
    private Button mAcceptButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.create_user, container, false);

        initializeViewVariables(view);
        setOnClickListeners();

        return view;
    }

    private void initializeViewVariables(View view) {
        mUsernameEditText = view.findViewById(R.id.new_user_username_textView);
        mPasswordEditText = view.findViewById(R.id.new_user_password_textView);
        mCancelButton = view.findViewById(R.id.new_user_cancel_btn);
        mAcceptButton = view.findViewById(R.id.new_user_accept_btn);
    }

    private void setOnClickListeners() {
        mCancelButton.setOnClickListener(this);
        mAcceptButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.new_user_cancel_btn:
                launchLoginActivityFromCancel();
                break;
            case R.id.new_user_accept_btn:

                break;
            default:
                Toast.makeText(getContext(), "Action not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void launchLoginActivityFromCancel() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
