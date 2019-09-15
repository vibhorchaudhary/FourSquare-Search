package com.vibhor.project.managers;

import com.vibhor.project.entity.MyObjectBox;
import com.vibhor.project.entity.SearchResults;
import com.vibhor.project.entity.SearchResults_;
import com.vibhor.project.utils.MyApplication;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.exception.UniqueViolationException;

public class DatabaseManager {

    private static BoxStore boxStore;

    private Box<SearchResults> searchResultsBox;

    private final String DATABASE_NAME = "DATABASE";

    public DatabaseManager() {
        if (boxStore == null || boxStore.isClosed()) {
            boxStore = MyObjectBox.builder().androidContext(MyApplication.getContext()).name(DATABASE_NAME).build();
        }
    }

    private BoxStore getBoxStore() {
        if (boxStore == null || boxStore.isClosed()) {
            boxStore = MyObjectBox.builder().androidContext(MyApplication.getContext()).name(DATABASE_NAME).build();
        }
        return boxStore;
    }

    private Box<SearchResults> getSearchResultsBox() {
        if (searchResultsBox == null) {
            searchResultsBox = getBoxStore().boxFor(SearchResults.class);
        }
        return searchResultsBox;
    }

    public void saveSearchResultsInDatabase(List<SearchResults> searchResults) {
        try {
            getSearchResultsBox().put(searchResults);
        } catch (UniqueViolationException e) {
            for (SearchResults searchResult : searchResults) {
                try {
                    getSearchResultsBox().put(searchResult);
                } catch (UniqueViolationException ex) {
                    deleteSearchResultById(searchResult.getVenueId());
                    saveSearchResultInDatabase(searchResult);
                }
            }
        }
    }

    private void deleteSearchResultById(String venueId) {
        try {
            getSearchResultsBox().query().equal(SearchResults_.venueId, venueId).build().remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSearchResultInDatabase(SearchResults searchResults) {
        try {
            getSearchResultsBox().put(searchResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSearchResult(SearchResults searchResult) {
        try {
            getSearchResultsBox().remove(searchResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<SearchResults> getAllSearchResults() {
        return getSearchResultsBox().getAll();
    }

}
