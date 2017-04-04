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

	private GoogleApiClient mGoogleApiClient;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);


	}

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

		if ( action.equals(ACTION_LOGIN) ) {
			login();
		} else {
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
			GoogleSignInAccount acct = result.getSignInAccount();
			String idToken = acct.getIdToken();
			Log.d(TAG, "");
		} else {
			// Signed out, show unauthenticated UI.
		}
	}

	private void login() {
		cordova.setActivityResultCallback(this);

		String serverClientId = cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "server_client_id", "string", cordova.getActivity().getPackageName()));

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestEmail()
			.requestProfile()
			.requestIdToken(serverClientId)
			.build();

		mGoogleApiClient = new GoogleApiClient.Builder(webView.getContext())
				.addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
	}
}