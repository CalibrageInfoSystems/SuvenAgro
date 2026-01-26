package com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUiUtils;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.areaextension.UpdateUiListener;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.conversion.PalmDetailsEditListener;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.CropMaintenanceDocs;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Fertilizer;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cis.palm360.palmgrow.SuvenAgro.cropmaintenance.CommonUtilsNavigation.getKey;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.ArrayUtils;

//Used to recommend fertilizer during crop maintenance
public class FertilizerFragment extends Fragment implements View.OnClickListener, PalmDetailsEditListener, UpdateUiListener, OnPageChangeListener, OnLoadCompleteListener {
    private static final String LOG_TAG = FertilizerFragment.class.getName();
    private String Monthnumber;
    private ArrayList<Fertilizer> mFertilizerModelArray = new ArrayList<>();
    private View rootView;
    private Spinner sourceOfertilizerSpin,psourceOfertilizerSpin,bioFertilizerSpin,bioFertilizerSpin2, bioFertilizerSpin3,bioFertilizerSpin4,bioFertilizerSpin5,fertilizerProductNameSpin, uomSpin, frequencyOfApplicationSpin,fertilizerapplied,apptype,papptype,pfertilizerapplied;
    private EditText dosageGivenEdt, lastAppliedDateEdt,dosageGivenEdt1, lastAppliedDateEdt1,dosageGivenEdt2, lastAppliedDateEdt2,
            dosageGivenEdt3, lastAppliedDateEdt3,dosageGivenEdt4, lastAppliedDateEdt4,dosageGivenEdt5, lastAppliedDateEdt5,dosageGivenEdt6, lastAppliedDateEdt6, dosageGivenEdt7,lastAppliedDateEdt7,dosageGivenEdt8,lastAppliedDateEdt8,dosageGivenEdt31,dosageGivenEdt41,dosageGivenEdt51,
            dialog_otherEdt, otherEdt,comments,pcomments, sourceName,psourceName,Monthyear,pMonthyear;
    private LinearLayout otherLay, headerLL;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat inputFormatYYMMDD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ArrayList<String> months;
    Bundle args = new Bundle();
    private String applydate=null ;
    private String papplydate=null ;
    private String selectedbiofertilizer1;
    private String selectedbiofertilizer2;
    private String selectedbiofertilizer3;
    private String selectedbiofertilizer4;
    private String selectedbiofertilizer5;

    private ArrayList<Fertilizer> fertilizernonbiolastvisitdatamap;
    private ArrayList<Fertilizer> fertilizerbiolastvisitdatamap;

    private String Month =null;
    private  int Quater;
    private int Year ;
    private String pMonth =null;
    private  int pQuater;
    private int pYear ;
    private int cy,cm,cq,py,pm,pq,caly,pcaly;
    private long mindate,maxdate;
    private long ppmindate,ppmaxdate;
    private String pmindate,pmaxdate;
    private String cmindate,cmaxdate;
    private TextView tv3,tv4;
    int bioFertilizerId=0;
    int bioFertilizerId2=0;
    int bioFertilizerId3=0;
    int bioFertilizerId4=0;
    int bioFertilizerId5=0;
    private   String CMCode = "" ;
    int Biofertilizercount = 0;
    AdapterView.OnItemSelectedListener spinListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getId() ==  R.id.fertilizerProductNameSpin){
                    if (fertilizerProductNameSpin.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.other))) {
//                        dialog.show();
                        otherLay.setVisibility(View.VISIBLE);
                    } else {
                        otherLay.setVisibility(View.GONE);
                    }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private RecyclerView fertilizerList;
    private Button saveBtn,historyBtn, pdfBtn;
    private LinkedHashMap<String, String> fertilizerDataMap,bioDataMap, fertilizerTypeDataMap, uomDataMap, frequencyOfApplicationDataMap,IsAppliedDataMap,AppTypeDataMap, bioDataMap2;
    private DataAccessHandler dataAccessHandler;
    private GenericTypeAdapter fertilizerDataAdapter;
    private Context mContext;

    private UpdateUiListener updateUiListener;
    private Button complaintsBtn;
    private ActionBar actionBar;
    private Button btnRecommnd;
    private int cal = 99;
    Toolbar toolbar;

    File fileToDownLoad;
    CropMaintenanceDocs cropMaintenanceDocs;
    public FertilizerFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fertilizerdetailsfrag, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.fertilizerApplication));

        mContext = getActivity();
        dataAccessHandler = new DataAccessHandler(getActivity());

        cropMaintenanceDocs = (CropMaintenanceDocs) dataAccessHandler.getCMDocsData(Queries.getInstance().getFertilizerPDFfile(), 0);

        if (cropMaintenanceDocs != null) {

            fileToDownLoad = new File(CommonUtils.get3FFileRootPath() + "PalmGrow_CMDocs/" + cropMaintenanceDocs.getFileName() + cropMaintenanceDocs.getFileExtension());
        }

        setHasOptionsMenu(true);
        initViews();
        setViews();

        bindData();

        btnRecommnd=(Button) rootView.findViewById(R.id.btnRecommnd);
        btnRecommnd.setOnClickListener(v -> {
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
        });

        return rootView;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindData() {
        mFertilizerModelArray = (ArrayList<Fertilizer>) DataManager.getInstance().getDataFromManager(DataManager.FERTILIZER);
        if (mFertilizerModelArray != null) {
            int arraySize = mFertilizerModelArray.size();
            Log.e("====>216", mFertilizerModelArray.get(0).getBioFertilizerId() + "");
            fertilizerapplied.setSelection(mFertilizerModelArray.get(0).getIsFertilizerApplied() == null ? 0 : mFertilizerModelArray.get(0).getIsFertilizerApplied() == 1 ? 1 : 2);
            Monthyear.setText(mFertilizerModelArray.get(0).getApplicationMonth() == null ? "" : mFertilizerModelArray.get(0).getApplicationMonth());

            if (!TextUtils.isEmpty(mFertilizerModelArray.get(0).getApplicationMonth())){
                //Month = mFertilizerModelArray.get(0).getApplicationMonth();
                Month = mFertilizerModelArray.get(0).getApplicationMonth();
            }
            //Month = mFertilizerModelArray.get(0).getApplicationMonth();
            Log.d("Monthyear", mFertilizerModelArray.get(0).getApplicationMonth() + "");
            sourceName.setText(mFertilizerModelArray.get(0).getSourceName() == null ? "" : mFertilizerModelArray.get(0).getSourceName());
            Log.d("sourceOfertilizerSpin", mFertilizerModelArray.get(0).getFertilizersourcetypeid() + "");
            Log.d("apptype", mFertilizerModelArray.get(0).getApplicationType() + "");
            sourceOfertilizerSpin.setSelection(mFertilizerModelArray.get(0).getFertilizersourcetypeid() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(fertilizerDataMap, mFertilizerModelArray.get(0).getFertilizersourcetypeid()));
            comments.setText(mFertilizerModelArray.get(0).getComments() == null ? "" : mFertilizerModelArray.get(0).getComments());

            if (mFertilizerModelArray.get(0).getApplicationType() != null) {
                String apptypedesc = mFertilizerModelArray.get(0).getApplicationType();
                Log.d("apptype", apptypedesc);
                //int apptypeid = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getapptypeId(apptypedesc));
                apptype.setSelection(mFertilizerModelArray.get(0).getApplicationType() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(AppTypeDataMap, Integer.parseInt(apptypedesc)));
            }

            Map<Integer, List<Fertilizer>> biofertilizers = new HashMap<>();
            for (Fertilizer fertilizer : mFertilizerModelArray) {
                biofertilizers.computeIfAbsent(fertilizer.getFertilizerid(), k -> new ArrayList<>()).add(fertilizer);
            }
            int specificFertilizerId = 232;
            if (biofertilizers.containsKey(specificFertilizerId)) {
                List<Fertilizer> specificFertilizers = biofertilizers.get(specificFertilizerId);
                Biofertilizercount = specificFertilizers.size();

                System.out.println("Same Fertilizer ID: " + specificFertilizerId + " - Number of Biofertilizers: " + Biofertilizercount);
                for (Fertilizer biofertilizerData : specificFertilizers) {
                    System.out.println("   Fertilizer ID: " + biofertilizerData.getFertilizerid() + " - Biofertilizer ID: " + biofertilizerData.getBioFertilizerId());
                }
            } else {
                System.out.println("Fertilizer ID " + specificFertilizerId + " not found in the list.");
            }
            for (int i = 0; i < arraySize; i++) {
                if (Biofertilizercount == 5) {
                    Log.e("====>232", mFertilizerModelArray.get(arraySize - 5).getBioFertilizerId() + "");

                    // Setting bioFertilizerSpin
                    if (mFertilizerModelArray.get(arraySize - 5).getBioFertilizerId() != null) {
                        bioFertilizerSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 5).getBioFertilizerId()));
                        Double dosage5 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 5).getBioFertilizerId());
                        if (dosage5 != null) {
                            dosageGivenEdt7.setText(String.valueOf(dosage5));
                        }
                    }

                    // Setting bioFertilizerSpin2
                    if (mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId() != null) {
                        bioFertilizerSpin2.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId()));
                        Double dosage6 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId());
                        if (dosage6 != null) {
                            dosageGivenEdt8.setText(String.valueOf(dosage6));
                        }
                    }

                    // Setting bioFertilizerSpin3
                    if (mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId() != null) {
                        bioFertilizerSpin3.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId()));
                        Double dosage7 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId());
                        if (dosage7 != null) {
                            dosageGivenEdt31.setText(String.valueOf(dosage7)); // Assuming a dosage EditText for bioFertilizerSpin3
                        }
                    }

                    // Setting bioFertilizerSpin4
                    if (mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId() != null) {
                        bioFertilizerSpin4.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId()));
                        Double dosage8 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId());
                        if (dosage8 != null) {
                            dosageGivenEdt41.setText(String.valueOf(dosage8)); // Assuming a dosage EditText for bioFertilizerSpin4
                        }
                    }

                    // Setting bioFertilizerSpin5
                    if (mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId() != null) {
                        bioFertilizerSpin5.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId()));
                        Double dosage9 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId());
                        if (dosage9 != null) {
                            dosageGivenEdt51.setText(String.valueOf(dosage9)); // Assuming a dosage EditText for bioFertilizerSpin5
                        }
                    }

                } else if (Biofertilizercount == 4) {
                    // Setting bioFertilizerSpin
                    if (mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId() != null) {
                        bioFertilizerSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId()));
                        Double dosage5 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 4).getBioFertilizerId());
                        if (dosage5 != null) {
                            dosageGivenEdt7.setText(String.valueOf(dosage5));
                        }
                    }

                    // Setting bioFertilizerSpin2
                    if (mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId() != null) {
                        bioFertilizerSpin2.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId()));
                        Double dosage6 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId());
                        if (dosage6 != null) {
                            dosageGivenEdt8.setText(String.valueOf(dosage6));
                        }
                    }

                    // Setting bioFertilizerSpin3
                    if (mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId() != null) {
                        bioFertilizerSpin3.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId()));
                        Double dosage7 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId());
                        if (dosage7 != null) {
                            dosageGivenEdt31.setText(String.valueOf(dosage7));
                        }
                    }

                    // Setting bioFertilizerSpin4
                    if (mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId() != null) {
                        bioFertilizerSpin4.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId()));
                        Double dosage8 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId());
                        if (dosage8 != null) {
                            dosageGivenEdt41.setText(String.valueOf(dosage8));
                        }
                    }

                } else if (Biofertilizercount == 3) {
                    // Setting bioFertilizerSpin
                    if (mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId() != null) {
                        bioFertilizerSpin.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId()));
                        Double dosage5 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 3).getBioFertilizerId());
                        if (dosage5 != null) {
                            dosageGivenEdt7.setText(String.valueOf(dosage5));
                        }
                    }

                    // Setting bioFertilizerSpin2
                    if (mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId() != null) {
                        bioFertilizerSpin2.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId()));
                        Double dosage6 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 2).getBioFertilizerId());
                        if (dosage6 != null) {
                            dosageGivenEdt8.setText(String.valueOf(dosage6));
                        }
                    }

                    // Setting bioFertilizerSpin3
                    if (mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId() != null) {
                        bioFertilizerSpin3.setSelection(CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId()));
                        Double dosage7 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize - 1).getBioFertilizerId());
                        if (dosage7 != null) {
                            dosageGivenEdt31.setText(String.valueOf(dosage7));
                        }
                    }
                }
                    else if(Biofertilizercount == 2){
                    Log.e("====>232", mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() + "");

                    if (mFertilizerModelArray.get(arraySize-2).getBioFertilizerId() != null) {
                        Log.e("====>232", mFertilizerModelArray.get(arraySize-2).getBioFertilizerId() + "");
                        bioFertilizerSpin.setSelection(mFertilizerModelArray.get(arraySize-2).getBioFertilizerId() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize-2).getBioFertilizerId()));
                        Double dosage8 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize-2).getBioFertilizerId());
                        if (dosage8 != null) {
                            dosageGivenEdt7.setText(String.valueOf(dosage8));
                        }
                    }
                    Log.e("====>273", mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() + "");

                    if (mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() != null) {
                        Log.e("====>276", mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() + "");
                        bioFertilizerSpin2.setSelection(mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize-1).getBioFertilizerId()));
                        Double dosage9 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize-1).getBioFertilizerId());
                        if (dosage9 != null) {
                            dosageGivenEdt8.setText(String.valueOf(dosage9));
                        }
                    }
                }
                else if(Biofertilizercount == 1){
                    Log.e("====>232", mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() + "");

                    if (mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() != null) {
                        Log.e("====>232", mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() + "");
                        bioFertilizerSpin.setSelection(mFertilizerModelArray.get(arraySize-1).getBioFertilizerId() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(bioDataMap, mFertilizerModelArray.get(arraySize-1).getBioFertilizerId()));
                        Double dosage8 = getDosageForBioId(mFertilizerModelArray, mFertilizerModelArray.get(arraySize-1).getBioFertilizerId());
                        if (dosage8 != null) {
                            dosageGivenEdt7.setText(String.valueOf(dosage8));
                        }
                    }
                }
            }
            Double dosage1 = getDosageForFertilizerId(mFertilizerModelArray, 58);
            if (dosage1 != 0.0) {
                dosageGivenEdt5.setText(String.valueOf(dosage1));
            }

            Double dosage2 = getDosageForFertilizerId(mFertilizerModelArray, 59);
            if (dosage2 != 0.0) {
                dosageGivenEdt.setText(String.valueOf(dosage2));
            }

            Double dosage3 = getDosageForFertilizerId(mFertilizerModelArray, 60);
            if (dosage3 != 0.0) {
                dosageGivenEdt2.setText(String.valueOf(dosage3));
            }

            Double dosage4 = getDosageForFertilizerId(mFertilizerModelArray, 61);
            if (dosage4 != 0.0) {
                dosageGivenEdt1.setText(String.valueOf(dosage4));
            }

            Double dosage5 = getDosageForFertilizerId(mFertilizerModelArray, 63);
            if (dosage5 != 0.0) {
                dosageGivenEdt3.setText(String.valueOf(dosage5));
            }

            Double dosage6 = getDosageForFertilizerId(mFertilizerModelArray, 64);
            if (dosage6 != 0.0) {
                dosageGivenEdt4.setText(String.valueOf(dosage6));
            }

            Double dosage7 = getDosageForFertilizerId(mFertilizerModelArray, 229);
            if (dosage7 != 0.0) {
                dosageGivenEdt6.setText(String.valueOf(dosage7));
            }
        }
    }



    private double getDosageForBioId(List<Fertilizer> mFertilizerModelArray, Integer bioFertilizerId) {
        for (Fertilizer model : mFertilizerModelArray) {
            Log.e("==========>model", model.getBioFertilizerId() + "" + bioFertilizerId);
            if (model.getBioFertilizerId() != null && model.getBioFertilizerId().equals(bioFertilizerId)) {
                Log.e("==========>model2", model.getBioFertilizerId() + "" + bioFertilizerId);
                return model.getDosage();
            }
        }
        return 0; // Return a default value if the fertilizer ID is not found.
    }
    public boolean contains(final int[] array, final int key) {
        return ArrayUtils.contains(array, key);
    }

    private double getDosageForFertilizerId(List<Fertilizer> mFertilizerModelArray, int fertilizerId) {
        for (Fertilizer model : mFertilizerModelArray) {
            if (model.getFertilizerid() == fertilizerId) {
                Double dosage = model.getDosage();
                return dosage != null ? dosage : 0.0;
            }
        }
        return 0.0;
    }



    private void initViews() {
        sourceOfertilizerSpin = (Spinner) rootView.findViewById(R.id.sourceOfertilizerSpin);
        //    psourceOfertilizerSpin = (Spinner) rootView.findViewById(R.id.psourceOfertilizerSpin);
        apptype = (Spinner) rootView.findViewById(R.id.apptype);
        //   papptype = (Spinner) rootView.findViewById(R.id.papptype);
        fertilizerapplied = (Spinner) rootView.findViewById(R.id.FertilizerApplied);
        //   pfertilizerapplied = (Spinner) rootView.findViewById(R.id.pFertilizerApplied);
        fertilizerProductNameSpin = (Spinner) rootView.findViewById(R.id.fertilizerProductNameSpin);
        uomSpin = (Spinner) rootView.findViewById(R.id.uomSpin);
        // frequencyOfApplicationSpin = (Spinner) rootView.findViewById(R.id.frequencyOfApplicationSpin);
        dosageGivenEdt = (EditText) rootView.findViewById(R.id.dosageGivenEdt);
        dosageGivenEdt1 = (EditText) rootView.findViewById(R.id.dosageGivenEdt1);
        dosageGivenEdt2 = (EditText) rootView.findViewById(R.id.dosageGivenEdt2);
        dosageGivenEdt3 = (EditText) rootView.findViewById(R.id.dosageGivenEdt3);
        dosageGivenEdt4 = (EditText) rootView.findViewById(R.id.dosageGivenEdt4);
        dosageGivenEdt5 = (EditText) rootView.findViewById(R.id.dosageGivenEdt5);
        dosageGivenEdt6 = (EditText) rootView.findViewById(R.id.dosageGivenEdt6);
        dosageGivenEdt7 = (EditText) rootView.findViewById(R.id.dosageGivenEdt7);
        dosageGivenEdt8 = (EditText) rootView.findViewById(R.id.dosageGivenEdt8);
        bioFertilizerSpin = (Spinner) rootView.findViewById(R.id.bioFertilizerSpin);
        bioFertilizerSpin2 = (Spinner) rootView.findViewById(R.id.bioFertilizerSpin2);
        bioFertilizerSpin3 = (Spinner) rootView.findViewById(R.id.bioFertilizerSpin3);
        bioFertilizerSpin4 = (Spinner) rootView.findViewById(R.id.bioFertilizerSpin4);
        bioFertilizerSpin5 = (Spinner) rootView.findViewById(R.id.bioFertilizerSpin5);
        dosageGivenEdt31 = (EditText) rootView.findViewById(R.id.dosageGivenEdt31);
        dosageGivenEdt41 = (EditText) rootView.findViewById(R.id.dosageGivenEdt41);
        dosageGivenEdt51 = (EditText) rootView.findViewById(R.id.dosageGivenEdt51);
//        lastAppliedDateEdt3 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt3);
//        lastAppliedDateEdt4 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt4);
//        lastAppliedDateEdt5 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt5);
//        lastAppliedDateEdt6 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt6);
//        lastAppliedDateEdt7 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt7);
//        lastAppliedDateEdt8 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt8);
        comments = (EditText) rootView.findViewById(R.id.commentsTv);
        //  pcomments = (EditText) rootView.findViewById(R.id.pcommentsTv);
        sourceName = (EditText) rootView.findViewById(R.id.nameofshopEv);
        //    psourceName = (EditText) rootView.findViewById(R.id.pnameofshopEv);
        Monthyear = (EditText) rootView.findViewById(R.id.Monthyear);
        //     pMonthyear = (EditText) rootView.findViewById(R.id.pMonthyear);
        //  lastAppliedDateEdt6 = (EditText) rootView.findViewById(R.id.lastAppliedDateEdt6);
        otherEdt = (EditText) rootView.findViewById(R.id.otherEdt);
        otherLay = (LinearLayout) rootView.findViewById(R.id.otherLay);
        fertilizerList = (RecyclerView) rootView.findViewById(R.id.fertilizerList);
        saveBtn = (Button) rootView.findViewById(R.id.saveBtn);
        historyBtn = (Button) rootView.findViewById(R.id.historyBtn);
        pdfBtn = (Button) rootView.findViewById(R.id.pdfBtn);
        complaintsBtn = (Button) rootView.findViewById(R.id.complaintsBtn);
        complaintsBtn.setVisibility(View.GONE);
        complaintsBtn.setEnabled(false);
        headerLL = (LinearLayout) rootView.findViewById(R.id.headerLL);
//        tv3 =  (TextView) rootView.findViewById(R.id.tv3);
//        tv4 =  (TextView) rootView.findViewById(R.id.id4);

        tv3 =  (TextView) rootView.findViewById(R.id.currentqtr);
        tv4 =  (TextView) rootView.findViewById(R.id.prvsqtr);


        dosageGivenEdt.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt1.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt2.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt3.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt4.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt5.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt6.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt7.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt8.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt31.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt41.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });
        dosageGivenEdt51.setFilters(new InputFilter[]{
                CommonUiUtils.getDecimalDigitsInputFilter(10 , 2, getContext())
                });


        if (cropMaintenanceDocs != null) {

            if (null != fileToDownLoad && fileToDownLoad.exists()) {

                pdfBtn.setVisibility(View.VISIBLE);

            } else {
                pdfBtn.setVisibility(View.GONE);
            }
        }

        Calendar calendar = Calendar.getInstance();
        cy = calendar.get(Calendar.YEAR);
        cm = calendar.get(Calendar.MONTH);

        if(cm>=0 && cm<=2){
            cy=cy-1;

            cq=4;
            py=cy;
            pq=3;
            caly=cy+1;
            pcaly=py;
            calendar.clear();
            calendar.set(caly, 0, 1);
            mindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(caly, 2, 31);
            maxdate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 9, 1);
            ppmindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 11, 31);
            ppmaxdate = calendar.getTimeInMillis();
            pm=8;
            pmindate=String.valueOf(pcaly)+"-10-01";
            pmaxdate=String.valueOf(pcaly)+"-12-31";
            cmindate=String.valueOf(caly)+"-01-01";
            cmaxdate=String.valueOf(caly)+"-03-31";
        }else if (cm>=3 && cm<=5){
            cq=1;
            py=cy-1;
            pq=4;
            caly=cy;
            pcaly=py+1;
            calendar.clear();
            calendar.set(caly, 3, 1);
            mindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(caly, 5, 30);
            maxdate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 0, 1);
            ppmindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 2, 31);
            ppmaxdate = calendar.getTimeInMillis();
            pm=0;
            pmindate=String.valueOf(pcaly)+"-01-01";
            pmaxdate=String.valueOf(pcaly)+"-03-31";
            cmindate=String.valueOf(caly)+"-04-01";
            cmaxdate=String.valueOf(caly)+"-06-30";
        }else if (cm>=6 && cm<=8){
            cq=2;
            py=cy;
            pq=1;
            caly=cy;
            pcaly=py;
            calendar.clear();
            calendar.set(caly, 6, 1);
            mindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(caly, 8, 30);
            maxdate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 3, 1);
            ppmindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 5, 30);
            ppmaxdate = calendar.getTimeInMillis();
            pm=3;
            pmindate=String.valueOf(pcaly)+"-04-01";
            pmaxdate=String.valueOf(pcaly)+"-06-30";
            cmindate=String.valueOf(caly)+"-07-01";
            cmaxdate=String.valueOf(caly)+"-09-30";
        }else if (cm>=9 && cm<=11){
            cq=3;
            py=cy;
            pq=2;
            caly=cy;
            pcaly=py;
            calendar.clear();
            calendar.set(caly, 9, 1);
            mindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(caly, 11, 31);
            maxdate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 6, 1);
            ppmindate = calendar.getTimeInMillis();

            calendar.clear();
            calendar.set(pcaly, 8, 30);
            ppmaxdate = calendar.getTimeInMillis();
            pm=6;
            pmindate=String.valueOf(pcaly)+"-07-01";
            pmaxdate=String.valueOf(pcaly)+"-09-30";
            cmindate=String.valueOf(caly)+"-10-01";
            cmaxdate=String.valueOf(caly)+"-12-31";
        }
        tv3.setText(String.valueOf(cq)+"-"+"Qtr"+"-"+String.valueOf(cy));
        tv3.setText(String.valueOf(cq)+"-"+"Qtr"+"-"+String.valueOf(cy));


        months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        //complaintsBtn.setVisibility((CommonUiUtils.isComplaintsDataEntered()) ? View.GONE : View.VISIBLE);
        Monthyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                        .getInstance(cm, caly,mindate,maxdate);

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {

                        // do something

                        Month = months.get(monthOfYear);
                        Year =cy;
                        Quater = cq;
                        Monthnumber =String.valueOf(monthOfYear+1).length()==1? "0"+String.valueOf(monthOfYear+1) : String.valueOf(monthOfYear+1);
                        Monthyear.setText(Month+"-"+String.valueOf(year));
                        applydate = String.valueOf(year)+"-"+Monthnumber+"-01 00:00:00";
                        CommonConstants.fertilizerapplydate = applydate;

                        //Toast.makeText(getActivity().getApplicationContext(),String.valueOf(year)+"-"+String.valueOf(monthOfYear),Toast.LENGTH_SHORT);
                    }
                });

                dialogFragment.show(getActivity().getSupportFragmentManager(), null);


            }

        });

//        pMonthyear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
//                        .getInstance(pm, pcaly,ppmindate,ppmaxdate);
//
//                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(int year, int monthOfYear) {
//
//                        // do something
//
//                        pMonth = months.get(monthOfYear);
//                        pYear=cy;
//                        pQuater = pq;
//                        Monthnumber =String.valueOf(monthOfYear+1).length()==1? "0"+String.valueOf(monthOfYear+1) : String.valueOf(monthOfYear+1);
//                        pMonthyear.setText(pMonth+"-"+String.valueOf(year));
//                        papplydate = String.valueOf(year)+"-"+Monthnumber+"-01 00:00:00";
//
//                        //Toast.makeText(getActivity().getApplicationContext(),String.valueOf(year)+"-"+String.valueOf(monthOfYear),Toast.LENGTH_SHORT);
//                    }
//                });
//
//                dialogFragment.show(getActivity().getSupportFragmentManager(), null);
//
//
//            }
//
//        });


        complaintsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putBoolean(CommonConstants.KEY_NEW_COMPLAINT, true);
                ComplaintDetailsFragment complaintDetailsFragment = new ComplaintDetailsFragment();
                complaintDetailsFragment.setArguments(dataBundle);
                complaintDetailsFragment.setUpdateUiListener(FertilizerFragment.this);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, complaintDetailsFragment).addToBackStack(null)
                        .commit();
            }
        });
    }

    //    private void fetchprevqtr(){
//        int yr=py;
//        int qr=pq;
//
//        DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
//
//
//        CMCode   =   dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrCM(qr,yr,CommonConstants.PLOT_CODE,pmindate,pmaxdate));
//
//        if(CMCode!=null){
//            CommonConstants.Prev_Fertilizer_CMD=CMCode;
//            pMonthyear.setEnabled(false);
//            psourceName.setEnabled(false);
//            psourceOfertilizerSpin.setEnabled(false);
//            pfertilizerapplied.setEnabled(false);
//            pcomments.setEnabled(false);
//            papptype.setEnabled(false);
//            lastAppliedDateEdt.setEnabled(false);
//            lastAppliedDateEdt1.setEnabled(false);
//            lastAppliedDateEdt2.setEnabled(false);
//            lastAppliedDateEdt3.setEnabled(false);
//            lastAppliedDateEdt4.setEnabled(false);
//            lastAppliedDateEdt5.setEnabled(false);
//            lastAppliedDateEdt6.setEnabled(false);
//            lastAppliedDateEdt7.setEnabled(false);
//            lastAppliedDateEdt8.setEnabled(false);
//
//
//            lastAppliedDateEdt.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,59,pmindate,pmaxdate)));
//            lastAppliedDateEdt1.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,61,pmindate,pmaxdate)));
//            lastAppliedDateEdt2.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,60,pmindate,pmaxdate)));
//            lastAppliedDateEdt3.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,63,pmindate,pmaxdate)));
//            lastAppliedDateEdt4.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,64,pmindate,pmaxdate)));
//            lastAppliedDateEdt5.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,58,pmindate,pmaxdate)));
//            lastAppliedDateEdt6.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFertilizerPrevQtrDosage(qr,yr,CMCode,229,pmindate,pmaxdate)));
//            lastAppliedDateEdt7.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getBioFertilizerPrevQtrDosage(qr,yr,CMCode,232,bioFertilizerId,pmindate,pmaxdate)));
//            lastAppliedDateEdt8.setText(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getBioFertilizerPrevQtrDosage(qr,yr,CMCode,232,bioFertilizerId2,pmindate,pmaxdate)));
//            List<Fertilizer> fertilizerList = (List<Fertilizer>) dataAccessHandler.getFertilizerPrevQtrdtls(Queries.getInstance().getFertilizerPrevQtrdtls(qr,yr,CMCode,pmindate,pmaxdate),1);
//            if(fertilizerList.size()>0){
//
//                //psourceName.setText(fertilizerList.get(0).getSourceName());
//
//                if (fertilizerList.get(0).getSourceName().isEmpty() || fertilizerList.get(0).getSourceName() == null){
//
//                //    psourceName.setText(fertilizerList.get(0).getSourceName());
//                }else{
//
//                //    psourceName.setText("");
//                }
//
//                //pcomments.setText(fertilizerList.get(0).getComments());
//
//                if (fertilizerList.get(0).getComments().isEmpty() || fertilizerList.get(0).getComments() == null){
//
//                    pcomments.setText(fertilizerList.get(0).getComments());
//                }else{
//
//                    pcomments.setText("");
//                }
//
//                Set<String> keys = AppTypeDataMap.keySet();
//                int p=1;
//                if(fertilizerList.get(0).getApplicationType()!=null && !fertilizerList.get(0).getApplicationType().equals("null"))
//                {
//                    for(String k:keys){
//                        if(k.equals(fertilizerList.get(0).getApplicationType()))
//                        {
//                            break;
//                        }
//                        p++;
//                    }
//                    papptype.setSelection(p);
//                }else{
//                    papptype.setSelection(0);
//                }
//
//                if(fertilizerList.get(0).getFertilizersourcetypeid()!=null) {
//                    keys = fertilizerDataMap.keySet();
//                    p = 1;
//                    for (String k : keys) {
//                        if (k.equals(fertilizerList.get(0).getFertilizersourcetypeid().toString())) {
//                            break;
//                        }
//                        p++;
//                    }
//                    psourceOfertilizerSpin.setSelection(p);
//                }else
//                    psourceOfertilizerSpin.setSelection(0);
//
//
//                keys = IsAppliedDataMap.keySet();
//                p = 1;
//                for (String k : keys) {
//                    if (k.equals(String.valueOf(fertilizerList.get(0).getIsFertilizerApplied()))) {
//                        break;
//                    }
//                    p++;
//                }
//                pfertilizerapplied.setSelection(p);
//
//                if (fertilizerList.get(0).getApplicationMonth().contains("null") || fertilizerList.get(0).getApplicationMonth().isEmpty()){
//
//                   // pMonthyear.setText("");
//                }else{
//
//                 //   pMonthyear.setText(fertilizerList.get(0).getApplicationMonth()+"-"+String.valueOf(fertilizerList.get(0).getApplicationYear()));
//                }
//
//
//
//            }
//        }
//        else {
////            pMonthyear.setEnabled(true);
////            psourceName.setEnabled(true);
////            psourceOfertilizerSpin.setEnabled(true);
////            pfertilizerapplied.setEnabled(true);
////            pcomments.setEnabled(true);
////            papptype.setEnabled(true);
////            lastAppliedDateEdt.setEnabled(true);
////            lastAppliedDateEdt1.setEnabled(true);
////            lastAppliedDateEdt2.setEnabled(true);
////            lastAppliedDateEdt3.setEnabled(true);
////            lastAppliedDateEdt4.setEnabled(true);
////            lastAppliedDateEdt5.setEnabled(true);
////            lastAppliedDateEdt6.setEnabled(true);
////            lastAppliedDateEdt7.setEnabled(true);
////            lastAppliedDateEdt8.setEnabled(true);
//
//        }
//
//    }
    private void setViews() {

        // updateLabel();
        saveBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        pdfBtn.setOnClickListener(this);
//        lastAppliedDateEdt.setOnClickListener(this);
//        lastAppliedDateEdt1.setOnClickListener(this);
//        lastAppliedDateEdt2.setOnClickListener(this);
//        lastAppliedDateEdt3.setOnClickListener(this);
//        lastAppliedDateEdt4.setOnClickListener(this);
//        lastAppliedDateEdt5.setOnClickListener(this);
//        lastAppliedDateEdt6.setOnClickListener(this);
//        lastAppliedDateEdt7.setOnClickListener(this);
//        lastAppliedDateEdt8.setOnClickListener(this);

        /*fertilizerProductNameSpin.setOnItemSelectedListener(spinListener);*/

        IsAppliedDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getYesNo());
        bioDataMap=dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("66"));
        AppTypeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("65"));
        fertilizerDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("33"));
        fertilizerTypeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getLookUpData("23"));
        frequencyOfApplicationDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeCdDmtData("30"));
        uomDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getUOM());

        bioFertilizerSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(),"Bio Fertilizer1",bioDataMap));

        bioFertilizerSpin2.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(),"Bio Fertilizer2",bioDataMap));
        bioFertilizerSpin3.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(),"Bio Fertilizer3",bioDataMap));

        bioFertilizerSpin4.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(),"Bio Fertilizer4",bioDataMap));
        bioFertilizerSpin5.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(),"Bio Fertilizer5",bioDataMap));



        sourceOfertilizerSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Source of Fertilizer", fertilizerDataMap));
        //  psourceOfertilizerSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Source of Fertilizer", fertilizerDataMap));

        apptype.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Application Type", AppTypeDataMap));
        //  papptype.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Application Type", AppTypeDataMap));

        fertilizerapplied.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Fertilizer Applied", IsAppliedDataMap));
        // pfertilizerapplied.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Fertilizer Applied", IsAppliedDataMap));
        //fertilizerProductNameSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "fertilizer Product Name", fertilizerTypeDataMap));
        //uomSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Select UOM", uomDataMap));
        //  frequencyOfApplicationSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Select Frequency of Application / yr", frequencyOfApplicationDataMap));


        fertilizerapplied.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (fertilizerapplied.getSelectedItemPosition() == 2){
                    Monthyear.setText("");
                    sourceName.setText("");
                    sourceOfertilizerSpin.setSelection(0);
                    comments.setText("");
                    apptype.setSelection(0);
                    dosageGivenEdt.setText("");
                    dosageGivenEdt1.setText("");
                    dosageGivenEdt2.setText("");
                    dosageGivenEdt3.setText("");
                    dosageGivenEdt4.setText("");
                    dosageGivenEdt5.setText("");
                    dosageGivenEdt6.setText("");
                    dosageGivenEdt7.setText("");
                    dosageGivenEdt8.setText("");
                    dosageGivenEdt31.setText("");
                    dosageGivenEdt41.setText("");
                    Monthyear.setEnabled(false);
                    sourceName.setEnabled(false);
                    //   sourceOfertilizerSpin.setSelection(0);
                    sourceOfertilizerSpin.setEnabled(false);
                    comments.setEnabled(false);
                    //  apptype.setSelection(0);
                    apptype.setEnabled(false);
                    dosageGivenEdt.setEnabled(false);
                    dosageGivenEdt1.setEnabled(false);
                    dosageGivenEdt2.setEnabled(false);
                    dosageGivenEdt3.setEnabled(false);
                    dosageGivenEdt4.setEnabled(false);
                    dosageGivenEdt5.setEnabled(false);
                    dosageGivenEdt6.setEnabled(false);
                    dosageGivenEdt7.setEnabled(false);
                    dosageGivenEdt8.setEnabled(false);
                    dosageGivenEdt31.setEnabled(false);
                    dosageGivenEdt41.setEnabled(false);
                    dosageGivenEdt51.setEnabled(false);
                    bioFertilizerSpin3.setSelection(0);
                    bioFertilizerSpin4.setSelection(0);
                    bioFertilizerSpin5.setSelection(0);
                    bioFertilizerSpin.setSelection(0);
                    bioFertilizerSpin2.setSelection(0);


                }

                if (fertilizerapplied.getSelectedItemPosition() == 1){

                    Monthyear.setEnabled(true);
                    sourceName.setEnabled(true);
                    //  sourceOfertilizerSpin.setSelection(0);
                    sourceOfertilizerSpin.setEnabled(true);
                    comments.setEnabled(true);
                    //    apptype.setSelection(0);
                    apptype.setEnabled(true);
                    dosageGivenEdt.setEnabled(true);
                    dosageGivenEdt1.setEnabled(true);
                    dosageGivenEdt2.setEnabled(true);
                    dosageGivenEdt3.setEnabled(true);
                    dosageGivenEdt4.setEnabled(true);
                    dosageGivenEdt5.setEnabled(true);
                    dosageGivenEdt6.setEnabled(true);
                    dosageGivenEdt7.setEnabled(true);
                    dosageGivenEdt8.setEnabled(true);
                    dosageGivenEdt31.setEnabled(true);
                    dosageGivenEdt41.setEnabled(true);
                    dosageGivenEdt51.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bioFertilizerSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (bioFertilizerSpin.getSelectedItemPosition() != 0) {
                    bioFertilizerId = Integer.parseInt(bioDataMap.keySet().toArray()[position - 1].toString());
                    selectedbiofertilizer1 = bioFertilizerSpin.getSelectedItem().toString();
                    Log.d("Selected Item1", selectedbiofertilizer1);

                    // Check if selectedbiofertilizer1 matches any other selected fertilizers
                    if (isDuplicateSelected(selectedbiofertilizer1, selectedbiofertilizer2, selectedbiofertilizer3, selectedbiofertilizer4, selectedbiofertilizer5)) {
//                        Toast.makeText(mContext, "Duplicate fertilizer selection not allowed", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Duplicate fertilizer selection not allowed", mContext, 1);
                        bioFertilizerSpin.setSelection(0); // Reset selection
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bioFertilizerSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (bioFertilizerSpin2.getSelectedItemPosition() != 0) {
                    bioFertilizerId2 = Integer.parseInt(bioDataMap.keySet().toArray()[position - 1].toString());
                    selectedbiofertilizer2 = bioFertilizerSpin2.getSelectedItem().toString();
                    Log.d("Selected Item2", selectedbiofertilizer2);
                    Log.d("Selected Item Id 2", bioFertilizerId2 + "");
                    if (isDuplicateSelected(selectedbiofertilizer2, selectedbiofertilizer1, selectedbiofertilizer3, selectedbiofertilizer4, selectedbiofertilizer5)) {
//                        Toast.makeText(mContext, "Duplicate fertilizer selection not allowed", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Duplicate fertilizer selection not allowed", mContext, 1);

                        bioFertilizerSpin2.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bioFertilizerSpin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (bioFertilizerSpin3.getSelectedItemPosition() != 0) {
                    bioFertilizerId3 = Integer.parseInt(bioDataMap.keySet().toArray()[position - 1].toString());
                    selectedbiofertilizer3 = bioFertilizerSpin3.getSelectedItem().toString();
                    Log.d("Selected Item3", selectedbiofertilizer3);
                    Log.d("Selected Item Id3", bioFertilizerId3 + "");


                    if (isDuplicateSelected(selectedbiofertilizer3, selectedbiofertilizer1, selectedbiofertilizer2, selectedbiofertilizer4, selectedbiofertilizer5)) {
//                        Toast.makeText(mContext, "Duplicate fertilizer selection not allowed", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Duplicate fertilizer selection not allowed", mContext, 1);

                        bioFertilizerSpin3.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bioFertilizerSpin4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (bioFertilizerSpin4.getSelectedItemPosition() != 0) {
                    bioFertilizerId4 = Integer.parseInt(bioDataMap.keySet().toArray()[position - 1].toString());
                    selectedbiofertilizer4 = bioFertilizerSpin4.getSelectedItem().toString();
                    Log.d("Selected Item4", selectedbiofertilizer4);
                    Log.d("Selected Item Id 4", bioFertilizerId4+ "");
                    if (isDuplicateSelected(selectedbiofertilizer4, selectedbiofertilizer1, selectedbiofertilizer2, selectedbiofertilizer3, selectedbiofertilizer5)) {
//                        Toast.makeText(mContext, "Duplicate fertilizer selection not allowed", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Duplicate fertilizer selection not allowed", mContext, 1);

                        bioFertilizerSpin4.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bioFertilizerSpin5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (bioFertilizerSpin5.getSelectedItemPosition() != 0) {
                    bioFertilizerId5 = Integer.parseInt(bioDataMap.keySet().toArray()[position - 1].toString());
                    selectedbiofertilizer5 = bioFertilizerSpin5.getSelectedItem().toString();
                    Log.d("Selected Item5", selectedbiofertilizer5);
                    Log.d("Selected Item Id 5", bioFertilizerId5+ "");
                    if (isDuplicateSelected(selectedbiofertilizer5, selectedbiofertilizer1, selectedbiofertilizer2, selectedbiofertilizer3, selectedbiofertilizer4)) {
//                        Toast.makeText(mContext, "Duplicate fertilizer selection not allowed", Toast.LENGTH_SHORT).show();
                        UiUtils.showCustomToastMessage("Duplicate fertilizer selection not allowed", mContext, 1);

                        bioFertilizerSpin5.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private boolean isDuplicateSelected(String selected, String... others) {
        for (String other : others) {
            if (selected != null && selected.equals(other)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

            if (id == R.id.saveBtn) {

                mFertilizerModelArray = new ArrayList<Fertilizer>();
                DataManager.getInstance().deleteData(DataManager.FERTILIZER);


                // if(spinnerSelect(fertilizerapplied, "Fertilizer Applied", mContext)){
//                if (DataManager.getInstance().getDataFromManager(DataManager.RECMND_FERTILIZER) != null)
                if (validateUI()) {
                    Date date;
                    String lastAppliedDate = "";
                    String outputDate;

                    for (int k = 0; k < 12; k++) {
                        Fertilizer mFertilizerModel = new Fertilizer();
                        Fertilizer mFertilizerModelp = new Fertilizer();
                        switch (k) {
                            case 0:
                                if (dosageGivenEdt.getText().toString().length() > 0 && dosageGivenEdt.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(59);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                            case 1:
                                if (dosageGivenEdt1.getText().toString().length() > 0 && dosageGivenEdt1.getText().toString() != "0") {
                                    // CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(61);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt1.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;

                            case 2:
                                if (dosageGivenEdt2.getText().toString().length() > 0 && dosageGivenEdt2.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(60);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt2.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;

                            case 3:
                                if (dosageGivenEdt3.getText().toString().length() > 0 && dosageGivenEdt3.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(63);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt3.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;

                            case 4:
                                if (dosageGivenEdt4.getText().toString().length() > 0 && dosageGivenEdt4.getText().toString() != "0") {
                                    // CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(64);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt4.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    // mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;

                            case 5:
                                if (dosageGivenEdt5.getText().toString().length() > 0 && dosageGivenEdt5.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(58);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt5.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    // mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;

                            case 6:
                                if (dosageGivenEdt6.getText().toString().length() > 0 && dosageGivenEdt6.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(229);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt6.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                            case 7:


                                if (bioFertilizerSpin.getSelectedItemPosition() != 0 && dosageGivenEdt7.getText().toString().length() > 0 && dosageGivenEdt7.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    mFertilizerModel.setFertilizerid(232);
                                    mFertilizerModel.setBioFertilizerId(bioFertilizerId);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt7.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);

                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);

                                }
                                break;
                            case 8:
                                if (bioFertilizerSpin2.getSelectedItemPosition() != 0 && dosageGivenEdt8.getText().toString().length() > 0 && dosageGivenEdt8.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    Log.d("Selected Item Id2", bioFertilizerId2 + "");

                                    mFertilizerModel.setFertilizerid(232);
                                    mFertilizerModel.setBioFertilizerId(bioFertilizerId2);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt8.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);
                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                            case 9:
                                if (bioFertilizerSpin3.getSelectedItemPosition() != 0 && dosageGivenEdt31.getText().toString().length() > 0 && dosageGivenEdt31.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    Log.d("Selected Item Id 3", bioFertilizerId3 + "");

                                    mFertilizerModel.setFertilizerid(232);
                                    mFertilizerModel.setBioFertilizerId(bioFertilizerId3);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt31.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);
                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                            case 10:
                                if (bioFertilizerSpin4.getSelectedItemPosition() != 0 && dosageGivenEdt41.getText().toString().length() > 0 && dosageGivenEdt41.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    Log.d("Selected Item Id 4", bioFertilizerId4 + "");
                                    mFertilizerModel.setFertilizerid(232);
                                    mFertilizerModel.setBioFertilizerId(bioFertilizerId4);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt41.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);
                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                            case 11:
                                if (bioFertilizerSpin5.getSelectedItemPosition() != 0 && dosageGivenEdt51.getText().toString().length() > 0 && dosageGivenEdt51.getText().toString() != "0") {
                                    //CommonConstants.Prev_Fertilizer_CMD = "Done";
                                    Log.d("Selected Item Id 5", bioFertilizerId5 + "");
                                    mFertilizerModel.setFertilizerid(232);
                                    mFertilizerModel.setBioFertilizerId(bioFertilizerId5);
                                    mFertilizerModel.setDosage(Double.parseDouble(dosageGivenEdt51.getText().toString()));
                                    lastAppliedDate = applydate;
                                    mFertilizerModel.setFertilizersourcetypeid(Integer.parseInt(getKey(fertilizerDataMap, sourceOfertilizerSpin.getSelectedItem().toString())));
                                    mFertilizerModel.setSourceName(sourceName.getText().toString());
                                    mFertilizerModel.setComments(comments.getText().toString());
                                    mFertilizerModel.setUomid(1);
                                    mFertilizerModel.setApplicationType(getKey(AppTypeDataMap, apptype.getSelectedItem().toString()));
                                    mFertilizerModel.setApplicationMonth(Month);
                                    mFertilizerModel.setApplicationYear(Year);
                                    //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                                    mFertilizerModel.setIsFertilizerApplied(1);
                                    mFertilizerModel.setQuarter(Quater);
                                    mFertilizerModel.setLastapplieddate(CommonConstants.fertilizerapplydate);
                                    mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                                    mFertilizerModelArray.add(mFertilizerModel);
                                }
                                break;
                        }


                    }
                    Fertilizer mFertilizerModel = new Fertilizer();
                    Fertilizer mFertilizerModelp = new Fertilizer();
                    if (Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())) == 0) {
                        //CommonConstants.Prev_Fertilizer_CMD = "Done";
                        mFertilizerModel.setFertilizerid(234);
                        mFertilizerModel.setDosage(0.00);
                        lastAppliedDate = applydate;
                        mFertilizerModel.setFertilizersourcetypeid(155);
                        mFertilizerModel.setSourceName("");
                        mFertilizerModel.setComments("");
                        mFertilizerModel.setUomid(1);

                        mFertilizerModel.setApplicationMonth("");
                        mFertilizerModel.setApplicationYear(cy);
                        //  mFertilizerModel.setIsFertilizerApplied(Integer.parseInt(getKey(IsAppliedDataMap, fertilizerapplied.getSelectedItem().toString())));
                        mFertilizerModel.setIsFertilizerApplied(0);
                        mFertilizerModel.setQuarter(cq);

                        mFertilizerModel.setLastapplieddate(cmindate + " 00:00:00");
                        mFertilizerModel.setApplyfertilizerfrequencytypeid(null);
                        mFertilizerModelArray.clear();
                        mFertilizerModelArray.add(mFertilizerModel);
                    }

                    DataManager.getInstance().addData(DataManager.FERTILIZER, mFertilizerModelArray);
                    mFertilizerModelArray = (ArrayList<Fertilizer>) DataManager.getInstance().getDataFromManager(DataManager.FERTILIZER);
                    Log.d("Monthyearsecondsave", mFertilizerModelArray.get(0).getApplicationMonth() + "");


                    getFragmentManager().popBackStack();
                    CommonUtilsNavigation.hideKeyBoard(getActivity());
                    clearFields();
                    //     fertilizerDataAdapter.notifyDataSetChanged();

                    updateUiListener.updateUserInterface(0);

                }

                CommonUtilsNavigation.hideKeyBoard(getActivity());
            }


            if (id == R.id.historyBtn) {
                showDialog(getContext());
            }

            if (id == R.id.pdfBtn) {
                showPDFDialog(getContext());
            }

    }

    public void showDialog(Context activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.fertilizerlastvisteddata);

        Toolbar titleToolbar;
        titleToolbar = (Toolbar) dialog.findViewById(R.id.titleToolbar);
        titleToolbar.setTitle("Fertilizer History");
        titleToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        FertilizerNonBioVisitedDataAdapter fertilizernonbioVisitedDataAdapter;
        FertilizerBioVisitedDataAdapter fertilizerbioVisitedDataAdapter;


        LinearLayout fertilizermainlyt = (LinearLayout) dialog.findViewById(R.id.fertilizermainlyt);
        LinearLayout biolayout = (LinearLayout) dialog.findViewById(R.id.biolayout);

//        LinearLayout fertilizerappliedll = (LinearLayout) dialog.findViewById(R.id.fertilizerappliedll);
//        LinearLayout fertilizermonthyearll = (LinearLayout) dialog.findViewById(R.id.fertilizermonthyearll);
//        LinearLayout nameoftheshopll = (LinearLayout) dialog.findViewById(R.id.nameoftheshopll);
//        LinearLayout sourceoffertilizerll = (LinearLayout) dialog.findViewById(R.id.sourceoffertilizerll);
//        LinearLayout fertcommentsll = (LinearLayout) dialog.findViewById(R.id.fertcommentsll);
//        LinearLayout fertapptypell = (LinearLayout) dialog.findViewById(R.id.fertapptypell);


        RecyclerView nonbiofertrcv = (RecyclerView) dialog.findViewById(R.id.nonbiofertrcv);
        RecyclerView biofertrcv = (RecyclerView) dialog.findViewById(R.id.biofertrcv);

//        TextView fertilizerapplied = (TextView) dialog.findViewById(R.id.fertilizerapplied);
//        TextView fertilizermonthyear = (TextView) dialog.findViewById(R.id.fertilizermonthyear);
//        TextView nameoftheshop = (TextView) dialog.findViewById(R.id.nameoftheshop);
//        TextView sourceoffertilizer = (TextView) dialog.findViewById(R.id.sourceoffertilizer);
//        TextView fertcomments = (TextView) dialog.findViewById(R.id.fertcomments);
//        TextView fertapptype = (TextView) dialog.findViewById(R.id.fertapptype);

        TextView norecords = (TextView) dialog.findViewById(R.id.fertilizernorecord_tv);


        String lastVisitCode = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getLatestCropMaintanaceHistoryCode(CommonConstants.PLOT_CODE));

        Log.e("======>lastVisitCode",lastVisitCode+"");

        fertilizernonbiolastvisitdatamap = (ArrayList<Fertilizer>) dataAccessHandler.getFertilizerData(Queries.getInstance().getFertilizerCropMaintenanceHistoryData(lastVisitCode), 1);
        fertilizerbiolastvisitdatamap = (ArrayList<Fertilizer>) dataAccessHandler.getFertilizerData(Queries.getInstance().getBioFertilizerCropMaintenanceHistoryData(lastVisitCode), 1);

        if (fertilizernonbiolastvisitdatamap.size() > 0 || fertilizerbiolastvisitdatamap.size() > 0){
            norecords.setVisibility(View.GONE);
            fertilizermainlyt.setVisibility(View.VISIBLE);

            if (fertilizernonbiolastvisitdatamap.size() > 0){
                nonbiofertrcv.setVisibility(View.VISIBLE);
                fertilizernonbioVisitedDataAdapter = new FertilizerNonBioVisitedDataAdapter(getActivity(), fertilizernonbiolastvisitdatamap,dataAccessHandler);
                nonbiofertrcv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                nonbiofertrcv.setAdapter(fertilizernonbioVisitedDataAdapter);
            }else{
                nonbiofertrcv.setVisibility(View.GONE);
            }

            if (fertilizerbiolastvisitdatamap.size() > 0){
                biolayout.setVisibility(View.VISIBLE);
                fertilizerbioVisitedDataAdapter = new FertilizerBioVisitedDataAdapter(getActivity(), fertilizerbiolastvisitdatamap,dataAccessHandler);
                biofertrcv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                biofertrcv.setAdapter(fertilizerbioVisitedDataAdapter);
            }else{
                biolayout.setVisibility(View.GONE);
            }



        }else{
            fertilizermainlyt.setVisibility(View.GONE);
            norecords.setVisibility(View.VISIBLE);
        }

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }

    public void showPDFDialog(Context activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.pdfdialog);

        Toolbar titleToolbar;
        titleToolbar = (Toolbar) dialog.findViewById(R.id.titleToolbar);
        titleToolbar.setTitle("Fertilizer PDF");
        titleToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        PDFView fertpfdview;

        fertpfdview = dialog.findViewById(R.id.fertpdfview);

        fertpfdview.fromFile(fileToDownLoad)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .onPageChange(this)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .load();

        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }


    private void clearFields() {

        fertilizerapplied.setSelection(0);
        sourceOfertilizerSpin.setSelection(0);
        apptype.setSelection(0);
        otherEdt.setText("");
        dosageGivenEdt.setText("");
        // lastAppliedDateEdt.setText("");
        dosageGivenEdt1.setText("");
        //  lastAppliedDateEdt1.setText("");
        dosageGivenEdt2.setText("");
        //    lastAppliedDateEdt2.setText("");
        dosageGivenEdt3.setText("");
        //  lastAppliedDateEdt3.setText("");
        dosageGivenEdt4.setText("");
        // lastAppliedDateEdt4.setText("");
        dosageGivenEdt5.setText("");
        //lastAppliedDateEdt5.setText("");
        dosageGivenEdt6.setText("");
        // lastAppliedDateEdt6.setText("");
        dosageGivenEdt7.setText("");
        //lastAppliedDateEdt7.setText("");
        dosageGivenEdt8.setText("");
        // lastAppliedDateEdt8.setText("");

        otherEdt.setText("");
        comments.setText("");
        // pcomments.setText("");
        sourceName.setText("");
        //  psourceName.setText("");
        Monthyear.setText("");
        //   pMonthyear.setText("");




    }
    private boolean validateUI() {

        if (fertilizerapplied.getSelectedItemPosition() == 0){
            UiUtils.showCustomToastMessage("Please Select Fertilizer Applied?", mContext, 1);
            return false;
        }
        if (fertilizerapplied.getSelectedItemPosition() == 1){

            if (TextUtils.isEmpty(Monthyear.getText().toString())){
                UiUtils.showCustomToastMessage("Please Select Month/Year", mContext, 1);
                return false;
            }
            if (TextUtils.isEmpty(sourceName.getText().toString())){
                UiUtils.showCustomToastMessage("Please Enter Shop Name", mContext, 1);
                return false;
            }

            if (sourceOfertilizerSpin.getSelectedItemPosition() == 0){
                UiUtils.showCustomToastMessage("Please Select Source of Fertilizer", mContext, 1);
                return false;
            }

            if (apptype.getSelectedItemPosition() == 0){
                UiUtils.showCustomToastMessage("Please Select App Type", mContext, 1);
                return false;
            }

//            if (TextUtils.isEmpty(dosageGivenEdt.getText().toString()) && TextUtils.isEmpty(dosageGivenEdt1.getText().toString()) && TextUtils.isEmpty(dosageGivenEdt2.getText().toString())
//            && TextUtils.isEmpty(dosageGivenEdt3.getText().toString()) && TextUtils.isEmpty(dosageGivenEdt4.getText().toString()) && TextUtils.isEmpty(dosageGivenEdt5.getText().toString())
//                    && TextUtils.isEmpty(dosageGivenEdt6.getText().toString())  && TextUtils.isEmpty(dosageGivenEdt7.getText().toString())  && TextUtils.isEmpty(dosageGivenEdt8.getText().toString())
//            ){
//                Toast.makeText(mContext, "Please Enter Any One Fertilizer Dosage", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            if (dosageGivenEdt.getText().toString() == "0" && dosageGivenEdt1.getText().toString() == "0" && dosageGivenEdt2.getText().toString() == "0" &&
//                    dosageGivenEdt3.getText().toString() == "0" && dosageGivenEdt4.getText().toString() == "0" &&  dosageGivenEdt5.getText().toString() == "0" &&
//                    dosageGivenEdt6.getText().toString() == "0" && dosageGivenEdt7.getText().toString() == "0" &&  dosageGivenEdt8.getText().toString() == "0"){
//
//                Toast.makeText(mContext, "Please Enter Any One Fertilizer Dosage", Toast.LENGTH_SHORT).show();
//                return false;
//            }

            EditText[] dosageEditTexts = {
                    dosageGivenEdt, dosageGivenEdt1, dosageGivenEdt2, dosageGivenEdt3,
                    dosageGivenEdt4, dosageGivenEdt5, dosageGivenEdt6, dosageGivenEdt7, dosageGivenEdt8
            };

            boolean hasNonEmptyOrNonZeroValue = false;

            for (EditText editText : dosageEditTexts) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value) && !value.equals("0")) {
                    hasNonEmptyOrNonZeroValue = true;
                    break;
                }
            }

            if (!hasNonEmptyOrNonZeroValue) {
                UiUtils.showCustomToastMessage("Please Enter Any One Fertilizer Dosage", mContext, 1);
                return false;
            }

            if (bioFertilizerSpin2.getSelectedItemPosition() == 0 &&
                    (!TextUtils.isEmpty(dosageGivenEdt8.getText().toString().trim()) && !dosageGivenEdt8.getText().toString().trim().equals("0"))) {
                UiUtils.showCustomToastMessage("Please Select BioFertilizer 2", mContext, 1);
                return false;
            }
            if (bioFertilizerSpin.getSelectedItemPosition() == 0 &&
                    (!TextUtils.isEmpty(dosageGivenEdt7.getText().toString().trim()) && !dosageGivenEdt7.getText().toString().trim().equals("0"))) {
                UiUtils.showCustomToastMessage("Please Select BioFertilizer 1", mContext, 1);
                return false;
            }

            if (bioFertilizerSpin3.getSelectedItemPosition() == 0 &&
                    (!TextUtils.isEmpty(dosageGivenEdt31.getText().toString().trim()) && !dosageGivenEdt31.getText().toString().trim().equals("0"))) {
                UiUtils.showCustomToastMessage("Please Select BioFertilizer 3", mContext, 1);
                return false;
            }
            if (bioFertilizerSpin4.getSelectedItemPosition() == 0 &&
                    (!TextUtils.isEmpty(dosageGivenEdt41.getText().toString().trim()) && !dosageGivenEdt41.getText().toString().trim().equals("0"))) {
                UiUtils.showCustomToastMessage("Please Select BioFertilizer 4", mContext, 1);
                return false;
            }
            if (bioFertilizerSpin5.getSelectedItemPosition() == 0 &&
                    (!TextUtils.isEmpty(dosageGivenEdt51.getText().toString().trim()) && !dosageGivenEdt51.getText().toString().trim().equals("0"))) {
                UiUtils.showCustomToastMessage("Please Select BioFertilizer 5", mContext, 1);
                return false;
            }

        } else {
            return true;
        }

        return true;
    }

//    private void updateLabel() {
//        String myFormat = "MM/dd/yyyy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        if(cal==0)
//            lastAppliedDateEdt.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==1)
//            lastAppliedDateEdt1.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==2)
//            lastAppliedDateEdt2.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==3)
//            lastAppliedDateEdt3.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==4)
//            lastAppliedDateEdt4.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==5)
//            lastAppliedDateEdt5.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==6)
//            lastAppliedDateEdt6.setText(sdf.format(myCalendar.getTime()));
//        else if(cal==7)
//            lastAppliedDateEdt7.setText(sdf.format(myCalendar.getTime()));
//
//    }

    @Override
    public void onEditClicked(int position) {
        Log.v(LOG_TAG, "@@@ selected position " + position);
        mFertilizerModelArray.remove(position);
        fertilizerDataAdapter.notifyDataSetChanged();
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    @Override
    public void updateUserInterface(int refreshPosition) {
        complaintsBtn.setVisibility(View.GONE);
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }
}