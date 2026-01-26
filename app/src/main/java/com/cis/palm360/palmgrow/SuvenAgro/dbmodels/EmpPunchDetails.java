package com.cis.palm360.palmgrow.SuvenAgro.dbmodels;
public class EmpPunchDetails {

    private int id;
    private int userId;
    private String logDate; // Or use java.util.Date if parsing
    private boolean isPunchIn;
    private double latitude;
    private double longitude;
    private String address;
    private String fileName;
    private String fileLocation;
    private String fileExtension;
    private String serverUpdatedStatus; // Assuming it's a String; update type if different
    private String ByteImage;

    // Constructor
/*
    public EmpPunchDetails(int id, int userId, String logDate, boolean isPunchIn, double latitude, double longitude,
                           String address, String fileName, String fileLocation, String fileExtension, String serverUpdatedStatus) {
        this.id = id;
        this.userId = userId;
        this.logDate = logDate;
        this.isPunchIn = isPunchIn;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
        this.fileExtension = fileExtension;
        this.serverUpdatedStatus = serverUpdatedStatus;
    }
*/

    // Default constructor
    public EmpPunchDetails() {
    }

    // Getters and Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getLogDate() { return logDate; }

    public void setLogDate(String logDate) { this.logDate = logDate; }

    public boolean isPunchIn() { return isPunchIn; }

    public void setPunchIn(boolean punchIn) { isPunchIn = punchIn; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileLocation() { return fileLocation; }

    public void setFileLocation(String fileLocation) { this.fileLocation = fileLocation; }

    public String getFileExtension() { return fileExtension; }

    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }

    public String getServerUpdatedStatus() { return serverUpdatedStatus; }

    public void setServerUpdatedStatus(String serverUpdatedStatus) { this.serverUpdatedStatus = serverUpdatedStatus; }
    public String getByteImage() {
        return ByteImage;
    }

    public void setByteImage(String byteImage) {
        ByteImage = byteImage;
    }
}
