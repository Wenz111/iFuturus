package com.example.ifuturus.model;

public class chatmodel {

    // Declare Variables
    private String userid;
    private String reportid;
    private String chatmessage;
    private Long timestamp;
    private String photoUrl;

    public chatmodel() {
    }

    public chatmodel(String userid, String reportid, String chatmessage, Long timestamp, String photoUrl) {
        this.userid = userid;
        this.reportid = reportid;
        this.chatmessage = chatmessage;
        this.timestamp = timestamp;
        this.photoUrl = photoUrl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public String getChatmessage() {
        return chatmessage;
    }

    public void setChatmessage(String chatmessage) {
        this.chatmessage = chatmessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
