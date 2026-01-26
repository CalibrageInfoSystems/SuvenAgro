package com.cis.palm360.palmgrow.SuvenAgro.service;

//import android.database.Observable;

import com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel.PlantationPdfResponseModel;
import com.cis.palm360.palmgrow.SuvenAgro.palmcare.OtpResponceModel;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {


//    @GET
//    Observable<LerningsModel> getlernings(@Url String url);
//
//
//    @GET
//    Observable<RecomPlotcodes> getplots(@Url String url);
//
//
    @GET
    Observable<OtpResponceModel> getFormerOTP(@Url String url);

    @POST(APIConstantURL.PlantationDDReceiptPDF)
    Observable<PlantationPdfResponseModel> plantationPdf(@Body JsonObject data);


//    @GET
//    Observable<FarmerOtpResponceModel> getFormerdetails(@Url String url);


}
