package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

public class SaplingDispatchRequest implements Serializable {
    private String AdvanceReceiptNumber;
    private String ReceiptNumber;
    private Integer NoOfImportedSaplingsToDispatch;
    private Integer NoOfIndigenousSaplingsToDispatch;
    private Integer NoOfSaplingsToDispatch;
    private Integer StatusId;
    private String ExpDateOfPickup;
    private int IsActive;
    private String Comments;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;

    public SaplingDispatchRequest() {
        // Default constructor
    }


    public String getReceiptNumber() {
        return ReceiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNumber = receiptNumber;
    }

    public Integer getNoOfImportedSaplingsToDispatch() {
        return NoOfImportedSaplingsToDispatch;
    }

    public void setNoOfImportedSaplingsToDispatch(Integer noOfImportedSaplingsToDispatch) {
        NoOfImportedSaplingsToDispatch = noOfImportedSaplingsToDispatch;
    }

    public Integer getNoOfIndigenousSaplingsToDispatch() {
        return NoOfIndigenousSaplingsToDispatch;
    }

    public void setNoOfIndigenousSaplingsToDispatch(Integer noOfIndigenousSaplingsToDispatch) {
        NoOfIndigenousSaplingsToDispatch = noOfIndigenousSaplingsToDispatch;
    }

    public Integer getNoOfSaplingsToDispatch() {
        return NoOfSaplingsToDispatch;
    }

    public void setNoOfSaplingsToDispatch(Integer noOfSaplingsToDispatch) {
        NoOfSaplingsToDispatch = noOfSaplingsToDispatch;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getExpDateOfPickup() {
        return ExpDateOfPickup;
    }

    public void setExpDateOfPickup(String expDateOfPickup) {
        ExpDateOfPickup = expDateOfPickup;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
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


    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public int getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getAdvanceReceiptNumber() {
        return AdvanceReceiptNumber;
    }

    public void setAdvanceReceiptNumber(String advanceReceiptNumber) {
        AdvanceReceiptNumber = advanceReceiptNumber;
    }
}
