package com.bran.chordretriever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Beni on 8/4/14.
 */
public class SongChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "ChordRetriever";
    private MainActivity mainActivity;
    private WebView webview;

    public SongChangeReceiver(WebView wv, MainActivity ma) {
        webview = wv;
        mainActivity = ma;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //webview.loadUrl("about:blank");
        String url = getUrlOld(intent);
        webview.loadUrl(url);
    }

    private String getUrlOld(Intent intent) {
        String action = intent.getAction();
        String cmd = intent.getStringExtra("command");
        Log.v("tag ", action + " / " + cmd);
        String artist = intent.getStringExtra("artist");
        String album = intent.getStringExtra("album");
        String track = intent.getStringExtra("track");
        String url = "https://www.google.com/search?q=" + track.replaceAll(" ", "+") + "+" + artist.replaceAll(" ", "+") + "+chords";
        Log.v("tag", artist + ":" + album + ":" + track);
        Toast.makeText(mainActivity, track, Toast.LENGTH_SHORT).show();
        Log.v("tag", "URL: " + url);
        return url;
    }

    private String getExtrasString(Intent pIntent) {
        String extrasString = "";
        Bundle extras = pIntent.getExtras();
        try {
            if (extras != null) {
                Set<String> keySet = extras.keySet();
                for (String key : keySet) {
                    try {
                        String extraValue = pIntent.getExtras().get(key).toString();
                        extrasString += key + ": " + extraValue + "\n";
                    } catch (Exception e) {
                        Log.d(TAG, "Exception 2 in getExtrasString(): " + e.toString());
                        extrasString += key + ": Exception:" + e.getMessage() + "\n";
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception in getExtrasString(): " + e.toString());
            extrasString += "Exception:" + e.getMessage() + "\n";
        }
        Log.d(TAG, "extras=" + extrasString);
        return extrasString;
    }

}
