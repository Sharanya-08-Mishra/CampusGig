package com.campusgig.model;

public class Application {

    private int applicationId;
    private int gigId;
    private int applicantId;
    private String pitchText;
    private String portfolioPath;
    private String status;

    public Application() {
    }

    public Application(int applicationId, int gigId, int applicantId,
                       String pitchText, String portfolioPath, String status) {
        this.applicationId = applicationId;
        this.gigId = gigId;
        this.applicantId = applicantId;
        this.pitchText = pitchText;
        this.portfolioPath = portfolioPath;
        this.status = status;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getGigId() {
        return gigId;
    }

    public void setGigId(int gigId) {
        this.gigId = gigId;
    }

    public int getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(int applicantId) {
        this.applicantId = applicantId;
    }

    public String getPitchText() {
        return pitchText;
    }

    public void setPitchText(String pitchText) {
        this.pitchText = pitchText;
    }

    public String getPortfolioPath() {
        return portfolioPath;
    }

    public void setPortfolioPath(String portfolioPath) {
        this.portfolioPath = portfolioPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
