package com.example.sbandroidsampleapptutorial.main;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.example.sbandroidsampleapptutorial.R;
import com.example.sbandroidsampleapptutorial.groupchannel.GroupChannelActivity;


// this is a page where we show the channel options (group / open)
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CoordinatorLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        mMainLayout = (CoordinatorLayout) findViewById(R.id.layout_main);

        // set linear layout to be clickable
        findViewById(R.id.linear_layout_group_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroupChannelActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.linear_layout_open_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: navigate to open channel
//                Log.d("log", "--------------------");
                showSnackBar("working in progress");
            }
        });

        findViewById(R.id.linear_layout_super_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: navigate to supergroup channel
                showSnackBar("supergroup coming soon");
            }
        });


        findViewById(R.id.button_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    /**
     * Unregisters all push tokens for the current user so that they do not receive any notifications,
     * then disconnects from SendBird.
     */
    // todo
    private void disconnect(){
//        Toast.makeText(MainActivity.this, "work in progress", Toast.LENGTH_SHORT).show();
        showSnackBar("work in progress");
        return;
    }


    // show snackbar which will show the info message at the bottom of the screen
    public void showSnackBar(String text){
        Snackbar snackbar = Snackbar.make(mMainLayout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
