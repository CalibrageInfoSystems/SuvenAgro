package com.cis.palm360.palmgrow.SuvenAgro.service;

public interface APIConstantURL {
//public static  final  String LOCAL_URL="http://182.18.157.215/Ecopalm_DataMigration/API/";//UAT
 //public static final String LOCAL_URL= "http://137.59.201.212/Ecopalm/API/"; //Live
   public static final String LOCAL_URL= "http://182.18.157.215/Ecopalm/API/"; //local

    String SendOTPForCropMaintenance ="api/SyncTransactions/SendOTPForCropMaintenance";
    String VerifyCropMaintenanceOTP ="api/SyncTransactions/VerifyCropMaintenanceOTP/";
    String SendOTPForHarvestorVisit ="api/SyncTransactions/SendOTPForHarvestorVisit";
    String VerifyForHarvestorOTP ="api/SyncTransactions/VerifyForHarvestorOTP/";
    String PlantationDDReceiptPDF = "api/Farmer/DownloadPlantationDDReceiptPDF";

}
// project_folder/appbuild/outputs/apk/debug/