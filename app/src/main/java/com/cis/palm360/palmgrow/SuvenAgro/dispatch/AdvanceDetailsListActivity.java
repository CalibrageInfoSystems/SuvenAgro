package com.cis.palm360.palmgrow.SuvenAgro.dispatch;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonConstants;
import com.cis.palm360.palmgrow.SuvenAgro.common.CommonUtils;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataManager;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.Adapter.AdvanceDetailsAdapter;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.AdvancedDetails;
import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.PlantationPdfResponseModel;
import com.cis.palm360.palmgrow.SuvenAgro.service.ApiService;
import com.cis.palm360.palmgrow.SuvenAgro.service.ServiceFactory;
import com.cis.palm360.palmgrow.SuvenAgro.utils.UiUtils;
import com.google.gson.JsonObject;

import org.reactivestreams.Subscription;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class AdvanceDetailsListActivity extends AppCompatActivity implements AdvanceDetailsAdapter.ClickListener {

    private static final String LOG_TAG = AdvanceDetailsListActivity.class.getSimpleName();
    private RecyclerView advanceDetailsList;
    private ProgressBar progressBar;
    private EditText searchView;
    private AdvanceDetailsAdapter adapter;
    private List<AdvancedDetails> fullDataList = new ArrayList<>();
    LinkedHashMap<Integer, String> paymentmodeMap;
    private DataAccessHandler dataAccessHandler;
    Button add_Addvancedetails;
    ImageButton btnDownload;
    TextView noData;
    Integer stateID, ZoneID;
    private Integer getPendingSaplingsCount;
    private Subscription mSubscription;
    private Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_details_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Plantation DD");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        loadAdvanceDetails();
    }

    private void initUI() {
        advanceDetailsList = findViewById(R.id.advacedetails_list);
        progressBar = findViewById(R.id.progress_bar);
        // searchView = findViewById(R.id.search_view);
        noData = findViewById(R.id.no_data_text);
        advanceDetailsList.setLayoutManager(new LinearLayoutManager(this));
        add_Addvancedetails = findViewById(R.id.add_Addvancedetails);

        stateID = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getStateIdForPlotCode(CommonConstants.PLOT_CODE));
        ZoneID = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getZoneIdForPlotCode(CommonConstants.PLOT_CODE));
        android.util.Log.e("getStateIdForPlotCode", stateID + " | " + ZoneID + " | " + CommonConstants.PLOT_CODE);
        getPendingSaplingsCount = dataAccessHandler.fetchPendingSaplingsCount(Queries.getInstance().fetchPendingSaplingsCount(CommonConstants.PLOT_CODE));
        android.util.Log.e("getPendingSaplingsCount", getPendingSaplingsCount + "");
        btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(v -> {
            if (!CommonUtils.isNetworkAvailable(this)) {
                UiUtils.showCustomToastMessage("Please check your network connection", getApplicationContext(), 1);
            } else {
                startPdfDownload();
            }
        });

        add_Addvancedetails.setOnClickListener(v -> {

            if (ZoneID == null || stateID == null) {
                UiUtils.showCustomToastMessage("Zone Data Not Available. Please Do Master Sync", AdvanceDetailsListActivity.this, 1);
            } else {
                Intent nav = new Intent(AdvanceDetailsListActivity.this, AdvanceDetailsActivity.class);
                nav.putExtra("isFromUpdate", "notfromUpdate");
                startActivityForResult(nav, 101); // Use a request code
                DataManager.getInstance().deleteData(DataManager.ADVANCE_DETAILS_UPDATE);
            }
//            if (getPendingSaplingsCount > 0) {
//                UiUtils.showCustomToastMessage("You Have Pending Saplings To be Dispatched", AdvanceDetailsListActivity.this, 0);
//
//                if (ZoneID == null || stateID == null) {
//                    UiUtils.showCustomToastMessage("Zone Data Not Available. Please Do Master Sync", AdvanceDetailsListActivity.this, 0);
//                } else {
//                    Intent nav = new Intent(AdvanceDetailsListActivity.this, AdvanceDetailsActivity.class);
//                    nav.putExtra("isFromUpdate", "notfromUpdate");
//                    startActivityForResult(nav, 101); // Use a request code
//                    DataManager.getInstance().deleteData(DataManager.ADVANCE_DETAILS_UPDATE);
//                }
//            }
//            else {
//
//                if (ZoneID == null || stateID == null) {
//                    UiUtils.showCustomToastMessage("Zone Data Not Available. Please Do Master Sync", AdvanceDetailsListActivity.this, 0);
//                } else {
//                    Intent nav = new Intent(AdvanceDetailsListActivity.this, AdvanceDetailsActivity.class);
//                    nav.putExtra("isFromUpdate", "notfromUpdate");
//                    startActivityForResult(nav, 101); // Use a request code
//                    DataManager.getInstance().deleteData(DataManager.ADVANCE_DETAILS_UPDATE);
//                }
//            }

        });

    }


/*    private void startPdfDownload() {

        JsonObject object = PlantationRequest();
        ApiService service = ServiceFactory.createRetrofitService(this, ApiService.class);
        mSubscription = service.plantationPdf(object)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlantationPdf>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(PlantationPdf plantationPdf) {

                        // This Base64 string should come from your API
                        String sampleBase64Pdf = "JVBERi0xLjQKMSAwIG9iago8PC9UeXBlIC9DYXRhbG9nCi9QYWdlcyAyIDAgUgo+PgplbmRvYmoK"
                                + "MiAwIG9iago8PC9UeXBlIC9QYWdlcwovS2lkcyBbMyAwIFJdCi9Db3VudCAxCj4+CmVuZG9iagoz"
                                + "IDAgb2JqCjw8L1R5cGUgL1BhZ2UKL1BhcmVudCAyIDAgUgovTWVkaWFCb3ggWzAgMCA1OTUgODQy"
                                + "XQovQ29udGVudHMgNSAwIFIKL1Jlc291cmNlcyA8PC9Qcm9jU2V0IFsvUERGIC9UZXh0XQovRm9u"
                                + "dCA8PC9GMSA0IDAgUj4+Cj4+Cj4+CmVuZG9iago0IDAgb2JqCjw8L1R5cGUgL0ZvbnQKL1N1YnR5"
                                + "cGUgL1R5cGUxCi9OYW1lIC9GMQovQmFzZUZvbnQgL0hlbHZldGljYQovRW5jb2RpbmcgL01hY1Jv"
                                + "bWFuRW5jb2RpbmcKPj4KZW5kb2JqCjUgMCBvYmoKPDwvTGVuZ3RoIDUzCj4+CnN0cmVhbQpCVAov"
                                + "RjEgMjAgVGYKMjIwIDQwMCBUZAooRHVtbXkgUERGKSBUagpFVAplbmRzdHJlYW0KZW5kb2JqCnhy"
                                + "ZWYKMCA2CjAwMDAwMDAwMDAgNjU1MzUgZgowMDAwMDAwMDA5IDAwMDAwIG4KMDAwMDAwMDA2MyAw"
                                + "MDAwMCBuCjAwMDAwMDAxMjQgMDAwMDAgbgowMDAwMDAwMjc3IDAwMDAwIG4KMDAwMDAwMDM5MiAw"
                                + "MDAwMCBuCnRyYWlsZXIKPDwvU2l6ZSA2Ci9Sb290IDEgMCBSCj4+CnN0YXJ0eHJlZgo0OTUKJSVF"
                                + "T0YK";

                        try {
                            // 1. Decode the Base64 string into bytes
                            byte[] pdfBytes = Base64.decode(sampleBase64Pdf, Base64.DEFAULT);

                            // 2. Create directory where file will be saved

                            String rootPath = CommonUtils.get3FFileRootPath() + "PlantationDDReceipt/";
                            File directory = new File(rootPath);
                            if (!directory.exists()) {
                                boolean created = directory.mkdirs();
                                Log.d(TAG, "Directory created: " + created);
                            }

//            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartGeoTrack/PalmGrow_DigitalContract/";
//            File directory = new File(rootPath);
//            if (!directory.exists()) {
//                boolean created = directory.mkdirs();
//                Log.d(TAG, "Directory created: " + created);
//            }

                            // 3. Create the file
                            File pdfFile = new File(directory, CommonConstants.PLOT_CODE + "_DDReceipt.pdf");

                            // 4. Write bytes to the file
                            FileOutputStream fos = new FileOutputStream(pdfFile);
                            fos.write(pdfBytes);
                            fos.flush();
                            fos.close();

                            // 5. Show confirmation
                            if (pdfFile.exists()) {
                                UiUtils.showCustomToastMessage("PDF Saved Successfully ", AdvanceDetailsListActivity.this, 0);
                                Toast.makeText(AdvanceDetailsListActivity.this, "PDF saved at: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "PDF saved at: " + pdfFile.getAbsolutePath());
                            } else {
                                UiUtils.showCustomToastMessage("Failed to save PDF", AdvanceDetailsListActivity.this, 1);
                                Toast.makeText(AdvanceDetailsListActivity.this, "Failed to save PDF", Toast.LENGTH_LONG).show();
                                Log.e(TAG, "PDF file not found after saving!");
                            }

                        } catch (IOException e) {
                            Log.e(TAG, "Error writing PDF file", e);
                            UiUtils.showCustomToastMessage( "Error: " + e.getMessage(), AdvanceDetailsListActivity.this, 1);
                            Toast.makeText(AdvanceDetailsListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "PDF download error", t);
                        UiUtils.showCustomToastMessage("Error downloading PDF: " + t.getMessage(), AdvanceDetailsListActivity.this, 1);
                        Toast.makeText(AdvanceDetailsListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "PDF download completed");
                    }

                });
    }*/

    private void startPdfDownload() {
        // Create request object
        JsonObject requestObject = PlantationRequest();

        ApiService service = ServiceFactory.createRetrofitService(this, ApiService.class);

        // Make the API call
        service.plantationPdf(requestObject)
                .subscribeOn(Schedulers.io()) // Use IO thread for network calls
                .observeOn(AndroidSchedulers.mainThread()) // Observe on main thread for UI updates
                .subscribe(new Observer<PlantationPdfResponseModel>() {

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            ((HttpException) e).code();
                            ((HttpException) e).message();
                            ((HttpException) e).response().errorBody();
                            try {
                                ((HttpException) e).response().errorBody().string();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PlantationPdfResponseModel responseModel) {
                        if (responseModel != null && responseModel.getSuccess()) {
                            try {
                                String base64Pdf = responseModel.getReceiptDataInBytes();

                                if (base64Pdf != null && !base64Pdf.isEmpty()) {
                                    saveBase64PdfToFile(base64Pdf);

                                } else {
                                    UiUtils.showCustomToastMessage("No PDF data received",
                                            getApplicationContext(), 1);
                                }
                            } catch (Exception e) {
                                Log.e("PDF_DOWNLOAD", "Error processing PDF: " + e.getMessage());
                                UiUtils.showCustomToastMessage("Failed to process PDF",
                                        getApplicationContext(), 1);
                            }
                        } else {
                            UiUtils.showCustomToastMessage(responseModel.getMessage(),
                                    getApplicationContext(), 1);
                        }
                    }
                });
    }

    private void saveBase64PdfToFile(String sampleBase64Pdf) {

        try {
            // 1. Decode the Base64 string into bytes
            byte[] pdfBytes = Base64.decode(sampleBase64Pdf, Base64.DEFAULT);

            // 2. Create directory where file will be saved

            String rootPath = CommonUtils.get3FFileRootPath() + "PlantationDDReceipt/";
            File directory = new File(rootPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                Log.d(TAG, "Directory created: " + created);
            }

//            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartGeoTrack/PalmGrow_DigitalContract/";
//            File directory = new File(rootPath);
//            if (!directory.exists()) {
//                boolean created = directory.mkdirs();
//                Log.d(TAG, "Directory created: " + created);
//            }

            // 3. Create the file
            File pdfFile = new File(directory, CommonConstants.PLOT_CODE + "_DDReceipt.pdf");

            // 4. Write bytes to the file
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdfBytes);
            fos.flush();
            fos.close();

            // 5. Show confirmation
            if (pdfFile.exists()) {
                UiUtils.showCustomToastMessage("PDF downloaded successfully", getApplicationContext(), 0);
//                Toast.makeText(AdvanceDetailsListActivity.this, "PDF saved at: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "PDF saved at: " + pdfFile.getAbsolutePath());
            } else {
                UiUtils.showCustomToastMessage("Failed to save PDF", AdvanceDetailsListActivity.this, 1);
                Toast.makeText(AdvanceDetailsListActivity.this, "Failed to save PDF", Toast.LENGTH_LONG).show();
                Log.e(TAG, "PDF file not found after saving!");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error writing PDF file", e);
            UiUtils.showCustomToastMessage("Error: " + e.getMessage(), AdvanceDetailsListActivity.this, 1);
            Toast.makeText(AdvanceDetailsListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Prepares the JSON request object for the API call
     */
    private JsonObject PlantationRequest() {
        JsonObject object = new JsonObject();
        object.addProperty("PlotCode", CommonConstants.PLOT_CODE);
        return object;
    }


    private void filter(String query) {
        List<AdvancedDetails> filteredList = new ArrayList<>();
        for (AdvancedDetails model : fullDataList) {
            if (model.getPlotCode().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(model);
            }
        }
        adapter.updateList(filteredList);
    }


    private void loadAdvanceDetails() {
        // progressBar.setVisibility(View.VISIBLE);
        getPendingSaplingsCount = dataAccessHandler.fetchPendingSaplingsCount(Queries.getInstance().fetchPendingSaplingsCount(CommonConstants.PLOT_CODE));
        String query = Queries.getAllAdvancedDetails(CommonConstants.PLOT_CODE);
        List<AdvancedDetails> advancedDetailsList = (List<AdvancedDetails>) dataAccessHandler.getAdvancedDetails(query, 1);
        paymentmodeMap = dataAccessHandler.getGenericData(Queries.getInstance().getpaymentmodeforadv());
        if (advancedDetailsList.size() == 1) {
            add_Addvancedetails.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
        }

        if (advancedDetailsList != null && !advancedDetailsList.isEmpty()) {
            advanceDetailsList.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            fullDataList = advancedDetailsList;
            adapter = new AdvanceDetailsAdapter(fullDataList, this, paymentmodeMap, dataAccessHandler);
            advanceDetailsList.setAdapter(adapter);

        } else {
            advanceDetailsList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.GONE);
        }
        //   progressBar.setVisibility(View.GONE);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(AdvancedDetails model) {

        if (ZoneID == null || stateID == null) {
            UiUtils.showCustomToastMessage("Please Do Master Sync", AdvanceDetailsListActivity.this, 0);
        } else {
            Intent editadvdetails = new Intent(AdvanceDetailsListActivity.this, AdvanceDetailsActivity.class);
            editadvdetails.putExtra("isFromUpdate", "fromUpdate");
            editadvdetails.putExtra("receiptNumberforedit", model.getReceiptNumber());
            AdvancedDetails advancedDetailsforupdate = (AdvancedDetails) dataAccessHandler.getsync_AdvancedDetails(Queries.getInstance().getAdvancedDetailsforUpdate(model.getReceiptNumber()), 0);
            DataManager.getInstance().addData(DataManager.ADVANCE_DETAILS_UPDATE, advancedDetailsforupdate);
            startActivityForResult(editadvdetails, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            loadAdvanceDetails(); // Refresh data
        }
    }

}
