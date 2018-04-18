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
	//�����������
	LinearLayout measureNetworkTestLl;
	//���������������
	LinearLayout singleMeasurementUnitTestLl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulictest);
	
		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		
	    //�����������
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
		//ȷ��
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
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)HydraulicTestActivity.this).finish();
			}
		}
    };  
}
