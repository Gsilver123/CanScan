package com.example.canscan.User;

import android.util.Log;

import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.example.canscan.DataBaseUtils.BIKE;
import static com.example.canscan.DataBaseUtils.METRO;
import static com.example.canscan.DataBaseUtils.PASSWORD;
import static com.example.canscan.DataBaseUtils.POINTS;
import static com.example.canscan.DataBaseUtils.STITCH;
import static com.example.canscan.DataBaseUtils.TICKETS;
import static com.example.canscan.DataBaseUtils.TOTAL_POINTS;
import static com.example.canscan.DataBaseUtils.USER_ID;
import static com.example.canscan.DataBaseUtils.USER_NAME;
import static com.example.canscan.DataBaseUtils.ZIPCODE;
import static com.example.canscan.DataBaseUtils.getMongoCollection;
import static com.example.canscan.DataBaseUtils.getStitchClient;

public class UserLab {

    private User mCurrentUser;

    private static UserLab sUserLab;
    private ArrayList<DatabaseObserver> mDatabaseObservers = new ArrayList<>();
    private ArrayList<User> mLeaderBoardUsers;

    public static UserLab get() {
        if (sUserLab == null) {
            sUserLab = new UserLab();
        }
        return sUserLab;
    }

    private UserLab() { }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void updateCurrentUserFromDatabase() {
        getStitchClient().getAuth().loginWithCredential(
                new AnonymousCredential()).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.e(STITCH, "Update failed!");
                throw Objects.requireNonNull(task.getException());
            }
            List<Document> docs = new ArrayList<>();

            return getMongoCollection()
                    .find(new Document(USER_ID, Objects.requireNonNull(getStitchClient()
                            .getAuth().getUser()).getId()))
                    .limit(1)
                    .into(docs);
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(STITCH, "Found User");
                try {
                    updateCurrentUserFromJson(Objects.requireNonNull(task.getResult()));
                }
                catch (JSONException | IndexOutOfBoundsException exception) {
                    Log.d(STITCH, exception.getLocalizedMessage());
                }
                return;
            }
            Log.e(STITCH, "Error: " + Objects.requireNonNull(task.getException()).toString());
            task.getException().printStackTrace();
        });
    }

    private void updateCurrentUserFromJson(List<Document> userDocument)
            throws JSONException, IndexOutOfBoundsException {

        if (userDocument.get(0) != null) {
            JSONObject jsonObject = new JSONObject(userDocument.get(0));

            mCurrentUser = new User.Builder()
                    .setUsername(jsonObject.get(USER_NAME).toString())
                    .setPassword(jsonObject.get(PASSWORD).toString())
                    .setTotalScore(Integer.parseInt(jsonObject.get(TOTAL_POINTS).toString()))
                    .setScore(Integer.parseInt(jsonObject.get(POINTS).toString()))
                    .setMetroTickets(Integer.parseInt(jsonObject.get(METRO).toString()))
                    .setBikeTickets(Integer.parseInt(jsonObject.get(BIKE).toString()))
                    .setGameTickets(Integer.parseInt(jsonObject.get(TICKETS).toString()))
                    .setZipcode(Integer.parseInt(jsonObject.get(ZIPCODE).toString()))
                    .create();

            notifyDatabaseObserversUserUpdated(true);
        }
        else {
            Log.d(STITCH, "Document is empty");
        }
    }

    public void createLeaderBoardList() {
        mLeaderBoardUsers = new ArrayList<>();

        int mayorScore = 60001 + (int) (Math.random() * (100000));
        int amyScore = 10000 + (int) (Math.random() * (50000));
        int cletisScore = 10000 + (int) (Math.random() * (50000));
        int gerardScore = 10000 + (int) (Math.random() * (50000));
        int jacquelynScore = 10000 + (int) (Math.random() * (50000));
        int josepScore = 10000 + (int) (Math.random() * (50000));
        int justinScore = 10000 + (int) (Math.random() * (50000));
        int luisScore = 10000 + (int) (Math.random() * (50000));

        for (String name : RandomNameList.nameList) {
            int standardScore = (int) (Math.random() * (10000));

            mLeaderBoardUsers.add(createUser(name, "password", standardScore, standardScore));
        }

        mLeaderBoardUsers.add(mCurrentUser);
        mLeaderBoardUsers.add(createUser("Byron B", "mayor", mayorScore, mayorScore));
        mLeaderBoardUsers.add(createUser("Amy K", "password", amyScore, amyScore));
        mLeaderBoardUsers.add(createUser("Cletis E", "password", cletisScore, cletisScore));
        mLeaderBoardUsers.add(createUser("Gerard A", "password", gerardScore, gerardScore));
        mLeaderBoardUsers.add(createUser("Jacquelyn M", "password", jacquelynScore, jacquelynScore));
        mLeaderBoardUsers.add(createUser("Josep J", "password", josepScore, josepScore));
        mLeaderBoardUsers.add(createUser("Justin B", "password", justinScore, justinScore));
        mLeaderBoardUsers.add(createUser("Luis T", "password", luisScore, luisScore));

        Collections.sort(mLeaderBoardUsers, new UserComparator());
    }

    private User createUser(String name, String password, int totalScore, int score) {
        return new User.Builder()
                .setUsername(name)
                .setPassword(password)
                .setTotalScore(totalScore)
                .setScore(score)
                .setMetroTickets(0)
                .setBikeTickets(0)
                .setGameTickets(0)
                .setZipcode(0)
                .create();
    }

    public ArrayList<User> getLeaderBoardUserList() {
        return mLeaderBoardUsers;
    }

    public void registerDatabaseObserver(DatabaseObserver databaseObserver) {
        if (!mDatabaseObservers.contains(databaseObserver)) {
            mDatabaseObservers.add(databaseObserver);
        }
    }

    public void removeDatabaseObserver(DatabaseObserver databaseObserver) {
        mDatabaseObservers.remove(databaseObserver);
    }

    public void notifyDatabaseObserversUserUpdated(boolean shouldPullFromDatabase) {
        for (DatabaseObserver databaseObserver : mDatabaseObservers) {
            databaseObserver.notifyObserverUserCreatedFromDatabase(shouldPullFromDatabase);
        }
    }

    public interface DatabaseObserver {
        void notifyObserverUserCreatedFromDatabase(boolean shouldPushToDatabase);
    }

    private static class RandomNameList {

        static final String[] nameList =  {"Mary", "Anna", "Emma", "Elizabeth", "Minnie", "Margaret",
            "Ida", "Alice", "Bertha", "Sarah", "Angelina", "Corrie", "Maye", "Harry", "Fred", "David",
            "Joe", "Charlie", "Richard", "Will", "Oscar", "Robert", "Frank", "Thomas", "Charles"};
    }

    private static class UserComparator implements Comparator<User> {

        @Override
        public int compare(User left, User right) {
            return left.compareTo(right);
        }
    }
}
