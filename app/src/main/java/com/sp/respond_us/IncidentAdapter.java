package com.sp.respond_us;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class IncidentAdapter extends FirestoreRecyclerAdapter<Incident, IncidentAdapter.IncidentHolder> {


    public IncidentAdapter(@NonNull FirestoreRecyclerOptions<Incident> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull IncidentHolder holder, int position, @NonNull Incident model) {
        holder.textViewDate.setText(model.getDateOfIncident());
        holder.textViewTime.setText(model.getTimeOfIncident());
        holder.textViewName.setText(model.getOffenderName());
    }

    @NonNull
    @Override
    public IncidentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_item,
                parent,false);
        return new IncidentHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }

    class IncidentHolder extends RecyclerView.ViewHolder{
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewName;

        public IncidentHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewName = itemView.findViewById(R.id.text_view_name);
        }
    }
}
