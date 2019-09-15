package com.vibhor.project.network;

import com.vibhor.project.model.PlacesResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiServices {

    @GET("search")
    Observable<Response<PlacesResponse>> explorePlacesNearby(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("v") String date, @Query("ll") String latLng, @Query("radius") int radius);

    @GET("search")
    Observable<Response<PlacesResponse>> searchPlacesNearby(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("v") String date, @Query("ll") String latLng, @Query("radius") int radius, @Query("query") String query);

}
