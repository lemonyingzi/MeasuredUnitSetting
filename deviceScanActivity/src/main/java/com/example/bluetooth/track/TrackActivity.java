package com.example.bluetooth.track;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.bluetooth.db.TrackDB;
import com.example.bluetooth.le.BindTimerActivity;
import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.R.id;
import com.example.bluetooth.le.R.layout;
import com.example.bluetooth.list.RC;
import com.example.bluetooth.list.RCAdatper;
import com.example.bluetooth.list.ResultAdapter;
import com.example.bluetooth.list.Track;
import com.example.bluetooth.list.TrackAdapter;
import com.example.bluetooth.result.ResultActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrackActivity extends Activity{
	private final static String TAG=TrackActivity.class.getSimpleName();
	public LocationClient mLocationClient;
	private TextView locationTv;
	TrackDB trackDB;//数据库
	TrackAdapter trackAdapter;
	private List<Track> trackList=new ArrayList<Track>(); 
	ListView trackListView;
	LinearLayout addTrackBtn;
	TextView  backTv;//返回箭头
	TextView myText;
	LinearLayout currentLocationLy;//当前定位
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        
        locationTv=(TextView)findViewById(R.id.locationTv);
        currentLocationLy=(LinearLayout) findViewById(R.id.currentLocationLy);
        currentLocationLy.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				String track=locationTv.getText().toString();
				if (track!=null && !track.equals("")) {
					trackDB.updateSelected(track);
					((Activity)TrackActivity.this).finish();
				}
				
			}
	
		});
        myText=(TextView) findViewById(R.id.myText);
        mLocationClient=new LocationClient(getApplicationContext());//初始化位置信息
        mLocationClient.registerLocationListener(new MyLocationListener());//位置信息监听器
        trackDB=new TrackDB(getApplicationContext(), "TimerStore", null, 7);//赛道表
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        //权限申请
        List<String> permissionList=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(TrackActivity.this, android.Manifest.permission
        		.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}
        if (ContextCompat.checkSelfPermission(TrackActivity.this, android.Manifest.permission
        		.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.READ_PHONE_STATE);
		}
        if (ContextCompat.checkSelfPermission(TrackActivity.this, android.Manifest.permission
        		.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
			permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
       if (!permissionList.isEmpty()) {
    	   String[] permissions=permissionList.toArray(new String[permissionList.size()]);
    	   ActivityCompat.requestPermissions(TrackActivity.this, permissions, 1);
       }
       else
       {
    	   requestLocation();
       }
       ////////////////////设置list显示track//////////////////////////////////
       trackList=trackDB.GetTracks();//初始化trackList
 
       //加载Track
       if (trackList!=null) {
    	    trackAdapter=new TrackAdapter(TrackActivity.this, R.layout.track_item, trackList);
    	    trackListView=(ListView) findViewById(R.id.trackList);
    	    trackListView.setAdapter(trackAdapter);
    	    trackListView.setVisibility(0);
    	    myText.setVisibility(8);
       }
       else
       {
    	   trackListView.setVisibility(8);
    	   myText.setVisibility(0);
       }

       //长按删除
       trackListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final String trackName=trackAdapter.getItem(position).getTrack();
	            AlertDialog.Builder builder=new Builder(TrackActivity.this);  
	            builder.setMessage(getResources().getString(R.string.deleteOrNotNoQuestion)+trackName+"?");  
	            builder.setTitle(R.string.note);  
	            //添加AlertDialog.Builder对象的setPositiveButton()方法   
	            builder.setPositiveButton(R.string.confirmStr, new OnClickListener() {  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	                	trackDB.delete(trackName);
	                	trackList=trackDB.GetTracks();
	                	trackAdapter=new TrackAdapter(TrackActivity.this, R.layout.track_item, trackList);
	                	trackListView.setAdapter(trackAdapter);
	                }  
	            });  
	            //添加AlertDialog.Builder对象的setNegativeButton()方法   
	            builder.setNegativeButton(R.string.cancelStr, new OnClickListener() {  
	                  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {  
	                      
	                }  
	            });  
	            builder.create().show();  
				return true;
			}});
       //单击选中
       trackListView.setOnItemClickListener(new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String trackName=trackAdapter.getItem(position).getTrack();
			trackDB.updateSelected(trackName);
			((Activity)TrackActivity.this).finish();
		}
    	   
       });
       //////////////////添加新赛道/////////////////////////////////////////////
       addTrackBtn=(LinearLayout)findViewById(R.id.addTrackLy);
       addTrackBtn.setOnClickListener(new View.OnClickListener()
	   {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TrackActivity.this,AddTrackActivity.class);
				startActivity(intent);
			}
	   });
       
       
       
    }
    
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)TrackActivity.this).finish();
			}
		}
    };  
    
    /////////////////////以下位置信息获取//////////////////////////////////////////////////////
    private void requestLocation()
    {
    	initLocation();
    	mLocationClient.start();
    }
    private void initLocation()
    {
    	LocationClientOption option=new LocationClientOption();
    	option.setIsNeedAddress(true);
    	option.setScanSpan(5000);
    	option.setCoorType("bd09ll");
    	option.setAddrType("all");
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
     * 位置信息监听器
     * @author Administrator
     *
     */
    public class MyLocationListener implements com.baidu.location.BDLocationListener{

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {

			
		}

		@Override
		public void onReceiveLocation(BDLocation location) {

			double latitude=location.getLatitude();//纬度
			double longitude=location.getLongitude();//经度	String trackName=null;
			final String trackName=getLatestTrack(longitude,latitude);
			Log.d(TAG, "trackName:"+trackName);
	        TrackActivity.this.runOnUiThread(new Runnable() {  
	            @Override  
	            public void run() {   
	            	if (trackName!=null) {
	            		if (trackName!=null) {
	            			locationTv.setText(trackName);  
						}
					}
	                
	            }  
	        });  
			
		}
    }
    /**
     * 获取最近的赛道
     * @param longitude
     * @param latitude
     * @return
     */
    private String getLatestTrack(double longitude,double latitude)
    {
    	
		for(int i=0;i<trackList.size();i++)
		{
			Track track=trackList.get(i);
			double originalLongitude=Double.parseDouble(track.getLongitude());
			double originalLatitude=Double.parseDouble(track.getLatitude());	
			double lat1=(Math.PI/180)*originalLatitude;   
			double lat2=(Math.PI/180)*latitude;
			double lon1=(Math.PI/180)*originalLongitude;
			double lon2=(Math.PI/180)*longitude;
			//地球半径  单位KM
	        double R = 6371;
	        double distiance=  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;  
	        Log.d(TAG, "disticance:"+distiance+",track:"+track.getTrack());
			if (distiance<0.2) {
				String trackName=track.getTrack();
				Log.d(TAG, "return track name:"+trackName);
				return track.getTrack();
			}
		}
		return null;
    }
    protected void onPause()
    {
    	super.onPause();
    	mLocationClient.stop();
    }
    
    
    protected void onDestory() {
		super.onDestroy();
	} 
    
    protected void onStart() {
    	super.onStart();
//      	trackList.clear();
//    	trackList=trackDB.GetTracks();
//    	trackAdapter=new TrackAdapter(TrackActivity.this, R.layout.track_item, trackList);
//    	trackListView.setAdapter(trackAdapter);
	}
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	trackList.clear();
    	trackList=trackDB.GetTracks();
    	trackAdapter=new TrackAdapter(TrackActivity.this, R.layout.track_item, trackList);
    	trackListView.setAdapter(trackAdapter);
    }
    //////////////////////////////////////以上位置信息获取///////////////////////////////////
}
