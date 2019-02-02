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

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.data.model.Skill;
import com.servicenow.skilledserviceapp.data.model.Task;
import com.servicenow.skilledserviceapp.data.model.TaskType;
import com.servicenow.skilledserviceapp.utils.Constants;

import java.util.ArrayList;

public class DashbardFragment extends Fragment {
    private Activity mActivity;
    private RecyclerView mDashboardRecyclerview;
    private ArrayList<Task> mTaskArrayList = new ArrayList<>();
    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();

    private SkillAdapter mSkillAdapter;
    private int skillItemSelectedColor;
    private int skillItemDefaultColor;

    private int selectedSkillId = -1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        skillItemDefaultColor = getResources().getColor(R.color.off_white);
        skillItemSelectedColor = getResources().getColor(R.color.colorPrimary);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initFragmentView(mFragmentView);
        getCurrentTaskInfo();
        requestSkillList();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     * @param mView - {@link Fragment}
     */
    private void initFragmentView (View mView) {
        mDashboardRecyclerview = mView.findViewById(R.id.dashboardRecyclerView);
        mDashboardRecyclerview.setHasFixedSize(true);
        mDashboardRecyclerview.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * requests current task info
     */
    private void getCurrentTaskInfo() {
        try {
            mTaskArrayList.add(new Task());
            DatabaseManager manager = new DatabaseManager(mActivity);
            manager.closeDatabase();
            Cursor mCursor = manager.getTaskInfo(TaskType.TASK_PENDING);
            if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() > 0) {
                do {
                    Task mTask = new Task();
                    mTask.setTaskId(mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_ID)));
                    mTask.setTaskDescription(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_DESCRIPTION)));
                    mTask.setTaskFrom(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_FROM)));
                    mTask.setTaskTo(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TO)));
                    mTask.setTaskRatings(mCursor.getFloat(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_RATING)));
                    mTask.setTaskRequiredSkill(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_SKILL_TYPE)));
                    mTask.setTaskStatus(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_STATUS)));
                    mTaskArrayList.add(mTask);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
            manager.closeDatabase();
        } catch (Exception ignored) {
        }
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
            mCursor.close();
            manager.closeDatabase();
        } catch (Exception ignored) {
        }
        mSkillAdapter.notifyDataSetChanged();
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
            }
        }

        class SkillSectionViewHolder extends RecyclerView.ViewHolder {
            private RecyclerView mSkillRecyclerView;
            SkillSectionViewHolder(View itemView) {
                super(itemView);
                mSkillRecyclerView = (RecyclerView) itemView;
                mSkillRecyclerView.setHasFixedSize(true);
                mSkillRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                mSkillAdapter = new SkillAdapter();
                mSkillRecyclerView.setAdapter(mSkillAdapter);
            }
        }
    }

    /**
     * Adapter to show skills
     */
    private class SkillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private View previousSelectedItem;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_item_skill, viewGroup, false);
            return new SkillViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof SkillViewHolder) {
                SkillViewHolder mSkillViewHolder = (SkillViewHolder) viewHolder;
                Skill mSkill = mSkillArrayList.get(i);
                mSkillViewHolder.mSkillType.setText(mSkill.getSkillType());
                mSkillViewHolder.mSkillType.setTag(mSkill.getSkillId());
                mSkillViewHolder.mSkillImage.setImageResource(Constants.skillIconsArray[i]);
            }
        }

        @Override
        public int getItemCount() {
            return mSkillArrayList.size();
        }

        class SkillViewHolder extends RecyclerView.ViewHolder {
            private AppCompatTextView mSkillType;
            private AppCompatImageView mSkillImage;

            SkillViewHolder(@NonNull final View itemView) {
                super(itemView);
                mSkillType = itemView.findViewById(R.id.skillType);
                mSkillImage = itemView.findViewById(R.id.skillImage);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (previousSelectedItem != null)
                            previousSelectedItem.setBackgroundColor(skillItemDefaultColor);
                        itemView.setBackgroundColor(skillItemSelectedColor);
                        selectedSkillId = mSkillArrayList.get(getAdapterPosition()).getSkillId();
                        previousSelectedItem = itemView;
                    }
                });
            }
        }
    }
}
