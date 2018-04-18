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
	//��ʾ����
	TextView timeTv,IRIDTv,temperatureTv, humidityTv, pressureTv,lapsTv,
			 tolTimeTv,averageTimeTv,bestTimeTv,bestLapTv,modeTv;
	String roundCounter;
	String deviceID;
	Cursor detailInfoCursor,roundCounterCursor;
	DataConvert dataConvert;
	/*******�������ݿ�******/
	RoundCounterDetailInfoDB roundCounterDetailDB;
	RoundCounterDB roundCounterDB;
	RcIridDB  rcIridDB;
	TrackDB trackDB;
	EditText remarkEt;
	
	LineChart mChart;
	TextView backTv;
	////////////track������//////////////////////////////
	Spinner trackSpinner;
	private List<String> trackList;
	private ArrayAdapter<String> trackSpinnerAdapter;
	//////////////RC������////////////////////////////
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
		//��ʼ��
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
		
		
		/**********************track������*****************************/
		   //����
        trackList = new ArrayList<String>();
        trackList=trackDB.GetTrackNames();
        trackList.add(0, getResources().getString(R.string.unknow));
        if (trackList!=null && trackList.size()>0) {
        	  //������
            trackSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,trackList);
            //������ʽ
            trackSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
            //����������
            trackSpinner.setAdapter(trackSpinnerAdapter);
		}
        //ѡ���¼�
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
		 * ��ʾ��ϸ��Ϣ���б�
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
        /******************RC������*************************************/
         if (rcList!=null && rcList.size()>0) {
        	 //������
             rcSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,rcList);
             //������ʽ
             rcSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
             //����������
             rcSpinner.setAdapter(rcSpinnerAdapter);
		}
         
		Log.d(TAG, "rccar:"+rc);
		if (rc!=null) {
			for(int i=0;i<rcList.size();i++)
			{
				Log.d(TAG, "rcList:"+rcList.get(i));
				if (rc.equals(rcList.get(i))) {
					Log.d(TAG, "���"+"i��"+i);
					rcSpinner.setSelection(i);
					break;
				}
			}
		  }
		
		
         //rc�������ѡ���¼�
        rcSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String rc=rcSpinner.getSelectedItem().toString();
				Log.d(TAG, "ѡ���RC:"+rc+",roundCounter:"+roundCounter);
				roundCounterDetailDB.updateRC(roundCounter, rc,deviceID);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
        });
	}
	
    /** 
     * ����Back�������¼�,����1: 
     * ע��: 
     * super.onBackPressed()���Զ�����finish()����,�ر� 
     * ��ǰActivity. 
     * ��Ҫ����Back����,ע�͸��д��뼴�� 
     */  
    @Override  
    public void onBackPressed() {  
        super.onBackPressed();  
	   	String remarkStr=remarkEt.getText().toString();
    	roundCounterDetailDB.updateRemark(roundCounter, remarkStr,deviceID);      
     }  
     
    /**
     * �ײ���ʱ�� �ɼ� ���� �� ��ת
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
        public void onClick(View v)
        {    
        	int id = v.getId();
		    if (id==R.id.backTv) {
		     	String remarkStr=remarkEt.getText().toString();
		    	roundCounterDetailDB.updateRemark(roundCounter, remarkStr,deviceID);     
		    	Log.d(TAG, "�������ݿ�");
				((Activity) RoundCounterDetailActivity.this).finish();
			}
		}
    };  
	private void initChart()
	{
		//����ͼ��
		Legend legend=mChart.getLegend();
		legend.setEnabled(false);
		//Y��
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
		
		//X��
		XAxis xAxis=mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		//����

		mChart.setDescription("");
//		mChart.setContentDescription("");

		mChart.setAlpha(0.8f);

		mChart.setTouchEnabled(true);
		//�����Ƿ�����϶�������
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		//�����Ƿ�����������
		mChart.setPinchZoom(true);
		//���ñ�����ɫ
		mChart.setBackgroundColor(0xff10a9c4);
		mChart.setGridBackgroundColor(0xff10a9c4);
		mChart.setDrawGridBackground(true);		
		 // ��������
        setData(lapsInt,averageTime);
        //��Y�����Ķ���
        mChart.animateX(1000);
		
		//������С������
		 mChart.setScaleMinima(0.5f, 1f);

        // ˢ��ͼ��
        mChart.invalidate();
	}
	 /**
	  * ��ʼ��DetailResult
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
	        
	        //y������
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
//	        set1.setDrawCubic(false);  //��������ΪԲ������
	        set1.setDrawValues(false);
			set1.setCubicIntensity(0.2f);
			set1.setDrawFilled(false);  //���ð����ķ�Χ���������ɫ
			set1.setDrawCircles(false);  //������Բ��
			set1.setLineWidth(2f);    //�����ߵĿ��
			set1.setHighLightColor(Color.rgb(244, 117, 117));
			set1.setColor(Color.WHITE);    //�������ߵ���ɫ
	
	        // create a data object with the datasets
	        LineData data = new LineData(xVals, set1);
//	        LineData data=new LineData(set1);
	        // set data
	        mChart.setData(data);
	    }

	/**
	 * ��ʼ����ʾ
	 */
	private void init()
	{
		//��ѯroundCounterDetailInfo��
		
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
				temperatureTv.setText(temperature+"��");
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
			Log.d(TAG, "��������ŵõ���RC:"+rc);
			
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
			//����IRID
		}
	}
	
	
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
