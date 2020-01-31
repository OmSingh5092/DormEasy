package com.example.dormeasy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentComplaintSend.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentComplaintSend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentComplaintSend extends Fragment {

    Spinner spinner;
    TextInputEditText text;
    Button submit;

    String authority;
    String complaint;
    String hostel;
    String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ProgressBar pb;
    TextInputEditText subject;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StudentComplaintSend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentComplaintSend.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentComplaintSend newInstance(String param1, String param2) {
        StudentComplaintSend fragment = new StudentComplaintSend();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_student_complaint_send, container, false);

        spinner = rootview.findViewById(R.id.student_complaint_send_spinner);
        text = rootview.findViewById(R.id.student_complaint_send_text);
        submit = rootview.findViewById(R.id.student_complaint_send_submit);
        subject = rootview.findViewById(R.id.student_complaint_send_subject);
        pb = rootview.findViewById(R.id.student_complaint_send_pb);

        pb.setVisibility(View.GONE);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                authority = String.valueOf(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(authority.equals("0") || authority.equals(null)){

                    Toast.makeText(getActivity(),"Please Select An Authority",Toast.LENGTH_SHORT).show();

                }

                else if(subject.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Please Enter The Subject",Toast.LENGTH_SHORT).show();
                }

                else if(text.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Please Enter The Complaint",Toast.LENGTH_SHORT).show();
                }



                else{

                    pb.setVisibility(View.VISIBLE);
                    complaint = text.getText().toString();
                    final String subjectstring = subject.getText().toString();
                    hostel = GlobalVar.sinfo.hostel;



                    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                    String regno = GlobalVar.regno;
                    String time = String.valueOf(System.currentTimeMillis());


                    myRef.child("hostels").child(hostel).child("complaints").child(authority).child(regno).child(time).child("complaint").setValue(complaint);
                    myRef.child("hostels").child(hostel).child("complaints").child(authority).child(regno).child(time).child("done").setValue("0");
                    myRef.child("hostels").child(hostel).child("complaints").child(authority).child(regno).child(time).child("subject").setValue(subjectstring);
                    pb.setVisibility(View.GONE);

                    Toast.makeText(getContext(),"Complaint Successfully Submitted",Toast.LENGTH_SHORT).show();

                }




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
