package com.measuredunitsetting.hydraulic.query;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.measuredunitsetting.NetUilts;
import com.measuredunitsetting.R;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.entity.RequestResult;
import com.measuredunitsetting.global.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.measuredunitsetting.global.LogUtil;

public class HydraulicMeasuredUnitQueryWaitActivity extends Activity{
	private final static String TAG=HydraulicMeasuredUnitQueryWaitActivity.class.getSimpleName();

	TextView backTv;
	LinearLayout backLl;
	TextView  scanresultTv;
	String id;
	UserDB userDB=null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunitquerywait);
		
		//数据库
		userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
			            ((Activity)HydraulicMeasuredUnitQueryWaitActivity.this).finish();
					}
				});

		id=getIntent().getStringExtra("id");
		if (id!=null) {
		    new Thread(runnable).start();
		}
	}
	
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	    	List<String> nameAndToken=new ArrayList<String>();
			nameAndToken=userDB.getNameAndToken();
			if (nameAndToken!=null && nameAndToken.size()==2)
			{
				String state=NetUilts.loginofPost(GlobalVariable.getRequest(),nameAndToken.get(0),id,getResources().getString(R.string.hydraulicLevel),nameAndToken.get(1));
				showResponse(state);
			}
	    }
	};
	

	private void showResponse(final String  response)
	{
		runOnUiThread(new Runnable(){

			@SuppressLint("NewApi")
			@Override
			public void run() {
		        
	            JSONObject jsonObject = null;
		        RequestResult requestResult = null;
		        String jsonArrayStr=null;
				try {
						jsonObject = new JSONObject(response);
						requestResult = new RequestResult(jsonObject.getString("type"),jsonObject.getString("deviceId"),
						jsonObject.getString("deviceType"),jsonObject.getString("author"),jsonObject.getString("date"),
						jsonObject.getString("version"),jsonObject.getString("inspection"),jsonObject.getString("marginError"),
						jsonObject.getString("coeff"),jsonObject.getString("caliSheet"));
						jsonArrayStr=jsonObject.get("caliSheet").toString();
				} catch (JSONException e) {
					LogUtil.e(TAG, e.toString());
				}      
				catch (NullPointerException e) {
					Intent intent=new Intent(HydraulicMeasuredUnitQueryWaitActivity.this,com.measuredunitsetting.NetworkUnconnectedActivity.class);
					startActivity(intent);
					finish();
				}
		        //请求成功
				if (requestResult!=null && requestResult.getType().equals(GlobalVariable.getRequestSuccess())) {
//					跳转界面
					Intent intent=new Intent(HydraulicMeasuredUnitQueryWaitActivity.this,HydraulicMeasuredUnitQueryResultActivity.class);
					intent.putExtra("RequestResult", requestResult);
					intent.putExtra("JsonArrayStr", jsonArrayStr);
					startActivity(intent);		
					finish();
				}
				else if (requestResult!=null && requestResult.getType().equals(GlobalVariable.getNotFound())) {
					Intent intent=new Intent(HydraulicMeasuredUnitQueryWaitActivity.this,HydraulicNotFoundActivity.class);
					startActivity(intent);
					finish();
				}
				else
				{	Intent intent=new Intent(HydraulicMeasuredUnitQueryWaitActivity.this,HydraulicNotFoundActivity.class);
					startActivity(intent);
					finish();
				}
					
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
            ((Activity)HydraulicMeasuredUnitQueryWaitActivity.this).finish();
			}
		}
    };  
	
    
    
}
