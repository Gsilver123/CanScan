package com.example.canscan;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

class DataBaseUtils {

    static final String STITCH = "STITCH";

    private static final String APP_CLIENT = "canscandb-cgwjo";
    private static final String SERVICE_NAME = "mongodb-atlas";
    private static final String DATABASE_NAME = "CanScanUsers";
    private static final String COLLECTION_NAME = "Users";

    static final String USER_ID = "user_id";
    static final String USER_NAME = "user_name";
    static final String PASSWORD = "password";
    static final String POINTS = "points";

    private static StitchAppClient mClient;
    private static RemoteMongoCollection<Document> mCollection;

    static void initializeDatabase() {
        mClient = Stitch.initializeDefaultAppClient(APP_CLIENT);
        mCollection = mClient
                .getServiceClient(RemoteMongoClient.factory, SERVICE_NAME)
                .getDatabase(DATABASE_NAME)
                .getCollection(COLLECTION_NAME);
    }

    static StitchAppClient getStitchClient() {
        return mClient;
    }

    static RemoteMongoCollection<Document> getMongoCollection() {
        return mCollection;
    }
}
