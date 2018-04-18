package com.example.bluetooth.le;

import com.example.bluetooth.db.RcIridDB;
import com.example.bluetooth.track.TrackActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputRCTextActivity extends Activity{
	private final String TAG=InputRCTextActivity.class.getSimpleName();
	TextView iridTv;
    EditText rcEv;
	String irid=null;
	String iridint=null;
	RcIridDB rcIridDB;
	TextView backTv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputrc_text);
		Intent intent=getIntent();
		irid=intent.getStringExtra("irid");
		iridint=intent.getStringExtra("iridint");
		
		iridTv=(TextView) findViewById(R.id.irid);
		iridTv.setText(iridint);
		rcEv=(EditText) findViewById(R.id.input_rc);
		rcIridDB =new RcIridDB(getApplicationContext(), "TimerStore", null, 7);
		backTv=(TextView) findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//将文本框的值设置为原始值
		String originalRc=rcIridDB.selectRc(irid);
		if (originalRc!=null) {
			rcEv.setText(originalRc);
			rcEv.setSelection(originalRc.length());//将光标移至文字末尾
		}
//		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
//		imm.showSoftInput((View) rcEv.getWindowToken(), 0);  
		
		rcEv.setOnEditorActionListener(new TextView.OnEditorActionListener() { 
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  
		{                          
			if (actionId==EditorInfo.IME_ACTION_DONE)
			{                
				InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			   	String RCCar=rcEv.getText().toString();
	        	rcIridDB.updateRC(RCCar, irid);      
              	Intent intent=new Intent(InputRCTextActivity.this,InputRCActivity.class);
            	startActivity(intent);
				return true;             
			}               
			return false;           
		}
		});

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
         	String RCCar=rcEv.getText().toString();
        	rcIridDB.updateRC(RCCar, irid);  
        	finish();
			System.exit(0);
        	return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	   	String RCCar=rcEv.getText().toString();
	        	rcIridDB.updateRC(RCCar, irid);  
        		((Activity)InputRCTextActivity.this).finish();
			}
		}
    };
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	rcEv.requestFocus();
    }
}
