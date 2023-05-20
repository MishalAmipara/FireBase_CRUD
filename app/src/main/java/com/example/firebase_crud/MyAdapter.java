package com.example.firebase_crud;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyAdapter extends FirebaseRecyclerAdapter<Producs_Data,MyAdapter.ProductHolder>
{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyAdapter(@NonNull FirebaseRecyclerOptions<Producs_Data> options) {
        super(options);
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        ProductHolder productHolder=new ProductHolder(view);
        return productHolder;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Producs_Data model) {
        holder.proName.setText(""+model.getProName());
        holder.proPrice.setText(""+model.getProPrice());
        Log.d("TTT", "onBindViewHolder: ProName="+model.getProName()+"\tImgUrl="+model.getProImageUrl());
       // holder.imageView.setImageURI(Uri.parse(model.getProImageUrl()));

        Glide.with(holder.itemView.getContext()).load(model.getProImageUrl()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Products").orderByChild("id").equalTo(model.id);
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance(); // initializing object of database
                            DatabaseReference myRef = database.getReference("Products").push(); // Creating main (parent) reference
                            String id = myRef.getKey();

                            Producs_Data dataModel = new Producs_Data(id,"CPU","12345","RRR","https://play-lh.googleusercontent.com/8W4Ai-J22SM8Xzwy75_wXqnYPm04zbc0QpXm6dfKiS-VNDgPjeS3rtu_yymuGWmGP4w=s48-rw");

                            appleSnapshot.getRef().setValue(dataModel);
                            //appleSnapshot.getRef().removeValue();
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView proName,proPrice;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.listImg);
            proName=itemView.findViewById(R.id.item_text1);
            proPrice=itemView.findViewById(R.id.item_txt2);
        }
    }
}
