package com.example.dormeasy.activities.students;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.dormeasy.R;
import com.example.dormeasy.fragments.StudentComplaintSend;
import com.example.dormeasy.fragments.StudentComplaintSent;
import com.google.android.material.tabs.TabLayout;

public class StudentComplaints extends AppCompatActivity {
    ImageButton back;
    TabLayout tabLayout;
    ViewPager vp;

    class PagerAdapter extends FragmentPagerAdapter

    {

        public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

        @Override
        public Fragment getItem(int position) {
        switch (position){
            case 0: return new StudentComplaintSend();
            case 1: return new StudentComplaintSent();
            default : return null;
        }
    }

        @Override
        public int getCount() {
        return 2;
    }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaints);

        back = findViewById(R.id.student_complaints_back);
        vp = findViewById(R.id.student_complaint_vp);
        tabLayout = findViewById(R.id.student_complaint_tab);

        Integer tabposition = getIntent().getIntExtra("tab",0);
        vp.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout.setupWithViewPager(vp);
        tabLayout.getTabAt(tabposition).select();

        tabLayout.getTabAt(0).setText("Send");
        tabLayout.getTabAt(1).setText("Sent");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentComplaints.this.onBackPressed();
            }
        });
    }
}
