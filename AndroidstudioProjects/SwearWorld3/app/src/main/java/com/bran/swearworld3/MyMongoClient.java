package com.bran.swearworld3;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Beni on 7/29/14.
 */
public class MyMongoClient {
    private static final int RETURN_LIMIT = 100;// Limit on the number of results returned from one call.
    private static final MyMongoClient instance = new MyMongoClient();
    private Context context;
    private com.mongodb.MongoClient client;
    private DB db;
    private DBCollection swears;
    private BasicDBObject getSwearsFindQuery;
    private BasicDBObject getSwearsOrderBy;

    private MyMongoClient() {
        (new Thread(new Runnable(){
            @Override
            public void run() {
                MongoClientURI uri  = new MongoClientURI("mongodb://guest:guest@ds053429.mongolab.com:53429/swear_world_data");
                try {
                    client = new com.mongodb.MongoClient(uri);
                } catch (UnknownHostException e) {
                    //TODO: Handle this....
                    e.printStackTrace();
                }
                db = client.getDB(uri.getDatabase());
                swears = db.getCollection("swears");
                getSwearsFindQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte",10));
                getSwearsOrderBy = new BasicDBObject("decade", 1);
            }
        }, "MongoClient Thread")).start();

    }


    public static MyMongoClient getInstance() {
        return instance;
    }

    public ArrayAdapter<DBObject> getSwearsFromMongo(String query) {// TODO: Add concurrency mechanism to stop execution until swears initialized.
        DBCursor cursor = swears.find(/*getSwearsFindQuery*/)/*.sort(getSwearsOrderBy).limit(RETURN_LIMIT)*/;
        List<DBObject> items = cursor.toArray();

        ArrayAdapter<DBObject> adapter = new ArrayAdapter<DBObject>(
                context,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                items);
        return adapter;// runOnUiThread()? If execution doesn't switch back to UI thread in SwearList Activity/Fragment
    }

    public void addSwearToMongo() {
        // TODO
    }
}
