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

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.DatabaseHelper;
import com.servicenow.skilledserviceapp.data.DatabaseManager;
import com.servicenow.skilledserviceapp.data.model.Skill;
import com.servicenow.skilledserviceapp.utils.Constants;

import java.util.ArrayList;

public class RequestNewTaskFragment extends Fragment {

    private Activity mActivity;
    private ProgressBar mProgressBar;
    private RecyclerView mSkillRecyclerView;
    private SkillAdapter mSkillAdapter;

    private ArrayList<Skill> mSkillArrayList = new ArrayList<>();

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
        View mFragmentView = inflater.inflate(R.layout.fragment_request_new_task, container, false);
        initFragmentView(mFragmentView);
        setUpListener();
        requestSkillList();
        return mFragmentView;
    }

    /**
     * initialize fragment view
     *
     * @param mView - {@link Fragment}
     */
    private void initFragmentView(View mView) {
        mProgressBar = mView.findViewById(R.id.progressBar);
        mSkillRecyclerView = mView.findViewById(R.id.skillRecyclerView);
        mSkillRecyclerView.setHasFixedSize(true);
        mSkillRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mSkillAdapter = new SkillAdapter();
        mSkillRecyclerView.setAdapter(mSkillAdapter);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * sets up listeners
     */
    private void setUpListener() {

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
        mSkillAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
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
