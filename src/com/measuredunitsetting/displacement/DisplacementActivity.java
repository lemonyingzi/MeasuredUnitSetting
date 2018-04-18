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
	//返回
	TextView backTv;
	//设置
	LinearLayout settingLl;
	//测试
	LinearLayout testLl;
	//查询
	LinearLayout measureUnitQueryLl;
	//监测网
	LinearLayout monitoringNetworkLl;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacement);
		final String connectState=getIntent().getStringExtra("connectState");
	
		LogUtil.i(TAG,"connectState:"+connectState);
	
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
	    //设置
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
	    //测试
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
	    //测量单元查询
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
		//监测点
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
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementActivity.this).finish();
			}
		}
    };  
}
