package com.cis.palm360.palmgrow.SuvenAgro.dbmodels;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class DripIrrigation {

    @SerializedName("PlotCode")
    private String plotCode;

    @SerializedName("StatusTypeId")
    private int statusTypeId;

    @SerializedName("DripStatusDone")
    private boolean dripStatusDone;

    @SerializedName("CompanyId")
    private int companyId;

    @SerializedName("Date")
    private String date;

    @SerializedName("Comments")
    private String comments;

    @SerializedName("FileName")
    private String fileName;

    @SerializedName("FileLocation")
    private String fileLocation;

    @SerializedName("FileExtension")
    private String fileExtension;

    @SerializedName("AckFileName")
    private String ackFileName;

    @SerializedName("AckFileExtension")
    private String ackFileExtension;

    @SerializedName("AckFileLocation")
    private String ackFileLocation;

    @SerializedName("Amount")
    private Double Amount;

    @SerializedName("DDBank")
    private String ddBank;

    @SerializedName("DDBankAccountNumber")
    private String ddBankAccountNumber;

    @SerializedName("DDChequeNumber")
    private String ddChequeNumber;

    @SerializedName("ModeOfPayment")
    private Integer modeOfPayment;

    @SerializedName("TrenchMarkingTypeId")
    private Integer trenchMarkingTypeId;

    @SerializedName("HorticultureRecommendedPlants")
    private Integer horticultureRecommendedPlants;

    @SerializedName("IsActive")
    private boolean isActive;

    @SerializedName("CreatedByUserId")
    private int createdByUserId;

    @SerializedName("CreatedDate")
    private String createdDate;

    @SerializedName("UpdatedByUserId")
    private int updatedByUserId;

    @SerializedName("UpdatedDate")
    private String updatedDate;

    @SerializedName("ServerUpdatedStatus")
    private boolean serverUpdatedStatus;

    @SerializedName("HOId")
    private Integer HOId;

    //     "CompanyId": 413,
//             "HOId": null,
    // ✅ Getters and Setters
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("PlotCode", plotCode);
        map.put("StatusTypeId", statusTypeId);
        map.put("DripStatusDone", dripStatusDone ? 1 : 0);
        map.put("CompanyId", companyId);
        map.put("Date", date);
        map.put("Comments", comments);
        map.put("FileName", fileName);
        map.put("FileLocation", fileLocation);
        map.put("FileExtension", fileExtension);
        map.put("AckFileName", ackFileName);
        map.put("AckFileExtension", ackFileExtension);
        map.put("AckFileLocation", ackFileLocation);
        map.put("Amount", Amount);
        map.put("DDBank", ddBank);
        map.put("DDBankAccountNumber", ddBankAccountNumber);
        map.put("DDChequeNumber", ddChequeNumber);
        map.put("ModeOfPayment", modeOfPayment);
        map.put("TrenchMarkingTypeId", trenchMarkingTypeId);
        map.put("HorticultureRecommendedPlants", horticultureRecommendedPlants);
        map.put("IsActive", isActive ? 1 : 0);
        map.put("CreatedByUserId", createdByUserId);
        map.put("CreatedDate", createdDate);
        map.put("UpdatedByUserId", updatedByUserId);
        map.put("UpdatedDate", updatedDate);
        map.put("ServerUpdatedStatus", serverUpdatedStatus ? 1 : 0);
        map.put("HOId", HOId);
        return map;
    }

    public String getPlotCode() {
        return plotCode;
    }

    public void setPlotCode(String plotCode) {
        this.plotCode = plotCode;
    }

    public int getStatusTypeId() {
        return statusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        this.statusTypeId = statusTypeId;
    }

    public boolean isDripStatusDone() {
        return dripStatusDone;
    }

    public void setDripStatusDone(boolean dripStatusDone) {
        this.dripStatusDone = dripStatusDone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }



    public String getDdBank() {
        return ddBank;
    }

    public void setDdBank(String ddBank) {
        this.ddBank = ddBank;
    }

    public String getDdBankAccountNumber() {
        return ddBankAccountNumber;
    }

    public void setDdBankAccountNumber(String ddBankAccountNumber) {
        this.ddBankAccountNumber = ddBankAccountNumber;
    }

    public String getDdChequeNumber() {
        return ddChequeNumber;
    }

    public void setDdChequeNumber(String ddChequeNumber) {
        this.ddChequeNumber = ddChequeNumber;
    }

    public Integer getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(Integer modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public Integer getTrenchMarkingTypeId() {
        return trenchMarkingTypeId;
    }

    public void setTrenchMarkingTypeId(Integer trenchMarkingTypeId) {
        this.trenchMarkingTypeId = trenchMarkingTypeId;
    }

    public Integer getHorticultureRecommendedPlants() {
        return horticultureRecommendedPlants;
    }

    public void setHorticultureRecommendedPlants(Integer horticultureRecommendedPlants) {
        this.horticultureRecommendedPlants = horticultureRecommendedPlants;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }
    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getAckFileName() {
        return ackFileName;
    }

    public void setAckFileName(String ackFileName) {
        this.ackFileName = ackFileName;
    }

    public String getAckFileExtension() {
        return ackFileExtension;
    }

    public void setAckFileExtension(String ackFileExtension) {
        this.ackFileExtension = ackFileExtension;
    }

    public String getAckFileLocation() {
        return ackFileLocation;
    }

    public void setAckFileLocation(String ackFileLocation) {
        this.ackFileLocation = ackFileLocation;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }
    public Integer getHOId() {
        return HOId;
    }

    public void setHOId(Integer HOId) {
        this.HOId = HOId;
    }
}
