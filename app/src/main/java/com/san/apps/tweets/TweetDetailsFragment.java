package com.san.apps.tweets;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.san.apps.tweets.utility.Utils;
import com.squareup.picasso.Picasso;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

/**
 * Created by sanoojkp on 2/14/2016.
 */
public class TweetDetailsFragment extends Fragment {

    private static final String TAG = TweetDetailsFragment.class.getName();

    private ImageView mUserImage;
    private ImageView mTweetMedia;
    private TextView mUserName;
    private TextView mScreenName;
    private TextView mTweetText;
    private TextView mArticleUrl;

    private TextView mTvTime;
    private TextView mTvFav;
    private TextView mTvRetweets;

    private Status mStatus;

    private OnArticleClosedListener mListener;

    public interface OnArticleClosedListener {
        void onArticleClosed();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnArticleClosedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnArticleClosedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tweet_details, container, false);
        setHasOptionsMenu(true);

        mUserImage = (ImageView) rootView.findViewById(R.id.iv_user);
        mTweetMedia = (ImageView) rootView.findViewById(R.id.iv_media);
        mUserName = (TextView) rootView.findViewById(R.id.tv_user_name);
        mScreenName = (TextView) rootView.findViewById(R.id.tv_handle);
        mTweetText = (TextView) rootView.findViewById(R.id.tv_tweetText);
        mArticleUrl = (TextView) rootView.findViewById(R.id.tv_articleUrl);
        mTvFav = (TextView) rootView.findViewById(R.id.tv_favourites);
        mTvRetweets = (TextView) rootView.findViewById(R.id.tv_retweet);
        mTvTime = (TextView) rootView.findViewById(R.id.tv_dateTime);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (null == mStatus) {
            mStatus = (Status) getArguments().get(Constants.EXTENDED_DATA_STATUS);
        }

        User user = mStatus.getUser();
        Picasso.with(getActivity()).load(user.getBiggerProfileImageURL()).into(mUserImage);
        mUserName.setText(user.getName());
        mScreenName.setText(getString(R.string.sn_prefix, user.getScreenName()));

        mTvTime.setText(Utils.formatDate(mStatus.getCreatedAt()));
        mTvRetweets.setText(String.valueOf(mStatus.getRetweetCount()));
        mTvFav.setText(String.valueOf(mStatus.getFavoriteCount()));
        mTweetText.setText(Utils.getPlainText(mStatus.getText()));

        URLEntity[] entities = mStatus.getURLEntities();
        if (null != entities && entities.length > 0) {
            mArticleUrl.setText(entities[0].getURL());
            Log.d(TAG, "URL: " + entities[0].getURL());
        } else {
            mArticleUrl.setVisibility(View.GONE);
            Log.e(TAG, "URL info is NULL");
        }

        MediaEntity[] media = mStatus.getMediaEntities();
        if (null != media && media.length > 0) {
            MediaEntity entity = media[0];
            if (Constants.MEDIA_TYPE_PHOTO.equals(entity.getType())) {
                Picasso.with(getActivity()).load(entity.getMediaURL()).into(mTweetMedia);
                Log.d(TAG, "Media URL: " + entity.getMediaURL());
            }
        } else {
            Log.e(TAG, "Media info is NULL");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_tweet_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != mListener) {
                    mListener.onArticleClosed();
                }
                break;

            case R.id.action_share:
                doShare();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getFormattedMessage());

        startActivity(intent);
    }

    private String getFormattedMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append(mStatus.getUser().getName()).append(" @").append(mStatus.getUser().getScreenName()).append("\n");
        msg.append(Utils.getPlainText(mStatus.getText())).append("\n");
        URLEntity[] entities = mStatus.getURLEntities();
        msg.append(null != entities && entities.length > 0 ? entities[0].getURL() : "");

        return msg.toString();
    }
}
