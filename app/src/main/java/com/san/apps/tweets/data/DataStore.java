package com.san.apps.tweets.data;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by sanoojkp on 2/19/2016.
 */
abstract class DataStore {
    static final long TIMELINE_CACHE_EXPIRY = 1000 * 60; // 1 minute
    static final long PROFILE_CACHE_EXPIRY = 1000 * 60 * 60 * 24; // 1 day

    static final String FILE_NAME_TIMELINE = "timeline_data";
    static final String FILE_NAME_PROFILE = "profile_data";

    Context mContext;

    abstract boolean saveTimelineToStore(List<Status> timeline);

    abstract boolean saveProfileToStore(User user);

    abstract List<Status> readTimelineFromStore();

    abstract User readProfileFromStore();

    boolean writeData(File file, Object data) {
        boolean isSaved = false;

        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        try {
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(data);
            isSaved = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) { /* do nothing */ }
        }

        return isSaved;
    }

    Object readData(File file) {
        Object data = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;

        try {
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
            data = in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ignored) {
            }
        }

        return data;
    }

    boolean isCacheStale(File file, long expiry){
        long currentTime = System.currentTimeMillis();
        long cachedTime = file.lastModified();

        return currentTime - cachedTime > expiry;
    }

}
