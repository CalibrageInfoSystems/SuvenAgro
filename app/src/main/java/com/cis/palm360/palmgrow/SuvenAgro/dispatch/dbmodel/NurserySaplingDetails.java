package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

/**
 * Created by balireddy on 3/10/2018.
 */

public class NurserySaplingDetails implements Serializable {
    private String PlotCode;
    private String SaplingPickUpDate;
    private int NoOfSaplingsDispatched;
    private int NoOfImportedSaplingsDispatched;
    private int NoOfIndigenousSaplingsDispatched;
    private String ReceiptNumber;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private Integer NurseryId;
    private Integer SaplingSourceId;
    private Integer SaplingVendorId;
    private Integer CropVarietyId;
    private String PurchaseDate;
    private String BatchNo;
    private String AdvanceReceiptNumber;
    private String Comments;
    private int ServerUpdatedStatus;

    public NurserySaplingDetails() {
    }

    public String getPlotCode() {
        return PlotCode;
    }

    public void setPlotCode(String plotCode) {
        PlotCode = plotCode;
    }

    public String getSaplingPickUpDate() {
        return SaplingPickUpDate;
    }

    public void setSaplingPickUpDate(String saplingPickUpDate) {
        SaplingPickUpDate = saplingPickUpDate;
    }

    public int getNoOfSaplingsDispatched() {
        return NoOfSaplingsDispatched;
    }

    public void setNoOfSaplingsDispatched(int noOfSaplingsDispatched) {
        NoOfSaplingsDispatched = noOfSaplingsDispatched;
    }

    public int getNoOfImportedSaplingsDispatched() {
        return NoOfImportedSaplingsDispatched;
    }

    public void setNoOfImportedSaplingsDispatched(int noOfImportedSaplingsDispatched) {
        NoOfImportedSaplingsDispatched = noOfImportedSaplingsDispatched;
    }

    public int getNoOfIndigenousSaplingsDispatched() {
        return NoOfIndigenousSaplingsDispatched;
    }

    public void setNoOfIndigenousSaplingsDispatched(int noOfIndigenousSaplingsDispatched) {
        NoOfIndigenousSaplingsDispatched = noOfIndigenousSaplingsDispatched;
    }

    public String getReceiptNumber() {
        return ReceiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNumber = receiptNumber;
    }

    public int getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public Integer getNurseryId() {
        return NurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        NurseryId = nurseryId;
    }

    public Integer getSaplingSourceId() {
        return SaplingSourceId;
    }

    public void setSaplingSourceId(Integer saplingSourceId) {
        SaplingSourceId = saplingSourceId;
    }

    public Integer getSaplingVendorId() {
        return SaplingVendorId;
    }

    public void setSaplingVendorId(Integer saplingVendorId) {
        SaplingVendorId = saplingVendorId;
    }

    public Integer  getCropVarietyId() {
        return CropVarietyId;
    }

    public void setCropVarietyId(Integer cropVarietyId) {
        CropVarietyId = cropVarietyId;
    }

    public String getPurchaseDate() {
        return PurchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        PurchaseDate = purchaseDate;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }


    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public String getAdvanceReceiptNumber() {
        return AdvanceReceiptNumber;
    }

    public void setAdvanceReceiptNumber(String advanceReceiptNumber) {
        AdvanceReceiptNumber = advanceReceiptNumber;
    }
}