package com.vibhor.yulusearch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("venues")
    @Expose
    private List<Venues> venues;

    private boolean confident;

    public List<Venues> getVenues() {
        return venues;
    }

    public void setVenues(List<Venues> venues) {
        this.venues = venues;
    }

    public boolean isConfident() {
        return confident;
    }

    public void setConfident(boolean confident) {
        this.confident = confident;
    }
}
