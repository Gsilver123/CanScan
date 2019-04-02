package com.example.canscan;

public class User {

    private String mUsername;
    private String mPassword;
    private long mScore;

    private User(String username, String password, long score) {
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

    public long getScore() {
        return mScore;
    }

    public void setScore(long score) {
        mScore = score;
    }

    static class Builder {

        private String mBuilderUsername;
        private String mBuilderPassword;

        public User.Builder setUsername(String username) {
            mBuilderUsername = username;
            return this;
        }

        public User.Builder setPassword(String password) {
            mBuilderPassword = password;
            return this;
        }

        public User create() throws IllegalStateException {
            if (mBuilderUsername == null || mBuilderPassword == null) {
                throw new IllegalStateException("Credentials not met");
            }

            return new User(mBuilderUsername, mBuilderPassword, 0);
        }
    }
 }
