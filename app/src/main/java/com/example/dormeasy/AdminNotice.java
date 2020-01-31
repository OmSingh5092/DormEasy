package com.example.dormeasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNotice extends AppCompatActivity {

    ImageButton back;
    EditText text;
    Button submit;
    TextInputEditText subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notice);

        back = findViewById(R.id.admin_notice_back);
        text = findViewById(R.id.admin_notice_text);
        submit = findViewById(R.id.admin_notice_submit);
        subject = findViewById(R.id.admin_notice_subject);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notice = text.getText().toString();
                String subjectstring = subject.getText().toString();

                if(notice.equals("")|| subjectstring.equals("")){

                    Toast.makeText(getApplicationContext(),"Please Enter a Notice",Toast.LENGTH_SHORT).show();
                }
                else{

                    String time = String.valueOf(System.currentTimeMillis());
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child("hostels").child(GlobalVar.info.hostel).child("noticeboard").child(time).child("text").setValue(notice);
                    myRef.child("hostels").child(GlobalVar.info.hostel).child("noticeboard").child(time).child("authority").setValue(GlobalVar.info.auth);
                    myRef.child("hostels").child(GlobalVar.info.hostel).child("noticeboard").child(time).child("name").setValue(GlobalVar.info.name);
                    myRef.child("hostels").child(GlobalVar.info.hostel).child("noticeboard").child(time).child("subject").setValue(subjectstring);

                    text.setText("");
                    subject.setText("");

                    Intent i = new Intent(AdminNotice.this,NoticeBoard.class);
                    startActivity(i);

                    Toast.makeText(getApplicationContext(),"Notice Uploaded Successfully",Toast.LENGTH_SHORT).show();


                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminNotice.this.onBackPressed();
            }
        });
    }
}
