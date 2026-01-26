package com.cis.palm360.palmgrow.SuvenAgro.ui;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.PlotDetailsFragment;

//Initializing the Plot details screen
public class PlotDetailsScreen extends OilPalmBaseActivity {

    @Override
    public void Initialize() {
        setTile("Plot Details");
        replaceFragment(new PlotDetailsFragment());
    }

}
