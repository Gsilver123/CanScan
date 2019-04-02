package com.example.canscan;


public class UserLab {

    private User mCurrentUser;

    private static UserLab sUserLab;

    public static UserLab get() {
        if (sUserLab == null) {
            return new UserLab();
        }

        return sUserLab;
    }

    private UserLab() { }

    public User getUser() {
        return mCurrentUser;
    }

    void makeCurrentUser(User user) {
        mCurrentUser = user;
    }
}
