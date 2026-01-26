package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

/**=
 * Created by baliReddy on 3/10/2018.
 */

public class AdvancedDetails implements Serializable {

   // private  Integer AdvanceId;
    private  String PlotCode;
    private  Double FarmerContributionReceived;
    private  String DateOfAdvanceReceived;
    private  String  ExpectedMonthOfPlanting;
    private  int NoOfSaplingsAdvancePaidFor;
    private  int NoOfImportedSaplingsToBeIssued;
    private  int NoOfIndigenousSaplingsToBeIssued;
    private  Float AdvanceReceivedArea;
    private  String  SurveyNumber;
    private  String  ReceiptNumber;
    private  String Comments;
    private  int CreatedByUserId;
    private  String CreatedDate;
    private  int UpdatedByUserId;
    private  String UpdatedDate;
    private  double FarmerContributionPriceForIndigenousSaplings;
    private  double FarmerContributionPriceForImportedSaplings;
    private  double ModeOfPayment;
    private String ChequeNo;
    private String ChequeDate;
    private int BankId;
    private  Float TotalPriceOfImportedSaplings;
    private  Float TotalPriceOfIndigenousSaplings;
    private  Float TotalSaplingsPrice;
    private  Float SubsidyPriceForImportedSaplings;
    private  Float SubsidyPriceForIndigenousSaplings;
    private  Float SubsidyPrice;
    private float TotalTransportationcost,FarmerContributionTransportationcost,SubsidyTransportationcost;
    private String DepositedBankName;
    private int PlantationTypeId,ServerUpdatedStatus;
    private String FileName , FileExtension, FileLocation,PlantationType;
    private String UPINo;
    private String ByteImage;

    public float getTotalTransportationcost() {
        return TotalTransportationcost;
    }


//    public Integer getAdvanceId() {
//        return AdvanceId;
//    }
//
//    public void setAdvanceId(Integer advanceId) {
//        AdvanceId = advanceId;
//    }

    public void setTotalTransportationcost(float totalTransportationcost) {
        TotalTransportationcost = totalTransportationcost;
    }

    public float getFarmerContributionTransportationcost() {
        return FarmerContributionTransportationcost;
    }

    public void setFarmerContributionTransportationcost(float farmerContributionTransportationcost) {
        FarmerContributionTransportationcost = farmerContributionTransportationcost;
    }

    public float getSubsidyTransportationcost() {
        return SubsidyTransportationcost;
    }

    public void setSubsidyTransportationcost(float subsidyTransportationcost) {
        SubsidyTransportationcost = subsidyTransportationcost;
    }

    public AdvancedDetails(){

    }

    public String getPlotCode() {
        return PlotCode;
    }

    public void setPlotCode(String plotCode) {
        PlotCode = plotCode;
    }

    public Double getFarmerContributionReceived() {
        return FarmerContributionReceived;
    }

    public void setFarmerContributionReceived(Double farmerContributionReceived) {
        FarmerContributionReceived = farmerContributionReceived;
    }

    public String getDateOfAdvanceReceived() {
        return DateOfAdvanceReceived;
    }

    public void setDateOfAdvanceReceived(String dateOfAdvanceReceived) {
        DateOfAdvanceReceived = dateOfAdvanceReceived;
    }

    public String getExpectedMonthOfPlanting() {
        return ExpectedMonthOfPlanting;
    }

    public void setExpectedMonthOfPlanting(String expectedMonthOfPlanting) {
        ExpectedMonthOfPlanting = expectedMonthOfPlanting;
    }

    public int getNoOfSaplingsAdvancePaidFor() {
        return NoOfSaplingsAdvancePaidFor;
    }

    public void setNoOfSaplingsAdvancePaidFor(int noOfSaplingsAdvancePaidFor) {
        NoOfSaplingsAdvancePaidFor = noOfSaplingsAdvancePaidFor;
    }

    public int getNoOfImportedSaplingsToBeIssued() {
        return NoOfImportedSaplingsToBeIssued;
    }

    public void setNoOfImportedSaplingsToBeIssued(int noOfImportedSaplingsToBeIssued) {
        NoOfImportedSaplingsToBeIssued = noOfImportedSaplingsToBeIssued;
    }

    public int getNoOfIndigenousSaplingsToBeIssued() {
        return NoOfIndigenousSaplingsToBeIssued;
    }

    public void setNoOfIndigenousSaplingsToBeIssued(int noOfIndigenousSaplingsToBeIssued) {
        NoOfIndigenousSaplingsToBeIssued = noOfIndigenousSaplingsToBeIssued;
    }

    public Float getAdvanceReceivedArea() {
        return AdvanceReceivedArea;
    }

    public void setAdvanceReceivedArea(Float advanceReceivedArea) {
        AdvanceReceivedArea = advanceReceivedArea;
    }

    public String getSurveyNumber() {
        return SurveyNumber;
    }

    public void setSurveyNumber(String surveyNumber) {
        SurveyNumber = surveyNumber;
    }

    public String getReceiptNumber() {
        return ReceiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNumber = receiptNumber;
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

    public double getFarmerContributionPriceForIndigenousSaplings() {
        return FarmerContributionPriceForIndigenousSaplings;
    }

    public void setFarmerContributionPriceForIndigenousSaplings(double farmerContributionPriceForIndigenousSaplings) {
        FarmerContributionPriceForIndigenousSaplings = farmerContributionPriceForIndigenousSaplings;
    }

    public double getFarmerContributionPriceForImportedSaplings() {
        return FarmerContributionPriceForImportedSaplings;
    }

    public void setFarmerContributionPriceForImportedSaplings(double farmerContributionPriceForImportedSaplings) {
        FarmerContributionPriceForImportedSaplings = farmerContributionPriceForImportedSaplings;
    }

    public double getModeOfPayment() {
        return ModeOfPayment;
    }

    public void setModeOfPayment(double modeOfPayment) {
        ModeOfPayment = modeOfPayment;
    }

    public String getChequeNo() {
        return ChequeNo;
    }

    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }

    public String getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(String chequeDate) {
        ChequeDate = chequeDate;
    }

    public int getBankId() {
        return BankId;
    }

    public void setBankId(int bankId) {
        BankId = bankId;
    }

    public Float getTotalPriceOfImportedSaplings() {
        return TotalPriceOfImportedSaplings;
    }

    public void setTotalPriceOfImportedSaplings(Float totalPriceOfImportedSaplings) {
        TotalPriceOfImportedSaplings = totalPriceOfImportedSaplings;
    }

    public Float getTotalPriceOfIndigenousSaplings() {
        return TotalPriceOfIndigenousSaplings;
    }

    public void setTotalPriceOfIndigenousSaplings(Float totalPriceOfIndigenousSaplings) {
        TotalPriceOfIndigenousSaplings = totalPriceOfIndigenousSaplings;
    }

    public Float getTotalSaplingsPrice() {
        return TotalSaplingsPrice;
    }

    public void setTotalSaplingsPrice(Float totalSaplingsPrice) {
        TotalSaplingsPrice = totalSaplingsPrice;
    }

    public Float getSubsidyPriceForImportedSaplings() {
        return SubsidyPriceForImportedSaplings;
    }

    public void setSubsidyPriceForImportedSaplings(Float subsidyPriceForImportedSaplings) {
        SubsidyPriceForImportedSaplings = subsidyPriceForImportedSaplings;
    }

    public Float getSubsidyPriceForIndigenousSaplings() {
        return SubsidyPriceForIndigenousSaplings;
    }

    public void setSubsidyPriceForIndigenousSaplings(Float subsidyPriceForIndigenousSaplings) {
        SubsidyPriceForIndigenousSaplings = subsidyPriceForIndigenousSaplings;
    }

    public Float getSubsidyPrice() {
        return SubsidyPrice;
    }

    public void setSubsidyPrice(Float subsidyPrice) {
        SubsidyPrice = subsidyPrice;
    }
    public String getDepositedBankName(){
        return DepositedBankName;
    }
    public void setDepositedBankName(String depositedBankName){
        DepositedBankName = depositedBankName;
    }
    public int getPlantationTypeId(){
        return PlantationTypeId;
    }
    public void setPlantationTypeId(int plantationTypeId){
        PlantationTypeId = plantationTypeId;
    }
    public int getServerUpdatedStatus(){
        return ServerUpdatedStatus;
    }
    public void setServerUpdatedStatus(int serverUpdatedStatus){
        ServerUpdatedStatus = serverUpdatedStatus;
    }
    public String getFileName(){
        return FileName;
    }
    public void setFileName(String fileName){
        FileName = fileName;
    }
    public String getFileExtension(){
        return FileExtension;
    }
    public void setFileExtension(String fileExtension){
        FileExtension = fileExtension;
    }
    public String getFileLocation(){
        return FileLocation;
    }
    public void setFileLocation(String fileLocation){
        FileLocation = fileLocation;
    }

    public String getByteImage() {
        return ByteImage;
    }

    public void setByteImage(String byteImage) {
        ByteImage = byteImage;
    }

    @Override
    public String toString() {
        return "AdvancedDetails{" +
                "PlotCode='" + PlotCode + '\'' +
                ", FarmerContributionReceived=" + FarmerContributionReceived +
                ", DateOfAdvanceReceived='" + DateOfAdvanceReceived + '\'' +
                ", ExpectedMonthOfPlanting='" + ExpectedMonthOfPlanting + '\'' +
                ", NoOfSaplingsAdvancePaidFor=" + NoOfSaplingsAdvancePaidFor +
                ", NoOfImportedSaplingsToBeIssued=" + NoOfImportedSaplingsToBeIssued +
                ", NoOfIndigenousSaplingsToBeIssued=" + NoOfIndigenousSaplingsToBeIssued +
                ", AdvanceReceivedArea=" + AdvanceReceivedArea +
                ", SurveyNumber='" + SurveyNumber + '\'' +
                ", ReceiptNumber='" + ReceiptNumber + '\'' +
                ", Comments='" + Comments + '\'' +
                ", CreatedByUserId=" + CreatedByUserId +
                ", CreatedDate='" + CreatedDate + '\'' +
                ", UpdatedByUserId=" + UpdatedByUserId +
                ", UpdatedDate='" + UpdatedDate + '\'' +
                ", FarmerContributionPriceForIndigenousSaplings=" + FarmerContributionPriceForIndigenousSaplings +
                ", FarmerContributionPriceForImportedSaplings=" + FarmerContributionPriceForImportedSaplings +
                ", ModeOfPayment=" + ModeOfPayment +
                ", ChequeNo='" + ChequeNo + '\'' +
                ", ChequeDate='" + ChequeDate + '\'' +
                ", BankId=" + BankId +
                ", TotalPriceOfImportedSaplings=" + TotalPriceOfImportedSaplings +
                ", TotalPriceOfIndigenousSaplings=" + TotalPriceOfIndigenousSaplings +
                ", TotalSaplingsPrice=" + TotalSaplingsPrice +
                ", SubsidyPriceForImportedSaplings=" + SubsidyPriceForImportedSaplings +
                ", SubsidyPriceForIndigenousSaplings=" + SubsidyPriceForIndigenousSaplings +
                ", SubsidyPrice=" + SubsidyPrice +
                ", TotalTransportationcost=" + TotalTransportationcost +
                ", FarmerContributionTransportationcost=" + FarmerContributionTransportationcost +
                ", SubsidyTransportationcost=" + SubsidyTransportationcost +
                ", DepositedBankName='" + DepositedBankName + '\'' +
                ", PlantationTypeId=" + PlantationTypeId +
                ", ServerUpdatedStatus=" + ServerUpdatedStatus +
                ", FileName='" + FileName + '\'' +
                ", FileExtension='" + FileExtension + '\'' +
                ", FileLocation='" + FileLocation + '\'' +
                ", ByteImage='" + ByteImage + '\'' +
                '}';
    }

    public String getUPINo() {
        return UPINo;
    }

    public void setUPINo(String UPINo) {
        this.UPINo = UPINo;
    }
}
