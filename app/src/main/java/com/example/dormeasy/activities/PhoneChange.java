package com.example.dormeasy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormeasy.activities.students.StudentHome;
import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneChange extends AppCompatActivity {

    TextInputEditText number,otp;
    Button otpsubmit;
    TextView number_display;
    ImageButton submit,back;
    ProgressBar pb;
    ConstraintLayout cl,otp_layout;
    String hostelno,verificatonid,phone;

    public void signInwithCredentials(final PhoneAuthCredential credential) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                    myRef.child("students").child(GlobalVar.regno).child("phone").setValue(phone);
                    GlobalVar.sinfo.phone = phone;

                    Intent i = new Intent(PhoneChange.this, StudentHome.class);
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
        setContentView(R.layout.activity_phone_change);

        number = findViewById(R.id.phone_number);
        otp = findViewById(R.id.phone_otp);
        otpsubmit = findViewById(R.id.phone_next);
        submit = findViewById(R.id.phone_submit);
        back = findViewById(R.id.phone_back);
        pb = findViewById(R.id.phone_pb);
        cl = findViewById(R.id.phone_cl);
        otp_layout = findViewById(R.id.phone_layout);

        pb.setVisibility(View.GONE);
        cl.removeView(otp_layout);

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
                cl.addView(otp_layout);
                cl.removeView(submit);

            }
        };

        otpsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);

                String otp_number = otp.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificatonid,otp_number);
                signInwithCredentials(credential);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(number.getText().equals(null)){
                    Toast.makeText(getApplicationContext(),"Please Enter The PhoneNumber",Toast.LENGTH_SHORT).show();
                }

                else if(number.getText().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                    Toast.makeText(getApplicationContext(),"Please Enter Another Number",Toast.LENGTH_SHORT).show();
                }

                else{

                    pb.setVisibility(View.VISIBLE);

                    phone = "+91"+number.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);


                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneChange.this.onBackPressed();
            }
        });
    }
}
