package com.san.apps.tweets.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Created by sanoojkp on 2/14/2016.
 */
public class Utils {
    private static final String HTTPS = "https://";

    public static String formatDate(Date date) {
        long now = System.currentTimeMillis();

        String relativeTime = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.MINUTE_IN_MILLIS).toString();
        String[] split = relativeTime.split(" ");

        if (split.length > 1){
            if(Objects.equals(split[0], "0")){
                relativeTime = "Now";
            }else {
                relativeTime = split[0];
                relativeTime += split[1].charAt(0);
            }
        }

        return relativeTime;
    }

    public static String getPlainText(String source) {
        String result = source;

        if (!TextUtils.isEmpty(source)) {
            if (source.contains(HTTPS)) {
                result = source.substring(0, source.indexOf(HTTPS));
            }
        }

        return result;
    }

    public static boolean isNwConnected(Context context) {
        if (context == null) {
            return true;
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        return nwInfo != null && nwInfo.isConnectedOrConnecting();
    }
}
