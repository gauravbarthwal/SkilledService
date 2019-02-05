package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.activity.SplashActivity;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.data.model.Skill;
import com.servicenow.skilledserviceapp.data.model.TaskType;
import com.servicenow.skilledserviceapp.data.model.Worker;
import com.servicenow.skilledserviceapp.interfaces.DialogListener;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.DialogType;
import com.servicenow.skilledserviceapp.utils.LogUtils;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.NavigationType;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;
import com.servicenow.skilledserviceapp.utils.adapters.SkillAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestNewTaskFragment extends Fragment implements SkillAdapter.SkillItemClickListener {

    private static final String TAG = RequestNewTaskFragment.class.getSimpleName();
    private Activity mActivity;
    private ProgressBar mProgressBar;
    private AppCompatTextView mHeading;
    private RecyclerView mSkillRecyclerView;

    private RelativeLayout mSendRequestLayout;
    private RecyclerView mWorkerRecyclerView;
    private WorkerAdapter mWorkerAdapter;

    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();
    private ArrayList<String> mAlreadyRequestedWorkers = new ArrayList<>();

    private int workerCardWidth;
    private int topBottomPadding;

    private NavigationType mNavigationType;
    private int skillId = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        workerCardWidth = getResources().getDimensionPixelOffset(R.dimen.card_worker_width);
        topBottomPadding = getResources().getDimensionPixelOffset(R.dimen.margin_2);
        try {
            Bundle mBundle = getArguments();
            if (mBundle.containsKey(Constants.KEY_LOAD_FRAGMENT)) {
                mNavigationType = NavigationType.getNavigationType(mBundle.getString(Constants.KEY_LOAD_FRAGMENT));
            }
            if (mBundle.containsKey(Constants.KEY_SKILL_ID))
                skillId = mBundle.getInt(Constants.KEY_SKILL_ID);
        } catch (Exception e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_request_new_task, container, false);
        initFragmentView(mFragmentView);
        if (mNavigationType != null && mNavigationType.equals(NavigationType.NAV_REQUEST_TASK_WITH_SKILL)
                && skillId > -1) {
            mProgressBar.setVisibility(View.VISIBLE);
            requestSkilledWorkerList(skillId);
        } else {
            requestSkillList();
        }
        return mFragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSkillArrayList.clear();
        mAlreadyRequestedWorkers.clear();
    }

    /**
     * method to be called onBackPress
     */
    public boolean closeActivity() {
        if (mNavigationType != null && mNavigationType.equals(NavigationType.NAV_REQUEST_TASK_WITH_SKILL))
            return true;
        if (mSendRequestLayout.getVisibility() == View.VISIBLE) {
            if (mWorkerAdapter != null)
                mWorkerAdapter.clearAdapter();
            mHeading.setText(getString(R.string.request_service));
            mSkillRecyclerView.setVisibility(View.VISIBLE);
            mSendRequestLayout.setVisibility(View.GONE);
            return false;
        } else return true;
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mProgressBar = mView.findViewById(R.id.progressBar);
        mHeading = mView.findViewById(R.id.heading);
        mSkillRecyclerView = mView.findViewById(R.id.skillRecyclerView);
        mSkillRecyclerView.setHasFixedSize(true);
        mSkillRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        mSendRequestLayout = mView.findViewById(R.id.sendRequestLayout);
        mWorkerRecyclerView = mView.findViewById(R.id.workerRecyclerView);
        mWorkerRecyclerView.setHasFixedSize(true);
        mWorkerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mWorkerRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int totalWidth = parent.getWidth();
                int cardWidth = workerCardWidth;
                int leftRightPadding = (totalWidth - cardWidth)/2;
                leftRightPadding = Math.max(0, leftRightPadding);
                outRect.set(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding);
            }
        });
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * request all the skills from DB
     */
    private void requestSkillList() {
        try {
            DatabaseManager manager = new DatabaseManager(mActivity);
            manager.openDatabase();
            Cursor mCursor = manager.fetchSkillData();
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    Skill mSkill = new Skill();
                    mSkill.setSkillId(mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_ID)));
                    mSkill.setSkillType(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_TYPE)));
                    mSkillArrayList.add(mSkill);
                } while (mCursor.moveToNext());
            }
            manager.closeDatabase();
        } catch (Exception ignored) {
        }
        SkillAdapter mSkillAdapter = new SkillAdapter(mActivity, mSkillArrayList, RequestNewTaskFragment.this);
        mSkillRecyclerView.setAdapter(mSkillAdapter);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(Skill mSkill) {
        if (mSkill != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            requestSkilledWorkerList(mSkill.getSkillId());
        }
    }

    /**
     * request all the workers associated with the given skill
     */
    private void requestSkilledWorkerList(int requiredSkill) {
        ArrayList<Worker> mWorkerArrayList = new ArrayList<>();
        try {
            DatabaseManager manager = new DatabaseManager(mActivity);
            manager.openDatabase();
            getAlreadyRequestedWorker(manager, requiredSkill);
            Cursor mCursor = manager.fetchSkilledWorkerData(requiredSkill);
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    Worker mWorker = new Worker();
                    mWorker.setWorkerId(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_WORKER_ID)));
                    mWorker.setWorkerName(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                    mWorker.setWorkerRatings(mCursor.getFloat(mCursor.getColumnIndex(DatabaseHelper.COLUMN_RATING)));
                    mWorker.setSkillId(mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_ID)));
                    mWorker.setSkillName(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_TYPE)));
                    mWorkerArrayList.add(mWorker);
                } while (mCursor.moveToNext());
            }
            manager.closeDatabase();
        } catch (Exception e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }
        if (mWorkerAdapter == null) {
            mWorkerAdapter = new WorkerAdapter(mWorkerArrayList);
            mWorkerRecyclerView.setAdapter(mWorkerAdapter);
        } else {
            mWorkerAdapter.updateWorkerList(mWorkerArrayList);
        }
        mHeading.setText(getString(R.string.select_worker));
        mSkillRecyclerView.setVisibility(View.GONE);
        mSendRequestLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * gets already requested workers list
     * @param manager - {@link DatabaseManager}
     * @param requiredSkill - Skill id
     */
    private void getAlreadyRequestedWorker(DatabaseManager manager, int requiredSkill) {
        Cursor mCursor = manager.fetchAlreadyRequestedWorkers(requiredSkill);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                mAlreadyRequestedWorkers.clear();
                mAlreadyRequestedWorkers.add(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TO)));
                LogUtils.e(TAG, "#getAlreadyRequestedWorker#mAlreadyRequestedWorkers.size() :: " + mAlreadyRequestedWorkers.size());
            } while (mCursor.moveToNext());
        }
    }

    /**
     * Adapter to show list of the Skilled workers
     */
    private class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

        private final ArrayList<Worker> mWorkerArrayList = new ArrayList<>();

        WorkerAdapter(ArrayList<Worker> workerList) {
            this.mWorkerArrayList.addAll(workerList);
        }

        void updateWorkerList(ArrayList<Worker> updatedWorkerList) {
            this.mWorkerArrayList.clear();
            this.mWorkerArrayList.addAll(updatedWorkerList);
            notifyDataSetChanged();
        }

        /**
         * clears {@link WorkerAdapter}
         */
        void clearAdapter(){
            mWorkerArrayList.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mView = LayoutInflater.from(mActivity).inflate(R.layout.card_worker, viewGroup, false);
            return new WorkerViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkerViewHolder workerViewHolder, int i) {
            Worker mWorker = mWorkerArrayList.get(i);
            workerViewHolder.mWorkerName.setText(mWorker.getWorkerName());
            workerViewHolder.mWorkerRatings.setText("Ratings : " + mWorker.getWorkerRatings());
            workerViewHolder.mWorkerSkillImage.setImageResource(Constants.getSkillIcon(mWorker.getSkillName()));
            if (mAlreadyRequestedWorkers.size() > 0 && mAlreadyRequestedWorkers.contains(mWorker.getWorkerId())) {
                workerViewHolder.mStatusIndicator.setBackgroundResource(R.drawable.ic_circle_red);
                //workerViewHolder.mStatusType.setText();
            } else {
                workerViewHolder.mStatusIndicator.setBackgroundResource(R.drawable.ic_circle_green);
            }
        }

        @Override
        public int getItemCount() {
            return mWorkerArrayList.size();
        }

        class WorkerViewHolder extends RecyclerView.ViewHolder {
            private AppCompatTextView mWorkerName;
            private AppCompatTextView mWorkerRatings;
            private AppCompatImageView mWorkerSkillImage;
            private View mStatusIndicator;
            private AppCompatTextView mStatusType;
            WorkerViewHolder(final View itemView) {
                super(itemView);
                mWorkerName = itemView.findViewById(R.id.worker_name);
                mWorkerRatings = itemView.findViewById(R.id.worker_rating);
                mWorkerSkillImage = itemView.findViewById(R.id.worker_skill);
                mStatusIndicator = itemView.findViewById(R.id.statusIndicator);
                mStatusType = itemView.findViewById(R.id.statusType);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = PreferenceUtils.getInstance(mActivity).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
                        if (userId != null && !userId.isEmpty()) {
                            try {
                                Worker mWorker = mWorkerArrayList.get(getAdapterPosition());
                                if (mAlreadyRequestedWorkers.size() > 0 && mAlreadyRequestedWorkers.contains(mWorker.getWorkerId())) {
                                    final HashMap<String, String> inputMap = new HashMap<>();
                                    inputMap.put(Constants.DIALOG_KEY_MESSAGE, getString(R.string.error_already_sent_request));
                                    NavigationHelper.showDialog(getActivity(), DialogType.DIALOG_FAILURE, inputMap, null);
                                } else {
                                    DatabaseManager manager = new DatabaseManager(mActivity);
                                    manager.openDatabase();
                                    if (!manager.sendRequestOfTask(mWorker.getSkillId(), mWorker.getWorkerId())) {
                                        final HashMap<String, String> inputMap = new HashMap<>();
                                        inputMap.put(Constants.DIALOG_KEY_MESSAGE, getString(R.string.error_oops_something_went_wrong));
                                        NavigationHelper.showDialog(getActivity(), DialogType.DIALOG_FAILURE, inputMap, null);
                                    } else {
                                        final HashMap<String, String> inputMap = new HashMap<>();
                                        inputMap.put(Constants.DIALOG_KEY_MESSAGE, getString(R.string.request_has_been_sent));
                                        NavigationHelper.showDialog(getActivity(), DialogType.DIALOG_SUCCESS, inputMap, new DialogListener() {
                                            @Override
                                            public void onButtonClicked(Button action) {
                                                mActivity.finish();
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                if (Constants.PRINT_LOGS)
                                    e.printStackTrace();
                            }
                        } else {
                            NavigationHelper.navigateToLogin(getActivity());
                        }
                    }
                });
            }
        }
    }
}
