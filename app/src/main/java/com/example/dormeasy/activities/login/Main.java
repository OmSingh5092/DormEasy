package com.example.dormeasy.activities.login;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dormeasy.R;
import com.example.dormeasy.activities.admin.AdminHome;
import com.example.dormeasy.activities.students.StudentHome;
import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {
    Button student,admin,web;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            int login = sharedPreferences.getInt("login",0);

            if(login ==1){

                Intent i = new Intent(Main.this, StudentHome.class);
                startActivity(i);
                finish();

            }

            else{
                Intent i = new Intent(Main.this, AdminHome.class);
                startActivity(i);
                finish();
            }

        }


        student = findViewById(R.id.main_students);
        admin = findViewById(R.id.main_admin);
        web = findViewById(R.id.main_website);

        final SharedPreferences.Editor editor = sharedPreferences.edit();


        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putInt("login",1);
                editor.apply();

                Intent i = new Intent(Main.this, StudentLogin.class);
                startActivity(i);
                finish();
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.mnnit.ac.in/";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putInt("login",0);
                editor.apply();

                Intent i = new Intent(Main.this, AdminLogin.class);
                startActivity(i);
                finish();

            }
        });
    }
}
