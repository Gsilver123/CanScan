package com.example.canscan;

public class User {

    private String mUsername;
    private String mPassword;
    private int mScore = 0;

    private User(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    String getUsername() {
        return mUsername;
    }

    String getPassword() {
        return mPassword;
    }

    long getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    static class Builder {

        private String mBuilderUsername;
        private String mBuilderPassword;

        User.Builder setUsername(String username) {
            mBuilderUsername = username;
            return this;
        }

        User.Builder setPassword(String password) {
            mBuilderPassword = password;
            return this;
        }

        User create() throws IllegalStateException {
            if (mBuilderUsername == null || mBuilderPassword == null) {
                throw new IllegalStateException("Credentials not met");
            }

            return new User(mBuilderUsername, mBuilderPassword);
        }
    }
 }
