package com.example.canscan;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

public class DataBaseUtils {

    public static final String STITCH = "STITCH";

    private static final String APP_CLIENT = "canscandb-cgwjo";
    private static final String SERVICE_NAME = "mongodb-atlas";
    private static final String DATABASE_NAME = "CanScanUsers";
    private static final String COLLECTION_NAME = "Users";

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String TOTAL_POINTS = "total_points";
    public static final String POINTS = "points";
    public static final String METRO = "metro";
    public static final String BIKE = "bike";
    public static final String TICKETS = "tickets";
    public static final String ZIPCODE = "zipcode";
    public static final String BARCODES = "barcodes";

    private static StitchAppClient mClient;
    private static RemoteMongoCollection<Document> mCollection;

    public static void initializeDatabase() {
        mClient = Stitch.initializeDefaultAppClient(APP_CLIENT);
        mCollection = mClient
                .getServiceClient(RemoteMongoClient.factory, SERVICE_NAME)
                .getDatabase(DATABASE_NAME)
                .getCollection(COLLECTION_NAME);
    }

    public static StitchAppClient getStitchClient() {
        return mClient;
    }

    public static RemoteMongoCollection<Document> getMongoCollection() {
        return mCollection;
    }
}
