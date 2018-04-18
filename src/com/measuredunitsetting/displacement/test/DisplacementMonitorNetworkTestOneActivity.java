package com.measuredunitsetting.displacement.test;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMeasuredUnitDB;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.global.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplacementMonitorNetworkTestOneActivity extends Activity{
	private final static String TAG=DisplacementMonitorNetworkTestOneActivity.class.getSimpleName();
	//返回
	TextView backTv;
	//开始
	LinearLayout startLl;
	TextView startTv;
	//监测网ID
	int monitorNetworkId;
	//测量单元总个数
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	DisplacementMonitorPointDB displacementMonitorPointDB;
	
	//请连接电缆后开始
	TextView connectMeasureUnitOKTv;
	
	boolean isBenchUnitsMany=false;
	boolean isunitNumberMany=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointtestone);
		//初始化
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		//初始化数据库
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		final DisplacementMonitorPoint displacementMonitorPoint=displacementMonitorPointDB.selectAccordingToMonitorPointId(monitorNetworkId,GlobalVariable.getUserId());
		
		final long unitNumber=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetworkId);
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
				if (unitNumber<(int)displacementMonitorPoint.getMonitorDepth()/displacementMonitorPoint.getUnitSpacing() && !isBenchUnitsMany) {
					connectMeasureUnitOKTv.setText(R.string.measurementUnitInYourSelectedMonitoringNetworkIsNotCompleted);
					startTv.setText(R.string.continuTest);
					startLl.setBackgroundColor(R.color.yellow);
					isBenchUnitsMany=true;
					return;
				}
				else if (unitNumber>34 && !isunitNumberMany) {
					connectMeasureUnitOKTv.setText(R.string.measurementUnitsNumberTooHigh);
					startTv.setText(R.string.continuTest);
					startLl.setBackgroundColor(R.color.yellow);
					isunitNumberMany=true;
					return;
				}

				Intent intent=new Intent(DisplacementMonitorNetworkTestOneActivity.this,DisplacementMonitorNetworkTestTwoActivity.class);
			   	intent.putExtra("monitorNetworkId", monitorNetworkId);
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
        	
            ((Activity)DisplacementMonitorNetworkTestOneActivity.this).finish();
			}
		}
    }; 
}
