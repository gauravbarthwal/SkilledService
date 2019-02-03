package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.servicenow.skilledserviceapp.BuildConfig;
import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

public class ProfileFragment extends Fragment {

    private Activity mActivity;
    private ProgressBar mProgressBar;
    private RecyclerView mProfileRecyclerView;
    private ProfileAdapter mAdapter;
    private String[] profileItems = new String[]{"Header", "App Version", "Logout"};
    private String userName;
    private float userRating;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        initFragmentView(mFragmentView);
        requestUserInformationFromDB();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mProgressBar = mView.findViewById(R.id.progressBar);
        mProfileRecyclerView = mView.findViewById(R.id.profileRecyclerView);
        mProfileRecyclerView.setHasFixedSize(true);
        mProfileRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * requests user's information from DB
     */
    private void requestUserInformationFromDB() {
        try {
            DatabaseManager manager = new DatabaseManager(mActivity);
            manager.openDatabase();
            Cursor mCursor = manager.getLoggedInUserData();
            userName = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            userRating = mCursor.getFloat(mCursor.getColumnIndex(DatabaseHelper.COLUMN_RATING));
            mCursor.close();
            manager.closeDatabase();
            mAdapter = new ProfileAdapter();
            mProfileRecyclerView.setAdapter(mAdapter);
        } catch (Exception ignored) {}
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Adapter to show skills
     */
    private class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int HEADER = 0;
        private final int PROFILE_ITEM = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;
            if (getItemViewType(i) == HEADER ) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.layout_item_profile_header, viewGroup, false);
                return new ProfileHeaderViewAdapter(view);
            } else {
                view = LayoutInflater.from(mActivity).inflate(R.layout.layout_item_profile, viewGroup, false);
                return new ProfileItemViewAdapter(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ProfileHeaderViewAdapter) {
                ProfileHeaderViewAdapter mProfileHeaderViewAdapter = (ProfileHeaderViewAdapter) viewHolder;
                mProfileHeaderViewAdapter.mUserName.setText(userName);
                mProfileHeaderViewAdapter.mUserRating.setText(""+userRating);
            } else if (viewHolder instanceof ProfileItemViewAdapter) {
                ProfileItemViewAdapter holder = (ProfileItemViewAdapter) viewHolder;
                String profileItem = profileItems[i];
                if (profileItem.equalsIgnoreCase("App Version")) {
                    profileItem = profileItem + " : " + BuildConfig.VERSION_NAME;
                    holder.mProfileItemIcon.setVisibility(View.GONE);
                } else {
                    holder.mProfileItemIcon.setImageResource(R.drawable.ic_logout);
                    holder.mProfileItemIcon.setVisibility(View.VISIBLE);
                }
                holder.mProfileItemTitle.setText(profileItem);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? HEADER : PROFILE_ITEM;
        }

        @Override
        public int getItemCount() {
            return profileItems.length;
        }

        class ProfileHeaderViewAdapter extends RecyclerView.ViewHolder {
            private AppCompatTextView mUserName;
            private AppCompatTextView mUserRating;
            ProfileHeaderViewAdapter(final View itemView) {
                super(itemView);
                mUserName = itemView.findViewById(R.id.userName);
                mUserRating = itemView.findViewById(R.id.userRating);
            }
        }

        class ProfileItemViewAdapter extends RecyclerView.ViewHolder {
            private AppCompatTextView mProfileItemTitle;
            private AppCompatImageView mProfileItemIcon;

            ProfileItemViewAdapter(@NonNull final View itemView) {
                super(itemView);
                mProfileItemTitle = itemView.findViewById(R.id.profileItemTitle);
                mProfileItemIcon = itemView.findViewById(R.id.profileItemIcon);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mProfileItemTitle.getText().toString().equalsIgnoreCase("Logout")) {
                            PreferenceUtils.getInstance(mActivity).setStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID, "");
                            PreferenceUtils.getInstance(mActivity).setBooleanPreference(Constants.PREF_KEY_IS_REQUESTER, false);
                            NavigationHelper.navigateToLogin(getActivity());
                            mActivity.finish();
                        }
                    }
                });
            }
        }
    }
}
