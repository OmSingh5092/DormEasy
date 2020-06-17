package com.example.dormeasy.activities.students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormeasy.activities.login.Main;
import com.example.dormeasy.activities.Scanner;
import com.example.dormeasy.activities.notices.NoticeBoard;
import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.R;
import com.example.dormeasy.utils.StudentInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StudentHome extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton image,drawer_close;
    ListView lv;
    ImageView drawer_image;
    ProgressBar pb;
    TextView name,reg;
    String feesurl;

    ZXingScannerView scannerView;

    int request_code=1111;

    Button mess,timetable,noticeboard,logout,web,complaint,message,scanner;

    int STORAGE_WRITE_REQUEST_CODE=100,STORAGE_READ_REQUET_CODE=100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        drawerLayout = findViewById(R.id.student_home_drawer);
        image = findViewById(R.id.student_home_image);
        lv = findViewById(R.id.student_home_lv);
        drawer_image = findViewById(R.id.student_home_drawer_image);
        pb = findViewById(R.id.student_home_pb);
        drawer_close = findViewById(R.id.student_home_drawer_close);
        name = findViewById(R.id.student_home_drawer_name);
        reg = findViewById(R.id.student_home_drawer_reg);
        mess = findViewById(R.id.student_home_mess);
        timetable = findViewById(R.id.student_home_timetable);
        noticeboard = findViewById(R.id.student_home_noticeboard);
        logout = findViewById(R.id.student_home_logout);
        web = findViewById(R.id.student_home_web);
        complaint = findViewById(R.id.student_home_complaint);
        message = findViewById(R.id.student_home_message);
        scanner = findViewById(R.id.student_home_qr);

        image.setEnabled(false);
        mess.setEnabled(false);
        timetable.setEnabled(false);
        noticeboard.setEnabled(false);
        complaint.setEnabled(false);
        message.setEnabled(false);
        scanner.setEnabled(false);

        Toast.makeText(getApplicationContext(),"Loading....",Toast.LENGTH_SHORT).show();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(StudentHome.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(StudentHome.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent i = new Intent(StudentHome.this, student_profile.class);
                    startActivity(i);
                }

                else if(position == 1){
                    Intent i = new Intent(StudentHome.this, StudentComplaints.class);
                    i.putExtra("tab",1);
                    startActivity(i);
                }
                else if(position ==2){

                    final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

                    File storageDir = new File(Environment.getExternalStorageDirectory()+"/DormEasy");

                    if(!storageDir.exists()){
                        storageDir.mkdir();
                    }



                    final File acadcal = new File(storageDir,"acadcal.pdf");
                    pb.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawers();

                    Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();

                    mStorageRef.child("data").child("calender.pdf").getFile(Uri.fromFile(acadcal)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                            final Uri data = FileProvider.getUriForFile(StudentHome.this, getPackageName() + ".provider", acadcal);
                            StudentHome.this.grantUriPermission(StudentHome.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            final Intent intent = new Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(data, "application/pdf")
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            pb.setVisibility(View.GONE);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Download Failed",Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    });


                }
                else if(position == 3){

                    if(feesurl != null){
                        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(feesurl));
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Payment Website Url Not Found",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        GlobalVar.regno = sharedPreferences.getString("regno",null);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();



        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                StudentInfo info = dataSnapshot.child("students").child(GlobalVar.regno).getValue(StudentInfo.class);


                name.setText(info.name);
                GlobalVar.sinfo = info;
                GlobalVar.hostel = info.hostel;
                reg.setText(GlobalVar.regno);

                feesurl = dataSnapshot.child("hostels").child(info.hostel).child("mess").child("url").getValue(String.class);

                try {
                    final File localFile = File.createTempFile("image","jpg");
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/students/"+GlobalVar.regno+"profile.jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {



                            try {
                                InputStream inputStream = new FileInputStream(localFile);

                                GlobalVar.Student.profileimage = localFile;

                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                image.setImageBitmap(bitmap);
                                drawer_image.setImageBitmap(bitmap);
                                pb.setVisibility(View.GONE);
                                image.setEnabled(true);
                                mess.setEnabled(true);
                                timetable.setEnabled(true);
                                noticeboard.setEnabled(true);
                                complaint.setEnabled(true);
                                message.setEnabled(true);
                                scanner.setEnabled(true);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Profile Photo Not Available",Toast.LENGTH_SHORT).show();
                            image.setEnabled(true);
                            mess.setEnabled(true);
                            timetable.setEnabled(true);
                            noticeboard.setEnabled(true);
                            complaint.setEnabled(true);
                            message.setEnabled(true);
                            scanner.setEnabled(true);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        drawer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });






        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_LONG).show();

                //File file = new File(StudentHome.this.getCacheDir(),"timetable");



                File storageDir = new File(Environment.getExternalStorageDirectory()+"/DormEasy/mess");
                if(!storageDir.exists()){
                    storageDir.mkdir();
                }
                final File localFile = new File(storageDir,"menu.pdf");


                pb.setVisibility(View.VISIBLE);

                StorageReference storageReference= FirebaseStorage.getInstance().getReference();

                storageReference.child(GlobalVar.sinfo.hostel).child("mess").child("menu.pdf").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                        final Uri data = FileProvider.getUriForFile(StudentHome.this, getPackageName() + ".provider", localFile);
                        StudentHome.this.grantUriPermission(StudentHome.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(data, "application/pdf")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        pb.setVisibility(View.GONE);


                        startActivity(intent);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Download Failed!",Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                });




            }
        });


        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);

                //File file = new File(StudentHome.this.getCacheDir(),"timetable");



                    File storageDir = new File(Environment.getExternalStorageDirectory()+"/DormEasy/timetable");
                    if(!storageDir.exists()){
                        storageDir.mkdir();
                    }
                    final File localFile = new File(storageDir,"timetable.pdf");
                    Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_LONG).show();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("timetable").child(GlobalVar.sinfo.batch+".pdf").getFile(Uri.fromFile(localFile)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            final Uri data = FileProvider.getUriForFile(StudentHome.this, getPackageName() + ".provider", localFile);
                            StudentHome.this.grantUriPermission(StudentHome.this.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            final Intent intent = new Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(data, "application/pdf")
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            pb.setVisibility(View.GONE);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Download Failed!",Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    });




            }
        });

        noticeboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentHome.this, NoticeBoard.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentHome.this, Main.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(i);
                finish();
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://academics.mnnit.ac.in/new/";
                    Intent i =new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    startActivity(i);
            }
        });

        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentHome.this,StudentComplaints.class);
                startActivity(i);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (StudentHome.this, StudentContact.class);
                startActivity(i);
            }
        });


        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(StudentHome.this, Scanner.class);
                startActivity(i);


            }
        });




    }







}
