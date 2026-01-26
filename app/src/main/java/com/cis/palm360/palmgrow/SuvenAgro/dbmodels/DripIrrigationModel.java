package com.cis.palm360.palmgrow.SuvenAgro.dbmodels;

public class DripIrrigationModel {
  //  private int id;
    private String plotCode;
    private int statusTypeId;
    private int dripStatusDone;
    private String date;
    private String comments;
    private String fileName;
    private String fileLocation;
    private String fileExtension;
    private Double Amount;
    private String ddBank;
    private String ddBankAccountNumber;
    private String ddChequeNumber;
    private Integer ModeOfPayment;// Nullable
    private Integer trenchMarkingTypeId; // Nullable
    private Integer horticultureRecommendedPlants;

    private int isActive;
    private int createdByUserId;
    private String createdDate;
    private int updatedByUserId;
    private String updatedDate;
    private Integer serverUpdatedStatus; // Nullable
    private Integer CompanyId;
    private String AckFileName;
    private String AckFileLocation;
    private String AckFileExtension;
    private String ByteImage;
    private String AckByteImage;
    private Integer HOId;
    // Constructors
    public DripIrrigationModel() {
    }



    // Getters and Setters
    // -- You can generate these using your IDE (Right-click → Generate → Getter and Setter)

    // Sample Getter & Setter:
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

    public int getDripStatusDone() {
        return dripStatusDone;
    }

    public void setDripStatusDone(int dripStatusDone) {
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

//    public Double getDdAmount() {
//        return ddAmount;
//    }
//
//    public void setDdAmount(Double ddAmount) {
//        this.ddAmount = ddAmount;
//    }

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

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
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

    public Integer getServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(Integer serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }

    public Integer getModeOfPayment() {
        return ModeOfPayment;
    }

    public void setModeOfPayment(Integer modeOfPayment) {
        ModeOfPayment = modeOfPayment;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getByteImage() {
        return ByteImage;
    }

    public void setByteImage(String byteImage) {
        ByteImage = byteImage;
    }


    public Integer getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(Integer companyId) {
        CompanyId = companyId;
    }

    public String getAckFileName() {
        return AckFileName;
    }

    public void setAckFileName(String ackFileName) {
        AckFileName = ackFileName;
    }

    public String getAckFileLocation() {
        return AckFileLocation;
    }

    public void setAckFileLocation(String ackFileLocation) {
        AckFileLocation = ackFileLocation;
    }

    public String getAckFileExtension() {
        return AckFileExtension;
    }

    public void setAckFileExtension(String ackFileExtension) {
        AckFileExtension = ackFileExtension;
    }

    public String getAckByteImage() {
        return AckByteImage;
    }

    public void setAckByteImage(String ackByteImage) {
        AckByteImage = ackByteImage;
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

//submit.setOnClickListener(v -> {
//        if (dripList.isEmpty()) {
//        Toast.makeText(getContext(), "No answers to submit", Toast.LENGTH_SHORT).show();
//        return;
//                }
//
//// Get latest/last answered model
//DripIrrigationModel latestModel = dripList.get(dripList.size() - 1);
//
//// Save to DataManager like WaterResource
//List<DripIrrigationModel> modelList = new ArrayList<>();
//    modelList.add(latestModel);
//    DataManager.getInstance().addData("DRIP_IRRIGATION", modelList);
//
//    Toast.makeText(getContext(), "Drip Irrigation Data Saved to DataManager!", Toast.LENGTH_SHORT).show();
//});
