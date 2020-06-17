package com.example.dormeasy.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormeasy.R;
import com.example.dormeasy.activities.admin.AdminHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
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

public class AdminLogin extends AppCompatActivity {

    TextInputEditText number,otp;
    Button otpsubmit;
    TextView number_display;
    ImageButton submit,back;
    ProgressBar pb;
    ConstraintLayout cl,layout;
    String hostelno,verificatonid,phone;

    public void signInwithCredentials(final PhoneAuthCredential credential) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    SharedPreferences sharedPreferences = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("phone",phone);

                    Intent i = new Intent(AdminLogin.this, AdminHome.class);
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
        setContentView(R.layout.activity_admin_login);
        number = findViewById(R.id.admin_login_number_display);
        otp = findViewById(R.id.admin_login_otp);
        otpsubmit = findViewById(R.id.admin_login_next);
        number_display = findViewById(R.id.admin_login_number_display);
        submit = findViewById(R.id.admin_login_submit);
        back = findViewById(R.id.admin_login_back);
        pb = findViewById(R.id.admin_login_pb);
        cl = findViewById(R.id.admin_login_cl);
        layout = findViewById(R.id.admin_login_layout);

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

                if(number.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter The PhoneNumber",Toast.LENGTH_SHORT).show();
                }

                else{

                    pb.setVisibility(View.VISIBLE);

                    phone = "+91"+number.getText().toString();

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                    myRef.child("admin").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.exists()){
                                Toast.makeText(getApplicationContext(),"The user is not registered",Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                            }
                            else{
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);
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
                Intent i = new Intent(AdminLogin.this, Main.class);
                startActivity(i);
                finish();
            }
        });



    }
}
