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
	
	//工程名称
	EditText projectNameEt;
	//监测网编号
	EditText monitorNumberEt;
	//监测深度
	EditText monitorDepthEt;
	//测量单元间距
	EditText unitSpacingEt;
	//数据库
	DisplacementMonitorPointDB displacementMonitorPointDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunittwosetting);
		
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);

		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//工程名称
		projectNameEt=(EditText)findViewById(R.id.projectNameEt);
		//监测网编号
		monitorNumberEt=(EditText) findViewById(R.id.monitorNumberEt);
		//监测网深度
		monitorDepthEt=(EditText)findViewById(R.id.monitorDepthEt);
		//测量单元间距
		unitSpacingEt=(EditText)findViewById(R.id.unitSpacingEt);
		//确定
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
				//监测深度不能小于1
				if (monitorDepth<1)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.monitorDepthCannotBeLessThan1),Toast.LENGTH_SHORT).show();
					return;
				}
				//监测深度不能大于100
				if (monitorDepth>100)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.monitorDepthCannotBeGreaterThan100),Toast.LENGTH_SHORT).show();
					return;
				}
				//测量单元间距不能小于0.5
				if (unitSpacing<0.5)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unitSpaceCannotBeLessThan0_5),Toast.LENGTH_SHORT).show();
					return;
				}
				//测量单元间距不能大于5
				if (unitSpacing>5.0)
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unitSpaceCannotBeGreaterThan5),Toast.LENGTH_SHORT).show();
					return;
				}
				//测量单元间距不能大于监测深度
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
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMeasuredUnitSettingTwoActivity.this).finish();
			}
		}
    };  
    
    
    
}
