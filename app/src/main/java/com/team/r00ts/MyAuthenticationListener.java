package com.team.r00ts;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MyAuthenticationListener implements LayerAuthenticationListener {
    String mUserId = "000000";
    MainActivity main_activity;
    ConversationListActivity conversationListActivity;

    public boolean firstAuthentication=true;

    //Main Activity Constructor
    public MyAuthenticationListener(MainActivity ma){
        main_activity = ma;
    }

    public void assignConversationListActivity(ConversationListActivity cla){
        conversationListActivity=cla;
    }


    //only used for main activity
    @Override
    public void onAuthenticated(LayerClient client, String arg1) {
        System.out.println("Authentication successful");
        Log.d("I reached here", "I reached here " + firstAuthentication);
        //Start the conversation view after a successful authentication
        if(main_activity != null && firstAuthentication && !MyConnectionListener.getReceive()) {
            firstAuthentication = false;
            try {
                ProgressBar progressBar = (ProgressBar) main_activity.pager.getChildAt(main_activity.pager.getChildCount() - 1).findViewById(R.id.login_progress);
                progressBar.setProgress(60);
            } catch (NullPointerException e) {
                Log.d("null","progress bar not updated, not on Main Activity view");
            }
            main_activity.onUserAuthenticated();
            }


    }








    //other
    public void setmUserId(String mUserIdLocal){
        mUserId=mUserIdLocal;
    }



    @Override
    public void onAuthenticationChallenge(final LayerClient layerClient, final String nonce) {


      /*
       * 2. Acquire an identity token from the Layer Identity Service
       */
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    Log.d("I reached here", "I reached here");
                    HttpPost post = new HttpPost("https://layer-identity-provider.herokuapp.com/identity_tokens"); //TODO: change to heroku app link
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");

                    JSONObject json = new JSONObject()
                            .put("app_id", layerClient.getAppId())
                            .put("user_id", mUserId)
                            .put("nonce", nonce );
                    post.setEntity(new StringEntity(json.toString()));

                    HttpResponse response = (new DefaultHttpClient()).execute(post);
                    //Log.d("test", "test"+EntityUtils.toString((response.getEntity())));
                    String eit = (new JSONObject(EntityUtils.toString(response.getEntity())))
                            .optString("identity_token");
                /*
                 * 3. Submit identity token to Layer for validation
                 */

                    System.out.println(eit);
                    Log.d("Authentication ","Answering Authentication Challenge"+eit);
                    layerClient.answerAuthenticationChallenge(eit);

                    //Authentication Error Occurs
                    // :(
                } catch (Exception e) {
                    e.printStackTrace();

                    Log.d("Did not Work", "Did not Work");
                    main_activity.authFailLayer();
                }
                return null;
            }
        }).execute();

    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        // TODO Auto-generated method stub
        System.out.println("There was an error authenticating");
        Log.d("Did not Work", "Did not Work");
        main_activity.authFailLayer();
    }

    //only used for Conversation Activity
    @Override
    public void onDeauthenticated(LayerClient client) {
        // TODO Auto-generated method stub

        Log.d("Deauthenticated", "Deauthenticated");
            firstAuthentication=true;
            if(conversationListActivity!=null)conversationListActivity.onUserDeauthenticated();
           // else if(reportedIDListActivity!=null)reportedIDListActivity.onUserDeauthenticated();

    }




}
