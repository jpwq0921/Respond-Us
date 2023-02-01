package com.sp.respond_us;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchModel, SearchAdapter.SearchHolder> {
    private OnItemClickListener listener;
    private String uid = "";
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<SearchModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchHolder holder, int position, @NonNull SearchModel model) {
            holder.search_Name.setText(model.getUsername());
            holder.search_Phone.setText(model.getPhoneNumber());
            holder.search_email.setText(model.getEmail());
            //uid = model.getuID();
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_searched,
                parent,false);

        return new SearchHolder(v);
    }

    class SearchHolder extends RecyclerView.ViewHolder{
        TextView search_Name;
        TextView search_Phone;
        TextView search_email;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            search_Name = itemView.findViewById(R.id.search_named);
            search_email = itemView.findViewById(R.id.search_email);
            search_Phone = itemView.findViewById(R.id.search_phoneNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnItemClick(getSnapshots().getSnapshot(position),position);

                        /*Intent i = new Intent(itemView.getContext(),OpenSearchedUser.class);
                        //i.putExtra("uID", uid);
                        i.putExtra("userName",search_Name.getText().toString());
                        i.putExtra("phoneNumber",search_Phone.getText().toString());
                        i.putExtra("email",search_email.getText().toString());
                        itemView.getContext().startActivity(i);*/
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
