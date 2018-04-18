package com.measuredunitsetting.hydraulic.setting;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
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

public class HydraulicMeasuredUnitSettingTwoActivity extends Activity{
	TextView backTv;
	LinearLayout confirmLl;
	//测量单元总数
	EditText measureUnitTotalNumberEt;
	//工程名称
	EditText projectNameEt;
	//监测网编号
	EditText monitorNumberEt;
	//数据库
	HydraulicMonitorNetworkDB hydraulicMonitorNetworkDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunittwosetting);
		
		hydraulicMonitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);

		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//工程名称
		projectNameEt=(EditText)findViewById(R.id.projectNameEt);
		//监测网编号
		monitorNumberEt=(EditText)findViewById(R.id.monitorNumberEt);
		//确定
		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
		confirmLl.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(projectNameEt.getText())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(monitorNumberEt.getText())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(measureUnitTotalNumberEt.getText())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.pleaseInputComplete), Toast.LENGTH_SHORT).show();
					return;
				}
				int measureUnitTotalNumber=Integer.parseInt(measureUnitTotalNumberEt.getText().toString());
				if (measureUnitTotalNumber<2) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.measurementUnitsNumberCannotLessThan2), android.widget.Toast.LENGTH_SHORT).show();
					return;
				}
				String projectName=projectNameEt.getText().toString();
				String monitoringNetworkNumber=monitorNumberEt.getText().toString();
				HydraulicMonitorNetwork monitorNetwork=hydraulicMonitorNetworkDB.selectAccordingToProjectNameAndMonitorNum(GlobalVariable.getUserId(),projectName, monitoringNetworkNumber);
				if (monitorNetwork==null) {
					hydraulicMonitorNetworkDB.insertProjectNameAndMonitorNum(GlobalVariable.getUserId(),projectName,monitoringNetworkNumber,measureUnitTotalNumber);
					Intent intent=new Intent(HydraulicMeasuredUnitSettingTwoActivity.this,HydraulicMeasuredUnitSettingThreeActivity.class);
					intent.putExtra("projectName", projectName);
                	intent.putExtra("monitorNetworkNumber", monitoringNetworkNumber);
                	intent.putExtra("totalUnitNumber", measureUnitTotalNumber);
					startActivity(intent);
				}
				else
				{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.alreadyExist), Toast.LENGTH_SHORT).show();
					
				}
			
			}
		});
	
		measureUnitTotalNumberEt=(EditText) findViewById(R.id.measureUnitTotalNumberEt);
		setRegion(measureUnitTotalNumberEt, 2, 255);
	}
	
	
    /** edittext只能输入数值的时候做最大最小的限制 */
	public static void setRegion(final EditText edit, final int MIN_MARK, final int MAX_MARK) {
		edit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (start > 1) {
					if (MIN_MARK != -1 && MAX_MARK != -1) {
						int num = Integer.parseInt(s.toString());
						if (num > MAX_MARK) {
							s = String.valueOf(MAX_MARK);
							edit.setText(s);
						} else if (num < MIN_MARK) {
							s = String.valueOf(MIN_MARK);
							edit.setText(s);
						}
						edit.setSelection(s.length());
					}
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			public void afterTextChanged(Editable s) {
				if (s != null && !s.equals("") && !s.equals(" ")) {
					if (MIN_MARK != -1 && MAX_MARK != -1) {
						try
						{
						    int markVal = Integer.parseInt(s.toString());
							if (markVal > MAX_MARK) {
								edit.setText(String.valueOf(MAX_MARK));
								edit.setSelection(String.valueOf(MAX_MARK).length());
							}
							else if (markVal==0) {
								edit.setText(String.valueOf(MIN_MARK));
								edit.setSelection(String.valueOf(MIN_MARK).length());
							}
						}
						catch(NumberFormatException e)
						{
							
						}
						return;
					}
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
        	
            ((Activity)HydraulicMeasuredUnitSettingTwoActivity.this).finish();
			}
		}
    };  
    
    
    
}
