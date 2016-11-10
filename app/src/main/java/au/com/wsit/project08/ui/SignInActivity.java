package au.com.wsit.project08.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import au.com.wsit.project08.R;
import au.com.wsit.project08.utils.TrackerConstants;

public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks
{
    private static final String TAG = SignInActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSharedPreferences = getSharedPreferences(TrackerConstants.PREFERENCES_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signIn();
            }
        });

        // Check if authenicated
        if(!mSharedPreferences.getBoolean(TrackerConstants.KEY_AUTHENTICATED, false))
        {
            Log.i(TAG, "Not authenicated yet");
        }
        else
        {
            // Navigate to main
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        }


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG, "User " + acct.getEmail() + " authenticated");
            Log.i(TAG, "ID " + acct.getId() + " authenticated");
            mEditor.putString(TrackerConstants.KEY_EMAIL, acct.getEmail());
            mEditor.putString(TrackerConstants.KEY_FIRSTNAME, acct.getGivenName());
            mEditor.putString(TrackerConstants.KEY_SECONDNAME, acct.getFamilyName());
            mEditor.putString(TrackerConstants.KEY_ID, acct.getId());
            mEditor.putBoolean(TrackerConstants.KEY_AUTHENTICATED, true);
            mEditor.apply();

            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);

        }
        else
        {
            // Signed out, show unauthenticated UI.
            Log.i(TAG, "User not authenticated");
            Toast.makeText(SignInActivity.this, "Problem signing in", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "Google API client connected in sign in");
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i(TAG, "SignIn: Connection suspended");
    }
}
