package com.measuredunitsetting.hydraulic.query;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.HydraulicCaliSheet;
import com.measuredunitsetting.entity.RequestResult;
import com.measuredunitsetting.global.LogUtil;
import com.measuredunitsetting.hydraulic.HydraulicActivity;
import com.measuredunitsetting.list.HydraulicCaliSheetAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HydraulicMeasuredUnitQueryResultActivity extends Activity{
	private final static String TAG=HydraulicMeasuredUnitQueryResultActivity.class.getSimpleName();
	RequestResult requestResult;
	//�豸����
	TextView deviceTypeTv;
	//ID
	TextView IDTv;
	//��Ȩ��Ϣ
	TextView authInfoTv;
	//��������
	TextView produceDateTv;
	//�̼��汾
	TextView hardwareVersionTv;
	//��������
	TextView factoryInspectionTv;
	//���ȵȼ�
	TextView precisionGradeTv;
	//�궨ϵ��
	TextView caliCoeffTv;
	//����
	LinearLayout backLl;
	TextView backTv;

	
	ListView caliSheetListTv;
	HydraulicCaliSheetAdapter hydraulicCaliSheetAdapter;
	String jsonArrayStr=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunitqueryresult);
		
		requestResult=(RequestResult)getIntent().getParcelableExtra("RequestResult");
		jsonArrayStr=getIntent().getStringExtra("JsonArrayStr");
		
		//�豸����
		deviceTypeTv=(TextView)findViewById(R.id.deviceTypeTv);
		deviceTypeTv.setText(requestResult.getDeviceType());
		//ID
		IDTv=(TextView)findViewById(R.id.IDTv);
		IDTv.setText(requestResult.getDeviceId());
		//��Ȩ��Ϣ
		authInfoTv=(TextView)findViewById(R.id.authInfoTv);
		authInfoTv.setText(requestResult.getAuthor());
		//��������
		produceDateTv=(TextView)findViewById(R.id.produceDateTv);
		produceDateTv.setText(requestResult.getDate());
		//�̼��汾
		hardwareVersionTv=(TextView)findViewById(R.id.hardwareVersionTv);
		hardwareVersionTv.setText(requestResult.getVersion());
		//��������
		factoryInspectionTv=(TextView)findViewById(R.id.factoryInspectionTv);
		factoryInspectionTv.setText(requestResult.getInspection());
		//���ȵȼ�
		precisionGradeTv=(TextView)findViewById(R.id.precisionGradeTv);
		precisionGradeTv.setText(requestResult.getMarginError());
		//�궨ϵ��
		caliCoeffTv=(TextView)findViewById(R.id.caliCoeffTv);
		caliCoeffTv.setText(requestResult.getCoeff());
		
	    View vhead=View.inflate(this, R.layout.hydrauliccalisheethead, null);
	    caliSheetListTv=(ListView) findViewById(R.id.caliSheetListTv);
	    caliSheetListTv.addHeaderView(vhead);
	    
	    //����
  		backTv=(TextView)findViewById(R.id.backTv);
  		backTv.setOnClickListener(bottomListener);
		//����
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
					Intent intent=new Intent(HydraulicMeasuredUnitQueryResultActivity.this,HydraulicActivity.class);
					startActivity(intent);
					}
				});
		
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonArrayStr);
			List<HydraulicCaliSheet> caliSheets=new ArrayList<HydraulicCaliSheet>();
			for (int i = 0; i < jsonArray.length(); i++) {     
	            JSONObject item;
//	            HydraulicCaliSheet caliSheet=new HydraulicCaliSheet();
	            item = jsonArray.getJSONObject(i);
	            HydraulicCaliSheet hcs=new HydraulicCaliSheet(item.optString("conclusion"),item.optString("marginError"),
	            		item.optString("outputValue"),item.optString("measurePoint"),item.optString("temperature"),
	            		item.optString("error"),item.optString("measuredValue"),item.optString("coeff"));
	            caliSheets.add(hcs);
	            }  
			hydraulicCaliSheetAdapter=new HydraulicCaliSheetAdapter(HydraulicMeasuredUnitQueryResultActivity.this, R.layout.hydrauliccalisheet, caliSheets);
			caliSheetListTv.setAdapter(hydraulicCaliSheetAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv || id==R.id.backLl) {
            Intent intent=new Intent(HydraulicMeasuredUnitQueryResultActivity.this,HydraulicActivity.class);
            startActivity(intent);
			}
		}
	    };

	@Override
	public void onBackPressed() {
		Intent intent=new Intent(HydraulicMeasuredUnitQueryResultActivity.this,HydraulicActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
