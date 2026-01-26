package com.cis.palm360.palmgrow.SuvenAgro.transportservice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cis.palm360.palmgrow.SuvenAgro.R;

public class TransportActivity extends AppCompatActivity {
Button farmerBtn,vendorBtn;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        farmerBtn=findViewById(R.id.farmerBtn);
        vendorBtn=findViewById(R.id.vendorBtn);

    }

    public void nextScreen(View view) {

        int id = view.getId();

            if (id == R.id.farmerBtn) {
                TransportFarmerFragment transportFarmerFragment = new TransportFarmerFragment();

                replaceFragment(transportFarmerFragment);
            }

            if (id == R.id.vendorBtn) {
                TransportVendorFragment transportVendorFragment = new TransportVendorFragment();

                replaceFragment(transportVendorFragment);
            }
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
        );
        mFragmentTransaction.replace(android.R.id.content, fragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

}