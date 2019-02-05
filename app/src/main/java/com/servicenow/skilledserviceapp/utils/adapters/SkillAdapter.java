package com.servicenow.skilledserviceapp.utils.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.servicenow.skilledserviceapp.R;
import com.servicenow.skilledserviceapp.data.model.Skill;
import com.servicenow.skilledserviceapp.utils.Constants;

import java.util.ArrayList;

/**
 * Adapter class to show all the skills available
 */
public class SkillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private SkillItemClickListener mSkillItemClickListener;
    private View previousSelectedItem;
    private final ArrayList<Skill> mSkillArrayList = new ArrayList<>();

    private int skillItemSelectedColor;
    private int skillItemDefaultColor;

    public SkillAdapter(Activity mActivity, ArrayList<Skill> mSkillArrayList, SkillItemClickListener mSkillItemClickListener) {
        this.mActivity = mActivity;
        this.mSkillItemClickListener = mSkillItemClickListener;
        this.mSkillArrayList.addAll(mSkillArrayList);
        skillItemDefaultColor = mActivity.getResources().getColor(R.color.off_white);
        skillItemSelectedColor = mActivity.getResources().getColor(R.color.colorPrimary);
    }

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
            mSkillViewHolder.mSkillImage.setImageResource(Constants.getSkillIcon(mSkill.getSkillType()));
        }
    }

    @Override
    public int getItemCount() {
        return mSkillArrayList.size();
    }

    private class SkillViewHolder extends RecyclerView.ViewHolder {
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
                    if (mSkillItemClickListener != null) {
                        mSkillItemClickListener.onItemClick(mSkillArrayList.get(getAdapterPosition()));
                    }
                    previousSelectedItem = itemView;
                }
            });
        }
    }

    /**
     * interface to get clicked Skill item
     */
    public interface SkillItemClickListener {
        void onItemClick(Skill mSkill);
    }
}