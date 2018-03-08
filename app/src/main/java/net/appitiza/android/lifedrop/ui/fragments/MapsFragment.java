package net.appitiza.android.lifedrop.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.appitiza.android.lifedrop.BuildConfig;
import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.AppLog;
import net.appitiza.android.lifedrop.constants.Constants;
import net.appitiza.android.lifedrop.model.SearchItem;
import net.appitiza.android.lifedrop.model.SearchListModel;
import net.appitiza.android.lifedrop.ui.activities.LocationUpdatesService;
import net.appitiza.android.lifedrop.utils.Utils;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback, PlaceSelectionListener, View.OnClickListener, WebserviceCallBack/*,SharedPreferences.OnSharedPreferenceChangeListener*/  {
    private GoogleMap mMap;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private CustomTextview mTvSetManualy;
    private ImageView mIvGps;
    private ProgressDialog pd;
    private int SEARCH_AROUND = 1;
    private double mSelectedLatitude;
    private double mSelectedLongituude;
    private String mSelectedPlace;
    private Location mGpsLocation;
    public static final int  LOCATION_SETTINGS = 2;

    private static final String TAG = "MAP";

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // UI elements.
   // private Button mRequestLocationUpdatesButton;
    //private Button mRemoveLocationUpdatesButton;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myReceiver = new MyReceiver();

        mTvSetManualy = getActivity().findViewById(R.id.tv_set_manualy);
        mTvSetManualy.setOnClickListener(this);
        mIvGps = getActivity().findViewById(R.id.iv_gps);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (NetWorkUtil.checkGpsAvailable(getActivity())) {
            return;
        }



    }


    @Override
    public void onStart() {
        super.onStart();


        mIvGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

                        // Check that the user hasn't revoked permissions by going to Settings.
                       // if (Utils.requestingLocationUpdates(getActivity())) {
                            if (!checkPermissions()) {
                                requestPermissions();
                            }
                            else
                            {
                                displayLocationSettingsRequest();
                            }
                     //   }

                }
                else
                {
                    // Restore the state of the buttons when the activity (re)launches.
                    //setButtonsState(Utils.requestingLocationUpdates(getActivity()));

                    // Bind to the service. If the service is in foreground mode, this signals to the service
                    // that since this activity is in the foreground, the service can exit foreground mode.

                    displayLocationSettingsRequest();
                }


            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            getActivity().unbindService(mServiceConnection);
            mBound = false;
        }
       /* PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);*/
        super.onStop();
    }



    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    getActivity().findViewById(R.id.map),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                          //  new Utils().reDirectToPermissionScreen(getActivity(), getString(R.string.permission_message_calendar), getString(R.string.ok));
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
          //  new Utils().reDirectToPermissionScreen(getActivity(), getString(R.string.permission_message_calendar), getString(R.string.ok));

        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                displayLocationSettingsRequest();
            } else {
                // Permission denied.
                Snackbar.make(
                        getActivity().findViewById(R.id.map),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(getActivity(), Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
                setFetchedLocation(location);
            }
        }
    }

   /* @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }*/


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void startLocationIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            AppLog.logErrorString(e.getMessage());
        }
    }


@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    this.onPlaceSelected(place);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    this.onError(status);
                }
                break;
            case LOCATION_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        displayLocationSettingsRequest();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        mMap.clear();
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_me", 60, 60))));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(10).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (NetWorkUtil.isNetworkAvailable(getContext())) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                pd = new ProgressDialog(getActivity());
                pd.setMessage("Searching around you...");
                pd.show();
                WebserviceHandler.searchAround("" + place.getLatLng().latitude, "" + place.getLatLng().longitude, MapsFragment.this, SEARCH_AROUND);
            } else {
                showNetworkAlert();
            }
        }
    }

    @Override
    public void onError(Status status) {
        mTvSetManualy.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_set_manualy:
                startLocationIntent();
                break;

        }
    }


    private void displayLocationSettingsRequest() {
       /* PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);*/
        getActivity().bindService(new Intent(getActivity(), LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        initiateLocationReceiverAndService();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void initiateLocationReceiverAndService() {


        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Fetching Location...");
        pd.show();
        mService.requestLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    @Override
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (requestCode == SEARCH_AROUND && response != null) {

            if (response.getErrorcode() == 0) {
                SearchListModel searchListModel = ResponseParser.parseSearch(response);
                if (searchListModel != null && searchListModel.getList() != null && searchListModel.getList().size() > 0) {

                    for (SearchItem mModel : searchListModel.getList()) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mModel.getLat()), Double.parseDouble(mModel.getLon()))).title(mModel.getBlood()).snippet(mModel.getFirstName() + " " + mModel.getLastName() + "\n" + mModel.getNumber()).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_destination", 100, 100))));
                    }
                }

            }
        }

    }


    @Override
    public void connectionError(String msg, int requestCode) {

    }

    private void setFetchedLocation(Location fetchedLocation) {
        mService.removeLocationUpdates();
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        this.mGpsLocation = fetchedLocation;
        setCurrentLocation();
    }

    private void setCurrentLocation() {
        if (this.mGpsLocation != null) {

            if (mGpsLocation.getLatitude() != 0.0 && mGpsLocation.getLongitude() != 0.0) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                mSelectedLatitude = mGpsLocation.getLatitude();
                mSelectedLongituude = mGpsLocation.getLongitude();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(mSelectedLatitude, mSelectedLongituude, 1);
                    mSelectedPlace = addresses.get(0).getAddressLine(0);

                    mMap.clear();
                    if (mMap != null) {
                        LatLng mLatLng = new LatLng(mSelectedLatitude, mSelectedLongituude);
                        mMap.addMarker(new MarkerOptions().position(mLatLng).title(mSelectedPlace).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_me", 60, 60))));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(mLatLng).zoom(10).tilt(45).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        if (NetWorkUtil.isNetworkAvailable(getContext())) {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }

                            pd = new ProgressDialog(getActivity());
                            pd.setMessage("Searching around you...");
                            pd.show();
                            WebserviceHandler.searchAround("" + mSelectedLatitude, "" + mSelectedLongituude, MapsFragment.this, SEARCH_AROUND);
                        } else {
                            showNetworkAlert();
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
