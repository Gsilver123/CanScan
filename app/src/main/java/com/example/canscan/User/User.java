package com.example.canscan.User;

public class User {

    private String mUsername;
    private String mPassword;
    private int mTotalScore;
    private int mScore;
    private int mMetroTickets;
    private int mBikeTickets;
    private int mGameTickets;
    private int mZipcode;

    private User(String username, String password, int totalScore, int score,
                 int metroTickets, int bikeTickets, int gameTickets, int zipcode) {

        mUsername = username;
        mPassword = password;
        mTotalScore = totalScore;
        mScore = score;
        mMetroTickets = metroTickets;
        mBikeTickets = bikeTickets;
        mGameTickets = gameTickets;
        mZipcode = zipcode;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    public void setTotalScore(int totalScore) {
        mTotalScore = totalScore;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    public int getMetroTickets() {
        return mMetroTickets;
    }

    public void setMetroTickets(int metroTickets) {
        mMetroTickets = metroTickets;
    }

    public int getBikeTickets() {
        return mBikeTickets;
    }

    public void setBikeTickets(int bikeTickets) {
        mBikeTickets = bikeTickets;
    }

    public int getGameTickets() {
        return mGameTickets;
    }

    public void setGameTickets(int gameTickets) {
        mGameTickets = gameTickets;
    }

    public int getZipcode() {
        return mZipcode;
    }

    public void setZipcode(int zipcode) {
        mZipcode = zipcode;
    }

    public int compareTo(User user) {
        if (mScore > user.getTotalScore()) {
            return -1;
        }
        else if (mScore == user.getTotalScore()) {
            return 0;
        }
        return 1;
    }

    static class Builder {

        private String mBuilderUsername;
        private String mBuilderPassword;
        private int mBuilderTotalScore;
        private int mBuilderScore;
        private int mBuilderMetroTickets;
        private int mBuilderBikeTickets;
        private int mBuilderGameTickets;
        private int mBuilderZipcode;

        User.Builder setUsername(String username) {
            mBuilderUsername = username;
            return this;
        }

        User.Builder setPassword(String password) {
            mBuilderPassword = password;
            return this;
        }

        User.Builder setTotalScore(int totalScore) {
            mBuilderTotalScore = totalScore;
            return this;
        }

        User.Builder setScore(int score) {
            mBuilderScore = score;
            return this;
        }

        User.Builder setMetroTickets(int metroTickets) {
            mBuilderMetroTickets = metroTickets;
            return this;
        }

        User.Builder setBikeTickets(int bikeTickets) {
            mBuilderBikeTickets = bikeTickets;
            return this;
        }

        User.Builder setGameTickets(int gameTickets) {
            mBuilderGameTickets = gameTickets;
            return this;
        }

        User.Builder setZipcode(int zipcode) {
            mBuilderZipcode = zipcode;
            return this;
        }

        User create() throws IllegalStateException {
            if (mBuilderUsername == null
                    || mBuilderPassword == null
                    || mBuilderScore < 0
                    || mBuilderTotalScore < 0
                    || mBuilderMetroTickets < 0
                    || mBuilderBikeTickets < 0
                    || mBuilderGameTickets < 0
                    || mBuilderZipcode < 0) {

                throw new IllegalStateException("Credentials not met");
            }

            return new User(mBuilderUsername,
                    mBuilderPassword,
                    mBuilderTotalScore,
                    mBuilderScore,
                    mBuilderMetroTickets,
                    mBuilderBikeTickets,
                    mBuilderGameTickets,
                    mBuilderZipcode);
        }
    }
 }
