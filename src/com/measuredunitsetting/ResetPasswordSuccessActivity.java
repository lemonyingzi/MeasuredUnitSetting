package com.measuredunitsetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResetPasswordSuccessActivity extends Activity {
	private final static String TAG = ResetPasswordActivity.class.getSimpleName();

	TextView backTv;
	LinearLayout backLl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpasswordsuccess);
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View v) {
		    		Intent intent=new Intent(ResetPasswordSuccessActivity.this,LoginActivity.class);
					startActivity(intent);
		    		}
		        });

		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
	}
	
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)ResetPasswordSuccessActivity.this).finish();
			}
		}
    };  
}
