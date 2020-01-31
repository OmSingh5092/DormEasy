package com.example.dormeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticeBoard extends AppCompatActivity {
    RecyclerView rv;

    ArrayList<String> subject = new ArrayList<>();
    ArrayList<String> sent = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> text = new ArrayList<>();
    ArrayList<String> authority = new ArrayList<>();


    ProgressBar pb;
    ImageButton back;

    class RecycerViewAdapter extends RecyclerView.Adapter<NoticeBoard.RecycerViewAdapter.viewHolder>{

        ArrayList<String> rv_subject = new ArrayList<>();
        ArrayList<String> rv_sent = new ArrayList<>();
        ArrayList<String> rv_time = new ArrayList<>();
        ArrayList<String> rv_text = new ArrayList<>();
        ArrayList<String> rv_auth = new ArrayList<>();

        public RecycerViewAdapter(ArrayList<String> rv_subject, ArrayList<String> rv_sent, ArrayList<String> rv_time, ArrayList<String> rv_text, ArrayList<String> rv_auth) {
            this.rv_subject = rv_subject;
            this.rv_sent = rv_sent;
            this.rv_time = rv_time;
            this.rv_text = rv_text;
            this.rv_auth = rv_auth;
        }




        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.noticeboard_rv,parent,false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
            Long time = Long.valueOf(rv_time.get(position));
            Date date = new Date(time);
            String timeString = sdf.format(date);

            String dept[] = getResources().getStringArray(R.array.departments);

            holder.subject.setText(rv_subject.get(position));
            holder.authority.setText(dept[Integer.valueOf(rv_auth.get(position))]);
            holder.text.setText(rv_text.get(position));
            holder.time.setText(timeString);
            holder.sent.setText(rv_sent.get(position));

        }

        @Override
        public int getItemCount() {
            return rv_auth.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView subject,authority,text,time,sent;
            public viewHolder(@NonNull View itemView) {
                super(itemView);

                subject = itemView.findViewById(R.id.noticeboard_subject);
                authority = itemView.findViewById(R.id.noticeboard_auth);
                text = itemView.findViewById(R.id.noticeboard_text);
                time = itemView.findViewById(R.id.noticeboard_time);
                sent = itemView.findViewById(R.id.noticeboard_sent);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(NoticeBoard.this,NoticeEvents.class);
                        i.putExtra("time",rv_time.get(getAdapterPosition()));
                        i.putExtra("hostel",GlobalVar.hostel);
                        startActivity(i);
                    }
                });



            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        pb = findViewById(R.id.noticeboard_pb);
        back = findViewById(R.id.noticeboard_back);

        rv = findViewById(R.id.noticeboard_rv);

        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        rv.setLayoutManager(layoutManager);

        String hostel = GlobalVar.hostel;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();



        myRef.child("hostels").child(hostel).child("noticeboard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                subject.clear();
                text.clear();
                sent.clear();
                authority.clear();
                time.clear();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    subject.add(snap.child("subject").getValue(String.class));
                    text.add(snap.child("text").getValue(String.class));
                    sent.add(snap.child("name").getValue(String.class));
                    authority.add(snap.child("authority").getValue(String.class));
                    time.add(snap.getKey());
                }

                RecycerViewAdapter adapter = new RecycerViewAdapter(subject,sent,time,text,authority);
                rv.setAdapter(adapter);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeBoard.this.onBackPressed();
            }
        });



    }
}
