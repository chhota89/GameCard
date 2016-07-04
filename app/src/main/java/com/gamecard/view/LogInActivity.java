package com.gamecard.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gamecard.R;
import com.gamecard.utility.EditSharedPrefrence;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogInActivity extends AppCompatActivity {

    TextView textView;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggedIn()) {
          startHomeActivity(true);
            finish();
            //checkCurrentTokenChage();
        }
        setContentView(R.layout.activity_log_in);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebookLogin);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                EditSharedPrefrence.setUserLogin(true,LogInActivity.this);
                startHomeActivity(true);
                //Picasso.with(MainActivity.this).load("https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large").into(image);
                // profilePictureView.setProfileId(loginResult.getAccessToken().getUserId());

            }

            @Override
            public void onCancel() {
                //Toast.makeText(LogInActivity.this,"On Cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                //Toast.makeText(LogInActivity.this,"On Error"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        getHashKey();
    }

    public void skipClick(View view){
        startHomeActivity(false);
    }

    private void startHomeActivity(boolean flag){
        Intent intent= new Intent(LogInActivity.this, HomeView.class);
        intent.putExtra("Facebook_Login",flag);
        startActivity(intent);
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.gamecard", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:...........", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void checkCurrentTokenChage() {
        AccessTokenTracker accessToken = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                Toast.makeText(LogInActivity.this, "Access Token is Chaged", Toast.LENGTH_LONG).show();
            }
        };

    }


}
