/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.result;


import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.bluetooth.data.AnalyticalData;
import com.example.bluetooth.db.*;
import com.example.bluetooth.le.IActivity;
import com.example.bluetooth.le.MainActivity;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.RankingActivity;
import com.example.bluetooth.list.Result;
import com.example.bluetooth.list.ResultAdapter;
import com.example.bluetooth.list.ResultCompartor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class ResultActivity extends Activity implements OnChartValueSelectedListener{
    private final static String TAG = ResultActivity.class.getSimpleName();
    RoundCounterDetailInfoDB roundCounterDetailInfoDB;
    RoundCounterDB roundCounterDB;
    TrackDB trackDB;
    RcIridDB rcIridDB;
    SyncDataDB syncDataDB;
    private List<Result> resultList=new ArrayList<Result>(); 
    Cursor detailInfoCursor;
 
    private ViewPager viewPagerDate; 
    /**
     * 底部四个textview
     */
    LinearLayout iTv;//我
    LinearLayout resultTv;//成绩
    LinearLayout rankingTv;//排名
    LinearLayout timerTv;//计时器
    TextView myText;
	private CombinedChart[] dateCombinedCharts;
	
	TextView dateTv;
	TextView trackTv;
	ResultAdapter adapter;
	ListView listView;
	/////////////////////////赛道下拉列表框//////////////////
	private Spinner trackSpinner;//选择赛道下拉框
	private List<String> trackList;
	private ArrayAdapter<String> trackSpinnerAdapter;
	////////////////////////RC下拉列表框////////////////////
	private Spinner rcSpinner;
	private List<String> rcList;
	private ArrayAdapter<String> rcSpinnerAdapter;
	//////////////////////开始日期//////////////
	private Spinner startDateSpinner;
	private List<String> startDateList;
	private ArrayAdapter<String> startDateSpinnerAdapter;
	////////////////终止日期下拉框////////////////////////
	private Spinner endDateSpinner;
	private List<String> endDateList;
	private ArrayAdapter<String> endDateSpinnerAdapter;
	//////////////模式下拉列表框//////////////////////////
	private Spinner modeSpinner;
	private List<String> modeList;
	private ArrayAdapter<String> modeSpinnerAdapter;
	//////////////手势识别/////////////////////////////
	 private GestureDetector mGestureDetector;
	 private int verticalMinDistance = 180;
	 private int minVelocity         = 200;
	 RelativeLayout resultRl;
	 
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_result);
        roundCounterDetailInfoDB=new RoundCounterDetailInfoDB(getApplicationContext(), "TimerStore", null, 7);
        trackDB=new TrackDB(getApplicationContext(), "TimerStore", null, 7);
        rcIridDB=new RcIridDB(getApplicationContext(), "TimerStore", null, 7);
        syncDataDB=new SyncDataDB(getApplicationContext(), "TimerStore", null, 7);
        roundCounterDB=new RoundCounterDB(getApplicationContext(), "TimerStore", null, 7);
        /**
         * 底部四个textview跳转
         */
        iTv=(LinearLayout) findViewById(R.id.bottomI);
        iTv.setOnClickListener(bottomListener);
        resultTv=(LinearLayout) findViewById(R.id.bottomRanking);
        resultTv.setOnClickListener(bottomListener);
        rankingTv=(LinearLayout) findViewById(R.id.bottomResults);
        rankingTv.setOnClickListener(bottomListener);
        timerTv=(LinearLayout) findViewById(R.id.bottomTimer);
        timerTv.setOnClickListener(bottomListener);
        viewPagerDate=(ViewPager) findViewById(R.id.viewpagerDate);
    
//        viewPagerTrack=(ViewPager) findViewById(R.id.viewpagerTrack);
        dateTv=(TextView) findViewById(R.id.date_tv);
//        trackTv=(TextView) findViewById(R.id.track_tv);
        trackSpinner=(Spinner) findViewById(R.id.trackSpinner);
        rcSpinner=(Spinner) findViewById(R.id.rcSpinner);
        startDateSpinner=(Spinner) findViewById(R.id.startDateSpinner);
        endDateSpinner=(Spinner) findViewById(R.id.endDateSpinner);
        modeSpinner=(Spinner) findViewById(R.id.modeSpinner);
        
        listView=(ListView) findViewById(R.id.list_view);
        myText=(TextView) findViewById(R.id.myText);
        
//        resultRl=(RelativeLayout) findViewById(R.id.resultRl);
//        resultRl.setOnTouchListener(this);
//        resultRl.setLongClickable(true);
//        mGestureDetector = new GestureDetector(ResultActivity.this);

        //分析数据
        AnalyticalData analyticalData=new AnalyticalData(getApplicationContext());
        analyticalData.GetRoundCounterFromSyncData();
        analyticalData.GetRoundCounterDetailInfoFromRoundCounter();
        
        ///////////////////////////初始化赛道下拉框//////////////////////////////////
        //数据
        trackList = new ArrayList<String>();
        trackList=trackDB.GetTrackNames();
        if (trackList!=null) {
        	  for(int i=0;i<trackList.size();i++)
              {
              	Log.d(TAG, "track:"+trackList.get(i));
              }
		}
      
        if (trackList==null) {
			trackList=new ArrayList<String>();
			trackList.add(0,getResources().getString(R.string.all));
		}
        else
        {
            trackList.add(0, getResources().getString(R.string.all));
        }
        trackList.add(1, getResources().getString(R.string.unknow));
        if (trackList!=null && trackList.size()>0) {
        	  //适配器
            trackSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,trackList);
            //设置样式
            trackSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
            //加载适配器
            trackSpinner.setAdapter(trackSpinnerAdapter);
		}

        ////////////////////////初始化RC下拉框////////////////////////////////////
    	 rcList=new ArrayList<String>();
         rcList=rcIridDB.getRCs();
         if (rcList==null) {
        	 rcList=new ArrayList<String>();
        	 rcList.add(0,getResources().getString(R.string.all));
		 }
         else
         {
             rcList.add(0,getResources().getString(R.string.all));
         }
         if (rcList!=null && rcList.size()>0) {
        	 //适配器
             rcSpinnerAdapter= new ArrayAdapter<String>(this, R.layout.spinner_track, R.id.text,rcList);
             //设置样式
             rcSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
             //加载适配器
             rcSpinner.setAdapter(rcSpinnerAdapter);
		}

      ////////////////////////////初始化开始日期/////////////////////////////
         startDateList=roundCounterDetailInfoDB.QueryDate();
         if (startDateList!=null) {
			Log.d(TAG, "startDateList:"+startDateList.size());
		}
         endDateList=new ArrayList<String>();
         if (startDateList!=null && startDateList.size()>0) {
        	 //适配器
        	 startDateSpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_track,R.id.text,startDateList);
        	 //设置样式
        	 startDateSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
			//加载适配器
        	 startDateSpinner.setAdapter(startDateSpinnerAdapter);
        	 startDateSpinner.setSelection(0);
         }

         /////////////////////初始换终止日期//////////////////////////
         for(int i=startDateList.size()-1;i>=0;i--)
         {
         	endDateList.add(startDateList.get(i));
         }
         if (endDateList!=null && endDateList.size()>0) {
         	 //适配器
         	 endDateSpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_track,R.id.text,endDateList);
         	 //设置样式
         	 endDateSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
 			//加载适配器
         	 endDateSpinner.setAdapter(endDateSpinnerAdapter);
         	 endDateSpinner.setSelection(0);
 		}
         ///////////////////////////track选中事件//////////////////////////////
         //选中事件
         trackSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
         		{
 					@Override
 					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 						Log.d(TAG, "track spinner选中事件");
 						GetChartAndList();
 					}
 					@Override
 					public void onNothingSelected(AdapterView<?> parent) {
 						
 					}
         		});
         /////////////////////////RC选中事件//////////////////////////////////////
         //rc下拉框的选中事件
        rcSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "rc spinner选中事件");
				GetChartAndList();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        });
         ///////////////////////开始日期选中事件/////////////////////////////////
        startDateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try
				{
					String startDate=startDateSpinner.getSelectedItem().toString();
					String endDate=endDateSpinner.getSelectedItem().toString();
					int compare=startDate.compareTo(endDate);
					if (compare>0) {
						Toast.makeText(getApplicationContext(), R.string.startCannotLargerEndTime, Toast.LENGTH_SHORT).show();
						startDateSpinner.setSelection(0);
					}
					Log.d(TAG, "start date spinner选中事件");
					GetChartAndList();
				}
				catch (Exception e) {
					Log.d(TAG, "startDateSpinner onItemselectedListener:"+e.toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {				
			}
        	
        });
      /////////////////////////终止日期选中事件///////////////////////////////////
         endDateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try
				{
					String startDate=startDateSpinner.getSelectedItem().toString();
					String endDate=endDateSpinner.getSelectedItem().toString();
					int compare=startDate.compareTo(endDate);
					if (compare>0) {
						Toast.makeText(getApplicationContext(), R.string.endCannotSmallerStartTime, Toast.LENGTH_SHORT).show();
						endDateSpinner.setSelection(0);				
					}
					Log.d(TAG, "end date spinner选中事件");
					GetChartAndList();
				}
				catch (Exception e) {
					Log.d(TAG, "endDateSpinner onItemSelectedListener:"+e.toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
        	 
         });
         ////////////////////////模式下拉列表框///////////////////////////////
         modeList=new ArrayList<String>();
         modeList.add(getResources().getString(R.string.all));
         modeList.add(getResources().getString(R.string.timeLimit));
         modeList.add(getResources().getString(R.string.freeStr));
        if (modeList!=null && modeList.size()>0) {
	       	 //适配器
	       	 modeSpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_track,R.id.text,modeList);
	       	 //设置样式
	       	 modeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_track);
				//加载适配器
	       	 modeSpinner.setAdapter(modeSpinnerAdapter);
	       	 modeSpinner.setSelection(0);
       	 }
         
        modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        		{

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						Log.d(TAG, "mode spinner 选中事件");
						GetChartAndList();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
        	
        		});
        
        if (resultList!=null) {
             View vhead=View.inflate(this, R.layout.result_item_head, null);
             listView.addHeaderView(vhead);
             //////////////listView单击跳转到详细页面///////////////////////////////////////////
             listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
             	@Override
             	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
             		if (position>0) {
             		   	Result result=resultList.get(position-1);
                     	Intent intent=new Intent(ResultActivity.this,RoundCounterDetailActivity.class);
                     	intent.putExtra("roundCounter", result.getRoundCounter());
                     	intent.putExtra("deviceId", result.getDeviceID());
                     	startActivity(intent);
     				}
             	}
             });
             //////////////listView长按删除////////////////////////////////////////////////
             listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					if (position>0) {
						final String roundCounter=adapter.getItem(position-1).getRoundCounter();
						final String deviceId=adapter.getItem(position-1).getDeviceID();
			            AlertDialog.Builder builder=new Builder(ResultActivity.this);  
			            builder.setMessage(R.string.deleteOrNot);  
			            builder.setTitle(R.string.note);  
			            builder.setPositiveButton(R.string.confirmStr, new OnClickListener() {  
			                @Override  
			                public void onClick(DialogInterface dialog, int which) {  
			                	roundCounterDetailInfoDB.deleteAccorrdingToRoundCounter(roundCounter,deviceId);
			                	roundCounterDB.deleteAccordingToRoundCounter(roundCounter, deviceId);
			                	syncDataDB.deleteAccordingToRoundcounter(roundCounter, deviceId);

			                	GetChartAndList();
			                }  
			            });  
			            builder.setNegativeButton(R.string.cancelStr, new OnClickListener() {  
			                  
			                @Override  
			                public void onClick(DialogInterface dialog, int which) {  
			                      
			                }  
			            });  
			            builder.create().show();  
						return true;
					}
					return true;
				}
            	 
             });}
    }

  
    ///////////获取chart和list的值
    private void GetChartAndList() {
    	try
    	{
    		GetChartData(trackSpinner.getSelectedItem().toString(),rcSpinner.getSelectedItem().toString(),startDateSpinner.getSelectedItem().toString(),endDateSpinner.getSelectedItem().toString(),modeSpinner.getSelectedItem().toString());
    	    initResults(startDateSpinner.getSelectedItem().toString(), endDateSpinner.getSelectedItem().toString(), trackSpinner.getSelectedItem().toString(), rcSpinner.getSelectedItem().toString(),modeSpinner.getSelectedItem().toString()); 
    		if (resultList!=null) {
        	 adapter=new ResultAdapter(ResultActivity.this, R.layout.result_item, resultList);
             listView.setAdapter(adapter);
             myText.setVisibility(8);
             listView.setVisibility(0);
    		}
             else
             {
            	 listView.setVisibility(8);
            	 myText.setVisibility(0);
             }
    	}
    	catch (Exception e) {
    		Log.d(TAG,"GetChartAndList:"+e.toString());
    	}

	}
    
    ////////////////////////////生成图表///////////////////////////////////////////
    @SuppressWarnings("deprecation")
	private void GetChartData(String track,String rc,String startDate,String endDate,String mode) {
     	final List<String> date=roundCounterDetailInfoDB.QueryDate(startDate,endDate);
     	date.add(0,getResources().getString(R.string.all));
        List<String> list2 = new ArrayList<String>();
        list2.add(null);
        date.removeAll(list2);
        dateTv.setText(date.get(0));
        dateCombinedCharts=new CombinedChart[date.size()];
     	for(int j=0;j<date.size();j++)
     	{
     		try
     		{
     			ArrayList<Entry> temperatureEntries = new ArrayList<Entry>();
      		    ArrayList<BarEntry> averageTimeEntries=new ArrayList<BarEntry>();
      		    ArrayList<Entry> bestTimeEntries=new ArrayList<Entry>(); 
      		    List<String> laps=new ArrayList<String>();
              	//查询最后一个日期对应的所有Lap
      		    if (j==0) {
					detailInfoCursor=roundCounterDetailInfoDB.QueryAccordingDate(startDate, endDate,track, rc, mode);
				}
      		    else
      		    {
                  	detailInfoCursor=roundCounterDetailInfoDB.QueryAccordingDate(date.get(j), track, rc,mode);
      		    }
              	int i=0;
              	if (detailInfoCursor.moveToFirst()) {
          			do {
          				float temperature=detailInfoCursor.getFloat(detailInfoCursor.getColumnIndex("Temperature"));
          				float averageTime=detailInfoCursor.getFloat(detailInfoCursor.getColumnIndex("AverageTime"));
          				float bestTime=detailInfoCursor.getFloat(detailInfoCursor.getColumnIndex("BestTime"));
          				if (temperature!=-100) {
          					temperatureEntries.add(new Entry(temperature, i));
          				}
          				averageTimeEntries.add(new BarEntry(averageTime, i));
          				bestTimeEntries.add(new Entry(bestTime, i));
          				i=i+1;
          				laps.add(String.valueOf(i));
          			} while (detailInfoCursor.moveToNext());
          		}
                detailInfoCursor.close();
              	CombinedChart combinedChart=new CombinedChart(ResultActivity.this);
              	combinedChart=initChart(laps,temperatureEntries,averageTimeEntries,bestTimeEntries);
              	dateCombinedCharts[j]=combinedChart;
     		}
     		catch (Exception e) {
     			Log.e(TAG, "GetChartData:"+e.toString());
     		}
     	}
        //-----初始化PagerAdapter-----
        viewPagerDate.setAdapter(pagerAdapter);
        viewPagerDate.setOnPageChangeListener(new OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int arg0) {
         
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override

            public void onPageSelected(int position) {
                dateTv.setText(date.get(position));
            }
        });
      }
    
    PagerAdapter pagerAdapter=new PagerAdapter(){
        @Override
        public int getCount() {
            return dateCombinedCharts.length;
        }
        @Override

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override

        public void destroyItem(ViewGroup container,int position,Object o){
        }
        @Override

        public Object instantiateItem(ViewGroup container,int position){
        	CombinedChart chart=new CombinedChart(ResultActivity.this);
            chart=dateCombinedCharts[position];
            container.removeView(chart);
            container.addView(chart);
            return chart;
        }};
        
    public class DateUtil {  
        public Date stringToDate(String dateString) {  
            ParsePosition position = new ParsePosition(0);  
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            Date dateValue = simpleDateFormat.parse(dateString, position);  
            return dateValue;  
        }  
    }  


    private CombinedChart initChart(List<String> laps,ArrayList<Entry> temperatureEntries, ArrayList<BarEntry> averageTimeEntries , ArrayList<Entry> bestTimeEntries)
    {
    	CombinedChart chart=new CombinedChart(ResultActivity.this);
        //初始化组合图
    	chart.setDescription("");
    	chart.setBackgroundColor(0xff10a9c4);
    	chart.setDrawGridBackground(false);
    	chart.setHighlightPerDragEnabled(false);
    	chart.setTouchEnabled(true);
		//设置是否可以拖动，缩放
    	chart.setDragEnabled(true);
    	chart.setScaleEnabled(true);
		//设置是否能扩大扩下
    	chart.setPinchZoom(true);
    	chart.setOnChartValueSelectedListener(this);
    	
    	
    	YAxis rightAxis=chart.getAxisRight();
    	rightAxis.setEnabled(false);
    	rightAxis.setDrawGridLines(false);
    	
    	YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        LineDataSet set1 = new LineDataSet(temperatureEntries, "Temperature");
        set1.setDrawCubic(true);  //设置曲线为圆滑的线
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(false);  //设置有圆点
        set1.setLineWidth(2f);    //设置线的宽度
        set1.setCircleSize(5f);   //设置小圆的大小
        set1.setHighLightColor(Color.GREEN);
        set1.setColor(Color.GREEN);    //设置曲线的颜色
        set1.setValueTextColor(Color.GREEN);
        set1.setDrawCubic(false);// 改变折线样式，用曲线。  
        set1.setValueTextSize(9f);
        dataSets.add(set1);
        
        LineDataSet set2=new LineDataSet(bestTimeEntries, "BestTime");
        set2.setDrawCubic(true);  //设置曲线为圆滑的线
        set2.setCubicIntensity(0.2f);
        set2.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set2.setDrawCircles(false);  //设置有圆点
        set2.setLineWidth(2f);    //设置线的宽度
        set2.setCircleSize(5f);   //设置小圆的大小
        set2.setHighLightColor(0xff97cbff);
        set2.setValueTextColor(0xff97cbff);
        set2.setColor(0xff97cbff);    //设置曲线的颜色
        set2.setDrawCubic(false);// 改变折线样式，用曲线。  
        set2.setValueTextSize(9f);

        dataSets.add(set2);
            //折线图
//        LineDataSet set3=new LineDataSet(bestTimeEntries, "BestTime");
//        set3.setDrawCubic(true);  //设置曲线为圆滑的线
//        set3.setCubicIntensity(0.2f);
//        set3.setDrawFilled(false);  //设置包括的范围区域填充颜色
//        set3.setDrawCircles(true);  //设置有圆点
//        set3.setLineWidth(2f);    //设置线的宽度
//        set3.setCircleSize(5f);   //设置小圆的大小
//        set3.setHighLightColor(Color.BLUE);
//        set3.setColor(Color.BLUE);    //设置曲线的颜色
//        set3.setDrawCubic(false);// 改变折线样式，用曲线。  
//
//        dataSets.add(set3);
        
        BarDataSet barDataSet=new BarDataSet(averageTimeEntries, "AverageTime");
        barDataSet.setColor(Color.rgb(0,191,255));
        barDataSet.setValueTextColor(Color.rgb(0,191,255));
        barDataSet.setValueTextSize(9f);
        barDataSet.setHighLightColor(Color.rgb(0,191,255));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        
        BarData barData=new BarData();
        barData.addDataSet(barDataSet);
        
        LineData mLineData=new LineData(laps,dataSets);

        CombinedData combinedData=new CombinedData(laps);
        combinedData.setData(mLineData);
        combinedData.setData(barData);
            
        chart.setData(combinedData);
        chart.invalidate();
        return chart;
    }
    /**
     * 温度
     * @return
     */
    private LineData generateLineData(ArrayList<Entry> temperatureEntries) {

        LineData d = new LineData();
        if (temperatureEntries==null || temperatureEntries.size()==0) {
			return null;
		}
        LineDataSet set = new LineDataSet(temperatureEntries, "Temperature");
        set.setColor(Color.GREEN);
        set.setLineWidth(3.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        // set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(false);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setHighLightColor(Color.MAGENTA);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }
    private LineData generateBestTimeLineData(ArrayList<Entry> bestTimeEntries) {

        LineData d = new LineData();
        if (bestTimeEntries==null || bestTimeEntries.size()==0) {
			return null;
		}
        LineDataSet set = new LineDataSet(bestTimeEntries, "BestTime");
        set.setColor(Color.BLACK);
        set.setLineWidth(3.5f);
        set.setCircleColor(Color.RED);
        set.setCircleSize(5f);
        // set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(false);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.DKGRAY);
        set.setHighLightColor(Color.MAGENTA);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }
    /**
     * 柱状图,平均时间
     * @return
     */
    private BarData generateBarData(ArrayList<BarEntry> averageTimeEntries) {
  	
        BarData d = new BarData();
        BarDataSet set = new BarDataSet(averageTimeEntries, "AverageTime");
        set.setColor(0xff97cbff);
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setHighLightColor(Color.RED);
        d.addDataSet(set);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return d;
    }
    /**
     * 柱状图，BestTime
     * @param bestTimeEntries
     * @return
     */
    private BarData generateBestTimeBarData(ArrayList<BarEntry> bestTimeEntries)
    {
    	  BarData d = new BarData();
          BarDataSet set = new BarDataSet(bestTimeEntries, "BestTime");
          set.setColor(0xffE1FFFF);
          set.setValueTextColor(Color.rgb(60, 220, 78));
          set.setValueTextSize(10f);
          set.setHighLightColor(Color.BLUE);
          d.addDataSet(set);
          set.setAxisDependency(YAxis.AxisDependency.LEFT);
          return d;
    }
    /**
     * 初始化结果数据
     */
    private void initResults(String startDate,String endDate,String track,String rc,String mode) {
    	Log.d(TAG, "get result list");
        resultList=roundCounterDetailInfoDB.query(startDate,endDate,track,rc,mode);
	}
    /**
     * 底部定时器 成绩 排名 我 跳转
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
			if (id == R.id.bottomI) 
			{
				Intent intent=new Intent(ResultActivity.this,IActivity.class);
				startActivity(intent);
			}
			else if(id==R.id.bottomTimer)
			{
				Intent intent=new Intent(ResultActivity.this,MainActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomRanking) {
				Intent intent=new Intent(ResultActivity.this,RankingActivity.class);
				startActivity(intent);
			}
		}
    };  
    

    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		
		
	}

	@Override
	public void onNothingSelected() {
	
		
	}

//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
//        	Log.d(TAG, "on fling");
//            // 切换Activity向左手势
//        	 Intent intent = new Intent(ResultActivity.this, RankingActivity.class);
//             startActivity(intent);
//           
//        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
//        	Log.d(TAG, "on fling");
//            // 切换Activity向右手势
//        	Intent intent = new Intent(ResultActivity.this,MainActivity.class);
//        	startActivity(intent);
//            finish();
//        }
//        return false;
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		Log.d(TAG, "onTouchEvent:"+mGestureDetector.onTouchEvent(event));
//		return mGestureDetector.onTouchEvent(event);
//	}
//
//	  public boolean dispatchTouchEvent(MotionEvent ev) {
//	      if (mGestureDetector != null) {  
//	          if (mGestureDetector.onTouchEvent(ev))  
//	              return false;  
//	      }  
//	      return super.dispatchTouchEvent(ev);  
//	  }  
//	
//	 public boolean onInterceptTouchEvent(MotionEvent event)
//	  {
//		 Log.d(TAG, "父类不做处理");
//		return false;
//		  
//	  }
}
