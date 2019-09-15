package com.vibhor.yulusearch.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vibhor.yulusearch.R;
import com.vibhor.yulusearch.adapters.PlacesListAdapter;
import com.vibhor.yulusearch.constants.NetworkConstants;
import com.vibhor.yulusearch.model.PlacesResponse;
import com.vibhor.yulusearch.model.Venues;
import com.vibhor.yulusearch.network.ServiceFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private LatLng latLng;

    @BindView(R.id.map_centered_bt)
    Button mapCenteredButton;

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.choose_nearby_tv)
    TextView nearbyTv;

    @BindView(R.id.search_et)
    AppCompatEditText searchEt;

    private static final double DEFAULT_LATITUDE = 12.972442;
    private static final double DEFAULT_LONGITUDE = 77.580643;

    private ServiceFactory serviceFactory;
    private SimpleDateFormat simpleDateFormat;

    private BottomSheetBehavior<View> sheetBehavior;
    @BindView(R.id.nearby_search_bs)
    View nearBySearchBs;

    private PlacesListAdapter placesListAdapter;
    @BindView(R.id.nearby_search_rv)
    RecyclerView nearBySearchRv;

    private final float[] markerColors = new float[]{
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        init();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, 0, 0, 0);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.setOnCameraIdleListener(this);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        latLng = cameraPosition.target;
    }


    @SuppressLint("SimpleDateFormat")
    private void init() {

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        sheetBehavior = BottomSheetBehavior.from(nearBySearchBs);
        serviceFactory = new ServiceFactory(NetworkConstants.PLACES_BASE_URL);

        placesListAdapter = new PlacesListAdapter(new ArrayList<>());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = searchEt.getText() != null ? searchEt.getText().toString() : null;
                if (TextUtils.isEmpty(searchQuery)) {
                    Toast.makeText(MapsActivity.this, "Search message cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
                    }
                    searchPlacesNearby(latLng, searchQuery);
                }
                return true;
            }
            return false;
        });

        getUserCurrentLocation();
    }

    private void getUserCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    } else {
                        latLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
                    }
                    setMap();
                }).addOnFailureListener(this, e -> {
            latLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
            setMap();
        });
    }

    private void setMap() {
        if (latLng != null) {
            setCameraPosition(15);
            getAllNearByPlaces(latLng);
            searchEt.setText("");
        }
    }


    @OnClick(R.id.map_centered_bt)
    public void onMapCenteredButtonClick() {
        getUserCurrentLocation();
    }

    private void reloadDetails() {
        progressBar.setVisibility(View.VISIBLE);
        nearbyTv.setVisibility(View.GONE);
        placesListAdapter.updateList(new ArrayList<>());
        nearBySearchRv.setAdapter(placesListAdapter);
        if (mMap != null) {
            mMap.clear();
        }
    }


    private void getAllNearByPlaces(LatLng latLng) {

        if (latLng == null) {
            Toast.makeText(MapsActivity.this, "Something went wrong. Cannot detect your location!", Toast.LENGTH_LONG).show();
            return;
        }

        reloadDetails();

        String date = simpleDateFormat.format(new Date());
        String latLngString = latLng.latitude + "," + latLng.longitude;

        serviceFactory.getBaseService()
                .explorePlacesNearby(getResources().getString(R.string.foursquare_client_id), getResources().getString(R.string.foursquare_client_secret),
                        date, latLngString, 500)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PlacesResponse>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("YULU", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("YULU", "onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<PlacesResponse> placesResponseResponse) {
                        Log.d("YULU", "onNext");
                        if (placesResponseResponse.code() == 200 && placesResponseResponse.body() != null) {
                            List<Venues> placeList = placesResponseResponse.body().getResponse().getVenues();
                            nearBySearchRv.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
                            placesListAdapter.updateList(placeList);
                            nearBySearchRv.setAdapter(placesListAdapter);
                            progressBar.setVisibility(View.GONE);
                            nearbyTv.setVisibility(View.VISIBLE);
                            sheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.peek_height));
                            setMarkers(placeList);
                        }
                    }
                });
    }


    private void setMarkers(List<Venues> venues) {
        if (mMap == null || venues == null || venues.size() == 0)
            return;

        for (Venues venue : venues) {
            double lat = Double.parseDouble((venue.getLocation().getLat()));
            double lng = Double.parseDouble((venue.getLocation().getLng()));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(venue.getName())
                    .snippet(venue.getLocation().getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColors[new Random().nextInt(markerColors.length)])));
        }

    }

    private void searchPlacesNearby(LatLng latLng, String searchQuery) {

        if (latLng == null) {
            Toast.makeText(MapsActivity.this, "Something went wrong. Cannot detect your location!", Toast.LENGTH_LONG).show();
            return;
        }

        reloadDetails();

        String date = simpleDateFormat.format(new Date());
        String latLngString = latLng.latitude + "," + latLng.longitude;

        serviceFactory.getBaseService()
                .searchPlacesNearby(getResources().getString(R.string.foursquare_client_id), getResources().getString(R.string.foursquare_client_secret),
                        date, latLngString, 2000, searchQuery)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PlacesResponse>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("YULU", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("YULU", "onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(Response<PlacesResponse> placesResponseResponse) {
                        Log.d("YULU", "onNext");
                        if (placesResponseResponse.code() == 200 && placesResponseResponse.body() != null) {
                            List<Venues> placeList = placesResponseResponse.body().getResponse().getVenues();
                            if (placeList.size() == 0) {
                                Toast.makeText(MapsActivity.this, "Oh Damn! No results found!", Toast.LENGTH_LONG).show();
                            }
                            nearBySearchRv.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
                            placesListAdapter.updateList(placeList);
                            nearBySearchRv.setAdapter(placesListAdapter);
                            progressBar.setVisibility(View.GONE);
                            nearbyTv.setVisibility(View.VISIBLE);
                            sheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.peek_height));
                            setMarkers(placeList);
                            setCameraPosition(13);
                        }
                    }
                });
    }

    private void setCameraPosition(int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}