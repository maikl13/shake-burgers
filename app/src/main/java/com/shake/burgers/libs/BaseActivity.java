package com.shake.burgers.libs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shake.burgers.R;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.List;
public abstract class BaseActivity extends Permissions implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();



    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public void load(String link , ImageView img , int placeholder){
        Glide.with(this).load("https://all-go.net/burger/"+link).apply(new RequestOptions()
                .placeholder(placeholder)
                .skipMemoryCache(false)
                .dontAnimate()
                .centerCrop()
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(link))) // here you add some value , if the next time you add the same value then it will load from cache otherwise if you put new value you will download , then save in cache
                .into(img);
    }
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    private void stopLocationUpdates() {
        if(mGoogleApiClient!=null&&mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //To check whether location settings are good to proceed or not.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    startGettingLocation();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        BaseActivity.this,
                                        1001);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
    @SuppressLint("MissingPermission")
    public void startGettingLocation() {

        setLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        startLocationUpdates();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startGettingLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "???????? ???? ???????? ?????????? ???????????? ???? ?????????? ????????????", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }
    public interface OnGetLocationListener {
        void OnGetLocation(String lat, String lng);
    }
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    OnGetLocationListener onGetLocationListener;
    @SuppressLint("MissingPermission")
    public void setupLocationManager(final OnGetLocationListener onGetLocationListener) {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                requestAppPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, new BaseActivity.OnPermissionsGrantedListener() {
                    @Override
                    public void onPermissionsGranted() {
                        try {
                            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                            if (locationManager != null) {
                                Location bestLocation = null;
                                try {
                                    Criteria mFineCriteria = new Criteria();
                                    mFineCriteria.setAccuracy(Criteria.ACCURACY_FINE);
                                    mFineCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                                    mFineCriteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                                    mFineCriteria.setBearingRequired(true);
                                    String provider = locationManager.getBestProvider(mFineCriteria, true);
                                    if (provider != null) {
                                        bestLocation = locationManager.getLastKnownLocation(provider);
                                    }
                                } catch (Exception e) {
                                }
                                if (bestLocation == null) {
                                    List<String> list = locationManager.getAllProviders();
                                    if (list.isEmpty()) {
                                        Toast.makeText(BaseActivity.this, "???????? ???? ???????????????? ?????? wifi ???? ???????? ???? ?????????? ???? gps ??????????????", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (String provider : list) {
                                            Location i = locationManager.getLastKnownLocation(provider);
                                            if (i == null) {
                                                continue;
                                            }
                                            if (bestLocation == null || i.getAccuracy() < bestLocation.getAccuracy()) {
                                                bestLocation = i;
                                            }
                                        }
                                    }
                                }
                                if (bestLocation == null) {
                                    BaseActivity.this.onGetLocationListener = onGetLocationListener;
                                    displayProgress();
                                    if (checkPlayServices()) {
                                        buildGoogleApiClient();
                                    }
                                } else {
                                    onGetLocationListener.OnGetLocation(String.valueOf(bestLocation.getLatitude()), String.valueOf(bestLocation.getLongitude()));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }, 500);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    private void displayProgress() {
        progressDialog.setMessage("?????? ???????????? ?????? ???????????? ...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    boolean showLocatedToast = true;
    public void setLocation(Location location) {
        if (location != null) {
            dismissProgress();
            if (showLocatedToast) {
                Toast.makeText(this, "?????????????? ???? ?????????? ???????????? ??????????", Toast.LENGTH_SHORT).show();
                showLocatedToast = false;
            }
            onGetLocationListener.OnGetLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public static void detectLocation(Activity activity, String user_lat, String user_lng) {
        try {
            PlacePicker.IntentBuilder intent = new PlacePicker.IntentBuilder()
                    .showLatLong(true)  // Show Coordinates in the Activity
                    .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False

                    .setMarkerImageImageColor(R.color.colorPrimary)
                    .setFabColor(R.color.colorPrimary)
                    .setPrimaryTextColor(R.color.colorPrimary) // Change text color of Shortened Address
                    .setSecondaryTextColor(R.color.colorPrimaryDark) // Change text color of full Address
                    .setMapType(MapType.NORMAL)
                    .onlyCoordinates(false);
            if (user_lat != null && user_lng != null) {
                intent.setLatLong(Double.parseDouble(user_lat), Double.parseDouble(user_lng));  // Initial Latitude and Longitude the Map will load into
            }
            activity.startActivityForResult(intent.build(activity), Constants.PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Toast.makeText(activity, "getting your location... , please waite some seconds and try again", Toast.LENGTH_SHORT).show();
        }
    }

}
