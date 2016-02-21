package com.san.apps.tweets;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import twitter4j.User;


/**
 * Created by sanoojkp on 2/20/2016.
 */
public class ProfileFragment extends DialogFragment {

    public static ProfileFragment newInstance(User user) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTENDED_DATA_PROFILE, user);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        User user = (User) getArguments().get(Constants.EXTENDED_DATA_PROFILE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile_dialog, null);
        ImageView profile = (ImageView) view.findViewById(R.id.iv_user);

        Picasso.with(getActivity()).load(user.getBiggerProfileImageURL()).into(profile);
        TextView screenName = (TextView) view.findViewById(R.id.tv_handle);
        screenName.setText(getString(R.string.sn_prefix, user.getScreenName()));
        TextView name = (TextView) view.findViewById(R.id.tv_user_name);
        name.setText(user.getName());
        builder.setView(view).setCancelable(true);

        return builder.create();
    }

}
