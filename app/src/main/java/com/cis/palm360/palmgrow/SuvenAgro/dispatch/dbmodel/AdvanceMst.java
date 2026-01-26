package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;

import com.google.gson.annotations.SerializedName;

public class AdvanceMst {
        @SerializedName("StateId")
        private int stateId;

        @SerializedName("TypeOfLifting")
        private int typeOfLifting;

        @SerializedName("SourceOfSaplings")
        private int sourceOfSaplings;

        @SerializedName("TotalSaplingsPrice")
        private float totalSaplingsPrice;

        @SerializedName("FarmerContributionReceived")
        private float farmerContributionReceived;

        @SerializedName("SubsidyPrice")
        private float subsidyPrice;

        @SerializedName("TotalTransportationCost")
        private float totalTransportationCost;

        @SerializedName("FarmerContributiontransportationcost")
        private float farmerContributionTransportationCost;

        @SerializedName("Subsidytransportationcost")
        private float subsidyTransportationCost;

        @SerializedName("IsActive")
        private int isActive;

        @SerializedName("CreatedByUserId")
        private int createdByUserId;

        @SerializedName("CreatedDate")
        private String createdDate;

        @SerializedName("UpdatedByUserId")
        private int updatedByUserId;

        @SerializedName("UpdatedDate")
        private String updatedDate;

        @SerializedName("ZoneId")
        private int zoneId;

        // Getters and Setters
        public int getStateId() {
            return stateId;
        }

        public void setStateId(int stateId) {
            this.stateId = stateId;
        }

        public int getTypeOfLifting() {
            return typeOfLifting;
        }

        public void setTypeOfLifting(int typeOfLifting) {
            this.typeOfLifting = typeOfLifting;
        }

        public int getSourceOfSaplings() {
            return sourceOfSaplings;
        }

        public void setSourceOfSaplings(int sourceOfSaplings) {
            this.sourceOfSaplings = sourceOfSaplings;
        }

        public float getTotalSaplingsPrice() {
            return totalSaplingsPrice;
        }

        public void setTotalSaplingsPrice(float totalSaplingsPrice) {
            this.totalSaplingsPrice = totalSaplingsPrice;
        }

        public float getFarmerContributionReceived() {
            return farmerContributionReceived;
        }

        public void setFarmerContributionReceived(float farmerContributionReceived) {
            this.farmerContributionReceived = farmerContributionReceived;
        }

        public float getSubsidyPrice() {
            return subsidyPrice;
        }

        public void setSubsidyPrice(float subsidyPrice) {
            this.subsidyPrice = subsidyPrice;
        }

        public float getTotalTransportationCost() {
            return totalTransportationCost;
        }

        public void setTotalTransportationCost(float totalTransportationCost) {
            this.totalTransportationCost = totalTransportationCost;
        }

        public float getFarmerContributionTransportationCost() {
            return farmerContributionTransportationCost;
        }

        public void setFarmerContributionTransportationCost(float farmerContributionTransportationCost) {
            this.farmerContributionTransportationCost = farmerContributionTransportationCost;
        }

        public float getSubsidyTransportationCost() {
            return subsidyTransportationCost;
        }

        public void setSubsidyTransportationCost(float subsidyTransportationCost) {
            this.subsidyTransportationCost = subsidyTransportationCost;
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

        public int getZoneId() {
            return zoneId;
        }

        public void setZoneId(int zoneId) {
            this.zoneId = zoneId;
        }

}
