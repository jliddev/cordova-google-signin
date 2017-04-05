package com.jliddev.plugins;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.cordova.*;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignin extends CordovaPlugin
	implements GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = GoogleSignin.class.getSimpleName();

	private static final int RC_SIGN_IN = 9001;

	private static final String ACTION_LOGIN = "glogin";
	private static final String ACTION_SET_SERVER_CLIENT_ID = "setServerClientId";

	private GoogleApiClient mGoogleApiClient;

	private CallbackContext mCurrentLoginCallback;

	private String mServerClientId = null;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);


	}

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

		if ( action.equals(ACTION_LOGIN) ) {
			login( callbackContext );
		}
		else if( action.equals(ACTION_SET_SERVER_CLIENT_ID))
		{
			setServerClientId(args.optString(0), callbackContext);
		}
		else {
			Log.i(TAG, "This action doesn't exist");
			return false;
		}
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}

	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			JSONObject responseJson = new JSONObject();
			GoogleSignInAccount acct = result.getSignInAccount();
			if( acct == null ){
				mCurrentLoginCallback.error("account object was null");
				return;
			}
			try {
				responseJson.put("displayName", acct.getDisplayName());
				responseJson.put("email", acct.getEmail());
				responseJson.put("familyName", acct.getFamilyName());
				responseJson.put("givenName", acct.getGivenName());
				responseJson.put("id", acct.getId());
				responseJson.put("idToken", acct.getIdToken());
				responseJson.put("photoUrl", acct.getPhotoUrl());
				responseJson.put("serverAuthCode", acct.getServerAuthCode());
			}catch (JSONException ex ){
				Log.e(TAG, "failed to create response object", ex);
			}

			if( mCurrentLoginCallback != null ){
				mCurrentLoginCallback.success( responseJson );
			}
		} else {
			//TODO add better logging
			mCurrentLoginCallback.error("login failed");
		}
	}

	private void setServerClientId(String id, CallbackContext callbackContext){
		if( id != null && id.length() > 0 ){
			mServerClientId = id;
			callbackContext.success();
		} else {
			callbackContext.error("invalid server client id");
		}
	}

	private void login( CallbackContext callbackContext ) {
		if( mCurrentLoginCallback != null ){
			callbackContext.error("a login is currently in progress");
			return;
		}

		//store the callback for after the activity finishes
		mCurrentLoginCallback = callbackContext;
		//register for activity callback events
		cordova.setActivityResultCallback(this);

		//create builder we can manipulate
		GoogleSignInOptions.Builder optionsBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestProfile();

		//if the user setup a server client id, add it
		if( mServerClientId != null ){
			optionsBuilder = optionsBuilder.requestIdToken(mServerClientId);
		}

		GoogleSignInOptions gso = optionsBuilder.build();

		//refresh the client
		if( mGoogleApiClient != null ){
			mGoogleApiClient.disconnect();
			mGoogleApiClient = null;
		}

		//create client with our options
		mGoogleApiClient = new GoogleApiClient.Builder(webView.getContext())
				.addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

		//start the signin activity
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
	}
} acct = result.getSignInAccount();
			if( acct == null ){
				mCurrentLoginCallback.error("account object was null");
				return;
			}
			try {
				responseJson.put("displayName", acct.getDisplayName());
				responseJson.put("email", acct.getEmail());
				responseJson.put("familyName", acct.getFamilyName());
				responseJson.put("givenName", acct.getGivenName());
				responseJson.put("id", acct.getId());
				responseJson.put("idToken", acct.getIdToken());
				responseJson.put("photoUrl", acct.getPhotoUrl());
				responseJson.put("serverAuthCode", acct.getServerAuthCode());
			}catch (JSONException ex ){
				Log.e(TAG, "failed to create response object", ex);
			}

			if( mCurrentLoginCallback != null ){
				mCurrentLoginCallback.success( responseJson );
			}
		} else {
			//TODO add better logging
			mCurrentLoginCallback.error("login failed");
		}
	}

	private void setServerClientId(String id, CallbackContext callbackContext){
		if( id != null && id.length() > 0 ){
			mServerClientId = id;
			callbackContext.success();
		} else {
			callbackContext.error("invalid server client id");
		}
	}

	private void login( CallbackContext callbackContext ) {
		if( mCurrentLoginCallback != null ){
			callbackContext.error("a login is currently in progress");
			return;
		}

		//store the callback for after the activity finishes
		mCurrentLoginCallback = callbackContext;
		//register for activity callback events
		cordova.setActivityResultCallback(this);

		//create builder we can manipulate
		GoogleSignInOptions.Builder optionsBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestProfile();

		//if the user setup a server client id, add it
		if( mServerClientId != null ){
			optionsBuilder = optionsBuilder.requestIdToken(mServerClientId);
		}

		GoogleSignInOptions gso = optionsBuilder.build();

		//refresh the client
		if( mGoogleApiClient != null ){
			mGoogleApiClient.disconnect();
			mGoogleApiClient = null;
		}

		//create client with our options
		mGoogleApiClient = new GoogleApiClient.Builder(webView.getContext())
				.addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

		//start the signin activity
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
	}
}
