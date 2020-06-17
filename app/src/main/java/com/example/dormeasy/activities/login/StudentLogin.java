package com.example.dormeasy.activities.login;

import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dormeasy.activities.students.StudentHome;
import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.R;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class StudentLogin extends AppCompatActivity {
    TextInputEditText en,otp;
    Button otpsubmit;
    TextView number;
    ImageButton submit,back;
    ProgressBar pb;
    ConstraintLayout cl,layout;
    String verificatonid,enrollno;


    public void signInwithCredentials(final PhoneAuthCredential credential) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                SharedPreferences preferences =getSharedPreferences("prefID", Context.MODE_PRIVATE);

                if(task.isSuccessful()){

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("regno",enrollno);
                    editor.apply();

                    GlobalVar.regno = enrollno;
                    Intent i = new Intent(StudentLogin.this, StudentHome.class);
                    startActivity(i);
                    finish();

                }
                else{

                    Toast.makeText(getApplicationContext(),"Wrong OTP",Toast.LENGTH_SHORT).show();
                    otp.setText("");
                }


            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        en = findViewById(R.id.student_login_en);
        otp = findViewById(R.id.student_login_otp);
        number = findViewById(R.id.student_login_number);
        submit = findViewById(R.id.student_login_submit);
        pb = findViewById(R.id.student_login_pb);
        cl = findViewById(R.id.student_login_cl);
        layout = findViewById(R.id.admin_login_layout);
        otpsubmit = findViewById(R.id.student_login_next);
        back = findViewById(R.id.student_login_back);

        pb.setVisibility(View.GONE);
        cl.removeView(layout);


        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInwithCredentials(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_LONG).show();
                pb.setVisibility(View.GONE);


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"OTP Sent",Toast.LENGTH_SHORT).show();

                verificatonid = s;
                pb.setVisibility(View.GONE);
                cl.addView(layout);
                cl.removeView(submit);

            }
        };

        otpsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(otp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter The OTP",Toast.LENGTH_SHORT).show();
                }

                else{

                    pb.setVisibility(View.VISIBLE);

                    String otp_number = otp.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificatonid,otp_number);
                    signInwithCredentials(credential);

                }


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(en.getText().equals(null)){
                    Toast.makeText(getApplicationContext(),"Please Enter The RegistrationNumber",Toast.LENGTH_SHORT).show();
                }
                else{

                    pb.setVisibility(View.VISIBLE);

                    enrollno = en.getText().toString();

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                    myRef.child("students").child(enrollno).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String mnum = dataSnapshot.child("phone").getValue(String.class);
                            number.setText("An OTP Has Been Sent to :"+mnum);

                            if(mnum == null){
                                Toast.makeText(getApplicationContext(),"The user is not registered",Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                            else{
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(mnum,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentLogin.this, Main.class);
                startActivity(i);
                finish();
            }
        });
    }
}
