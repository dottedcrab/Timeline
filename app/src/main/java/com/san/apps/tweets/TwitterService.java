package com.san.apps.tweets;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by sanoojkp on 2/20/2016.
 */
public class TwitterService extends IntentService {

    public TwitterService() {
        super("TwitterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
        builder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
        AccessToken accessToken = new AccessToken(Constants.ACCESS_TOKEN, Constants.ACCESS_SECRET);

        if (Constants.BC_ACTION_PROFILE.equals(action)) {
            Intent localIntent = new Intent(Constants.BC_ACTION_PROFILE);

            try {
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                User user = twitter.verifyCredentials();
                localIntent.putExtra(Constants.EXTENDED_DATA_PROFILE, user);
            } catch (TwitterException e) {
                localIntent.putExtra(Constants.EXTENDED_DATA_ERROR, e.getErrorMessage());
                e.printStackTrace();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        } else if (Constants.BC_ACTION_TIMELINE.equals(action)) {
            Intent localIntent = new Intent(Constants.BC_ACTION_TIMELINE);

            try {
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                List<Status> status = twitter.getHomeTimeline();
                localIntent.putExtra(Constants.EXTENDED_DATA_TIMELINE, (ArrayList) status);
            } catch (TwitterException e) {
                localIntent.putExtra(Constants.EXTENDED_DATA_ERROR, e.getErrorMessage());
                e.printStackTrace();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }
}
