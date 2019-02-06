package com.servicenow.skilledserviceapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.servicenow.skilledserviceapp.data.model.Skill;
import com.servicenow.skilledserviceapp.data.model.Task;
import com.servicenow.skilledserviceapp.utils.Constants;
import com.servicenow.skilledserviceapp.utils.NavigationHelper;
import com.servicenow.skilledserviceapp.utils.PreferenceUtils;
import com.servicenow.skilledserviceapp.utils.adapters.SkillAdapter;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    private Activity mActivity;
    private RecyclerView mDashboardRecyclerview;
    private ArrayList<Task> mTaskArrayList = new ArrayList<>();
    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();

    private DashboardAdapter mDashboardAdapter;
    private AppCompatTextView mErrorMessage;
    private SkillAdapter mSkillAdapter;
    private int skillItemSelectedColor;
    private int skillItemDefaultColor;

    private int selectedSkillId = -1;

    private int taskCardPadding;

    private boolean isRequester;
    private String userId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        skillItemDefaultColor = getResources().getColor(R.color.off_white);
        skillItemSelectedColor = getResources().getColor(R.color.colorPrimary);

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
        //requestSkillList();
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mDashboardRecyclerview = mView.findViewById(R.id.dashboardRecyclerView);
        mDashboardRecyclerview.setHasFixedSize(true);
        mDashboardRecyclerview.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mDashboardAdapter = new DashboardAdapter();
        mDashboardRecyclerview.setAdapter(mDashboardAdapter);

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
            mDashboardAdapter.notifyDataSetChanged();
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
     * request all the skills from DB
     */
    private void requestSkillList() {
        try {
            mSkillArrayList.clear();
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
                mCursor.close();
            }
            manager.closeDatabase();
        } catch (Exception ignored) {
        }
        //mSkillAdapter.notifyDataSetChanged();
    }


    private class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int TASK_SECTION = 0;
        private final int SKILL_SECTION = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mView;
            mView = LayoutInflater.from(mActivity).inflate(R.layout.layout_item_dashboard, viewGroup, false);
            if (getItemViewType(i) == TASK_SECTION) {
                return new TaskSectionViewHolder(mView);
            } else {
                return new SkillSectionViewHolder(mView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof TaskSectionViewHolder) {
                TaskSectionViewHolder _holder = (TaskSectionViewHolder) viewHolder;
                _holder.mTaskRecyclerView.setAdapter(new TaskAdapter());
            } else if (viewHolder instanceof SkillSectionViewHolder) {
                SkillSectionViewHolder _holder = (SkillSectionViewHolder) viewHolder;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? TASK_SECTION : SKILL_SECTION;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        class TaskSectionViewHolder extends RecyclerView.ViewHolder {
            private RecyclerView mTaskRecyclerView;

            TaskSectionViewHolder(View itemView) {
                super(itemView);
                mTaskRecyclerView = (RecyclerView) itemView;
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
            }
        }

        class SkillSectionViewHolder extends RecyclerView.ViewHolder {
            private RecyclerView mSkillRecyclerView;

            SkillSectionViewHolder(View itemView) {
                super(itemView);
                mSkillRecyclerView = (RecyclerView) itemView;
                mSkillRecyclerView.setHasFixedSize(true);
                mSkillRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                mSkillRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        outRect.left = taskCardPadding;
                        outRect.right = taskCardPadding;
                        outRect.top = taskCardPadding;
                        outRect.bottom = taskCardPadding;

                    }
                });
                mSkillAdapter = new SkillAdapter(mActivity, mSkillArrayList, new SkillAdapter.SkillItemClickListener() {
                    @Override
                    public void onItemClick(Skill mSkill) {
                        NavigationHelper.navigateToRequestNewTaskWithSkillsFragment(getActivity(), mSkill.getSkillId());
                    }
                });
                mSkillRecyclerView.setAdapter(mSkillAdapter);
            }
        }
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

            CurrentTaskViewHolder(@NonNull final View itemView) {
                super(itemView);
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
