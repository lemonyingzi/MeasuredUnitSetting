package com.measuredunitsetting.hydraulic.test;

import com.measuredunitsetting.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HydraulicTestActivity extends Activity {
	TextView backTv;
	LinearLayout confirmLl;
	//测量网络测试
	LinearLayout measureNetworkTestLl;
	//单个测量网络测试
	LinearLayout singleMeasurementUnitTestLl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulictest);
	
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		
	    //测量网络测试
	    measureNetworkTestLl=(LinearLayout)findViewById(R.id.measureNetworkTestLl);
	    measureNetworkTestLl.setOnClickListener(new View.OnClickListener(){
	    	public void onClick(View v)
	    	{
	    		Intent intent=new Intent(HydraulicTestActivity.this,HydraulicMonitorNetworkTestActivity.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    singleMeasurementUnitTestLl=(LinearLayout)findViewById(R.id.singleMeasurementUnitTestLl);
	    singleMeasurementUnitTestLl.setOnClickListener(new View.OnClickListener()
	    		{

					@Override
					public void onClick(View arg0) {

						Intent intent=new Intent(HydraulicTestActivity.this,HydraulicMeasuredUnitTestActivity.class);
						startActivity(intent);
					}
	    	
	    		});
		//确定
//		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
//		confirmLl.setOnClickListener(new View.OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
////				Intent intent=new Intent(DisplacementTestActivity.this,DisplacementTestActivity.class);
////				startActivity(intent);
//			}
//		});
	}
	
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)HydraulicTestActivity.this).finish();
			}
		}
    };  
}
