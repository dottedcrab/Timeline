package com.san.apps.tweets.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.san.apps.tweets.R;
import com.san.apps.tweets.utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * Created by sanoojkp on 2/14/2016.
 */
public class TweetAdapter extends BaseAdapter {
    private static final String MEDIA_TYPE_PHOTO = "photo";

    private final Activity mContext;
    private final List<twitter4j.Status> mTimeline;

    static class ViewHolder {
        public TextView screenName;
        public TextView userName;
        public TextView dateTime;
        public TextView tweet;
        public ImageView profileImage;
        public ImageView imageIndicator;
    }

    public TweetAdapter(Activity mContext, List<Status> mTimeline) {
        this.mContext = mContext;
        this.mTimeline = mTimeline;
    }

    @Override
    public int getCount() {
        return mTimeline.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.screenName = (TextView) rowView.findViewById(R.id.tv_screen_name);
            viewHolder.userName = (TextView) rowView.findViewById(R.id.tv_user_name);
            viewHolder.dateTime = (TextView) rowView.findViewById(R.id.tv_time);
            viewHolder.tweet = (TextView) rowView.findViewById(R.id.tv_tweeet);
            viewHolder.profileImage = (ImageView) rowView
                    .findViewById(R.id.iv_profile);
            viewHolder.imageIndicator = (ImageView) rowView.findViewById(R.id.iv_image_indicator);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Status status = mTimeline.get(position);
        holder.userName.setText(status.getUser().getName());
        holder.screenName.setText(mContext.getString(R.string.sn_prefix, status.getUser().getScreenName()));
        holder.dateTime.setText(Utils.formatDate(status.getCreatedAt()));
        holder.tweet.setText(Utils.getPlainText(status.getText()));
        boolean imageAttached = isImageAttached(status.getMediaEntities());
        holder.imageIndicator.setVisibility(imageAttached ? View.VISIBLE : View.INVISIBLE);
        Picasso.with(mContext).load(status.getUser().getBiggerProfileImageURL()).into(holder.profileImage);

        return rowView;
    }

    private boolean isImageAttached(MediaEntity[] media) {
        boolean status = false;

        if (null != media && media.length > 0) {
            MediaEntity entity = media[0];
            if (MEDIA_TYPE_PHOTO.equals(entity.getType())) {
                status = true;
            }
        }

        return status;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mTimeline.get(position);
    }
}
