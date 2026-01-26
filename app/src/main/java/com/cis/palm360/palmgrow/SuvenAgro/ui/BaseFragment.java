package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.RecomndFertilizerFragment;


//Common Base Fragment
public abstract class BaseFragment extends Fragment {

    private ActionBar actionBar;
    private View rootView;
    private Toolbar toolbar;
    public android.widget.RelativeLayout baseLayout;
    private Button btnRecommnd;
    private String mtitle;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    Bundle args = new Bundle();


    public BaseFragment() {

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base, container, false);
        initView();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Initialize();
//        buildEnterTransition();
        return rootView;
    }

    public abstract void Initialize();

    public void setTile(final String title) {
        mtitle=title;
        actionBar.setTitle(title);
        if(mtitle.contains(getString(R.string.fertilizerApplication)))
        {
            btnRecommnd.setVisibility(View.VISIBLE);}
        else {
            btnRecommnd.setVisibility(View.GONE);

    }
        actionBar.invalidateOptionsMenu();

    }


    private void initView() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        baseLayout = (RelativeLayout) rootView.findViewById(R.id.baseLayout);
        btnRecommnd=(Button) rootView.findViewById(R.id.btnRecommnd);
        btnRecommnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecomndFertilizerFragment mRecomNDScreen = new RecomndFertilizerFragment();
                String backStateName = mRecomNDScreen.getClass().getName();
                mRecomNDScreen.setArguments(args);
                mFragmentManager = getActivity().getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,0,0, R.anim.exit_to_left
                );
                mFragmentTransaction.replace(android.R.id.content, mRecomNDScreen);
                mFragmentTransaction.addToBackStack(backStateName);
                mFragmentTransaction.commit();
            }
        });
        baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Visibility buildEnterTransition() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(500);
        enterTransition.setSlideEdge(Gravity.RIGHT);
        return enterTransition;
    }
}
