package com.example.sbandroidsampleapptutorial.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sbandroidsampleapptutorial.R;
import com.example.sbandroidsampleapptutorial.utils.PreferenceUtils;
import com.example.sbandroidsampleapptutorial.utils.PushUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;


// this page is a login page : user can login
public class LoginActivity extends AppCompatActivity {

    private CoordinatorLayout mLoginLayout;
    private TextInputEditText mUserIdConnectEditText, mUserNicknameEditText;
    private Button mLoginButton;
    private ContentLoadingProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // connecting with xml
        mLoginLayout = (CoordinatorLayout) findViewById(R.id.layout_login);

        mUserIdConnectEditText = (TextInputEditText) findViewById(R.id.edittext_login_user_id);
        mUserNicknameEditText = (TextInputEditText) findViewById(R.id.edittext_login_user_nickname);

        mUserIdConnectEditText.setText(PreferenceUtils.getUserId()); // it will get the default userID set in the preferenceUtil
        mUserNicknameEditText.setText(PreferenceUtils.getNickname()); // it will get the default userNickName set in the preferenceUtil

        mLoginButton = (Button) findViewById(R.id.button_login_connect);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mUserIdConnectEditText.getText().toString();
                // Remove all space from userID
                userId = userId.replaceAll("\\s", "");

                String nickName = mUserNicknameEditText.getText().toString();

                // update userID, Nickname value in preference Util
                PreferenceUtils.setUserId(userId);
                PreferenceUtils.setNickname(nickName);

                // connecting to SendBird Server and navigate user to the next activity (MainActivity)
                connectToSendBird(userId, nickName);
            }
        });


        mUserIdConnectEditText.setSelectAllOnFocus(true);
        mUserNicknameEditText.setSelectAllOnFocus(true);

        // set a loading indicator
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_login);
    }


    // onStart
    @Override
    protected void onStart() {
        super.onStart();
        if (PreferenceUtils.getConnected(true)){
            connectToSendBird(PreferenceUtils.getUserId(), PreferenceUtils.getNickname());
        }
    }

    // connectToSendBird
    private void connectToSendBird(final String userId, final String userNickname){
        // show the loading indicator
        showProgressBar(true);
        mLoginButton.setEnabled(false);


        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; once user is connected then hide the progress bar.
                showProgressBar(false);

                if (e != null){
                    // Error
                    Toast.makeText(LoginActivity.this, "" + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // show error in snackbar
                    showSnackBar("Login to SendBird failed");
                    mLoginButton.setEnabled(true);
                    PreferenceUtils.getConnected(false);
                    return;
                }

                // success
                PreferenceUtils.setConnected(true);

                // update the user's nickname & push token
                updateCurrentUserInfo(userNickname);
                updateCurrentUserPushToken();

                // navigate to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    // updateCurrentUserInfo -> update the user's nickname
    /**
     * Updates the user's nickname.
     * @param userNickname  The new nickname of the user.
     */
    private void updateCurrentUserInfo(final String userNickname){
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null){
                    // Error
                    Toast.makeText(
                            LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // show failed message in snackbar
                    showSnackBar("Update user nickname failed");
                    return;
                }
                // success
                PreferenceUtils.setNickname(userNickname);
            }
        });
    }


    // Todo: update push token -> for push notification
    public void updateCurrentUserPushToken(){
        PushUtils.registerPushTokenForCurrentUser(LoginActivity.this, null);

    }


    /// -------- util ----------
    // show snackbar which will show the info message at the bottom of the screen
    public void showSnackBar(String text){
        Snackbar snackbar = Snackbar.make(mLoginLayout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    // show progressbar
    public void showProgressBar(boolean show){
        if (show){
            mProgressBar.show();
        } else {
            mProgressBar.hide();
        }
    }

}
