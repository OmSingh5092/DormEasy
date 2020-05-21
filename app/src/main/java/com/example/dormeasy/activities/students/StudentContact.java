package com.example.dormeasy.activities.students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dormeasy.utils.AdminInfo;
import com.example.dormeasy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentContact extends AppCompatActivity {

    RecyclerView rv;
    ImageButton back;
    ProgressBar pb;
    int REQUEST_PHONE_CALL = 100;

    List<AdminInfo> adminList  = new ArrayList<>();

    class RecyclerViewAdapter extends RecyclerView.Adapter<StudentContact.RecyclerViewAdapter.viewHolder>{

        List<AdminInfo> data;
        String [] authority = getApplicationContext().getResources().getStringArray(R.array.departments);

        public RecyclerViewAdapter(List<AdminInfo> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_contact_rv,parent,false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

            String auth_name = authority[Integer.valueOf(data.get(position).auth)];

            holder.name.setText(data.get(position).name);
            holder.auth.setText(auth_name);

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data.get(position).phone));
                    startActivity(i);
                }
            });

            holder.mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+data.get(position).email));
                    startActivity(Intent.createChooser(mailIntent,"Select An Application"));
                }
            });

            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://api.whatsapp.com/send?phone="+data.get(position).phone;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView name,auth;
            ImageButton call,message,mail;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.student_m_rv_name);
                auth= itemView.findViewById(R.id.student_m_rv_auth);
                call = itemView.findViewById(R.id.student_m_rv_call);
                message = itemView.findViewById(R.id.student_m_rv_message);
                mail = itemView.findViewById(R.id.student_m_rv_mail);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_contact);

        rv = findViewById(R.id.student_message_rv);
        back = findViewById(R.id.student_message_back);
        pb = findViewById(R.id.student_contacts_pb);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                        adminList.add(snap.getValue(AdminInfo.class));
                }

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(adminList);
                rv.setAdapter(adapter);
                pb.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);



        //Asking calling permission

        if (ContextCompat.checkSelfPermission(StudentContact.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StudentContact.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentContact.this.onBackPressed();
            }
        });

    }
}
