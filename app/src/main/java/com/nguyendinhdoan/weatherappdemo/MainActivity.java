package com.nguyendinhdoan.weatherappdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nguyendinhdoan.weatherappdemo.adapter.ViewPagerAdapter;
import com.nguyendinhdoan.weatherappdemo.common.Common;
import com.nguyendinhdoan.weatherappdemo.fragment.ForecastFragment;
import com.nguyendinhdoan.weatherappdemo.fragment.TodayWeatherFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private Toolbar toolbar;
    private TabLayout tabs;
    private ViewPager viewPager;

    private CoordinatorLayout coordinatorLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.root_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // request permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallback();

                            fusedLocationProviderClient = new FusedLocationProviderClient(MainActivity.this);
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                        Snackbar.make(coordinatorLayout, "Permission denied", Snackbar.LENGTH_LONG).show();
                    }
                }).onSameThread().check();

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Common.current_location = locationResult.getLastLocation();
                Log.d(TAG, "lat: " + Common.current_location.getLatitude());
                Log.d(TAG, "lng: " + Common.current_location.getLongitude());

                viewPager = findViewById(R.id.view_pager);
                setupViewPager(viewPager);
                tabs = findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
            }
        };
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(TodayWeatherFragment.getInstance(), "Today");
        viewPagerAdapter.addFragment(ForecastFragment.getInstance(), "5 DAY");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

}
