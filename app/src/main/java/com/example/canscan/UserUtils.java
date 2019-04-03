package com.example.canscan;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

class UserUtils {

    private static final String TAG = UserUtils.class.getSimpleName();

    static boolean maybeCreateUserAndLaunchActivity(EditText username, EditText password) {
        boolean canMakeUser = true;

        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Required");
            canMakeUser = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required");
            canMakeUser = false;
        }
        if (!canMakeUser) {
            return canMakeUser;
        }

        try {
            User user = new User.Builder()
                    .setUsername(username.getText().toString())
                    .setPassword(password.getText().toString())
                    .create();
            UserLab.get().makeCurrentUser(user);
        }
        catch (IllegalStateException exception) {
            Log.d(TAG, "Cannot make User");
        }

        return canMakeUser;
    }
}
