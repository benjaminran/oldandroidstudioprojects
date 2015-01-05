package com.bran.chordretriever;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity extends Activity {
    private WebView webview;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        mReceiver = new SongChangeReceiver(webview, MainActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter iF = initIntentFilter();
        //registerReceiver(mReceiver, new IntentFilter("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION"));
        registerReceiver(mReceiver, new IntentFilter("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION"));
        //registerReceiver(mReceiver, iF);
        //webview.loadUrl("http://developer.android.com/");
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        Log.v("tag", "onStop() was called.");
    }

    private void initWebView() {
        webview = new WebView(this);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        setContentView(webview);
    }

    private IntentFilter initIntentFilter() {
        IntentFilter iF = new IntentFilter();
        /*iF.addCategory("ComponentInfo");
        iF.addCategory("com.spotify.mobile.android.service.SpotifyIntentService");
        iF.addCategory("com.spotify.mobile.android.service.SpotifyService");

        iF.addAction("com.spotify.mobile.android.ui.widget.SpotifyWidget");
        iF.addAction("ComponentInfo");
        iF.addAction("com.spotify");
        iF.addAction("com.spotify.mobile.android.service.SpotifyIntentService");
        iF.addAction("com.spotify.mobile.android.service.SpotifyService");
        iF.addAction("com.spotify.mobile.android.ui");*/
        iF.addAction("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION");
        iF.addAction("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION");
        /*iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.*");*/
        return iF;
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
