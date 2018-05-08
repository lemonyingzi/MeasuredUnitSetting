package com.measuredunitsetting.displacement.query;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.measuredunitsetting.R;
import com.measuredunitsetting.displacement.DisplacementActivity;
import com.measuredunitsetting.entity.DisplacementCaliSheet;
import com.measuredunitsetting.entity.RequestResult;
import com.measuredunitsetting.list.DisplacementCaliSheetAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.measuredunitsetting.global.LogUtil;

public class DisplacementMeasuredUnitQueryResultActivity extends Activity {
	private final static String TAG=DisplacementMeasuredUnitQueryResultActivity.class.getSimpleName();
	
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
	String jsonArrayStr;
	DisplacementCaliSheetAdapter displacementCaliSheetAdapter;
	ListView caliSheetListTv;
	//����
	LinearLayout backLl;
	TextView backTv;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunitqueryresult);
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
		
	    View vhead=View.inflate(this, R.layout.displacementcalisheethead, null);
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
						Intent intent=new Intent(DisplacementMeasuredUnitQueryResultActivity.this,DisplacementActivity.class);
						startActivity(intent);
					}
				});
	    
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonArrayStr);
			List<DisplacementCaliSheet> caliSheets=new ArrayList<DisplacementCaliSheet>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item;
				item = jsonArray.getJSONObject(i);
				DisplacementCaliSheet dcs = new DisplacementCaliSheet(item.optString("CompareAngle"), item.optString("Zaxis"),
						item.optString("RepetitionDiff"), item.optString("RelativeDiff"), item.optString("DelayDiff"),
						item.optString("conclusion"));
				caliSheets.add(dcs);
				LogUtil.i(TAG, dcs.getConclusion() + "," + dcs.getCompareAngle() + "," + dcs.getTemperature() + "," + dcs.getZaxis() + "," + dcs.getRelativeDiff());
			}
			displacementCaliSheetAdapter=new DisplacementCaliSheetAdapter(DisplacementMeasuredUnitQueryResultActivity.this, R.layout.displacementcalisheet, caliSheets);
			caliSheetListTv.setAdapter(displacementCaliSheetAdapter);
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
				Intent intent=new Intent(DisplacementMeasuredUnitQueryResultActivity.this,DisplacementActivity.class);
				startActivity(intent);
			}
		}
    };

	@Override
	public void onBackPressed() {
		Intent intent=new Intent(DisplacementMeasuredUnitQueryResultActivity.this,DisplacementActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
