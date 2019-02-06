package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
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
import android.widget.Toast;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.data.model.Task;
import com.servicenow.skilledserviceapp.data.model.TaskType;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    private Activity mActivity;
    private RecyclerView mTaskRecyclerView;
    private ArrayList<Task> mTaskArrayList = new ArrayList<>();

    private TaskAdapter mTaskAdapter;
    private AppCompatTextView mErrorMessage;
    private int taskCardPadding;

    private boolean isRequester;
    private String userId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        taskCardPadding = getResources().getDimensionPixelOffset(R.dimen.margin_6);

        isRequester = PreferenceUtils.getInstance(context).getBooleanPreference(Constants.PREF_KEY_IS_REQUESTER);
        userId = PreferenceUtils.getInstance(mActivity).getStringPreference(Constants.PREF_KEY_LOGGED_IN_USER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initFragmentView(mFragmentView);
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentTaskInfo();
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mTaskRecyclerView = mView.findViewById(R.id.taskRecyclerView);
        mTaskRecyclerView.setHasFixedSize(true);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mTaskRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = taskCardPadding;
                outRect.right = taskCardPadding;
                outRect.top = taskCardPadding;
                outRect.bottom = taskCardPadding;

            }
        });

        mTaskAdapter = new TaskAdapter();
        mTaskRecyclerView.setAdapter(mTaskAdapter);

        mErrorMessage = mView.findViewById(R.id.errorMessage);
        mErrorMessage.setVisibility(View.GONE);
    }

    /**
     * requests current task info
     */
    private void getCurrentTaskInfo() {
        try {
            mTaskArrayList.clear();
            if (userId != null && !userId.isEmpty()) {
                if (isRequester)
                    mTaskArrayList.add(new Task());
                DatabaseManager manager = new DatabaseManager(mActivity);
                manager.openDatabase();
                Cursor mCursor = manager.getTaskInfo();
                if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() > 0) {
                    do {
                        Task mTask = new Task();
                        mTask.setTaskId(mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ID)));
                        //mTask.setTaskDescription(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_DESCRIPTION)));
                        mTask.setTaskFrom(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_FROM)));
                        mTask.setTaskTo(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TO)));
                        mTask.setTaskRatings(mCursor.getFloat(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_RATING)));
                        mTask.setTaskRequiredSkillId(mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_SKILL)));
                        mTask.setTaskRequiredSkillName(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_TYPE)));
                        mTask.setTaskStatus(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_STATUS)));
                        mTask.setCreatedAt(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_CREATED_AT)));
                        if (isRequester) {
                            mTask.setTaskTo(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                        } else {
                            mTask.setTaskFrom(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                        }
                        mTaskArrayList.add(mTask);
                    } while (mCursor.moveToNext());
                    mCursor.close();
                }
                manager.closeDatabase();
            }
        } catch (Exception e) {
            if (Constants.PRINT_LOGS)
                e.printStackTrace();
        }

        if (mTaskArrayList.size() > 0) {
            mTaskAdapter.notifyDataSetChanged();
            mErrorMessage.setVisibility(View.GONE);
        } else {
            showErrorMessage(getString(R.string.error_no_task_info_available));
        }
    }

    /**
     * shows error message
     *
     * @param errorMessage - Error message needs to be displayed
     */
    private void showErrorMessage(String errorMessage) {
        mErrorMessage.setText(errorMessage);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Adapter to show skills
     */
    private class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int NEW_TASK = 0;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (getItemViewType(i) == NEW_TASK) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.card_new_task, viewGroup, false);
                return new NewTaskViewHolder(view);
            } else {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.card_current_task, viewGroup, false);
                return new CurrentTaskViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof NewTaskViewHolder) {
                NewTaskViewHolder mNewTaskViewHolder = (NewTaskViewHolder) viewHolder;
                //Task mTask = mTaskArrayList.get(i);
            } else {
                CurrentTaskViewHolder mCurrentTaskViewHolder = (CurrentTaskViewHolder) viewHolder;
                Task mTask = mTaskArrayList.get(i);
                if (isRequester)
                    mCurrentTaskViewHolder.mTaskLabelTop.setText("To : " + mTask.getTaskToName());
                else mCurrentTaskViewHolder.mTaskLabelTop.setText("From : " + mTask.getTaskFromName());

                //mCurrentTaskViewHolder.mRating.setText("Ratings : ");
                if (mTask.getTaskStatus().equalsIgnoreCase(TaskType.TASK_PENDING.getValue())) {
                    mCurrentTaskViewHolder.mStatusIndicator.setBackgroundResource(R.drawable.ic_circle_yellow);
                    mCurrentTaskViewHolder.mStatusType.setText("Pending");
                } else if (mTask.getTaskStatus().equalsIgnoreCase(TaskType.TASK_ONGOING.getValue())) {
                    mCurrentTaskViewHolder.mStatusIndicator.setBackgroundResource(R.drawable.ic_circle_green);
                    mCurrentTaskViewHolder.mStatusType.setText("On Going");
                } else if (mTask.getTaskStatus().equalsIgnoreCase(TaskType.TASK_CANCELED.getValue())){
                    mCurrentTaskViewHolder.mStatusIndicator.setBackgroundResource(R.drawable.ic_circle_red);
                    mCurrentTaskViewHolder.mStatusType.setText("Canceled");
                }
            }
        }

        @Override
        public int getItemCount() {
            return mTaskArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? NEW_TASK : 1;
        }

        class NewTaskViewHolder extends RecyclerView.ViewHolder {

            NewTaskViewHolder(@NonNull final View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(mActivity, "New task", Toast.LENGTH_SHORT).show();
                        NavigationHelper.navigateToRequestNewTaskFragment(getActivity());
                    }
                });
            }
        }

        class CurrentTaskViewHolder extends RecyclerView.ViewHolder {
            private AppCompatTextView mTaskLabelTop;
            private AppCompatTextView mRating;
            private AppCompatImageView mTaskSkillImage;
            private View mStatusIndicator;
            private AppCompatTextView mStatusType;

            CurrentTaskViewHolder(@NonNull final View itemView) {
                super(itemView);
                mTaskLabelTop = itemView.findViewById(R.id.taskLabelTop);
                mRating = itemView.findViewById(R.id.rating);
                mTaskSkillImage = itemView.findViewById(R.id.task_skill);
                mStatusIndicator = itemView.findViewById(R.id.statusIndicator);
                mStatusType = itemView.findViewById(R.id.statusType);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mActivity, "Current TASK", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}