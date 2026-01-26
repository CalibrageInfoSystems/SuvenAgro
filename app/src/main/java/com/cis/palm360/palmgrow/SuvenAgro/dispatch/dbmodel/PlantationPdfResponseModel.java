package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlantationPdfResponseModel {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("receiptDataInBytes")
    @Expose
    private String receiptDataInBytes;

    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getReceiptDataInBytes() {
        return receiptDataInBytes;
    }

    public void setReceiptDataInBytes(String receiptDataInBytes) {
        this.receiptDataInBytes = receiptDataInBytes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
