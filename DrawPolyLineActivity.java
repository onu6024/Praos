package com.example.k.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by k on 2016-02-04.
 */
public class DrawPolyLineActivity extends FragmentActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,LocationListener {
    GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints;
    private LocationManager locationManager;
    private String provider;
    double value1 = 0;
    double value2 = 0;
    private float distance1[] = new float[1];
    private float total = 0;

    /*boolean mFirstLoc = true;
    LocationManager mLocMgr;
    MyLocationOverlay2 mLocation;
    protected boolean isRouteDisplayed() {
        return false;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        int googlePlayServiceResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(DrawPolyLineActivity.this);
        if( googlePlayServiceResult !=   ConnectionResult.SUCCESS){ //구글 플레이 서비스를 활용하지 못할경우 <계정이 연결이 안되어 있는 경우
            //실패
            GooglePlayServicesUtil.getErrorDialog(googlePlayServiceResult, this, 0, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    finish();
                }
            }).show();
        }else { //구글 플레이가 활성화 된 경우
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);

            if (provider == null) {  //위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
                new AlertDialog.Builder(DrawPolyLineActivity.this)
                        .setTitle("위치서비스 동의")
                        .setNeutralButton("이동", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                        .show();
            } else {   //위치 정보 설정이 되어 있으면 현재위치를 받아옵니다

                //locationManager.requestLocationUpdates(provider, 1, 1, DrawPolyLineActivity.this); //기본 위치 값 설정
                setUpMapIfNeeded(); //Map ReDraw
            }

            setMyLocation(); //내위치 정하는 함수
        }
        /*Location locationA = new Location("point A");
        locationA.setLatitude(latLng.latitude);
        locationA.setLongitude(latLng.longitude);

        Location locationB = new Location("point B");
        locationA = locationB;
        locationB.setLatitude(latLng.latitude);
        locationB.setLongitude(latLng.longitude);*/

        //distance += locationA.distanceTo(locationA);

        /*MapController mapControl = mMap.getController();
        mapControl.setZoom(13);
        mMap.setBuiltInZoomControls(true);*/

        /*GeoPoint pt = new GeoPoint(37881311, 127729968);

        mapControl.setCenter(pt);

        mLocation = new MyLocationOverlay2(this, mMap);

        List<Overlay> overlays = mMap.getOverlays();

        overlays.add(mLocation);

        mLocation.runOnFirstFix(new Runnable() {

            public void run() {

                mMap.getController().animateTo(mLocation.getMyLocation());

            }

        });*/
        this.init();


        //mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void setMyLocation()
    {
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    /*LocationListener mLocListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            if (mFirstLoc) {
                mFirstLoc = false;
                mMarkerStart.setPosition(position);
            }

            mMarkerMan.setPosition(position);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void onResume() {
        super.onResume();
        String locProv = LocationManager.GPS_PROVIDER;
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLocMgr.requestLocationUpdates(locProv, (long) 3000, 3.f, mLocListener);
    }

    public void onPause() {
        super.onPause();
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLocMgr.removeUpdates(mLocListener);
    }*/

    private void init() {
        // 맵 위치이동.
        arrayPoints = new ArrayList<LatLng>();

        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng pos = new LatLng(37.4980, 127.027);
        // 맵 중심 위치 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
/*
        // 시작 위치에 마커 추가
        MarkerOptions moStart = new MarkerOptions();
        moStart.position(pos);
        moStart.title("출발");
        mMarkerStart = mMap.addMarker(moStart);
        mMarkerStart.showInfoWindow();
        //mMarkerStart = mGoogleMap.addMarker(new MarkerOptions().position(pos).title("출발"));

        //마커 클릭 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplication(), "출발 위치", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // 보행자 마커 추가
        MarkerOptions moMan = new MarkerOptions();
        moMan.position(pos);
        //moMan.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        mMarkerMan = mMap.addMarker( moMan );*/
    }

    Marker mMarker;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Log.d("KTH", "location.getLatitude(), location.getLongitude() -> " + location.getLatitude() + "," + location.getLongitude());
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//위치설정 엑티비티 종료 후
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                if(provider==null){//사용자가 위치설정동의 안했을때 종료
                    finish();
                }else{//사용자가 위치설정 동의 했을때
                    //locationManager.requestLocationUpdates(provider, 1L, 2F, DrawPolyLineActivity.this);
                    Log.d("COY","117 locationMaanger done");
                    setUpMapIfNeeded();
                }
                break;
        }
    }
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getMyLocation();
    }
    boolean locationTag=true;

    @Override
    public void onLocationChanged(Location location) {
        if(locationTag){//한번만 위치를 가져오기 위해서 tag를 주었습니다
            Log.d("myLog"  , "onLocationChanged: !!"  + "onLocationChanged!!");
            double lat =  location.getLatitude();
            double lng = location.getLongitude();

            Toast.makeText(DrawPolyLineActivity.this, "위도  : " + lat +  " 경도: "  + lng ,  Toast.LENGTH_SHORT).show();
            locationTag=false;
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //add marker
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        mMap.addMarker(marker);

        // 맵셋팅
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);

        Location location = new Location("point A");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        //Location location2 = new Location("point B");

        if (value1 == 0 && value2 == 0) {
            //Toast.makeText(DrawPolyLineActivity.this, value1+"  "+value2+"  "+distance, Toast.LENGTH_SHORT).show();
        } else {
            location.distanceBetween(value1, value2, location.getLatitude(), location.getLongitude(), distance1);
            //distance=distance1[0];
            total += distance1[0];
            Toast.makeText(DrawPolyLineActivity.this, "거리 : " + (int) total + "m", Toast.LENGTH_SHORT).show();
        }
        value1 = location.getLatitude();
        value2 = location.getLongitude();
        //Toast.makeText(DrawPolyLineActivity.this, value1+"  "+value2, Toast.LENGTH_SHORT).show();
        /*Point point = mMap.getProjection().toScreenLocation(latLng);
        LatLng latLng1 = mMap.getProjection().fromScreenLocation(point);*/
    }

    @Override
    public void onMapLongClick(LatLng arg0)
    {
        mMap.clear();
        arrayPoints.clear();
        value1 = 0;
        value2 = 0;
        total = 0;
    }

}

