package com.measuredunitsetting.hydraulic.query;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.measuredunitsetting.R;
import com.measuredunitsetting.hydraulic.HydraulicActivity;

public class HydraulicNotFoundActivity extends Activity{


	TextView backTv;
	LinearLayout backLl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notfound);
		
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
						Intent intent=new Intent(HydraulicNotFoundActivity.this,HydraulicActivity.class);
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
        	if (id==R.id.backTv || id==R.id.backLl) {
				Intent intent=new Intent(HydraulicNotFoundActivity.this,HydraulicActivity.class);
				startActivity(intent);
			}
		}
    };


	@Override
	public void onBackPressed() {
		Intent intent=new Intent(HydraulicNotFoundActivity.this,HydraulicActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
