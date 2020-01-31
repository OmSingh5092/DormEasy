package com.example.dormeasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;

    ZXingScannerView mScannerView;

    private boolean checkPermission(){
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }



    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();

                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        int MY_CAMERA_REQUEST_CODE = 100;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Scanner.this, new String[] {Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.setResultHandler(this);
        mScannerView.stopCamera();
    }




    @Override
    public void handleResult(Result result) {

        final String code = result.getText();

        String [] parsedcode = code.split("/");

        if(parsedcode[0].equals("#DormEasy")){
            Intent i = new Intent(Scanner.this,NoticeEvents.class);
            i.putExtra("time", parsedcode[1]);
            i.putExtra("hostel",parsedcode[2]);

            startActivity(i);
            finish();


        }
    }
}
