package com.example.bluetooth.le;

import com.example.bluetooth.db.ParaDB;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity{
	TextView  backTv;//���ؼ�ͷ
	TextView timerVersionTv;
	TextView timerSerialNumTv;
	ParaDB paraDB;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		paraDB=new ParaDB(getApplicationContext(), "TimerStore", null, 7);
		
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        timerVersionTv=(TextView)findViewById(R.id.timerVersionTv);
        timerSerialNumTv=(TextView)findViewById(R.id.timerSerialNumTv);
        timerSerialNumTv.setText(com.example.bluetooth.data.GlobalVariable.getIrTimerID());
        
        String versionStr=paraDB.getDeviceVersion();
        timerVersionTv.setText(versionStr);
	}
	
	   
    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)AboutActivity.this).finish();
			}
		}
    };
}
