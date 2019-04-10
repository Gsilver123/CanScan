package com.example.canscan;

public class User {

    private String mUsername;
    private String mPassword;
    private int mScore;

    private User(String username, String password, int score) {
        mUsername = username;
        mPassword = password;
        mScore = score;
    }

    String getUsername() {
        return mUsername;
    }

    String getPassword() {
        return mPassword;
    }

    int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
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
