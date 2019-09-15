package com.vibhor.yulusearch.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;

@Entity
public class SearchResults {

    @Id
    long dbId;

    @Unique
    private String venueId;
    private String venueJson;

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueJson() {
        return venueJson;
    }

    public void setVenueJson(String venueJson) {
        this.venueJson = venueJson;
    }
}
