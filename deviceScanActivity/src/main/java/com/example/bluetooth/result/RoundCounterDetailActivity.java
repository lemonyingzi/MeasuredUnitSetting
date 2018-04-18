package com.example.bluetooth.result;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.data.DataConvert;
import com.example.bluetooth.db.RcIridDB;
import com.example.bluetooth.db.RoundCounterDB;
import com.example.bluetooth.db.RoundCounterDetailInfoDB;
import com.example.bluetooth.db.TrackDB;
import com.example.bluetooth.le.R;
import com.example.bluetooth.list.DetailedResult;
import com.example.bluetooth.list.DetailedResultAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RoundCounterDetailActivity extends Activity {
	 private final static String TAG = RoundCounterDetailActivity.class.getSimpleName();
	//显示部分
	TextView timeTv,IRIDTv,temperatureTv, humidityTv, pressureTv,lapsTv,
			 tolTimeTv,averageTimeTv,bestTimeTv,bestLapTv,modeTv;
	String roundCounter;
	String deviceID;
	Cursor detailInfoCursor,roundCounterCursor;
	DataConvert dataConvert;
	/*******定义数据库******/
	RoundCounterDetailInfoDB roundCounterDetailDB;
	RoundCounterDB roundCounterDB;
	RcIridDB  rcIridDB;
	TrackDB trackDB;
	EditText remarkEt;
	
	LineChart mChart;
	TextView backTv;
	////////////track下拉框//////////////////////////////
	Spinner trackSpinner;
	private List<String> trackList;
	private ArrayAdapter<String> trackSpinnerAdapter;
	//////////////RC下拉框////////////////////////////
	Spinner rcSpinner;
	private List<String> rcList;
	private ArrayAdapter<String> rcSpinnerAdapter;
	private String rc;

	
	String irid=null;
	private List<DetailedResult> detailedResultList=new ArrayList<DetailedResult>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roundcounterdetail);
		
		dataConvert=new DataConvert();
		//初始化
		timeTv=(TextView)  findViewById(R.id.time);
		IRIDTv=(TextView) findViewById(R.id.IRID);
		rcSpinner=(Spinner) findViewById(R.id.RCCarSpinner);
		trackSpinner=(Spinner) findViewById(R.id.trackSpinner);
		temperatureTv=(TextView) findViewById(R.id.Temperature);
		humidityTv=(TextView) findViewById(R.id.Humidity);
		pressureTv=(TextView) findViewById(R.id.Pressure);
		lapsTv=(TextView) findViewById(R.id.Laps);
		tolTimeTv=(TextView) findViewById(R.id.TolTime);
		averageTimeTv=(TextView) findViewById(R.id.AverageTime);
		bestTimeTv=(TextView) findViewById(R.id.BestTime);
		bestLapTv=(TextView) findViewById(R.id.BestLAP);
		mChart = (LineChart) findViewById(R.id.chart1);
		backTv=(TextView) findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		modeTv=(TextView) findViewById(R.id.modeTv);
        remarkEt=(EditText) findViewById(R.id.remarkEd);
        
	 
		
		Intent intent=getIntent();
		roundCounter=intent.getStringExtra("roundCounter");
		deviceID=intent.getStringExtra("deviceId");
		Log.d(TAG, "deviceID:"+deviceID);
		roundCounterDetailDB=new RoundCounterDetailInfoDB(getApplicationContext(), "TimerStore", null, 7);
		roundCounterDB=new RoundCounterDB(getApplicationContext(), "TimerStore", null, 7);
		rcIridDB=new RcIridDB(getApplicationContext(), "TimerStore", null, 7);
		trackDB=new TrackDB(getApplicationContext(), "TimerStore", null, 7);
	  	
		rcList=new ArrayList<String>();
	    rcList=rcIridDB.getRCs();
		
		
		/**********************track下拉框*****************************/
		   //数据
        trackList = new ArrayList<String>();
        trackList=trackDB.GetTrackNames();
        trackList.add(0, getResources().getString(R.string.unknow));
        if (trackList!=null && trackList.size()>0) {
        	  //适配器
            trackSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,trackList);
            //设置样式
            trackSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
            //加载适配器
            trackSpinner.setAdapter(trackSpinnerAdapter);
		}
        //选中事件
        trackSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        		{
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						String track=trackSpinner.getSelectedItem().toString();
						roundCounterDetailDB.updateTrack(roundCounter, track,deviceID);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						
					}
        		});
		/**
		 * 显示详细信息的列表
		 */
		initDetailedResult();
		DetailedResultAdapter adapter=new DetailedResultAdapter(RoundCounterDetailActivity.this, R.layout.detailedresult_item, detailedResultList);
        ListView listView=(ListView) findViewById(R.id.DetailedResultsListView);
        View vhead=View.inflate(this, R.layout.detailedresult_item_head, null);
        listView.addHeaderView(vhead);
        listView.setAdapter(adapter);
        
		if(roundCounter!=null)
		{	
			detailInfoCursor=roundCounterDetailDB.QueryAccordingToRoundCounter(roundCounter,deviceID);
			roundCounterCursor=roundCounterDB.queryAccordingToRoundCounter(Integer.parseInt(roundCounter),deviceID);
			if (roundCounterCursor.moveToFirst()) {
				Log.d(TAG, "roundCounterCursor");
			}
			init();
			initChart();
		}
		
		String remarkStr=roundCounterDetailDB.getRemark(roundCounter,deviceID);
		if (remarkStr!=null && !remarkStr.equals("")) {
			remarkEt.setText(remarkStr);

		}
        /******************RC下拉框*************************************/
         if (rcList!=null && rcList.size()>0) {
        	 //适配器
             rcSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,rcList);
             //设置样式
             rcSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
             //加载适配器
             rcSpinner.setAdapter(rcSpinnerAdapter);
		}
         
		Log.d(TAG, "rccar:"+rc);
		if (rc!=null) {
			for(int i=0;i<rcList.size();i++)
			{
				Log.d(TAG, "rcList:"+rcList.get(i));
				if (rc.equals(rcList.get(i))) {
					Log.d(TAG, "相等"+"i："+i);
					rcSpinner.setSelection(i);
					break;
				}
			}
		  }
		
		
         //rc下拉框的选中事件
        rcSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String rc=rcSpinner.getSelectedItem().toString();
				Log.d(TAG, "选择的RC:"+rc+",roundCounter:"+roundCounter);
				roundCounterDetailDB.updateRC(roundCounter, rc,deviceID);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
        });
	}
	
    /** 
     * 监听Back键按下事件,方法1: 
     * 注意: 
     * super.onBackPressed()会自动调用finish()方法,关闭 
     * 当前Activity. 
     * 若要屏蔽Back键盘,注释该行代码即可 
     */  
    @Override  
    public void onBackPressed() {  
        super.onBackPressed();  
	   	String remarkStr=remarkEt.getText().toString();
    	roundCounterDetailDB.updateRemark(roundCounter, remarkStr,deviceID);      
     }  
     
    /**
     * 底部定时器 成绩 排名 我 跳转
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
		    if (id==R.id.backTv) {
		     	String remarkStr=remarkEt.getText().toString();
		    	roundCounterDetailDB.updateRemark(roundCounter, remarkStr,deviceID);     
		    	Log.d(TAG, "更新数据库");
				((Activity) RoundCounterDetailActivity.this).finish();
			}
		}
    };  
	private void initChart()
	{
		//设置图例
		Legend legend=mChart.getLegend();
		legend.setEnabled(false);
		//Y轴
		YAxis leftAxis=mChart.getAxisLeft();
		YAxis rightAxis=mChart.getAxisRight();
		leftAxis.setStartAtZero(false);
		rightAxis.setEnabled(false);
		leftAxis.setEnabled(false);
		leftAxis.setDrawGridLines(false);
		rightAxis.setDrawGridLines(false);
	  	int lapsInt=detailInfoCursor.getInt(detailInfoCursor.getColumnIndex("LAPS"));
    	float averageTime=detailInfoCursor.getFloat(detailInfoCursor.getColumnIndex("AverageTime"));
		int maxLapTime=roundCounterDB.getMaxLapTimeAccordingToRoundCounter(roundCounter,deviceID);
		int minLapTime=roundCounterDB.getMinLapTimeAccordingToRoundCounter(roundCounter,deviceID);
		float minY=(float) (averageTime/(maxLapTime/1000)-0.2);
		float maxY=(float)(averageTime/(minLapTime/1000)+0.2);
		
		leftAxis.setAxisMaxValue(maxY);
		leftAxis.setAxisMinValue(minY);
		
		//X轴
		XAxis xAxis=mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		//数据

		mChart.setDescription("");
//		mChart.setContentDescription("");

		mChart.setAlpha(0.8f);

		mChart.setTouchEnabled(true);
		//设置是否可以拖动，缩放
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		//设置是否能扩大扩下
		mChart.setPinchZoom(true);
		//设置背景颜色
		mChart.setBackgroundColor(0xff10a9c4);
		mChart.setGridBackgroundColor(0xff10a9c4);
		mChart.setDrawGridBackground(true);		
		 // 加载数据
        setData(lapsInt,averageTime);
        //从Y轴进入的动画
        mChart.animateX(1000);
		
		//设置最小的缩放
		 mChart.setScaleMinima(0.5f, 1f);

        // 刷新图表
        mChart.invalidate();
	}
	 /**
	  * 初始化DetailResult
	  */
	private void initDetailedResult()
	{
    	detailedResultList=roundCounterDB.QueryDetailedResultAccordingToRoundCounter(roundCounter,deviceID);
	}
	private void setData(int lapsInt,float averageTime) {	
		
	        ArrayList<String> xVals = new ArrayList<String>();
	        for (int i = 1; i <=lapsInt; i++) {
	            xVals.add(String.valueOf(i));
	        }
	        
	        //y轴数据
	        ArrayList<Entry> yVals = new ArrayList<Entry>();
	        int i=0;
	        if (roundCounterCursor.moveToFirst()) {
	        	do {
					int lapTime=roundCounterCursor.getInt(roundCounterCursor.getColumnIndex("LAPTime"));
					float yfloat=averageTime/((float)lapTime/1000);
					yVals.add(new Entry(yfloat,i));
	        		i++;
				} while (roundCounterCursor.moveToNext());
				
			}
	        // create a dataset and give it a type
	        LineDataSet set1 = new LineDataSet(yVals, "DataSet Line");
//	        set1.setDrawCubic(false);  //设置曲线为圆滑的线
	        set1.setDrawValues(false);
			set1.setCubicIntensity(0.2f);
			set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
			set1.setDrawCircles(false);  //设置有圆点
			set1.setLineWidth(2f);    //设置线的宽度
			set1.setHighLightColor(Color.rgb(244, 117, 117));
			set1.setColor(Color.WHITE);    //设置曲线的颜色
	
	        // create a data object with the datasets
	        LineData data = new LineData(xVals, set1);
//	        LineData data=new LineData(set1);
	        // set data
	        mChart.setData(data);
	    }

	/**
	 * 初始化显示
	 */
	private void init()
	{
		//查询roundCounterDetailInfo表
		
		if (detailInfoCursor.moveToFirst()) {
			String startDate=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("startDate"));
			String startTime=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("startTime"));
			timeTv.setText(startDate+" "+startTime);
			String track=null;
			try
			{
				track=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("track"));
				Log.d(TAG, "track:"+track);
				if (track!=null) {
					for(int i=0;i<trackList.size();i++)
					{
						if (track.equals(trackList.get(i))) {
							trackSpinner.setSelection(i);
							break;
						}
					}
				}
				else
				{
					trackSpinner.setSelection(0);
				}
				
			}
			catch (Exception e) {
				
			}
	
			String temperature=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("Temperature"));
			if (temperature!=null && temperature.equals("-100.00")) {
				temperatureTv.setText(R.string.disabled);
			}
			else
			{
				temperatureTv.setText(temperature+"℃");
			}
			String humidity=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("Humidity"));
			if (humidity!=null && humidity.equals("-1")) {
				humidityTv.setText(R.string.disabled);
			}
			else
			{
				humidityTv.setText(humidity+"%RH");
			}
			String pressure=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("Pressure"));
			if (pressure!=null && pressure.equals("0.00")) {
				pressureTv.setText(R.string.disabled);
			}
			else
			{
				pressureTv.setText(pressure+"hPa");
			}
			String laps=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("LAPS"));
			lapsTv.setText(laps);
			String tolTime=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("TolTime"));
			tolTimeTv.setText(tolTime);
			String averageTime=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("AverageTime"));
			averageTimeTv.setText(averageTime);
			String BestTime=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("BestTime"));
			bestTimeTv.setText(BestTime);
			String bestLap=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("BestLap"));
			bestLapTv.setText(bestLap);
			String IRID=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("irid"));
			irid=dataConvert.getIRIDInt(IRID); 
			rc=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("RCCar"));
			Log.d(TAG, "根据轮序号得到的RC:"+rc);
			
			IRIDTv.setText(irid);
			String mode=detailInfoCursor.getString(detailInfoCursor.getColumnIndex("mode"));
			if (mode!=null && mode.equals("Time")) {
				modeTv.setText(R.string.timeLimit);
			}
			else if (mode!=null && mode.equals("Free")) {
				modeTv.setText(R.string.freeStr);
			}
			if (!rcList.contains(rc)) {
				rcList.add(0, rc);
			}
			//设置IRID
		}
	}
	
	
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
