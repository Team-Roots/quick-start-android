package com.layer.quick_start_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    //other variables
    Context context = this;
    String loginString;
    LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a LayerClient object no UserId included
        getSupportActionBar().hide();
        loginController = new LoginController();
        loginController.setLayerClient(context, this);
        setContentView(R.layout.activity_main);
    }

    protected void onResume(){

        super.onResume();
        //Program Login Button
        Button loginButton = (Button) findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText loginEditText = (EditText) findViewById(R.id.loginedittext);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(loginEditText.getWindowToken(), 0);
                setContentView(R.layout.loading_screen);
                TextView loggingoutintext=(TextView)findViewById(R.id.loginlogoutinformation);
                loggingoutintext.setText("Loading...");

                loginString = loginEditText.getText().toString().trim();

                loginController.login(loginString);
            }
        });


        //Login if Authentication exists from last session

        if (loginController.getLayerClient().isAuthenticated()) {
            setContentView(R.layout.loading_screen);
            TextView loggingoutintext=(TextView)findViewById(R.id.loginlogoutinformation);
            loggingoutintext.setText("Loading...");
            loginString=loginController.getLayerClient().getAuthenticatedUserId();

            loginController.login(loginString);

        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onUserAuthenticated(){

        Log.d("User Authenticated", "User Authenticated");
        Log.d("User Connected","isConnected"+loginController.getLayerClient().isConnected()+"isAuthenticated"+loginController.getLayerClient().isAuthenticated());

        //with condition check
        /*final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {

            @Override
            public void run() {

                while(loginController.getLayerClient().getConversations()==null){
                    Log.d("Re ran thread", "re ran thread");
                }
                    done.countDown();

            }
        }).start();
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, ConversationListActivity.class);
                intent.putExtra("mUserId", loginString);
                Log.d("Conversations","Conversations in Main Activity:"+loginController.getLayerClient().getConversations());
                finish();
                startActivity(intent);
            }
        }).start();



    }
}