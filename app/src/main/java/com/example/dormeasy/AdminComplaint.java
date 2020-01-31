package com.example.dormeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminComplaint extends AppCompatActivity {
    RecyclerView rv;
    ProgressBar pb;
    ImageButton back;

    ArrayList<String> datasubject = new ArrayList<>();
    ArrayList<String> datatext = new ArrayList<>();
    ArrayList<StudentInfo> datasent = new ArrayList<>();
    ArrayList<String> datatime = new ArrayList<>();
    ArrayList<String> datadone = new ArrayList<>();

    class RecycerViewAdapter extends RecyclerView.Adapter<AdminComplaint.RecycerViewAdapter.viewHolder>{

        ArrayList<StudentInfo> rvsent = new ArrayList<>();
        ArrayList<String> rvtext= new ArrayList<>();
        ArrayList<String> rvdone= new ArrayList<>();
        ArrayList<String> rvtime = new ArrayList<>();
        ArrayList<String> rvsubject = new ArrayList<>();
        ArrayList<String> rvreg = new ArrayList<>();

        public RecycerViewAdapter(ArrayList<StudentInfo> rvsent, ArrayList<String> rvtext, ArrayList<String> rvdone, ArrayList<String> rvtime, ArrayList<String> rvsubject, ArrayList<String> rvreg) {
            this.rvsent = rvsent;
            this.rvtext = rvtext;
            this.rvdone = rvdone;
            this.rvtime = rvtime;
            this.rvsubject = rvsubject;
            this.rvreg = rvreg;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_complaint_rv,parent,false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

            if(rvdone.get(position) != null){
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
                Long time = Long.valueOf(rvtime.get(position));
                Date date = new Date(time);
                String timeString = sdf.format(date);

                holder.subject.setText(rvsubject.get(position));
                holder.body.setText(rvtext.get(position));
                holder.sent.setText(rvreg.get(position));
                holder.time.setText(timeString);

                if(rvdone.get(position).equals("1")){
                    holder.done.setImageResource(R.drawable.icon_check);
                }
                else{
                    holder.done.setImageResource(R.drawable.icon_cross);
                }

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone_number = rvsent.get(position).phone;
                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone_number));
                        startActivity(i);
                    }
                });
                holder.mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email_string = rvsent.get(position).email;
                        Intent mailIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+email_string));
                        startActivity(Intent.createChooser(mailIntent,"Select An Application"));
                    }
                });
                holder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone_number = rvsent.get(position).phone;
                        String url = "https://api.whatsapp.com/send?phone="+phone_number;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

            }




        }

        @Override
        public int getItemCount() {
            return rvdone.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{
            TextView subject, body,time,sent;
            ImageView done;
            ImageButton call,mail,message;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                subject = itemView.findViewById(R.id.admin_complaint_rv_subject);
                body = itemView.findViewById(R.id.admin_complaint_rv_text);
                done = itemView.findViewById(R.id.admin_complaint_rv_done);
                time = itemView.findViewById(R.id.admin_complaint_rv_time);
                sent = itemView.findViewById(R.id.admin_complaint_rv_sent);
                call = itemView.findViewById(R.id.admin_complaint_rv_call);
                mail = itemView.findViewById(R.id.admin_complaint_rv_mail);
                message = itemView.findViewById(R.id.admin_complaint_rv_whatsapp);

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaint);

        rv = findViewById(R.id.admin_complaint_rv);
        pb = findViewById(R.id.admin_complaint_pb);
        back = findViewById(R.id.admin_complaint_back);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(layoutManager);





        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                datasent.clear();
                datatext.clear();
                datadone.clear();
                datatime.clear();
                datasubject.clear();

                ArrayList<String> student_data= new ArrayList<>();


                if(dataSnapshot.child("hostels").child(GlobalVar.info.hostel).child("complaints").child(GlobalVar.info.auth).exists()){

                    for(DataSnapshot snap: dataSnapshot.child("hostels").child(GlobalVar.info.hostel).child("complaints").child(GlobalVar.info.auth).getChildren()){

                        for(DataSnapshot tsnap: snap.getChildren()){

                            student_data.add(snap.getKey());
                            datatext.add(tsnap.child("complaint").getValue(String.class));
                            datadone.add(tsnap.child("done").getValue(String.class));
                            datatime.add(tsnap.getKey());
                            datasubject.add(tsnap.child("subject").getValue(String.class));

                        }


                    }

                    for(int i = 0 ; i<student_data.size(); i++){
                            datasent.add(dataSnapshot.child("students").child(student_data.get(i)).getValue(StudentInfo.class));
                    }



                    RecycerViewAdapter adapter = new RecycerViewAdapter(datasent,datatext,datadone,datatime,datasubject,student_data);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.GONE);


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminComplaint.this.onBackPressed();
            }
        });


    }
}
