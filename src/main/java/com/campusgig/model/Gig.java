package com.campusgig.model;

import java.time.LocalDate;

public class Gig {

    private int gigId;
    private int posterId;
    private String title;
    private String description;
    private double budget;
    private LocalDate deadline;
    private String status;

    public Gig() {
    }

    public Gig(int gigId, int posterId, String title, String description,
               double budget, LocalDate deadline, String status) {
        this.gigId = gigId;
        this.posterId = posterId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.status = status;
    }

    public int getGigId() {
        return gigId;
    }

    public void setGigId(int gigId) {
        this.gigId = gigId;
    }

    public int getPosterId() {
        return posterId;
    }

    public void setPosterId(int posterId) {
        this.posterId = posterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
