package com.sp.respond_us;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GroupFamilyAdapter extends FirestoreRecyclerAdapter<family,GroupFamilyAdapter.GroupFamilyHolder> {
    private OnItemClickListener listener;
    FirebaseStorage storage;
    private String uid = "";
    String description,allergies,blood,medicalConditions,phoneNumber;
    public GroupFamilyAdapter(@NonNull FirestoreRecyclerOptions<family> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupFamilyHolder holder, int position, @NonNull family model) {
        holder.textViewName.setText(model.getUserName());
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
    public GroupFamilyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item,parent,false);
        return new GroupFamilyHolder(v);
    }

    class GroupFamilyHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        ImageView search_pfp;

        public GroupFamilyHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.familyName);
            search_pfp=itemView.findViewById(R.id.familyPfp);

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

