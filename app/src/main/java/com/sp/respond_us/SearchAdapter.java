package com.sp.respond_us;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchModel, SearchAdapter.SearchHolder> {
    private OnItemClickListener listener;
    private String uid = "";
    FirebaseStorage storage;
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<SearchModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchHolder holder, int position, @NonNull SearchModel model) {
            holder.search_Name.setText(model.getUsername());
            holder.search_Phone.setText(model.getPhoneNumber());
            holder.search_email.setText(model.getEmail());
            uid = model.getuID();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child("images/"+uid);

        final long ONE_MEGABYTE = 1024 * 1024;
        pathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>(){
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.search_pfp.setImageBitmap(bitmap);

            }
        });
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
        ImageView search_pfp;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            search_Name = itemView.findViewById(R.id.search_named);
            search_email = itemView.findViewById(R.id.search_email);
            search_Phone = itemView.findViewById(R.id.search_phoneNumber);
            search_pfp = itemView.findViewById(R.id.searched_pfp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnItemClick(getSnapshots().getSnapshot(position),position);

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
