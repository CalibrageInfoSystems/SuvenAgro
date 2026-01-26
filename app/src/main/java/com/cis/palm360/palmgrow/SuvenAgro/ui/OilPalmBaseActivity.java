package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.RegistrationFlowScreen;


//Common Base activity
public abstract class OilPalmBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private ActionBar actionBar;
    private Toolbar toolbar;
    public android.widget.RelativeLayout baseLayout;
    public boolean onlyPopBackStack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_palm_base);

        initView();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Initialize();
    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,0,0, R.anim.exit_to_left
        );
        mFragmentTransaction.replace(android.R.id.content, fragment);
        mFragmentTransaction.addToBackStack(backStateName);
        mFragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, Bundle bundle) {
        String backStateName = fragment.getClass().getName();
        fragment.setArguments(bundle);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.enter_from_right,0,0, R.anim.exit_to_left
        );
        mFragmentTransaction.replace(android.R.id.content, fragment);
        mFragmentTransaction.addToBackStack(backStateName);
        mFragmentTransaction.commit();
    }

    public abstract void Initialize();

    public void setTile(final String title) {
        actionBar.setTitle(title);
        actionBar.invalidateOptionsMenu();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        baseLayout =  findViewById(R.id.content_oil_palm_base);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            onlyPopBackStack = true;
            getFragmentManager().popBackStack();
        }
        if (this instanceof RegistrationFlowScreen) {
//            DataManager.getInstance().deleteData(DataManager.FARMER_ADDRESS_DETAILS);
//            DataManager.getInstance().deleteData(DataManager.FARMER_PERSONAL_DETAILS);
        }
    }
}

