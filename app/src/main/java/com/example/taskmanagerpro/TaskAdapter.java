package com.example.taskmanagerpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends  RecyclerView.Adapter<TaskAdapter.TaskHolder>implements Filterable{
    private onTaskItemClickListener listener;
    private List<MyTask> tasks;
    private List<MyTask> taskList=new ArrayList<> ();


    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_items, parent, false);

        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        MyTask currentTask = taskList.get(holder.getAdapterPosition ());
       holder.textViewTitle.setText(currentTask.getTitleTask());
        holder.Description.setText(currentTask.getDescription());
        holder.Date.setText(currentTask.getTaskTime());

    }



    @Override
    public int getItemCount() {
        if(taskList==null){
            return 0;
        }
        return taskList.size ();
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private Filter taskFilter=new Filter () {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          List<MyTask>filteredTask=new ArrayList<> ();
          if(constraint==null||constraint.length ()==0){
              filteredTask=tasks;
          }
          else{
              String filterPattern=constraint.toString ().toLowerCase ().trim ();

              for(MyTask task:tasks){
                  if(task.getTitleTask ().toLowerCase ().contains (filterPattern)||
                          task.getDescription ().toLowerCase ().contains (filterPattern)){
                      filteredTask.add (task);
                  }
              }
          }
          FilterResults filterResults=new FilterResults ();
          filterResults.values=filteredTask;
          return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            taskList=(List<MyTask>)results.values;
            notifyDataSetChanged ();
        }
    };

    public MyTask getTaskAt(int position) {
        return taskList.get(position);
    }

    public void setTasks(List<MyTask> Tasks) {
        this.tasks = Tasks;
        taskList=tasks;
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView Description;
        private TextView Date;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
           textViewTitle = itemView.findViewById(R.id.titleTask);
          Description=itemView.findViewById(R.id.description);
           Date=itemView.findViewById(R.id.TaskDate);

            itemView.setOnClickListener(v -> {
                int position = TaskHolder.this.getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(taskList.get(position));
                }
            });
        }

    }

    public interface onTaskItemClickListener {
        void onItemClick(MyTask myTask);

    }


    public void setOnItemClickListener(onTaskItemClickListener listener) {
        this.listener = listener;
    }


}
