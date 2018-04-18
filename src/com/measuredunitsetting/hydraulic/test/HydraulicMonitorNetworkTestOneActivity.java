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
	//����
	TextView backTv;
	//��ʼ
	LinearLayout startLl;
	TextView startTv;
	//�����ID
	int monitorNetworkId;
	//������Ԫ�ܸ���
	int totalUnitNumber;
	HydraulicMeasuredUnitDB hydraulicMeasuredUnitDB;
	//�����ӵ��º�ʼ
	TextView connectMeasureUnitOKTv;
	
	boolean isBenchUnitsMany=false;
	boolean isunitNumberMany=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetwroktestone);
		//��ʼ��
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		totalUnitNumber=getIntent().getIntExtra("totalUnitNumber", 0);
		//��ʼ�����ݿ�
		hydraulicMeasuredUnitDB=new HydraulicMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		final long unitNumber=hydraulicMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetworkId);
		final long benchUnits=hydraulicMeasuredUnitDB.selectMeasureUnitAccordingToMeasureType(monitorNetworkId, "A0");
		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//�����ӵ��º�ʼ
		connectMeasureUnitOKTv=(TextView)findViewById(R.id.connectMeasureUnitOKTv);
		//
		//��ʼ
		startTv=(TextView)findViewById(R.id.startTv);
		startLl=(LinearLayout)findViewById(R.id.startLl);
		startLl.setOnClickListener(new View.OnClickListener()
		{
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View arg0) {
				//������Ԫ����С���ܲ�����Ԫ�������߻�׼��ĸ�������1
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
     * �����͵ײ����ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)HydraulicMonitorNetworkTestOneActivity.this).finish();
			}
		}
    }; 
}
