package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

public class PlantaionMethod implements Serializable {

    private int id;
    private int plantationMethodTypeId;
    private int saplingCount;
    private int isActive; // use 1 or 0
    private int createdByUserId;
    private String createdDate;      // format: "yyyy-MM-dd HH:mm:ss"
    private int updatedByUserId;
    private String updatedDate;

    // Empty constructor
    public PlantaionMethod() {
    }

    // Full constructor
    public PlantaionMethod(int id, int plantationMethodTypeId, int saplingCount, int isActive,
                           int createdByUserId, String createdDate,
                           int updatedByUserId, String updatedDate) {
        this.id = id;
        this.plantationMethodTypeId = plantationMethodTypeId;
        this.saplingCount = saplingCount;
        this.isActive = isActive;
        this.createdByUserId = createdByUserId;
        this.createdDate = createdDate;
        this.updatedByUserId = updatedByUserId;
        this.updatedDate = updatedDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlantationMethodTypeId() {
        return plantationMethodTypeId;
    }

    public void setPlantationMethodTypeId(int plantationMethodTypeId) {
        this.plantationMethodTypeId = plantationMethodTypeId;
    }

    public int getSaplingCount() {
        return saplingCount;
    }

    public void setSaplingCount(int saplingCount) {
        this.saplingCount = saplingCount;
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
}

