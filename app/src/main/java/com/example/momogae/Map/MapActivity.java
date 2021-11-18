package com.example.momogae.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.momogae.Login.SharedPreference;
import com.example.momogae.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        PlacesListener {
    // 병원찾기
    List<Marker> previous_marker = null;

    // 현재위치
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 9000; // 9초
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    String userID;
    StorageReference mStorage;

    FloatingActionButton fab, fabPetHospital, fabPetCafe, fabPetRestaurant, fabPetstore;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // current location
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Momogae");
        mLayout = findViewById(R.id.layout_map);

        userID = SharedPreference.getAttribute(getApplicationContext(), "userID");
        mStorage = FirebaseStorage.getInstance().getReference();

        // current location
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        previous_marker = new ArrayList<Marker>();


        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        // where to
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
            }
        });

        fabPetHospital = (FloatingActionButton) findViewById(R.id.fabMap);
        fabPetHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                showPlace_veterinarycare(currentPosition);
            }
        });

        fabPetCafe = (FloatingActionButton) findViewById(R.id.fabPetCafe);
        fabPetCafe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                anim();
//                showPlaceInformation(currentPosition);
                showPlace_petcafe(currentPosition);
                //Toast.makeText(getApplicationContext(),"앗! 근처에 펫카페가 없어요!", Toast.LENGTH_SHORT);
            }
        });

        fabPetRestaurant = (FloatingActionButton) findViewById(R.id.fabPetRestaurant);
        fabPetRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
                showPlace_PetRestaurant(currentPosition);
            }
        });

        fabPetstore = (FloatingActionButton) findViewById(R.id.fabPetstore);
        fabPetstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
                showPlace_petstore(currentPosition);
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());


                //현재 위치에 마커 생성하고 이동
                if (mCurrentLocatiion == null) {
                    setCurrentLocation(location, markerTitle, markerSnippet);
                }

                mCurrentLocatiion = location;
            }


        }

    };



    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
                return;
            }







            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mGoogleMap.setMyLocationEnabled(true);

        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

            }
        });



    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();



        if (checkPermission()) {

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mGoogleMap!=null)
                mGoogleMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            return "잘못된 GPS 좌표";
        }


        if (addresses == null || addresses.size() == 0) {

            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

//        Circle circle = mGoogleMap.addCircle(new CircleOptions()
//                .center(DEFAULT_LOCATION)
//                .radius(1000)
//                .strokeColor(Color.BLUE)
//                .strokeWidth(3)
//                .fillColor(Color.argb(125, 0, 0, 255))
//                .clickable(false));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    public void anim() {
        if (isFabOpen) {
            fabPetHospital.startAnimation(fab_close);
            fabPetCafe.startAnimation(fab_close);
            fabPetRestaurant.startAnimation(fab_close);
            fabPetstore.startAnimation(fab_close);
            fabPetHospital.setClickable(false);
            fabPetCafe.setClickable(false);
            fabPetRestaurant.setClickable(false);
            fabPetstore.setClickable(false);
            isFabOpen = false;
        } else {
            fabPetHospital.startAnimation(fab_open);
            fabPetCafe.startAnimation(fab_open);
            fabPetRestaurant.startAnimation(fab_open);
            fabPetstore.startAnimation(fab_open);
            fabPetHospital.setClickable(true);
            fabPetCafe.setClickable(true);
            fabPetRestaurant.setClickable(true);
            fabPetstore.setClickable(true);
            isFabOpen = true;
        }
    }

    public void findMap(){

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                for (Place place : places) {
                    LatLng latLng
                            = new LatLng(place.getLatitude(), place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    Marker item = mGoogleMap.addMarker(markerOptions);
                    previous_marker.add(item);
                }


                //중복 마커 제거

                HashSet<Marker> hashSet = new HashSet<Marker>();

                hashSet.addAll(previous_marker);

                previous_marker.clear();

                previous_marker.addAll(hashSet);


            }

        });

    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlace_veterinarycare(LatLng location)
    {
        mGoogleMap.clear(); //지도를 초기화하는 구문

        if (previous_marker != null)
            previous_marker.clear();//마커를 초기화하는 구문

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8") //구글맵 api 키 설정
                .latlng(location.latitude, location.longitude)//현재 위치 반경에서
                .radius(1000) // 7km 내에서 검색
                .type(PlaceType.VETERINARY_CARE) //구글에서 제공해주는 api
                .build()
                .execute();
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1000, null);
    }

    public void showPlace_petstore(LatLng location)
    {
        mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어


        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyCRAFrVWsDo4ECDz9kOMYS5Z6mjMbfy1nw")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(2000) // 7km 내에서 검색
                .type(PlaceType.PET_STORE)
                .build()
                .execute();
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1000, null);
    }

    public void showPlace_petcafe(LatLng location) {
        mGoogleMap.clear();//지도를 초기화하는 구문
        if (previous_marker != null)
            previous_marker.clear();//마커를 초기화하는 구문

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치 반경에서
                .radius(2000) // 4km 내에서 검색
                .type(PlaceType.CAFE) //모든 카페가 검색됨
                .language("ko", "KR")
                .keyword("애견")
                .build()
                .execute();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(2000) // 4km 내에서 검색
                .type(PlaceType.CAFE)
                .language("ko", "KR")
                .keyword("반려")
                .build()
                .execute();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치 반경에서
                .radius(2000) // 3km 내에서 검색
                .type(PlaceType.CAFE) //모든 카페 정보가 검색됨
                .language("ko", "KR")
                .keyword("애견동반") //키워드 "애견동반"을 포함하여 검색
                .build()
                .execute();
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1000, null);
    }

    public void showPlace_PetRestaurant(LatLng location) {
        mGoogleMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어


        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(3000) // 10km 내에서 검색
                .type(PlaceType.RESTAURANT)
                .language("ko", "KR")
                .keyword("애견동반")
                .build()
                .execute();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(3000) // 4km 내에서 검색
                .type(PlaceType.RESTAURANT)
                .language("ko", "KR")
                .keyword("반려")
                .build()
                .execute();

        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDvEYjENIJ0ff4NDH6_LaS1KrJRkdZNwf8")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(3000) // 4km 내에서 검색
                .type(PlaceType.RESTAURANT)
                .language("ko", "KR")
                .keyword("애견")
                .build()
                .execute();

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 1000, null);
    }
}