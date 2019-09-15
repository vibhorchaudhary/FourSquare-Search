package com.vibhor.yulusearch.network;

import com.vibhor.yulusearch.model.PlacesResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiServices {

    @GET("search")
    Observable<Response<PlacesResponse>> explorePlacesNearby(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("v") String date, @Query("ll") String latLng, @Query("radius") int radius);

}
