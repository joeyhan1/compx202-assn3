package com.example.assignment3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * MapsActivity Class
 *
 * This class is used to show the application in a map format
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //Variables
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    PlacesClient placesClient;
    //ArrayList to store the location latitude and longitude
    ArrayList<Double> locationList = new ArrayList<>();
    private float zoomLevel = 16f;
    //Counter for checking permission for the current location
    int counter = 1;
    JSONArray jsonArray;//API
    JSONObject weather;//API
    JSONObject main;//API
    JSONObject wind;//API
    JSONObject clouds;//API
    JSONObject rain;//API
    JSONObject snow;//API
    Marker marker, marker1, marker2, marker3, marker4, marker5;//API
    Bundle bundle = new Bundle();
    double longi;//API, do not move or modify
    double lati;//API, do not move or modify
    String a,b,c,d,e,f,g,h,i,k;
    JSONArray jArray;
    JSONObject result, jobject1, jobject2, jobject3, jobject4, jobject5;
    double locationLon, locationLat;
    String StrLocation, ImgUrl, city, region;

    /**
     * onLocationResult Method
     *
     * This method is used to get the latitude and longitude of the current location
     */
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if(locationResult == null) {
                return;
            }
            for(Location location : locationResult.getLocations()) {
                Log.d(TAG,"onLocationResult: " + location.toString());
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                getLocation();
            }
            stopLocationUpdates();
            //This is used to make sure it goes to the current location on the map
            if(counter == 1) {
                checkPermissions();
            }
            counter = 0;
        }
    };

    // Create a location permission request
    ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
        // Check if permission has now been granted
        if (fineLocationGranted != null && fineLocationGranted) {
            // Precise location access granted.
            Toast.makeText(getApplicationContext(),"Permission Status: has now been granted",Toast.LENGTH_SHORT).show();
            getLastKnownLocation();
        } else if (coarseLocationGranted != null && coarseLocationGranted) {
            // Only approximate location access granted.
            Toast.makeText(getApplicationContext(),"Permission Status: fine not granted",Toast.LENGTH_SHORT).show();
        } else {
            // No location access granted.
            Toast.makeText(getApplicationContext(),"Permission Status: no permissions granted",Toast.LENGTH_SHORT).show();
        }
    });

    /**
     * OnCreate Method
     * @param savedInstanceState
     * This method happens when the application is getting created
     * It checks whether the search bar been selected, check permissions and make location requests
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //Checking whether places has been initialised and initialise it if it hasn't
        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),"AIzaSyA5pUxD_2Xi1s-bga4itPVaq-VblEHmxg8");
        }
        placesClient = Places.createClient(this);
        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            //When the item on the search bar drop down list has been selected
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //The latitude and longitude of the selected item
                final LatLng latLng = place.getLatLng();
                Log.d(TAG,"onPlaceSelected: " + latLng.latitude + ", " + latLng.longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                locationList.add(latLng.latitude);
                locationList.add(latLng.longitude);
                getLocation();
                lati = latLng.latitude;
                longi = latLng.longitude;
                Parse();//Call Parse method to get weather information.
                NearbyWebCam();//Call NearbyWebCam method to get webcam information.
            }
        });
        mapFragment.getMapAsync(this);
        checkPermissions();
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * onStop Method
     *
     * This method happens when the app is stopped
     * It stops the application and stop the location updates
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * checkPermissions Method
     *
     * This method check for permissions
     */
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions have not yet been granted
            // Launch a location permission request
            askForPermission();
        } else {
            // Permissions have already been granted
            Toast.makeText(getApplicationContext(),"Permission Status: already granted",Toast.LENGTH_SHORT).show();
            getLastKnownLocation();
        }
    }

    /**
     * askForPermission Method
     *
     * This method asks the user for location permissions
     */
    public void askForPermission() {
        // Launch the location permission request
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * getLastKnownLocation Method
     *
     * This method gets the last known location of the user
     */
    public void getLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
                public void onSuccess(Location location) {
                    // Got last known location. This can sometimes be null.
                    if(location == null) {
                        checkSettingsAndStartLocationUpdates();
                    }
                    if (location != null) {
                        // Logic to handle location object
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        locationList.add(lat);
                        locationList.add(lon);
                        checkSettingsAndStartLocationUpdates();
                        Toast.makeText(getApplicationContext(),"Location at " + lat + ", " + lon,Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle a location not being found
                        Toast.makeText(getApplicationContext(),"Location co-ordinates: no location",Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    /**
     * getLocation Method
     *
     * This method gets the current location of the user
     */
    public void getLocation()
    {
        try
        {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Clear map
                    mMap.clear();
                    double currentLat = 0;
                    double currentLon = 0;
                    //Checking the array list is empty or not
                    if(!locationList.isEmpty()) {
                        //Getting the cords
                        currentLat = locationList.get(0);
                        currentLon = locationList.get(1);
                        LatLng pos = new LatLng(currentLat, currentLon);
                        //Clear the location list
                        locationList.clear();
                        //Move the camera to the position specified
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoomLevel));
                        //Current Location title
                        marker=mMap.addMarker(new MarkerOptions().position(pos).title("Current Location"));//API
                    }
                }
            });
        }
        catch(SecurityException S)
        {
            S.printStackTrace();
        }
    }

    /**
     * checkSettingsAndStartLocationUpdates Method
     *
     * This method check the settings and start location updates
     */
    private void checkSettingsAndStartLocationUpdates() {
        //Setting up the location request, setting clients and settings response task in order to start location updates
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
    }

    /**
     * startLocationUpdates Method
     *
     * This method starts the location updates
     */
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }

    /**
     * stopLocationUpdates
     *
     * This method stops the location updates
     */
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Parse Method
     *
     * Getting weather information
     */
    public void Parse()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getWeatherURL(lati,longi), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Array to contain the name of weathers
                    String[] Main = {"Thunderstorm", "Drizzle", "Rain", "Snow", "Clear", "Clouds", "Mist", "Smoke", "Haze", "Fog"};
                    //Array to contain icons for markers
                    int[] draw = {R.drawable.thunderstorm, R.drawable.drizzle, R.drawable.rain, R.drawable.snow, R.drawable.clear, R.drawable.clouds, R.drawable.atmosphere, R.drawable.atmosphere, R.drawable.atmosphere, R.drawable.atmosphere};
                    //Get weather information with jsonArray
                    jsonArray = response.getJSONArray("weather");
                    weather = jsonArray.getJSONObject(0);
                    //Declare new variables to store details of weather.
                    String weatherMain = weather.getString("main"), description = weather.getString("description"), icon = weather.getString("icon");
                    //Pass the relative values of weather to global variables.
                    a = weatherMain;
                    b = description;
                    c = icon;
                    main = response.getJSONObject("main");
                    //Declare new variables to store details of weather.
                    String temp=main.getString("temp"), feelsLike = main.getString("feels_like"), tempMin=main.getString("temp_min"), tempMax=main.getString("temp_max"), pressure=main.getString("pressure"), humidity=main.getString("humidity");
                    //Pass the relative values of weather to global variables.
                    d = temp;
                    e = feelsLike;
                    f = tempMin;
                    g = tempMax;
                    h = pressure;
                    i = humidity;
                    wind = response.getJSONObject("wind");
                    //Declare new variables to store details of weather.
                    String windSpeed = wind.getString("speed");
                    //Pass the relative values of weather to global variables.
                    k= windSpeed;
                    clouds = response.getJSONObject("clouds");
                    //Loop through main to set icon to a marker in terms of weather.
                    for(int i =0;i<Main.length;i++)
                    {
                        if(Main[i].compareTo(weatherMain)==0)
                        {
                            BitmapDescriptor Icon = BitmapDescriptorFactory.fromResource(draw[i]);//Declare bitmap object
                            marker.setIcon(Icon);//Set icon
                        }
                    }
                    //Set details of weather in title
                    marker.setSnippet("Weather: "+weatherMain+", Description: "+description+", Temperature: "+temp + ", Max temp: "+tempMax+ ", Min temp: "+tempMin+", Humidity: "+humidity);
                    //Signal the status
                    Toast.makeText(MapsActivity.this, "done", Toast.LENGTH_LONG);
                    checkOnClick();//***************************************
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        //Add this activity to RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    /**
     * NearbyWebCam Method
     *
     * A method used to get the nearest 5 webcams around a location.
     */
    public void NearbyWebCam()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getCamURL(lati,longi), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Get webcam results according to longitude and latitude.
                    result = response.getJSONObject("result");//Get the result
                    jArray = result.getJSONArray("webcams");//Get webcams
                    jobject1 = jArray.getJSONObject(0);//Get first webcam
                    locationLon = jobject1.getJSONObject("location").getDouble("longitude");//Get longitude
                    locationLat = jobject1.getJSONObject("location").getDouble("latitude");//Get latitude
                    marker1 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)).position(new LatLng(locationLat, locationLon)).title("webcam0"));

                    jobject2 = jArray.getJSONObject(1);
                    locationLon = jobject2.getJSONObject("location").getDouble("longitude");
                    locationLat = jobject2.getJSONObject("location").getDouble("latitude");
                    marker2 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)).position(new LatLng(locationLat, locationLon)).title("webcam1"));

                    jobject3 = jArray.getJSONObject(2);
                    locationLon = jobject3.getJSONObject("location").getDouble("longitude");
                    locationLat = jobject3.getJSONObject("location").getDouble("latitude");
                    marker3 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)).position(new LatLng(locationLat, locationLon)).title("webcam2"));

                    jobject4 = jArray.getJSONObject(3);
                    locationLon = jobject4.getJSONObject("location").getDouble("longitude");
                    locationLat = jobject4.getJSONObject("location").getDouble("latitude");
                    marker4 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)).position(new LatLng(locationLat, locationLon)).title("webcam3"));

                    jobject5 = jArray.getJSONObject(4);
                    locationLon = jobject5.getJSONObject("location").getDouble("longitude");
                    locationLat = jobject5.getJSONObject("location").getDouble("latitude");
                    marker5 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.camera)).position(new LatLng(locationLat, locationLon)).title("webcam4"));
                    //Call checkOnClick method.
                    checkOnClick();//************************************

                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //Add this activity to RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    /**
     * checkOnClick method
     *
     * This is the method used to check which marker for webcams is clicked.
     */
    public void checkOnClick()
    {
        if(mMap!=null)
        {
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {

                    try {
                        //Check which marker is clicked.
                        if(marker.equals(MapsActivity.this.marker))
                        {
                            //Create an intent object.
                            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                            //Create bundle object.
                            Bundle bundle = new Bundle();
                            //Pass weather information to new activity, such as name, description, temperature and wind speed etc.
                            bundle.putString("A", a);
                            bundle.putString("B", b);
                            bundle.putString("C", d);
                            bundle.putString("D", f);
                            bundle.putString("E", g);
                            bundle.putString("F", e);
                            bundle.putString("G", h);
                            bundle.putString("H", i);
                            bundle.putString("J", k);
                            //Pass bundle object.
                            intent.putExtras(bundle);
                            //Start a new activity.
                            startActivity(intent);
                        }
                        else
                        {
                            //Create an intent object.
                            Intent intent = new Intent(MapsActivity.this, MainActivity2.class);
                            //Check any one of webcams is clicked.
                            if(marker.equals(marker1))
                            {
                                //Pass webcam information to global variables.
                                StrLocation = jobject1.getString("title");
                                ImgUrl = jobject1.getJSONObject("image").getJSONObject("current").getString("preview");
                                city = jobject1.getJSONObject("location").getString("city");
                                region = jobject1.getJSONObject("location").getString("region");
                            }
                            else if(marker.equals(marker2))
                            {
                                //Pass webcam information to global variables.
                                StrLocation = jobject2.getString("title");
                                ImgUrl = jobject2.getJSONObject("image").getJSONObject("current").getString("preview");
                                city = jobject2.getJSONObject("location").getString("city");
                                region = city = jobject2.getJSONObject("location").getString("region");
                            }
                            else if(marker.equals(marker3))
                            {
                                //Pass webcam information to global variables.
                                StrLocation = jobject3.getString("title");
                                ImgUrl = jobject3.getJSONObject("image").getJSONObject("current").getString("preview");
                                city = jobject3.getJSONObject("location").getString("city");
                                region = jobject3.getJSONObject("location").getString("region");
                            }
                            else if(marker.equals(marker4))
                            {
                                //Pass webcam information to global variables.
                                StrLocation = jobject4.getString("title");
                                ImgUrl = jobject4.getJSONObject("image").getJSONObject("current").getString("preview");
                                city = jobject4.getJSONObject("location").getString("city");
                                region = jobject4.getJSONObject("location").getString("region");
                            }
                            else if(marker.equals(marker5))
                            {
                                //Pass webcam information to global variables.
                                StrLocation = jobject5.getString("title");
                                ImgUrl = jobject5.getJSONObject("image").getJSONObject("current").getString("preview");
                                city = jobject5.getJSONObject("location").getString("city");
                                region = jobject5.getJSONObject("location").getString("region");
                            }
                            //Pass webcam information to a bundle object.
                            bundle.putString("location", StrLocation);
                            bundle.putString("url", ImgUrl);
                            bundle.putString("city", city);
                            bundle.putString("region",region);
                            //Pass the bundle to a new activity.
                            intent.putExtras(bundle);
                            //Start a new activity.
                            startActivity(intent);
                        }

                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
            });
        }
    }

    /**
     * getWeather URL Method
     * @param lati - Latitude of location
     * @param longi - Longitude of location
     * @return url - Full url
     *
     * This method is used to get the full weather api URL
     */
    public String getWeatherURL(double lati, double longi) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lati+"&lon="+longi+"&appid=e70db8795aa5707f469928c01a59dc01";
        return url;
    }

    /**
     * getCamURL Method
     * @param lati - Latitude of location
     * @param longi - Longitude of location
     * @return url - Full url
     *
     * This method is used to get the cam api URL
     */
    public String getCamURL(double lati, double longi) {
        String url = "https://api.windy.com/api/webcams/v2/list/nearby="+lati+","+longi+",500/limit=5?show=webcams:location,image&key=PrcUozvG54yPWuuX6UyaOi0vJQeUHb7e";
        return url;
    }
}