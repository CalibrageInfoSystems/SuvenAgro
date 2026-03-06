package com.cis.palm360.palmgrow.SuvenAgro.cloudhelper;

import android.annotation.SuppressLint;

import com.cis.palm360.palmgrow.SuvenAgro.BuildConfig;

//Urls can be assigned here

public class Config {
    public static final boolean DEVELOPER_MODE = false;

    public static String live_url = "https://palm360.in/Suven/API/api"; //Local test


//  public static String live_url = "http://182.18.157.215/Ecopalm_DataMigration/API/api"; //UAT

   //public static String live_url = "http://137.59.201.212/Ecopalm/API/api"; //Live
// public static String live_url = "http://182.18.157.215/Ecopalm/API/api"; //Local test
    //public static String UOM = "(Ha)";
    public static String UOM = "(Ac)";

    //public static String live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api"; //Local test

    //local URl
    @SuppressLint("SuspiciousIndentation")
    public static void initialize() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {

            live_url = "https://palm360.in/Suven/API/api"; //Local test

            // live_url = "http://137.59.201.212/Ecopalm/API/api"; //Live
//     live_url = "http://182.18.157.215/Ecopalm/API/api"; //Local test
   //  live_url = "http://182.18.157.215/Ecopalm_DataMigration/API/api"; //UAT

        } else {

            live_url = "https://palm360.in/Suven/API/api"; //Local test

          //  live_url = "http://137.59.201.212/Ecopalm/API/api"; //Live
//           live_url = "http://182.18.157.215/Ecopalm/API/api"; //Local test
//             live_url = "http://182.18.157.215/Ecopalm_DataMigration/API/api"; //UAT
        }
    }


    public static final String masterSyncUrl = "/SyncMasters/Sync";

    public static final String transactionSyncURL = "/SyncTransactions/SyncTransactions";
    // public static final String transactionSyncURL = "/SyncTransactions/SyncTransactionss";
    public static final String locationTrackingURL = "/LocationTracker/SaveOfflineLocations";
    public static final String imageUploadURL = "/SyncTransactions/UploadImage";

    public static final String findcollectioncode = "/SyncTransactions/FindCollectionCode/%s";
    public static final String findconsignmentcode = "/SyncTransactions/FindConsignmentCode/%s";
    public static final String findcollectionplotcode = "/SyncTransactions/FindCollectionPlotXref/%s/%s";

    public static final String updatedbFile = "/TabDatabase/UploadDatabase";

    public static final String getTransCount = "/SyncTransactions/GetCount";//{Date}/{UserId}n
    public static final String getTransData = "/SyncTransactions/%s";//api/TranSync/SyncFarmers/{Date}/{UserId}/{Index}
    public static final String validateTranSync = "/TranSync/ValidateTranSync/%s";

   public static final String image_url = "https://palm360.in/Suven/SuvenRepository/FileRepository/";//Test
    public static final String GETMONTHLYTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetMonthlyTargetsByUserIdandFY";
    public static final String GETTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetAnnualTargetsByUserIdandFY";
    public static final String GET_ALERTS = "/SyncTransactions/SyncAlertDetails/";//{UserId}

    //public static final String image_url = "http://182.18.139.166/3FOilPalm/FileRepository";//Added on 26th new repo using//Live
    // public static final String image_url = "http://137.59.201.212/Ecopalm/EcopalmRepository/EcopalmRepository/";//Test
//   public static final String image_url = "http://182.18.157.215/3FSmartPalm_Nursery/3FSmartPalm_Nursery_Repo/FileRepository/";//Test

}
