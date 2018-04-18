package com.measuredunitsetting.hydraulic.test;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMeasuredUnitDB;
import com.measuredunitsetting.global.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HydraulicMonitorNetworkTestOneActivity extends Activity{
	private final static String TAG=HydraulicMonitorNetworkTestOneActivity.class.getSimpleName();
	//返回
	TextView backTv;
	//开始
	LinearLayout startLl;
	TextView startTv;
	//监测网ID
	int monitorNetworkId;
	//测量单元总个数
	int totalUnitNumber;
	HydraulicMeasuredUnitDB hydraulicMeasuredUnitDB;
	//请连接电缆后开始
	TextView connectMeasureUnitOKTv;
	
	boolean isBenchUnitsMany=false;
	boolean isunitNumberMany=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetwroktestone);
		//初始化
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		totalUnitNumber=getIntent().getIntExtra("totalUnitNumber", 0);
		//初始化数据库
		hydraulicMeasuredUnitDB=new HydraulicMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		final long unitNumber=hydraulicMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetworkId);
		final long benchUnits=hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureType(monitorNetworkId, "A0");
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//请连接电缆后开始
		connectMeasureUnitOKTv=(TextView)findViewById(R.id.connectMeasureUnitOKTv);
		//
		//开始
		startTv=(TextView)findViewById(R.id.startTv);
		startLl=(LinearLayout)findViewById(R.id.startLl);
		startLl.setOnClickListener(new View.OnClickListener()
		{
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View arg0) {
				//测量单元个数小于总测量单元个数或者基准点的个数大于1
				if ((unitNumber<totalUnitNumber || benchUnits>1) && !isBenchUnitsMany ) {
					connectMeasureUnitOKTv.setText(R.string.measurementUnitInYourSelectedMonitoringNetworkIsNotCompleted);
					startTv.setText(R.string.continuTest);
					startLl.setBackgroundColor(R.color.yellow);
					isBenchUnitsMany=true;
					return;
				}
				else if (unitNumber>37 && !isunitNumberMany) {
					connectMeasureUnitOKTv.setText(R.string.measurementUnitsNumberTooHigh);
					startTv.setText(R.string.continuTest);
					startLl.setBackgroundColor(R.color.yellow);
					isunitNumberMany=true;
					return;
				}

				Intent intent=new Intent(HydraulicMonitorNetworkTestOneActivity.this,HydraulicMonitorNetworkTestTwoActivity.class);
			   	intent.putExtra("monitorNetworkId", monitorNetworkId);
    		   	intent.putExtra("totalUnitNumber", totalUnitNumber);
				startActivity(intent);

				
			}
		});
		
		
	}
	
	
	
    /**
     * 顶部和底部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)HydraulicMonitorNetworkTestOneActivity.this).finish();
			}
		}
    }; 
}
