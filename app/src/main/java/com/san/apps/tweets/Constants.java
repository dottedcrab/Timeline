package com.san.apps.tweets;

/**
 * Created by sanoojkp on 2/13/2016.
 */
class Constants {
    // Access credentials for twitter. These should NOT be empty
    public static final String CONSUMER_KEY = "";
    public static final String CONSUMER_SECRET = "";
    public static final String ACCESS_TOKEN = "";
    public static final String ACCESS_SECRET = "";

    // Defines a custom Intent action
    public static final String BC_ACTION_PROFILE = "com.san.apps.tweets.BROADCAST_PROFILE";
    public static final String BC_ACTION_TIMELINE = "com.san.apps.tweets.BROADCAST_TIMELINE";

    // Keys for data sharing via intents
    public static final String EXTENDED_DATA_PROFILE = "com.san.apps.tweets.PROFILE";
    public static final String EXTENDED_DATA_TIMELINE = "com.san.apps.tweets.TIMELINE";
    public static final String EXTENDED_DATA_ERROR = "com.san.apps.tweets.ERROR";
    public static final String EXTENDED_DATA_STATUS = "com.san.apps.tweets.STATUS";

    // Fragment tags
    public static final String TAG_LIST_FRAGMENT = "tweetlistfragment";
    public static final String TAG_DETAILS_FRAGMENT = "tweetdetailsfragment";

    public static final String MEDIA_TYPE_PHOTO = "photo";
}
