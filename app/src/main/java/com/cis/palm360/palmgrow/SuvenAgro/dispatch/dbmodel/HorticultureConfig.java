package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import java.io.Serializable;

public class HorticultureConfig implements Serializable {
    private int Id;
    private int MandalId;
    private String HOName;
    private int IsActive;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;

    public HorticultureConfig()
    {

    }
    public int getMandalId(){
        return MandalId;
    }
    public void setMandalId(int mandalId){
        MandalId = mandalId;
    }
    public String getHOName(){
        return HOName;
    }
    public void setHOName(String hoName){
        HOName = hoName;
    }
    public int getIsActive(){
        return IsActive;
    }
    public void setIsActive(int isActive){
        IsActive = isActive;
    }
    public int getCreatedByUserId(){
        return CreatedByUserId;
    }
    public void setCreatedByUserId(int createdByUserId){
        CreatedByUserId = createdByUserId;
    }
    public String getCreatedDate(){
        return CreatedDate;
    }
    public void setCreatedDate(String createdDate){
        CreatedDate = createdDate;
    }
    public int getUpdatedByUserId(){
        return UpdatedByUserId;
    }
    public void setUpdatedByUserId(int updatedByUserId){
        UpdatedByUserId = updatedByUserId;
    }
    public String getUpdatedDate(){
        return UpdatedDate;
    }
    public void setUpdatedDate(String updatedDate){
        UpdatedDate = updatedDate;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}