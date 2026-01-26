package com.cis.palm360.palmgrow.SuvenAgro.dispatch.dbmodel;


public class SaplingDispatchRequestGet  {
        private String AdvanceReceiptNumber;
        private String ReceiptNumber;
        private int NoOfImportedSaplingsToDispatch;
        private int NoOfIndigenousSaplingsToDispatch;
        private int NoOfSaplingsToDispatch;
        private int StatusId;
        private String ExpDateOfPickup;
      //  private int IsActive;
        private String Comments;
        private int CreatedByUserId;
        private String CreatedDate;
        private int UpdatedByUserId;
        private String UpdatedDate;

        public SaplingDispatchRequestGet() {
            // Default constructor
        }


        public String getReceiptNumber() {
            return ReceiptNumber;
        }

        public void setReceiptNumber(String receiptNumber) {
            ReceiptNumber = receiptNumber;
        }

        public int getNoOfImportedSaplingsToDispatch() {
            return NoOfImportedSaplingsToDispatch;
        }

        public void setNoOfImportedSaplingsToDispatch(int noOfImportedSaplingsToDispatch) {
            NoOfImportedSaplingsToDispatch = noOfImportedSaplingsToDispatch;
        }

        public int getNoOfIndigenousSaplingsToDispatch() {
            return NoOfIndigenousSaplingsToDispatch;
        }

        public void setNoOfIndigenousSaplingsToDispatch(int noOfIndigenousSaplingsToDispatch) {
            NoOfIndigenousSaplingsToDispatch = noOfIndigenousSaplingsToDispatch;
        }

        public int getNoOfSaplingsToDispatch() {
            return NoOfSaplingsToDispatch;
        }

        public void setNoOfSaplingsToDispatch(int noOfSaplingsToDispatch) {
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

//        public int getIsActive() {
//            return IsActive;
//        }
//
//        public void setIsActive(int isActive) {
//            IsActive = isActive;
//        }

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
