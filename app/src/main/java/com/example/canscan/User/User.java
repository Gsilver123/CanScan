package com.example.canscan.User;

public class User {

    private String mUsername;
    private String mPassword;
    private int mScore;

    private User(String username, String password, int score) {
        mUsername = username;
        mPassword = password;
        mScore = score;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public int compareTo(User user) {
        if (mScore > user.getScore()) {
            return -1;
        }
        else if (mScore == user.getScore()) {
            return 0;
        }
        return 1;
    }

    static class Builder {

        private String mBuilderUsername;
        private String mBuilderPassword;
        private int mBuilderScore;

        User.Builder setUsername(String username) {
            mBuilderUsername = username;
            return this;
        }

        User.Builder setPassword(String password) {
            mBuilderPassword = password;
            return this;
        }

        User.Builder setScore(int score) {
            mBuilderScore = score;
            return this;
        }

        User create() throws IllegalStateException {
            if (mBuilderUsername == null || mBuilderPassword == null || mBuilderScore < 0) {
                throw new IllegalStateException("Credentials not met");
            }

            return new User(mBuilderUsername, mBuilderPassword, mBuilderScore);
        }
    }
 }
