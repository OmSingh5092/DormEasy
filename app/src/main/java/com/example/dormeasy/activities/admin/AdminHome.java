package com.example.dormeasy.activities.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormeasy.activities.login.Main;
import com.example.dormeasy.fragments.AdminHomeFragment;
import com.example.dormeasy.utils.AdminInfo;
import com.example.dormeasy.fragments.AdminMessageFragment;
import com.example.dormeasy.utils.GlobalVar;
import com.example.dormeasy.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHome extends AppCompatActivity {
    ViewPager vp;
    TabLayout tab;
    ImageButton menu,close;
    DrawerLayout drawerLayout;
    Button logout,web;
    TextView name,email,phone,auth;
    ProgressBar pb;

    class PagerAdapter extends FragmentPagerAdapter

    {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new AdminHomeFragment();
                case 1: return new AdminMessageFragment();
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
        setContentView(R.layout.activity_admin_home);

        vp = findViewById(R.id.admin_home_vp);
        tab = findViewById(R.id.admin_home_tab);
        menu = findViewById(R.id.admin_home_menu);
        drawerLayout = findViewById(R.id.admin_home_drawer);
        logout = findViewById(R.id.admin_home_logout);
        web  = findViewById(R.id.admin_home_web);
        name = findViewById(R.id.admin_home_name_display);
        email = findViewById(R.id.admin_home_email_display);
        phone = findViewById(R.id.admin_home_phone_display);
        auth = findViewById(R.id.admin_home_auth_display);
        close = findViewById(R.id.admin_home_drawer_close);
        pb = findViewById(R.id.admin_home_pb_parent);

        Toast.makeText(getApplicationContext(),"Loading....",Toast.LENGTH_SHORT).show();

        //Initially

        menu.setEnabled(false);




        SharedPreferences preferences =getSharedPreferences("prefID", Context.MODE_PRIVATE);

        GlobalVar.phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String[] authority = getApplicationContext().getResources().getStringArray(R.array.departments);
                GlobalVar.info = dataSnapshot.child("admin").child(GlobalVar.phone).getValue(AdminInfo.class);
                GlobalVar.hostel = GlobalVar.info.hostel;

                name.setText(GlobalVar.info.name);
                email.setText(GlobalVar.info.email);
                phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                auth.setText(authority[Integer.valueOf(GlobalVar.info.auth)]);

                PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
                vp.setAdapter(adapter);


                tab.setupWithViewPager(vp);

                tab.getTabAt(0).setText("Home");
                tab.getTabAt(1).setText("Contacts");

                pb.setVisibility(View.GONE);
                menu.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(AdminHome.this, Main.class);
                startActivity(i);
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });








    }
}
