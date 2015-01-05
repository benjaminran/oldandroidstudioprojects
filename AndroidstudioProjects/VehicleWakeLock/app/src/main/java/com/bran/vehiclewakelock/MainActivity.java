package com.bran.vehiclewakelock;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener  {
	
	public static final String TAG = "VehicleWakeLock";
    //...
    // Global constants
	// Constants that define the activity detection interval
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 20;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
    
    public enum REQUEST_TYPE {START, STOP}
    private REQUEST_TYPE mRequestType;
    
    /*
     * Store the PendingIntent used to send activity recognition events
     * back to the app
     */
    private PendingIntent mActivityRecognitionPendingIntent;
    // Store the current activity recognition client
    private ActivityRecognitionClient mActivityRecognitionClient;
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    //...
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        //...
    	// Start with the request flag set to false
        mInProgress = false;
        /*
         * Instantiate a new activity recognition client. Since the
         * parent Activity implements the connection listener and
         * connection failure listener, the constructor uses "this"
         * to specify the values of those parameters.
         */
        mActivityRecognitionClient = new ActivityRecognitionClient(this, this, this);//mContext? [1st one]
        /*
         * Create the PendingIntent that Location Services uses
         * to send activity recognition updates back to this app.
         */
        Intent intent = new Intent(this, ActivityRecognitionIntentService.class);//mContext?
        /*
         * Return a PendingIntent that starts the IntentService.
         */
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, intent,//mContext?
                PendingIntent.FLAG_UPDATE_CURRENT);
        //...
    }
    
    /**
     * Request activity recognition updates based on the current
     * detection interval.
     *
     */
     public void startUpdates() {
    	// Set the request type to START
         mRequestType = REQUEST_TYPE.START;
         /*
          * Test for Google Play services after setting the request type.
          * If Google Play services isn't present, the proper request type
          * can be restarted.
          */
        if (!servicesConnected()) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
        //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }
     
     /**
      * Turn off activity recognition updates
      *
      */
     public void stopUpdates() {
         // Set the request type to STOP
         mRequestType = REQUEST_TYPE.STOP;
         /*
          * Test for Google Play services after setting the request type.
          * If Google Play services isn't present, the request can be
          * restarted.
          */
         if (!servicesConnected()) {
             return;
         }
         // If a request is not already underway
         if (!mInProgress) {
             // Indicate that a request is in progress
             mInProgress = true;
             // Request a connection to Location Services
             mActivityRecognitionClient.connect();
         //
         } else {
             /*
              * A request is already underway. You can handle
              * this situation by disconnecting the client,
              * re-setting the flag, and then re-trying the
              * request.
              */
         }
         //...
     }
     
     /*
      * Called by Location Services once the location client is connected.
      *
      * Continue by requesting activity updates.
      */
     @Override
     public void onConnected(Bundle dataBundle) {
    	 switch (mRequestType) {
         	case START :
         		/*
         		 * Request activity recognition updates using the
         		 * preset detection interval and PendingIntent.
         		 * This call is synchronous.
         		 */
         		mActivityRecognitionClient.requestActivityUpdates(
         				DETECTION_INTERVAL_MILLISECONDS,
         				mActivityRecognitionPendingIntent);
         		break;
         	case STOP :
                mActivityRecognitionClient.removeActivityUpdates(
                        mActivityRecognitionPendingIntent);
                break;
         	default :
         		try { throw new Exception("Unknown request type in onConnected()."); } 
         		catch (Exception e) { e.printStackTrace(); }
         		break;
    	 }
         /*
          * Since the preceding call is synchronous, turn off the
          * in progress flag and disconnect the client
          */
         mInProgress = false;
         mActivityRecognitionClient.disconnect();
     }
     
     /*
      * Called by Location Services once the activity recognition
      * client is disconnected.
      */
     @Override
     public void onDisconnected() {
         // Turn off the request flag
         mInProgress = false;
         // Delete the client
         mActivityRecognitionClient = null;
     }
     
  // Implementation of OnConnectionFailedListener.onConnectionFailed
     @Override
     public void onConnectionFailed(ConnectionResult connectionResult) {
         // Turn off the request flag
         mInProgress = false;
         /*
          * If the error has a resolution, start a Google Play services
          * activity to resolve it.
          */
         if (connectionResult.hasResolution()) {
             try {
                 connectionResult.startResolutionForResult(
                         this,
                         CONNECTION_FAILURE_RESOLUTION_REQUEST);
             } catch (SendIntentException e) {
                 // Log the error
                 e.printStackTrace();
             }
         // If no resolution is available, display an error dialog
         } else {
             // Get the error code
             int errorCode = connectionResult.getErrorCode();
             // Get the error dialog from Google Play services
             Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                     errorCode,
                     this,
                     CONNECTION_FAILURE_RESOLUTION_REQUEST);
             // If Google Play services can provide an error dialog
             if (errorDialog != null) {
                 // Create a new DialogFragment for the error dialog
                 ErrorDialogFragment errorFragment =
                         new ErrorDialogFragment();
                 // Set the dialog in the DialogFragment
                 errorFragment.setDialog(errorDialog);
                 // Show the error dialog in the DialogFragment
                 errorFragment.show(
                         getSupportFragmentManager(),
                         "Activity Recognition");
             }
         }
         //...
     }
     
     //...
     /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            //...
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    //...
                    break;
                }
            //...
        }
        //...
    }
    //...
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Activity Recognition",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(
                        getSupportFragmentManager(),
                        "Activity Recognition");
            }
            return false;
        }
    }
    //...
}