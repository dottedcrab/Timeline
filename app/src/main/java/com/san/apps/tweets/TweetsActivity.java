package com.san.apps.tweets;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import twitter4j.Status;

public class TweetsActivity extends AppCompatActivity implements TweetsListFragment.OnArticleSelectedListener, TweetDetailsFragment.OnArticleClosedListener {

    private static final String KEY_HOME_ENABLED = "home_enabled";
    private static final String KEY_SCREEN_TITLE = "screen_title";

    private android.support.v7.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);

        mActionBar = getSupportActionBar();
        if (null != mActionBar) {
            mActionBar.setElevation(0);
        }

        FragmentManager fm = getFragmentManager();
        TweetsListFragment tlf = (TweetsListFragment) fm.findFragmentByTag(Constants.TAG_LIST_FRAGMENT);

        if (tlf == null) {
            tlf = new TweetsListFragment();
            fm.beginTransaction().add(android.R.id.content, tlf, Constants.TAG_LIST_FRAGMENT).commit();
        }

        if (savedInstanceState != null) {
            mActionBar.setTitle(savedInstanceState.getString(KEY_SCREEN_TITLE));
            mActionBar.setDisplayHomeAsUpEnabled(savedInstanceState.getBoolean(KEY_HOME_ENABLED));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (null != mActionBar.getTitle()) {
            outState.putString(KEY_SCREEN_TITLE, mActionBar.getTitle().toString());
        }
        outState.putBoolean(KEY_HOME_ENABLED, getFragmentManager().getBackStackEntryCount() > 0);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            restoreHomeScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onArticleSelected(Status status) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTENDED_DATA_STATUS, status);

        TweetDetailsFragment tdf = new TweetDetailsFragment();
        tdf.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, tdf, Constants.TAG_DETAILS_FRAGMENT).addToBackStack(null).commit();

        mActionBar.setTitle(getString(R.string.screen_tweet));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onArticleClosed() {
        restoreHomeScreen();
    }

    private void restoreHomeScreen() {
        getFragmentManager().popBackStack();
        mActionBar.setTitle(getString(R.string.app_name));
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }
}
