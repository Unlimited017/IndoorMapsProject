package com.example.indoormapsproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Resources;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;

import static com.example.indoormapsproject.Driver.initStore;
import static com.example.indoormapsproject.Driver.initialStores;
import static com.example.indoormapsproject.Driver.selectCount;

public class MapsActivityIndoor extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapsActivityIndoor.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(13.746830, 100.535066); //SIAM
    //private final LatLng mDefaultLocation = new LatLng(13.6467208,100.6794231); //MEGA
    private static final int DEFAULT_ZOOM = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    int PLACE_PICKER_REQUEST = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the Nearby place.
    private static final int M_MAX_ENTRIES = 10;
    private String[] nearbyPlaceNames;
    private String[] nearbyPlaceAddresses;
    private String[] nearbyPlaceAttributions;
    private LatLng[] nearbyPlaceLatLngs;

    public int bottom_select = 1;
    public int countSelect = 1;

    static double distanceAll;
    private static final int COLOR_HALFRED = 0x7fff0000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.indoor_map_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        //Initial Maps
        init();

        // Change Style
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
        Check Permission
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getCount();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            nearbyPlaceNames = new String[count];
                            nearbyPlaceAddresses = new String[count];
                            nearbyPlaceAttributions = new String[count];
                            nearbyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                // Build a list of likely places to show the user.
                                nearbyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                nearbyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                        .getAddress();
                                nearbyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                        .getAttributions();
                                nearbyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            // Release the place likelihood buffer, to avoid memory leaks.
                            likelyPlaces.release();

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.
                            openPlacesDialog();
                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = nearbyPlaceLatLngs[which];
            String markerSnippet = nearbyPlaceAddresses[which];
            if (nearbyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + nearbyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(new MarkerOptions()
                    .title(nearbyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(nearbyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void init() {
        mMap.setIndoorEnabled(true);
        initStoreMarker();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.select) {
            if (bottom_select == 1) {
                Toast.makeText(getApplicationContext(), "Select Store", Toast.LENGTH_SHORT).show();
                click();
            } else {
                reset();
            }
        }

        if (v.getId() == R.id.start_cal) {
            if (bottom_select == 1) {
                Toast.makeText(getApplicationContext(), "Please, Select Store", Toast.LENGTH_SHORT).show();
                }
            else if (bottom_select == 0) {
                Driver driver = new Driver();
                ArrayList<Store> stores = new ArrayList<Store>();

                Driver.selectLat[0] = mLastKnownLocation.getLatitude();
                Driver.selectLong[0] = mLastKnownLocation.getLongitude();
                Driver.nameStore[0] = "CurrentLocation";
                Driver.selectCount = countSelect;

                Driver.initStore();
                stores.addAll(Driver.initialStores);
                driver.printShortestRoute(new NearestNeighbor().findShortestRoute(stores));
            /*
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(new LatLng(Driver.selectLat[0],Driver.selectLong[0])));
                polyline1.setColor(COLOR_HALFRED);
            */
                Toast.makeText(getApplicationContext(),driver.nameStore[0] + " " + driver.nameStore[selectCount-1] + " " + distanceAll,Toast.LENGTH_SHORT).show();
            }
        }

        if (v.getId() == R.id.goToShop){
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }
    }

    private void reset(){
        mMap.clear();
        initStoreMarker();
        for (int i = 0 ; i < selectCount ; i++){
            Driver.nameStore[i] = null;
            Driver.selectLat[i] = 0;
            Driver.selectLong[i] = 0;
        }
        distanceAll = 0;
        bottom_select = 1;
        countSelect = 1;
        Driver.initialStores = null;
    }

    //To activity output
    private void openOutput() {
        Intent intent = new Intent(this,output.class);
        startActivity(intent);
    }

    public void click(){
        mMap.setOnMarkerClickListener(arg0 -> {
            mMap.addMarker(new MarkerOptions().position(arg0.getPosition())
                    .title(String.valueOf(arg0.getTitle())));
            //Toast.makeText(getApplicationContext()
            //        , "Select Store " + String.valueOf(arg0.getId())
            //        , Toast.LENGTH_SHORT).show();
            Driver.selectLat[countSelect] = arg0.getPosition().latitude;
            Driver.selectLong[countSelect] = arg0.getPosition().longitude;
            Driver.nameStore[countSelect] = arg0.getTitle();
            bottom_select = 0;
            countSelect = countSelect+1;
            return true;
        });
    }

    public void initStoreMarker(){
        //ทำ ชั้น1
        int storeValue = 10;
        String[] store_Id = {"Store A","Store B","Store C","Store D","Store E","Store F","Store G","Store H","Store I","Store J"};
        String[] store_Name = {"Zara","Aveda","P&P Jewelry","Nine West","Gems Pavilion","Le Beau","Jaspal","Paul Smith","Swarovski","Emporio Armani"};
        double[] lat = {13.7458379,13.7460369,13.747905,13.7468646,13.747441,13.7476286,13.7466783,13.7462979,13.7472052,13.7460543};
        double[] lng = {100.5354161,100.535018,100.5349539,100.5341774,100.5347373,100.5349237,100.5341949,100.5346495,100.5345737,100.5343196};
        for (int i = 0 ;i < storeValue ; i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat[i], lng[i]))
                    .title(store_Id[i])
                    .snippet(store_Name[i])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }
}
