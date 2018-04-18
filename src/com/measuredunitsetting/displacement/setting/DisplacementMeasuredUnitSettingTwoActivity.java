package com.measuredunitsetting.displacement.setting;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DisplacementMeasuredUnitSettingTwoActivity extends Activity{
	TextView backTv;
	LinearLayout confirmLl;
	
	//��������
	EditText projectNameEt;
	//��������
	EditText monitorNumberEt;
	//������
	EditText monitorDepthEt;
	//������Ԫ���
	EditText unitSpacingEt;
	//���ݿ�
	DisplacementMonitorPointDB displacementMonitorPointDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunittwosetting);
		
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);

		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//��������
		projectNameEt=(EditText)findViewById(R.id.projectNameEt);
		//��������
		monitorNumberEt=(EditText) findViewById(R.id.monitorNumberEt);
		//��������
		monitorDepthEt=(EditText)findViewById(R.id.monitorDepthEt);
		//������Ԫ���
		unitSpacingEt=(EditText)findViewById(R.id.unitSpacingEt);
		//ȷ��
		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
		confirmLl.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(projectNameEt.getText())) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(monitorNumberEt.getText())) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
                    return;
				}
				if (TextUtils.isEmpty((monitorDepthEt.getText()))) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(unitSpacingEt.getText())) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				String projectName=projectNameEt.getText().toString();
				String monitoringPointNumber=monitorNumberEt.getText().toString();
				float monitorDepth=Float.parseFloat(monitorDepthEt.getText().toString());
				float unitSpacing=Float.parseFloat(unitSpacingEt.getText().toString());
				//�����Ȳ���С��1
				if (monitorDepth<1)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.monitorDepthCannotBeLessThan1),Toast.LENGTH_SHORT).show();
					return;
				}
				//�����Ȳ��ܴ���100
				if (monitorDepth>100)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.monitorDepthCannotBeGreaterThan100),Toast.LENGTH_SHORT).show();
					return;
				}
				//������Ԫ��಻��С��0.5
				if (unitSpacing<0.5)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unitSpaceCannotBeLessThan0_5),Toast.LENGTH_SHORT).show();
					return;
				}
				//������Ԫ��಻�ܴ���5
				if (unitSpacing>5.0)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unitSpaceCannotBeGreaterThan5),Toast.LENGTH_SHORT).show();
					return;
				}
				//������Ԫ��಻�ܴ��ڼ�����
				if (unitSpacing>monitorDepth)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unitSpaceCannotBeGreaterThanMonitorDepth),Toast.LENGTH_SHORT).show();
					return;
				}
				DisplacementMonitorPoint monitorNetwork=displacementMonitorPointDB.selectAccordingToProjectNameAndMonitorNum(GlobalVariable.getUserId(),projectName, monitoringPointNumber);
				if (monitorNetwork==null) {
					DisplacementMonitorPoint displacementMonitorPoint=new DisplacementMonitorPoint(GlobalVariable.getUserId(),projectName, monitoringPointNumber, monitorDepth, unitSpacing);
					displacementMonitorPointDB.insert(displacementMonitorPoint);
					Intent intent=new Intent(DisplacementMeasuredUnitSettingTwoActivity.this,DisplacementMeasuredUnitSettingThreeActivity.class);
					intent.putExtra("projectName", projectName);
                	intent.putExtra("monitoringPointNumber", monitoringPointNumber);
//                	intent.putExtra("monitorDepth", monitorDepth);
//                	intent.putExtra("unitSpacing", unitSpacing);
					startActivity(intent);
				}
				else
				{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.alreadyExist), Toast.LENGTH_SHORT).show();
				}
			}
		});
	
		
	}

    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMeasuredUnitSettingTwoActivity.this).finish();
			}
		}
    };  
    
    
    
}
