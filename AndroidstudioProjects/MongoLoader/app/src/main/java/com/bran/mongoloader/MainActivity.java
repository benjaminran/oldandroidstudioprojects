package com.bran.mongoloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;


public class MainActivity extends Activity {
    private DBCollection songs;
    private MongoClient client;
    private TextView text;
    String textContent;

    public static BasicDBObject[] createSeedData(){

        BasicDBObject seventies = new BasicDBObject();
        seventies.put("decade", "1970s");
        seventies.put("artist", "Debby Boone");
        seventies.put("song", "You Light Up My Life");
        seventies.put("weeksAtOne", 10);

        BasicDBObject eighties = new BasicDBObject();
        eighties.put("decade", "1980s");
        eighties.put("artist", "Olivia Newton-John");
        eighties.put("song", "Physical");
        eighties.put("weeksAtOne", 10);

        BasicDBObject nineties = new BasicDBObject();
        nineties.put("decade", "1990s");
        nineties.put("artist", "Mariah Carey");
        nineties.put("song", "One Sweet Day");
        nineties.put("weeksAtOne", 16);

        final BasicDBObject[] seedData = {seventies, eighties, nineties};

        return seedData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
    }

    @Override
    public void onResume(){
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Create seed data

                final BasicDBObject[] seedData = createSeedData();

                // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

                MongoClientURI uri = new MongoClientURI("mongodb://guest:guest@ds053429.mongolab.com:53429/swear_world_data");
                try {
                    client = new MongoClient(uri);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                DB db = client.getDB(uri.getDatabase());

                /*
                * First we'll add a few songs. Nothing is required to create the
                * songs collection; it is created automatically when we insert.
                */

                //songs = db.getCollection("songs");
                songs = db.getCollection("mynewcollection");

                // Note that the insert method can take either an array or a document.

                //songs.insert(seedData);
                songs.insert(new BasicDBObject("foo", "insertedbyandroid"));

                /*
                 * Then we need to give Boyz II Men credit for their contribution to
                 * the hit "One Sweet Day".
                */

                //BasicDBObject updateQuery = new BasicDBObject("song", "One Sweet Day");
                //songs.update(updateQuery, new BasicDBObject("$set", new BasicDBObject("artist", "Mariah Carey ft. Boyz II Men")));


               /*
                * Finally we run a query which returns all the hits that spent 10
                * or more weeks at number 1.
                */
//                BasicDBObject findQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte", 10));
//                BasicDBObject orderBy = new BasicDBObject("decade", 1);
//
//                DBCursor docs = songs.find(findQuery).sort(orderBy);

                DBCursor docs = songs.find();

                textContent = "<b><u>Inserted Then Read From Database:</u></b>\n";
                try {
                    while (docs.hasNext()) {
                        DBObject doc = docs.next();
                        textContent += "foo: " + doc.get("foo") + "\n";
                    }
                }
                finally {
                    docs.close();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(textContent);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Since this is an example, we'll clean up after ourselves.

        songs.drop();

        // Only close the connection when your app is terminating

        client.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
