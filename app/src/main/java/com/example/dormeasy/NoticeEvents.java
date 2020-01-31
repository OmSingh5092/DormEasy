package com.example.dormeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeEvents extends AppCompatActivity {

    TextView subject, dept, text,sent,time;
    ImageButton back;
    ImageButton qr;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_events);

        subject = findViewById(R.id.notice_subject);
        dept = findViewById(R.id.notice_dept);
        text = findViewById(R.id.notice_text);
        back = findViewById(R.id.notice_back);
        time = findViewById(R.id.notice_time);
        sent = findViewById(R.id.notice_sent);
        qr = findViewById(R.id.notice_qrgen);



        final String timestamp = getIntent().getStringExtra("time");
        String hostel = getIntent().getStringExtra("hostel");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        Long timeval = Long.valueOf(timestamp);
        Date date = new Date(timeval);
        String timeString = sdf.format(date);

        time.setText(timeString);

        final String[] authority = NoticeEvents.this.getResources().getStringArray(R.array.departments);


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("hostels").child(hostel).child("noticeboard").child(timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String auth = authority[Integer.valueOf(dataSnapshot.child("authority").getValue(String.class))];
                subject.setText(dataSnapshot.child("subject").getValue(String.class));
                dept.setText(auth);
                text.setText(dataSnapshot.child("text").getValue(String.class));
                sent.setText(dataSnapshot.child("name").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeEvents.this.onBackPressed();
            }
        });


        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = "#DormEasy/"+timestamp+"/"+GlobalVar.hostel;
                imageView = new ImageView(NoticeEvents.this);

                QRCodeWriter writer = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512);
                    int width = 512;
                    int height = 512;
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            if (bitMatrix.get(x, y)==true)
                                bmp.setPixel(x, y, Color.BLACK);
                            else
                                bmp.setPixel(x, y, Color.WHITE);
                        }
                    }
                    imageView.setImageBitmap(bmp);
                } catch (WriterException e) {
                    Toast.makeText(getApplicationContext(),"Error in generating QR",Toast.LENGTH_LONG).show();

                }




                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                imageView.setLayoutParams(lp);

                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeEvents.this);
                builder.setView(imageView);

                builder.setTitle("QR of the notice");

                builder.show();



            }
        });
    }
}
