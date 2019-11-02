package com.example.ifuturus.model;

public class lodgereportmodel {

    // Declare Variables
    private String id;
    private String name;
    private String email;
    private String gender;
    private String complaintDetails;
    private String complaintNotes;
    private String complaintLocation;
    private String complaintCategory;
    private String complaintStatus;
    private String complaintId;
    private String complaintDate;
    private String complaintTime;
    private String photoUrl;

    public lodgereportmodel() {
    }

    public lodgereportmodel(String id, String name, String email, String gender, String complaintDetails, String complaintNotes, String complaintLocation, String complaintCategory, String complaintStatus, String complaintId, String complaintDate, String complaintTime, String photoUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.complaintDetails = complaintDetails;
        this.complaintNotes = complaintNotes;
        this.complaintLocation = complaintLocation;
        this.complaintCategory = complaintCategory;
        this.complaintStatus = complaintStatus;
        this.complaintId = complaintId;
        this.complaintDate = complaintDate;
        this.complaintTime = complaintTime;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getComplaintDetails() {
        return complaintDetails;
    }

    public void setComplaintDetails(String complaintDetails) {
        this.complaintDetails = complaintDetails;
    }

    public String getComplaintNotes() {
        return complaintNotes;
    }

    public void setComplaintNotes(String complaintNotes) {
        this.complaintNotes = complaintNotes;
    }

    public String getComplaintLocation() {
        return complaintLocation;
    }

    public void setComplaintLocation(String complaintLocation) {
        this.complaintLocation = complaintLocation;
    }

    public String getComplaintCategory() {
        return complaintCategory;
    }

    public void setComplaintCategory(String complaintCategory) {
        this.complaintCategory = complaintCategory;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(String complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getComplaintTime() {
        return complaintTime;
    }

    public void setComplaintTime(String complaintTime) {
        this.complaintTime = complaintTime;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
