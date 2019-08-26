package com.example.demo;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private static final int RC_SIGN_IN = 123;
    private com.google.android.gms.common.SignInButton sign_in_button;
    private com.facebook.login.widget.LoginButton login_button;

    private Button logTokenButton;
    private Button subscribeButton;
    private Button crashButton;
    private Button FirebaseAnalyticsButton;
    private EditText ETtoken;

    //google
    public GoogleSignInOptions gso;
    public GoogleApiClient mGoogleApiClient;
    //facebook
    private CallbackManager callbackManager;
    public ProfileTracker profileTracker;
    //分析
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the FirebaseAnalytics instance.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.e(TAG, "Key: " + key + " Value: " + value);
            }
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        callbackManager = CallbackManager.Factory.create();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                onFacebookLogin();
            }
        };
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        // App code
//                        Log.e("facebook","is login onSuccess");
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                        Log.e("facebook","is login onCancel");
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                        Log.e("facebook","is login onError");
//                    }
//                });


        sign_in_button = this.findViewById(R.id.sign_in_button);
        login_button = this.findViewById(R.id.login_button);
        logTokenButton = this.findViewById(R.id.logTokenButton);
        subscribeButton = this.findViewById(R.id.subscribeButton);
        crashButton = this.findViewById(R.id.crashButton);
        FirebaseAnalyticsButton = this.findViewById(R.id.FirebaseAnalyticsButton);
        ETtoken = this.findViewById(R.id.TVtoken);
        init();

        subscribeButton.setOnClickListener(this);
        logTokenButton.setOnClickListener(this);
        crashButton.setOnClickListener(this);
        login_button.setOnClickListener(this);
        FirebaseAnalyticsButton.setOnClickListener(this);
                // [END retrieve_current_token]


        login_button.setReadPermissions("email");
        // If using in a fragment
//        login_button.setFragment(this);

        // Callback registration
//        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                Log.e("facebook","is login onSuccess");
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.e("facebook","is login onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.e("facebook","is login onError");
//            }
//        });



    }

    private void onFacebookLogin() {
        Profile currentProfile = Profile.getCurrentProfile();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (currentProfile != null & accessToken != null) {
           String facebookToken = accessToken.getToken();
           String facebookId = currentProfile.getId();
           String facebookName = currentProfile.getName();
           Log.e(TAG,"facebookToken="+facebookToken+"facebookId="+facebookId+"facebookName ="+facebookName);
           Toast.makeText(MainActivity.this,"facebookToken="+facebookToken+"facebookId="+facebookId+"facebookName ="+facebookName,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * facebook登录
     */
    public void facebookLogin() {
        //指定登录跳转Facebook客户端还是页面
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.DIALOG_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    private void init() {
        //初始化谷歌登录服务
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener(){
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //登录
        sign_in_button.setSize(SignInButton.SIZE_STANDARD);
        sign_in_button.setScopes(gso.getScopeArray());
        sign_in_button.setOnClickListener(this);

        //退出
//                google_loginOut_bt.setOnClickListener(this) ;



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //谷歌登录成功回调
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 104      *google 登录
     * 105
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //获取用户名
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String token = acct.getIdToken();
            String id = acct.getId();
            Log.e("success", name + email + token + id);
            Toast.makeText(MainActivity.this,"success:"+"name = " + name + "email = " + email + "token = " + token + "id = " + id,Toast.LENGTH_SHORT).show();
        } else {
            Log.e("failed", "errer code ==" + result.getStatus());
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //定义处理接收的方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(MessageEvent messageEvent){
        String sound = messageEvent.getMessage();
        if (!TextUtils.isEmpty(sound)){
            Log.e(TAG,"播放MP3");
          PlayVoiceUtil.playVoice(MainActivity.this,R.raw.test1);
        }

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.login_button:
//                Bundle bundleEvent = new Bundle();
//                bundleEvent.putLong("click_time",System.currentTimeMillis());
//                bundleEvent.putString("key","value");
//                mFirebaseAnalytics.logEvent("click_event",bundleEvent);
                facebookLogin();
                break;

            case R.id.subscribeButton:
                Log.d(TAG, "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("weather")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.e(TAG, msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
                break;
            case R.id.logTokenButton:
                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.e(TAG, msg);
//                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                ETtoken.setText("");
                                ETtoken.setText(token);
                            }
                        });
                // [END retrieve_current_token]
                break;
            case R.id.crashButton:
                Crashlytics.log(Log.VERBOSE, TAG, "Crash");
                Crashlytics.log(Log.DEBUG, TAG, "Crash");
                Crashlytics.log(Log.INFO, TAG, "Crash");
                Crashlytics.log(Log.WARN, TAG, "Crash");
                Crashlytics.log(Log.INFO, TAG, "Crash");
                Crashlytics.log(Log.ASSERT, TAG, "Crash");
                Crashlytics.getInstance().crash();
                break;

            case R.id.FirebaseAnalyticsButton:

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Log.e("tag", "clickView: ");
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }
}