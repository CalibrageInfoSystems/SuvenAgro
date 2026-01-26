package com.cis.palm360.palmgrow.SuvenAgro.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.cis.palm360.palmgrow.SuvenAgro.R;
import com.cis.palm360.palmgrow.SuvenAgro.cloudhelper.ApplicationThread;
import com.cis.palm360.palmgrow.SuvenAgro.database.DataAccessHandler;
import com.cis.palm360.palmgrow.SuvenAgro.database.DatabaseKeys;
import com.cis.palm360.palmgrow.SuvenAgro.database.Queries;
import com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataSyncHelper;
import com.cis.palm360.palmgrow.SuvenAgro.dbmodels.Alerts;
import com.cis.palm360.palmgrow.SuvenAgro.uihelper.ProgressBar;

import java.util.Collections;
import java.util.List;

import static com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataSyncHelper.refreshtableNamesList;
import static com.cis.palm360.palmgrow.SuvenAgro.datasync.helpers.DataSyncHelper.refreshtransactionsDataMap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//Notifications Screen
public class NotificationsScreen extends AppCompatActivity {
    public static final String LOG_TAG = NotificationsScreen.class.getName();
    private Button refreshBtn;
    private RecyclerView notiRecyclerView;
    private NotificationDisplayAdapter notificationDisplayAdapter;
    private LinearLayoutManager layoutManager;
    private DataAccessHandler dataAccessHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        notiRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        layoutManager = new LinearLayoutManager(this);
        notiRecyclerView.setLayoutManager(layoutManager);

//        refreshBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DataSyncHelper.getAlertsData(NotificationsScreen.this, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            renderNotifications();
//                        } else {
//                            //UiUtils.showCustomToastMessage("Error while getting alerts Data", NotificationsScreen.this, 1);
//                        }
//                    }
//                });
//            }
//        });

       dataAccessHandler = new DataAccessHandler(this);

        renderNotifications();

        updateNotificationStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationStatus() {
        dataAccessHandler.upNotificationStatus();

        List<Alerts> dataToSendCloud = (List<Alerts>) dataAccessHandler.getAlertsDetails(Queries.getInstance()
                .getAlertsDetailsQueryToSendCloud(), 1, true);

        if (dataToSendCloud == null || dataToSendCloud.isEmpty()) {
            return;
        }

        DataSyncHelper.reverseSyncTransCount = 0;
        DataSyncHelper.transactionsCheck = 0;
        refreshtableNamesList.clear();
        refreshtransactionsDataMap.clear();
        refreshtableNamesList.add(DatabaseKeys.TABLE_ALERTS);
        refreshtransactionsDataMap.put(DatabaseKeys.TABLE_ALERTS, dataToSendCloud);

        DataSyncHelper.postTransactionsDataToCloud(this, DatabaseKeys.TABLE_ALERTS, dataAccessHandler, new ApplicationThread.OnComplete() {
            @Override
            public void execute(boolean success, Object result, String msg) {
                super.execute(success, result, msg);
                refreshtableNamesList.clear();
                refreshtransactionsDataMap.clear();
            }
        });

    }

    private void renderNotifications() {

        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "", new Runnable() {
            @Override
            public void run() {
                ProgressBar.hideProgressBar();
                final List<Alerts> alertsList = (List<Alerts>) dataAccessHandler.getAlertsDetails(Queries.getInstance().getAlertsDetailsQueryToRender(), 1, false);

               Collections.reverse(alertsList);
               ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        if (null != alertsList && !alertsList.isEmpty()) {
                            notificationDisplayAdapter = new NotificationDisplayAdapter(alertsList);
                            notiRecyclerView.setAdapter(notificationDisplayAdapter);
                        }
                    }
                });
            }
        });
    }

}
