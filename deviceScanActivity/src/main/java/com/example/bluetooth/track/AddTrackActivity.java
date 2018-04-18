package com.example.bluetooth.track;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.bluetooth.db.TrackDB;
import com.example.bluetooth.le.InputRCActivity;
import com.example.bluetooth.le.InputRCTextActivity;
import com.example.bluetooth.le.R;
import com.example.bluetooth.track.TrackActivity.MyLocationListener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddTrackActivity extends Activity{
	final private String TAG=AddTrackActivity.class.getSimpleName();
	LinearLayout addTrackLy;//��Ӱ�ť
	
	TrackDB trackDB;
	EditText inputTrackNameEt;
	public LocationClient mLocationClient;
	TextView locationInfoTv;
	TextView  backTv;//���ؼ�ͷ
	String longitude, latitude;//��γ��
	//�ٶȵ�ͼ
	private MapView mapView;
	private BaiduMap baiduMap;
	private boolean isFirstLocate=true;
	
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());//��ͼ��ʼ��

        setContentView(R.layout.activity_addtrack);      
        trackDB=new TrackDB(getApplicationContext(), "TimerStore", null, 7);
        inputTrackNameEt=(EditText) findViewById(R.id.inputTrackName);
        locationInfoTv=(TextView) findViewById(R.id.locationInfoTv);
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        //��Ӱ�ť
        addTrackLy=(LinearLayout)findViewById(R.id.addTrackLy);
        addTrackLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				String track=inputTrackNameEt.getText().toString();
				if (track!=null && !track.equals("")) {
					track=track.replace(" ", "");//ȥ���ո�
					trackDB.insert(track,longitude,latitude);
		        	Intent intent=new Intent(AddTrackActivity.this,TrackActivity.class);
	            	startActivity(intent);
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.trackNameEmpty, Toast.LENGTH_SHORT).show();
				}
			}
		});
        ///////////////////��λ����/////////////////////////////
        mLocationClient=new LocationClient(getApplicationContext());//��ʼ��λ����Ϣ
        mLocationClient.registerLocationListener(new MyLocationListener());//λ����Ϣ������
        List<String> permissionList=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(AddTrackActivity.this, android.Manifest.permission
        		.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}
        if (ContextCompat.checkSelfPermission(AddTrackActivity.this, android.Manifest.permission
        		.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.READ_PHONE_STATE);
		}
        if (ContextCompat.checkSelfPermission(AddTrackActivity.this, android.Manifest.permission
        		.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
       if (!permissionList.isEmpty()) {
    	   String[] permissions=permissionList.toArray(new String[permissionList.size()]);
    	   ActivityCompat.requestPermissions(AddTrackActivity.this, permissions, 1);
       }
       else
       {
    	   requestLocation();
       }
       ////////////////////////�ٶȵ�ͼ///////////////////////////////
       mapView=(MapView) findViewById(R.id.bmapView);
       baiduMap=mapView.getMap();
       baiduMap.setMyLocationEnabled(true);
   }
   
   /**
    * �������ؼ�
    */
   TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
       public void onClick(View v)
       {    
       	int id = v.getId();
       	if (id==R.id.backTv) {
       		((Activity)AddTrackActivity.this).finish();
			}
		}
   };  
   
   
   private void navigateTo(BDLocation location)
   {
	   if (isFirstLocate) {
		Log.d(TAG, "isFirstLocateΪtrue");
		LatLng ll=new LatLng(location.getLatitude(), location.getLongitude());
		MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
		baiduMap.animateMapStatus(update);
		update=MapStatusUpdateFactory.zoomTo(16f);
		baiduMap.animateMapStatus(update);
	     /*�ж�baiduMap���Ѿ��ƶ���ָ��λ��*/
        if (baiduMap.getLocationData()!=null)
        if (baiduMap.getLocationData().latitude==location.getLatitude()
                &&baiduMap.getLocationData().longitude==location.getLongitude()){
            isFirstLocate = false;
        	}
	   }

	   
	   MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
	   longitude=String.valueOf(location.getLongitude());
	   latitude=String.valueOf(location.getLatitude());
	   locationBuilder.latitude(location.getLatitude());
	   locationBuilder.longitude(location.getLongitude());
	   MyLocationData locationData=locationBuilder.build();
	   baiduMap.setMyLocationData(locationData);
   }
   private void requestLocation()
   {
	   	initLocation();
	   	mLocationClient.start();
   }
   private void initLocation()
   {
	   	LocationClientOption option=new LocationClientOption();
	   	option.setCoorType("bd09ll");
	   	option.setIsNeedAddress(true);
	   	option.setAddrType("all");
	   	option.setScanSpan(5000);
	   	mLocationClient.setLocOption(option);
   }
   public void onRequsetPermissionResult(int requestCode,String[] permissions,int[] grantResults)
   {
		switch(requestCode)
		{
		case 1:
			if (grantResults.length>0) {
					for (int result:grantResults) {
						if (result!=PackageManager.PERMISSION_GRANTED) {
							Toast.makeText(this, R.string.agreePermissions, Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
					}
					requestLocation();
				}
			else {
					Toast.makeText(this, R.string.errorOfLocation, Toast.LENGTH_SHORT).show();
				}
			break;
			default:
		}
   }
   /**
    * λ����Ϣ������
    * @author Administrator
    *
    */
   public class MyLocationListener implements com.baidu.location.BDLocationListener{

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {

			
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			final StringBuilder currentPosition=new StringBuilder();
//			currentPosition.append("γ�ȣ�").append(location.getLatitude());
//			currentPosition.append("���ߣ�").append(location.getAltitude());
//			currentPosition.append("��ַ��").append(location.getCountry());
//			currentPosition.append(location.getProvince());
//			currentPosition.append(location.getCity());
//			currentPosition.append(location.getDistrict());
//			currentPosition.append(location.getStreet());
			currentPosition.append(location.getAddrStr());
//			currentPosition.append(location.getBuildingID());
//			currentPosition.append(location.getBuildingName());
//			currentPosition.append(" ��λ��ʽ��");
//			if (location.getLocType()==BDLocation.TypeGpsLocation) {
//				currentPosition.append("GPS");
//			}
//			else if (location.getLocType()==BDLocation.TypeNetWorkLocation) {
//				currentPosition.append("����");
//			}
	        if (location.getLocType()==BDLocation.TypeGpsLocation 
	        		|| location.getLocType()==BDLocation.TypeNetWorkLocation) {
				navigateTo(location);
			}

			Log.d(TAG, "��λ��ַ��"+currentPosition);
	        AddTrackActivity.this.runOnUiThread(new Runnable() {  
	            @Override  
	            public void run() {   
	            	locationInfoTv.setText(currentPosition);  
	            }  
	        });  
			

		}
   }
   protected void onPause()
   {
	   	super.onPause();
	   	Log.d(TAG, "on pause");
	   	mLocationClient.stop();
	   	mapView.onPause();
	   	baiduMap.setMyLocationEnabled(false);
   }
   
   
   protected void onDestory() {
		super.onDestroy();
		Log.d(TAG, "on destory");
		mapView.onDestroy();
		baiduMap.setMyLocationEnabled(false);
	}
   
   protected void onStart() {
	super.onStart();
	isFirstLocate=true;
	baiduMap.setMyLocationEnabled(true);
	
   }
   
   protected void onResume() {
		super.onResume();
		Log.d(TAG, "on resume");
		mapView.onResume();
		mLocationClient.start();	
   }
}
