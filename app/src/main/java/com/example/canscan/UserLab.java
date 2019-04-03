package com.example.canscan;


class UserLab {

    private User mCurrentUser;

    private static UserLab sUserLab;

    static UserLab get() {
        if (sUserLab == null) {
            sUserLab = new UserLab();
        }
        return sUserLab;
    }

    private UserLab() { }

    User getCurrentUser() {
        return mCurrentUser;
    }

    void makeCurrentUser(User user) {
        mCurrentUser = user;
    }
}
