package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

public class DispatchRequestsModel implements Serializable {

    String ReceiptNumber;
    Integer NoOfImportedSaplingsToDispatch;
    Integer NoOfIndigenousSaplingsToDispatch;
    Integer NoOfSaplingsToDispatch;
    String Desc;
    String PlotCode;
    String ExpDateOfPickup;
    String Comments;
    Integer CreatedByUserId;
    String CreatedDate;
    Integer UpdateByUserId;
    String UpdatedDate;
    private String AdvanceReceiptNumber;


    public String getPlotCode() {
        return PlotCode;
    }

    public void setPlotCode(String plotCode) {
        PlotCode = plotCode;
    }

    public String getReceiptNumber() {
        return ReceiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNumber = receiptNumber;
    }

    public Integer getUpdateByUserId() {
        return UpdateByUserId;
    }

    public void setUpdateByUserId(Integer updateByUserId) {
        UpdateByUserId = updateByUserId;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public Integer getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getExpDateOfPickup() {
        return ExpDateOfPickup;
    }

    public void setExpDateOfPickup(String expDateOfPickup) {
        ExpDateOfPickup = expDateOfPickup;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Integer getNoOfSaplingsToDispatch() {
        return NoOfSaplingsToDispatch;
    }

    public void setNoOfSaplingsToDispatch(Integer noOfSaplingsToDispatch) {
        NoOfSaplingsToDispatch = noOfSaplingsToDispatch;
    }

    public Integer getNoOfIndigenousSaplingsToDispatch() {
        return NoOfIndigenousSaplingsToDispatch;
    }

    public void setNoOfIndigenousSaplingsToDispatch(Integer noOfIndigenousSaplingsToDispatch) {
        NoOfIndigenousSaplingsToDispatch = noOfIndigenousSaplingsToDispatch;
    }

    public Integer getNoOfImportedSaplingsToDispatch() {
        return NoOfImportedSaplingsToDispatch;
    }

    public void setNoOfImportedSaplingsToDispatch(Integer noOfImportedSaplingsToDispatch) {
        NoOfImportedSaplingsToDispatch = noOfImportedSaplingsToDispatch;
    }

    @Override
    public String toString() {
        return "DispatchRequestsModel{" +
                "ReceiptNumber='" + ReceiptNumber + '\'' +
                ", NoOfImportedSaplingsToDispatch=" + NoOfImportedSaplingsToDispatch +
                ", NoOfIndigenousSaplingsToDispatch=" + NoOfIndigenousSaplingsToDispatch +
                ", NoOfSaplingsToDispatch=" + NoOfSaplingsToDispatch +
                ", Desc='" + Desc + '\'' +
                ", PlotCode='" + PlotCode + '\'' +
                ", ExpDateOfPickup='" + ExpDateOfPickup + '\'' +
                ", Comments='" + Comments + '\'' +
                ", CreatedByUserId=" + CreatedByUserId +
                ", CreatedDate='" + CreatedDate + '\'' +
                ", UpdateByUserId=" + UpdateByUserId +
                ", UpdatedDate='" + UpdatedDate + '\'' +
                '}';
    }

    public String getAdvanceReceiptNumber() {
        return AdvanceReceiptNumber;
    }

    public void setAdvanceReceiptNumber(String advanceReceiptNumber) {
        AdvanceReceiptNumber = advanceReceiptNumber;
    }
}
