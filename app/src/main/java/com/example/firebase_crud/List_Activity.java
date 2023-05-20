package com.example.firebase_crud;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class List_Activity extends AppCompatActivity {

    FirebaseRecyclerAdapter adapter;
    ImageButton imageButton;
    RecyclerView recview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recview = findViewById(R.id.recview);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("MishalRealtime")
                .limitToLast(50);

        FirebaseRecyclerOptions<Producs_Data> options =
                new FirebaseRecyclerOptions.Builder<Producs_Data>()
                        .setQuery(query, Producs_Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Producs_Data, List_Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull List_Holder holder, int position, @NonNull Producs_Data model) {

                Glide.with(List_Activity.this).load(model.getProImageUrl()).into(holder.imageView);
                holder.textView1.setText(model.proName);

                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(List_Activity.this, holder.menu);
                        getMenuInflater().inflate(R.menu.edit_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getItemId() == R.id.deleteProduct) {

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    Query applesQuery = ref.child("MishalRealtime").orderByChild("id").equalTo(model.id);

                                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                                                //Producs_Data mymodalclass = new Producs_Data(model.getId(), "new name", "new number", "https://play-lh.googleusercontent.com/8W4Ai-J22SM8Xzwy75_wXqnYPm04zbc0QpXm6dfKiS-VNDgPjeS3rtu_yymuGWmGP4w=s48-rw");

                                                //appleSnapshot.getRef().setValue(mymodalclass);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("=====", "onCancelled", databaseError.toException());
                                        }
                                    });
                                }
                                return false;
                            }
                        });
                        popupMenu.show();

                    }
                });


            }

            @Override
            public List_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_items, parent, false);

                return new List_Holder(view);
            }


        };
        recview.setAdapter(adapter);
    }

    public class List_Holder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView1, textView2;
        ImageButton menu;

        public List_Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.listImg);
            textView1 = itemView.findViewById(R.id.item_text1);
            textView2 = itemView.findViewById(R.id.item_txt2);
            menu = itemView.findViewById(R.id.menu);

        }
    }

    @Override
    protected void onStart() {


        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}