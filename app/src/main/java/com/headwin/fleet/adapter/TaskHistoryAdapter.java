package com.headwin.fleet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.headwin.fleet.R;
import com.headwin.fleet.pojo.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lnc on 2017/6/29.
 */

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.ViewHolder> implements View.OnClickListener{

    private List<Task> mTaskList = new ArrayList<Task>();

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }

    private TaskHistoryAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(TaskHistoryAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public TaskHistoryAdapter(List<Task> taskList){
        mTaskList = taskList;
    }

    public Task getIndexItem(int position){
        return mTaskList.get(position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    public void addMoreTasks(List<Task> mores){
        int size = mTaskList.size();
        mTaskList.addAll(mores);
        notifyItemInserted(size);
    }

    public void clearTasks(){
        mTaskList.clear();
        notifyItemRemoved(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_history_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mTaskList.get(position);
        holder.tViewBookingNo.setText(catTextAndValue(holder, R.string.lab_booking_no, task.getBookingNo()));
        holder.tViewCtype.setText(catTextAndValue(holder, R.string.lab_ctype, task.getContainerInfo()));
        holder.tViewYardName.setText(catTextAndValue(holder, R.string.lab_yard_name, task.getYardName()));
        holder.tViewDockName.setText(catTextAndValue(holder, R.string.lab_dock_name, task.getWharfName()));
        holder.tViewRemark.setText(catTextAndValue(holder, R.string.lab_remark, task.getRemark()));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView  tViewBookingNo;
        TextView tViewCtype;
        TextView tViewYardName;
        TextView tViewDockName;
        TextView tViewRemark;

        public ViewHolder(View itemView) {
            super(itemView);
            tViewBookingNo = (TextView) itemView.findViewById(R.id.booking_no);
            tViewCtype = (TextView) itemView.findViewById(R.id.ctype);
            tViewYardName = (TextView) itemView.findViewById(R.id.yard_name);
            tViewDockName = (TextView) itemView.findViewById(R.id.dock_name);
            tViewRemark = (TextView) itemView.findViewById(R.id.remark);
        }


    }

    private String catTextAndValue(TaskHistoryAdapter.ViewHolder viewHolder, int labId, String value){
        Context context = viewHolder.tViewCtype.getContext();
        String lab = context.getResources().getString(labId);
        if(value == null) value = "";

        return  String.format("%s %s ", lab, value);
    }
}
