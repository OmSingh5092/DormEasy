package com.example.dormeasy.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormeasy.activities.admin.AdminNotice;
import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.activities.notices.NoticeBoard;
import com.example.dormeasy.R;
import com.example.dormeasy.activities.admin.AdminComplaint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {

    Button notice, complaint,noticeboard,upload_mess,menu,acadcal,mess_url;
    TextView auth;
    ConstraintLayout cl,messlayout;
    ProgressBar pb;
    EditText input;

    Integer UPLOAD_MENU = 100;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPLOAD_MENU){

            Uri uri = data.getData();



            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
            mStorage.child(GlobalVar.info.hostel).child("mess").child("menu.pdf").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),"Uploaded Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_admin_home, container, false);

        notice = rootview.findViewById(R.id.admin_home_notice);
        complaint = rootview.findViewById(R.id.admin_home_complaints);
        noticeboard = rootview.findViewById(R.id.admin_home_noticeboard);
        auth = rootview.findViewById(R.id.admin_home_auth);
        upload_mess = rootview.findViewById(R.id.admin_home_mess_upload);
        menu = rootview.findViewById(R.id.admin_home_mess);
        pb = rootview.findViewById(R.id.admin_home_pb_parent);
        cl = rootview.findViewById(R.id.admin_home_cl);
        messlayout = rootview.findViewById(R.id.admin_home_mess_layout);
        mess_url = rootview.findViewById(R.id.admin_home_messpayment);

        acadcal = rootview.findViewById(R.id.admin_home_acadcal);

        String department[] = getResources().getStringArray(R.array.departments);


        auth.setText(department[Integer.valueOf(GlobalVar.info.auth)]);

        pb.setVisibility(View.GONE);

        //Setting up mess buttons

        if(!GlobalVar.info.auth.equals("1")){
            cl.removeView(messlayout);
        }





        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AdminNotice.class);
                startActivity(i);
            }
        });

        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AdminComplaint.class);
                startActivity(i);
            }
        });
        noticeboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), NoticeBoard.class);
                startActivity(i);
            }
        });


        upload_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference mStorage = FirebaseStorage.getInstance().getReference();

                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("application/pdf");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, UPLOAD_MENU);

                Toast.makeText(getActivity(),"Uploading...",Toast.LENGTH_SHORT).show();


                pb.setVisibility(View.VISIBLE);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                Toast.makeText(getActivity(),"Downloading...",Toast.LENGTH_SHORT).show();




                    File storageDir = new File(Environment.getExternalStorageDirectory()+"/DormEasy/mess");
                    if(!storageDir.exists()){
                        storageDir.mkdir();
                    }
                    final File localFile = new File(storageDir,"menu.pdf");

                    pb.setVisibility(View.VISIBLE);

                    storageReference.child(GlobalVar.info.hostel).child("mess").child("menu.pdf").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                            final Uri data = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", localFile);
                            getActivity().grantUriPermission(getActivity().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            final Intent intent = new Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(data, "application/pdf")
                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            pb.setVisibility(View.GONE);


                            startActivity(intent);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Download Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });

        acadcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                Toast.makeText(getActivity(),"Downloading...",Toast.LENGTH_SHORT).show();




                File storageDir = new File(Environment.getExternalStorageDirectory()+"/DormEasy");
                if(!storageDir.exists()){
                    storageDir.mkdir();
                }
                final File localFile = new File(storageDir,"acadcal.pdf");

                pb.setVisibility(View.VISIBLE);

                storageReference.child("data").child("calender.pdf").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                        final Uri data = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", localFile);
                        getActivity().grantUriPermission(getActivity().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(data, "application/pdf")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        pb.setVisibility(View.GONE);


                        startActivity(intent);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Download Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        final DatabaseReference mess_payment = FirebaseDatabase.getInstance().getReference().child("hostels").child(GlobalVar.info.hostel).child("mess").child("url");




        mess_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = new EditText(getActivity());
                input.setHint("URL");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!input.getText().toString().contains("http")){
                            Toast.makeText(getActivity(),"Please Enter A Proper URL",Toast.LENGTH_SHORT).show();
                        }

                        else{
                            mess_payment.setValue(input.getText().toString());
                            Toast.makeText(getActivity(),"Submitted Successfully",Toast.LENGTH_SHORT).show();
                        }




                    }
                }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setTitle("Enter the URL of Payment Website");

                builder.show();
            }
        });




        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }  */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
