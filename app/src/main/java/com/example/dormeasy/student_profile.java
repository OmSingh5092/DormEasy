package com.example.dormeasy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class student_profile extends AppCompatActivity {

    ImageView image;
    TextView phonenumber,name_display,regno_display,dob_display,gender_display,hostel_display,batch_display;
    Button profile_photo,phone;
    String name,dob,gender,hostel;
    ProgressBar pb;
    ImageButton back;
    String regno = GlobalVar.regno;
    Uri imageUri;


    int PICK_IMAGE =100 , STORAGE_WRITE_REQUEST_CODE=100,STORAGE_READ_REQUET_CODE=100;


    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(300, 300)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    String currentPhotoPath = "";

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE){


            Uri sourceUri = data.getData(); // 1
            File file = null; // 2

            try {

                file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4

            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            imageUri = UCrop.getOutput(data);

            File file = new File(imageUri.getPath());


            try {
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);

                pb.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Uploading Photo....",Toast.LENGTH_SHORT).show();


                StorageReference storage = FirebaseStorage.getInstance().getReference();

                storage.child("profile/students/"+regno+"profile.jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Profile Photo Successfully Uploaded",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(student_profile.this,StudentHome.class);
                        startActivity(i);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Upload Unsuccessful",Toast.LENGTH_SHORT).show();

                    }
                });




            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        image = findViewById(R.id.student_profile_image);
        phonenumber = findViewById(R.id.student_profile_number_display);
        profile_photo = findViewById(R.id.student_profile_photo_update);
        phone = findViewById(R.id.student_profile_phone_update);
        pb = findViewById(R.id.student_profile_pb);
        name_display = findViewById(R.id.student_profile_name_display);
        regno_display = findViewById(R.id.student_profile_reg_display);
        dob_display = findViewById(R.id.student_profile_dob_display);
        gender_display = findViewById(R.id.student_profile_gender_display);
        hostel_display = findViewById(R.id.student_login_hostel_display);
        back = findViewById(R.id.student_profile_back);
        batch_display = findViewById(R.id.student_profile_batch_display);

        //Filling the displays

        name_display.setText(GlobalVar.sinfo.name);
        dob_display.setText(GlobalVar.sinfo.dob);
        gender_display.setText(GlobalVar.sinfo.gender);
        regno_display.setText(GlobalVar.regno);
        hostel_display.setText(GlobalVar.sinfo.hostel);
        batch_display.setText(GlobalVar.sinfo.batch);

        pb.setVisibility(View.GONE);


        String userphone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        phonenumber.setText(userphone);

        if(GlobalVar.Student.profileimage != null){
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(GlobalVar.Student.profileimage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else{

        }







        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(student_profile.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(student_profile.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
                }

                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");  // 1
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
                    pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
                startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE);  // 4

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student_profile.this.onBackPressed();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(student_profile.this,PhoneChange.class);
                startActivity(i);
            }
        });


    }
}
