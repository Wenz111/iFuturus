package com.example.ifuturus.model;

public class feedbackmodel {

    // Declare Variables
    private String userid;
    private String feedbackdetails;

    public feedbackmodel() {
    }

    public feedbackmodel(String userid, String feedbackdetails) {
        this.userid = userid;
        this.feedbackdetails = feedbackdetails;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFeedbackdetails() {
        return feedbackdetails;
    }

    public void setFeedbackdetails(String feedbackdetails) {
        this.feedbackdetails = feedbackdetails;
    }
}
