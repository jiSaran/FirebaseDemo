package com.saran.test.fireauthenticationtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by core I5 on 1/2/2017.
 */

public class SigninActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth firebaseAuth;
    private Button signin_btn,signout_btn,post_btn;
    private GoogleApiClient googleApiClient;
    private EditText emailtxt,passtxt;
    private ListView listView;
    private static final String TAG = "SigninActivity";
    private FirebaseAuth.AuthStateListener listener;
    private int type;
    private final int Google_R_CODE = 2;
    private CallbackManager callbackManager;

    private List<String> post_list = new ArrayList<>();

    private List<String> needpermissions = Arrays.asList("email","public_profile","user_posts");

    private List<String> writepermissions = Arrays.asList("publish_actions");

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String TWITTER_KEY = "twitter_key";
    private static final String TWITTER_SECRET = "twitter_secret_key";
    private TwitterAuthClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        signin_btn = (Button)findViewById(R.id.signin);
        emailtxt = (EditText)findViewById(R.id.email);
        passtxt = (EditText)findViewById(R.id.password);
        signout_btn = (Button)findViewById(R.id.signout);
        listView = (ListView)findViewById(R.id.lstview);
        post_btn = (Button)findViewById(R.id.post_fb);

        firebaseAuth = FirebaseAuth.getInstance();

        signin_btn.setOnClickListener(this);
        signout_btn.setOnClickListener(this);
        post_btn.setOnClickListener(this);

        type = getIntent().getExtras().getInt("Type");

        if(type == R.string.emailsgn){
            signin_btn.setVisibility(View.VISIBLE);
            emailtxt.setVisibility(View.VISIBLE);
            passtxt.setVisibility(View.VISIBLE);
            signout_btn.setVisibility(View.GONE);
        } else{
            signin_btn.setVisibility(View.VISIBLE);
            emailtxt.setVisibility(View.GONE);
            passtxt.setVisibility(View.GONE);
            signout_btn.setVisibility(View.GONE);
        }

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    signin_btn.setVisibility(View.GONE);
                    signout_btn.setVisibility(View.VISIBLE);
                    if(type == R.string.facebook){
                        post_btn.setVisibility(View.VISIBLE);
                        emailtxt.setVisibility(View.VISIBLE);
                        setListview();
                    }
                } else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        callbackManager = CallbackManager.Factory.create();

        twitterClient = new TwitterAuthClient();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == signin_btn.getId()){
            sign_in();
        } else if(view.getId() == signout_btn.getId()){
            sign_out();
        } else if(view.getId() == post_btn.getId()){
            post_feed();
        }
    }

    private void post_feed() {
        Bundle param = new Bundle();
        param.putString("message",emailtxt.getText().toString());
        Log.d(TAG,"Tokeeennn: "+AccessToken.getCurrentAccessToken());
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/feed", param, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.d(TAG,"Post on complete is true");
                Toast.makeText(SigninActivity.this,"Posted to fb",Toast.LENGTH_LONG).show();
            }
        }).executeAsync();

    }

    private void sign_in() {
        if(type == R.string.emailsgn){
            signWithEmail(emailtxt.getText().toString(),passtxt.getText().toString());
        } else if(type == R.string.google){
            signWithGoogle();
        } else if(type == R.string.facebook){
            signWithFacebook();
        } else if(type == R.string.twitter){
            signInWithTwitter();
        } else if(type == R.string.git){
            signInWithGit();
        }
    }

    private void signInWithGit() {
        String token = "<GITHUB-ACCESS-TOKEN>";
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onCompleteGit:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SigninActivity.this, "Git authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(SigninActivity.this, "Git Authentication Success.", Toast.LENGTH_LONG).show();
                        signin_btn.setVisibility(View.GONE);
                        signout_btn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void signInWithTwitter() {

        twitterClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        });
    }

    private void handleTwitterSession(TwitterSession data) {
        Log.d(TAG, "handleTwitterSession:" + data);

        AuthCredential credential = TwitterAuthProvider.getCredential(data.getAuthToken().token,data.getAuthToken().secret);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredentialTwitter:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredentialTwitter", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(SigninActivity.this, "Twitter Authentication Success.", Toast.LENGTH_LONG).show();
                        signin_btn.setVisibility(View.GONE);
                        signout_btn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void signWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this,needpermissions);
        LoginManager.getInstance().logInWithPublishPermissions(this,writepermissions);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(SigninActivity.this, "Authentication Success.", Toast.LENGTH_LONG).show();
                signin_btn.setVisibility(View.GONE);
                signout_btn.setVisibility(View.VISIBLE);
                post_btn.setVisibility(View.VISIBLE);
                emailtxt.setVisibility(View.VISIBLE);
            }
        });

        setListview();

    }

    private void setListview(){
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                post_list.clear();
                Log.d(TAG,"Feeds: "+response.toString());
                JSONObject object = response.getJSONObject();
                JSONArray jsonArray;
                Log.d(TAG,"ACCESSTOKEENN: "+AccessToken.getCurrentAccessToken());
                try {
                    jsonArray = object.getJSONArray("data");
                    int get = jsonArray.length();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject post = jsonArray.getJSONObject(i);
                        Iterator iterator = post.keys();
                        while(iterator.hasNext()){
                            String key = (String)iterator.next();
                            if(key.equals("message")){
                                String str = post.getString("message");
                                post_list.add(str);
                                Log.d(TAG,"YOYOY: "+post.getString("message"));
                            }
                        }
                    }
                    Log.d(TAG,"YOYOYO: "+jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CustomAdapter  adapter = new CustomAdapter(SigninActivity.this,post_list);
                listView.setAdapter(adapter);
            }
        }).executeAsync();



    }


    private void signWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,Google_R_CODE);
    }

    private void signWithEmail(String email, String password){
        Log.d(TAG, "signIn:" + email);
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:Failed");
                            Toast.makeText(SigninActivity.this,"Login failed!!!",Toast.LENGTH_LONG).show();
                            return;
                        }

                        Toast.makeText(SigninActivity.this,"Login Successful!!!",Toast.LENGTH_LONG).show();
                        signin_btn.setVisibility(View.GONE);
                        signout_btn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void sign_out(){
        firebaseAuth.signOut();
        signin_btn.setVisibility(View.VISIBLE);
        signout_btn.setVisibility(View.GONE);
        if(type == R.string.facebook){
            if(callbackManager!=null){
                LoginManager.getInstance().logOut();
                Toast.makeText(SigninActivity.this,"Successfully logged out Facebook",Toast.LENGTH_LONG).show();
                post_btn.setVisibility(View.GONE);
                emailtxt.setVisibility(View.GONE);
            }
        }else if(type == R.string.google){
            if(googleApiClient!=null){
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(SigninActivity.this,"Successfully logged out Google",Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        }else if(type == R.string.twitter){
            Toast.makeText(SigninActivity.this,"Successfully logged out Twitter",Toast.LENGTH_LONG).show();
        } else if(type == R.string.git){
            Toast.makeText(SigninActivity.this,"Successfully logged out Git",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(listener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(type == R.string.facebook){
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }else if(type == R.string.twitter){
            twitterClient.onActivityResult(requestCode, resultCode, data);
        } else{/*Sign in with google*/
            if(requestCode == Google_R_CODE){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }else{
                    Log.d(TAG,"Google signin error");
                    Toast.makeText(SigninActivity.this,"Google signin error",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(!task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:Failed");
                            Toast.makeText(SigninActivity.this,"Login failed!!!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(SigninActivity.this,"Login Successful!!!",Toast.LENGTH_LONG).show();
                        signin_btn.setVisibility(View.GONE);
                        signout_btn.setVisibility(View.VISIBLE);
                    }
                });
    }
}
