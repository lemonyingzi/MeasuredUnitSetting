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
	//����
	TextView backTv;
	//��ʼ
	LinearLayout startLl;
	TextView startTv;
	//�����ID
	int monitorNetworkId;
	//������Ԫ�ܸ���
	DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
	DisplacementMonitorPointDB displacementMonitorPointDB;
	
	//�����ӵ��º�ʼ
	TextView connectMeasureUnitOKTv;
	
	boolean isBenchUnitsMany=false;
	boolean isunitNumberMany=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointtestone);
		//��ʼ��
		monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		//��ʼ�����ݿ�
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(), null, 7);
		final DisplacementMonitorPoint displacementMonitorPoint=displacementMonitorPointDB.selectAccordingToMonitorPointId(monitorNetworkId,GlobalVariable.getUserId());
		
		final long unitNumber=displacementMeasuredUnitDB.selectCountAccordingToMonitorNetworkNumberId(monitorNetworkId);
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
     * �����͵ײ����ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMonitorNetworkTestOneActivity.this).finish();
			}
		}
    }; 
}
