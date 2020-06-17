package com.example.dormeasy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentComplaintSent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentComplaintSent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentComplaintSent extends Fragment {


    RecyclerView rv;
    ArrayList<String> datacomplaint =new ArrayList<>();
    ArrayList<String> datadone = new ArrayList<>();
    ArrayList<String> dataAuth = new ArrayList<>();
    ArrayList<String> dataTime = new ArrayList<>();
    ArrayList<String> datasubject = new ArrayList<>();

    ProgressBar pb;


    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    class RecycerViewAdapter extends RecyclerView.Adapter<StudentComplaintSent.RecycerViewAdapter.viewHolder>{


        ArrayList<String> rvcomplaint = new ArrayList<>();
        ArrayList<String> rvdone = new ArrayList<>();
        ArrayList<String> rvAuth = new ArrayList<>();
        ArrayList<String> rvTime = new ArrayList<>();
        ArrayList<String> rvSubject = new ArrayList<>();

        public RecycerViewAdapter(ArrayList<String> rvcomplaint, ArrayList<String> rvdone, ArrayList<String> rvAuth, ArrayList<String> rvTime, ArrayList<String> rvSubject) {
            this.rvcomplaint = rvcomplaint;
            this.rvdone = rvdone;
            this.rvAuth = rvAuth;
            this.rvTime = rvTime;
            this.rvSubject = rvSubject;
        }

        String[] dept = getResources().getStringArray(R.array.departments);


        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_complaint_rv,parent,false);
            return new viewHolder(v);
        }

        @Override
        public int getItemCount() {
            return rvcomplaint.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {



            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            Long time = Long.valueOf(rvTime.get(position));
            Date date = new Date(time);
            String timeString = sdf.format(date);



            holder.head.setText(dept[Integer.valueOf(rvAuth.get(position))]);
            holder.body.setText(rvcomplaint.get(position));
            holder.time.setText(timeString);
            holder.subject.setText(rvSubject.get(position));

            if(rvdone.get(position).equals("0")){
                holder.done.setImageResource(R.drawable.icon_cross);
            }
            else{
                holder.done.setImageResource(R.drawable.icon_check);
            }



            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rvdone.get(position).equals("0")){
                        rvdone.set(position,"1");
                        holder.done.setImageResource(R.drawable.icon_check);

                        myRef.child("hostels").child(GlobalVar.sinfo.hostel).child("complaints").child(rvAuth.get(position)).child(GlobalVar.regno).child(rvTime.get(position)).child("done").setValue("1");
                    }
                }
            });

        }

        class viewHolder extends RecyclerView.ViewHolder{
            TextView head, body,time,subject;
            ImageButton done;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                head = itemView.findViewById(R.id.student_complaint_rv_head);
                body = itemView.findViewById(R.id.student_complaint_rv_text);
                done = itemView.findViewById(R.id.student_complaint_rv_done);
                time = itemView.findViewById(R.id.student_complaint_rv_time);
                subject = itemView.findViewById(R.id.student_complaint_rv_subject);

            }
        }

    }



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StudentComplaintSent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentComplaintSent.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentComplaintSent newInstance(String param1, String param2) {
        StudentComplaintSent fragment = new StudentComplaintSent();
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
        View rootview = inflater.inflate(R.layout.fragment_student_complaint_sent, container, false);

        rv = rootview.findViewById(R.id.student_complaint_sent_rv);

        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);
        pb = rootview.findViewById(R.id.student_complaint_sent_pb);






        final String regno = GlobalVar.regno;
        String hostel = GlobalVar.sinfo.hostel;
        myRef.child("hostels").child(hostel).child("complaints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String authority = null,text = null;
                String done = null;

                datacomplaint.clear();
                datadone.clear();
                dataTime.clear();
                dataAuth.clear();
                datasubject.clear();




                for(DataSnapshot snap : dataSnapshot.getChildren()){

                    if(snap.child(regno).exists()){

                        for(DataSnapshot tsnap : snap.child(regno).getChildren()){

                            datacomplaint.add(tsnap.child("complaint").getValue(String.class));
                            datadone.add(tsnap.child("done").getValue(String.class));
                            dataTime.add(tsnap.getKey());
                            dataAuth.add(snap.getKey());
                            datasubject.add(tsnap.child("subject").getValue(String.class));

                        }



                    }


                }

                if(!datacomplaint.isEmpty()){
                    RecycerViewAdapter adapter = new RecycerViewAdapter(datacomplaint,datadone,dataAuth,dataTime,datasubject);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.GONE);

                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            final String regno = GlobalVar.regno;
            String hostel = GlobalVar.sinfo.hostel;
            myRef.child("hostels").child(hostel).child("complaints").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String authority = null,text = null;
                    String done = null;

                    datacomplaint.clear();
                    datadone.clear();
                    dataTime.clear();
                    dataAuth.clear();

                    datasubject.clear();




                    for(DataSnapshot snap : dataSnapshot.getChildren()){

                        if(snap.child(regno).exists()){

                            for(DataSnapshot tsnap : snap.child(regno).getChildren()){

                                datacomplaint.add(tsnap.child("complaint").getValue(String.class));
                                datadone.add(tsnap.child("done").getValue(String.class));
                                dataTime.add(tsnap.getKey());
                                dataAuth.add(snap.getKey());
                                datasubject.add(tsnap.child("subject").getValue(String.class));

                            }



                        }


                    }

                    rv.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rv.setLayoutManager(layoutManager);

                    RecycerViewAdapter adapter = new RecycerViewAdapter(datacomplaint,datadone,dataAuth,dataTime,datasubject);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.GONE);





                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
