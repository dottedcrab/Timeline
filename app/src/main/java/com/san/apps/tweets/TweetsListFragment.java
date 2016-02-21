package com.san.apps.tweets;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.san.apps.tweets.adapter.TweetAdapter;
import com.san.apps.tweets.data.Cache;
import com.san.apps.tweets.utility.Utils;

import java.util.List;

import twitter4j.Status;
import twitter4j.User;

/**
 * Created by sanoojkp on 2/13/2016.
 */
public class TweetsListFragment extends Fragment {

    private static final String TAG = TweetsListFragment.class.getName();

    private SwipeRefreshLayout mSwipe;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private List<Status> mTimeline;
    private OnArticleSelectedListener mListener;
    private int mCurrentPos = 0;
    private Cache mCache;

    public interface OnArticleSelectedListener {
        void onArticleSelected(Status status);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnArticleSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCache = new Cache(getActivity().getApplicationContext());

        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BC_ACTION_PROFILE);
        mStatusIntentFilter.addAction(Constants.BC_ACTION_TIMELINE);
        ResponseReceiver mResponseReceiver = new ResponseReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mResponseReceiver,
                mStatusIntentFilter);

        if (mCache.isProfileStale()) {
            getProfile();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        setHasOptionsMenu(true);

        mListView = (ListView) rootView.findViewById(R.id.tweets);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_progress);
        mSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.sw_swipe);
        mSwipe.setColorSchemeResources(R.color.colorAccent);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(true);
                getTimeline();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (null != mTimeline) {
            // Seems like an orientation change. Just show persistent data
            showTimeline(true);
        } else {
            // Fresh launch. Try to retrieve from cache first
            mTimeline = mCache.readTimelineFromStore();

            if (null == mTimeline) {
                // Cache is empty. Fetch fresh data
                Log.d(TAG, "Cache is empty. Fetch fresh data");

                mProgressBar.setVisibility(View.VISIBLE);
                getTimeline();
            } else if (mCache.isTimelineStale()) {
                // Cache is stale. Show the timeline anyway and fetch fresh data
                Log.d(TAG, "Cache is stale. Fetch fresh data");

                showTimeline(false);
                mSwipe.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(true);
                    }
                });

                getTimeline();
            } else {
                // Cache seems recent. Just show the timeline
                showTimeline(false);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mCurrentPos = mListView.getFirstVisiblePosition();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_article_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.view_profile:
                showProfile();
                break;

            case R.id.action_refresh:
                mSwipe.setRefreshing(true);
                getTimeline();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfile() {
        if (Utils.isNwConnected(getActivity())) {
            Intent serviceIntent = new Intent(getActivity(), TwitterService.class);
            serviceIntent.setAction(Constants.BC_ACTION_PROFILE);
            getActivity().startService(serviceIntent);
        }
    }

    private void showProfile() {
        User user = mCache.readProfileFromStore();

        if (null != user) {
            ProfileFragment newFragment = ProfileFragment.newInstance(user);
            newFragment.show(getFragmentManager(), "dialog");
        } else {
            Toast.makeText(getActivity(), getString(R.string.err_profile_empty), Toast.LENGTH_SHORT).show();
            getProfile(); // Cache is empty. Fetch profile info
        }
    }

    private void getTimeline() {
        if (Utils.isNwConnected(getActivity())) {
            Intent serviceIntent = new Intent(getActivity(), TwitterService.class);
            serviceIntent.setAction(Constants.BC_ACTION_TIMELINE);
            getActivity().startService(serviceIntent);
        } else {
            hideProgress();
            Toast.makeText(getActivity(), getString(R.string.err_network_failure), Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimeline(boolean retainPosition) {
        TweetAdapter adapter = new TweetAdapter(getActivity(), mTimeline);
        mListView.setAdapter(adapter);
        if (retainPosition) {
            mListView.setSelection(mCurrentPos);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Status status = mTimeline.get(position);
                Log.d(TAG, "ScreenName: " + status.getUser().getScreenName());

                if (null != mListener) {
                    mListener.onArticleSelected(status);
                }
            }
        });
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mSwipe.setRefreshing(false);
    }

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String error = intent.getExtras().getString(Constants.EXTENDED_DATA_ERROR);

            if (!TextUtils.isEmpty(error)) {
                hideProgress();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                return;
            }

            if (Constants.BC_ACTION_PROFILE.equals(action)) {
                User user = (User) intent.getExtras().get(Constants.EXTENDED_DATA_PROFILE);
                // Cache the response
                mCache.saveProfileToStore(user);
            } else if (Constants.BC_ACTION_TIMELINE.equals(action)) {
                List<Status> timeline = (List<Status>) intent.getExtras().get(Constants.EXTENDED_DATA_TIMELINE);

                // TODO: Do this off the UI thread?
                // Cache the response
                mCache.saveTimelineToStore(timeline);

                hideProgress();
                mTimeline = timeline;
                showTimeline(false);
            }
        }
    }
}
