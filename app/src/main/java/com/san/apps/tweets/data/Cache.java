package com.san.apps.tweets.data;

import android.content.Context;

import java.io.File;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by sanoojkp on 2/19/2016.
 */
public class Cache extends DataStore {

    public Cache(Context context) {
        mContext = context;
    }

    @Override
    public boolean saveTimelineToStore(List<Status> timeline) {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_TIMELINE);

        return null != timeline && writeData(file, timeline);
    }

    @Override
    public boolean saveProfileToStore(User user) {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_PROFILE);

        return null != user && writeData(file, user);
    }

    @Override
    public List<Status> readTimelineFromStore() {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_TIMELINE);

        return (List<Status>) readData(file);
    }

    @Override
    public User readProfileFromStore() {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_PROFILE);

        return (User) readData(file);
    }

    public boolean isTimelineStale() {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_TIMELINE);

        return isCacheStale(file, TIMELINE_CACHE_EXPIRY);
    }

    public boolean isProfileStale() {
        final File file = new File(mContext.getCacheDir(), FILE_NAME_PROFILE);

        return isCacheStale(file, PROFILE_CACHE_EXPIRY);
    }
}
