package com.measuredunitsetting.displacement;

import com.measuredunitsetting.R;
import com.measuredunitsetting.displacement.monitorpoint.DisplacementMonitorPointActivity;
import com.measuredunitsetting.displacement.query.DisplacementMeasuredUnitQueryActivity;
import com.measuredunitsetting.displacement.setting.DisplacementMeasuredUnitSettingActivity;
import com.measuredunitsetting.displacement.test.DisplacementTestActivity;
import com.measuredunitsetting.global.LogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplacementActivity extends Activity{
	private final static  String TAG=DisplacementActivity.class.getSimpleName();
	//����
	TextView backTv;
	//����
	LinearLayout settingLl;
	//����
	LinearLayout testLl;
	//��ѯ
	LinearLayout measureUnitQueryLl;
	//�����
	LinearLayout monitoringNetworkLl;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacement);
		final String connectState=getIntent().getStringExtra("connectState");
	
		LogUtil.i(TAG,"connectState:"+connectState);
	
		//����
		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
	    //����
	    settingLl=(LinearLayout)findViewById(R.id.settingLl);
	    settingLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		if(connectState!=null && connectState.equals(getResources().getString(R.string.connected)))
	    		{
	    			Intent intent=new Intent(DisplacementActivity.this,DisplacementMeasuredUnitSettingActivity.class);
					startActivity(intent);
	    		}
	    		}
	        });
	    //����
	    testLl=(LinearLayout)findViewById(R.id.testLl);
	    testLl.setOnClickListener(new View.OnClickListener(){
	    	public void onClick(View v)
	    	{
	    		if(connectState!=null && connectState.equals(getResources().getString(R.string.connected)))
	    		{
		    		Intent intent=new Intent(DisplacementActivity.this,DisplacementTestActivity.class);
		    		startActivity(intent);
	    		}
	    	}
	    });
	    //������Ԫ��ѯ
	    measureUnitQueryLl=(LinearLayout)findViewById(R.id.measureUnitQueryLl);
	    measureUnitQueryLl.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				
//				if(connectState!=null && connectState.equals(getResources().getString(R.string.connected)))
//	    		{
						Intent intent=new Intent(DisplacementActivity.this,DisplacementMeasuredUnitQueryActivity.class);
						startActivity(intent);
//	    		}
			}
	
	
		});
	    
		if(connectState!=null && connectState.equals(getResources().getString(R.string.unconnected)))
		{
			settingLl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.paddingbottomiszerobackgroundred));
			testLl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.paddingbottomiszerobackgroundred));
//			measureUnitQueryLl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.paddingbottomiszerobackgroundred));
		}
		//����
		monitoringNetworkLl=(LinearLayout)findViewById(R.id.monitoringNetworkLl);
		monitoringNetworkLl.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(DisplacementActivity.this,DisplacementMonitorPointActivity.class);
				startActivity(intent);
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
        	
            ((Activity)DisplacementActivity.this).finish();
			}
		}
    };  
}
