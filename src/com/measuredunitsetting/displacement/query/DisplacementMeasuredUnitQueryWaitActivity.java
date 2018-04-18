package com.measuredunitsetting.displacement.query;

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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.measuredunitsetting.global.LogUtil;

public class DisplacementMeasuredUnitQueryWaitActivity extends Activity{
	
	private final static String TAG=DisplacementMeasuredUnitQueryWaitActivity.class.getSimpleName();
	TextView backTv;
	LinearLayout backLl;
	TextView  scanresultTv;
	UserDB userDB=null;
	String id;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasuredunitquerywait);
	
		//数据库
		userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		id=getIntent().getStringExtra("id");
		//返回
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
			            ((Activity)DisplacementMeasuredUnitQueryWaitActivity.this).finish();
					}
				});
		if (id!=null) {
		    new Thread(runnable).start();
		}
	}
	
	
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
					LogUtil.d(TAG, e.toString());
				}
				catch (NullPointerException e)
				{
					Intent intent=new Intent(DisplacementMeasuredUnitQueryWaitActivity.this,com.measuredunitsetting.NetworkUnconnectedActivity.class);
					startActivity(intent);
					finish();
				}
		        //请求成功
				if (requestResult!=null && requestResult.getType().equals(GlobalVariable.getRequestSuccess())) {
					Intent intent=new Intent(DisplacementMeasuredUnitQueryWaitActivity.this,DisplacementMeasuredUnitQueryResultActivity.class);
					intent.putExtra("RequestResult", requestResult);
					intent.putExtra("JsonArrayStr", jsonArrayStr);
					startActivity(intent);		
					finish();
				}
				else if(requestResult!=null && requestResult.getType().equals(GlobalVariable.getNotFound()))
				{
					Intent intent =new Intent(DisplacementMeasuredUnitQueryWaitActivity.this,DisplacementNotFoundActivity.class);
					startActivity(intent);
					finish();
				}		
				else if (requestResult!=null && requestResult.getType().equals(GlobalVariable.getInvalidToken())) {
					
				}
				else
				{
					Intent intent =new Intent(DisplacementMeasuredUnitQueryWaitActivity.this,DisplacementNotFoundActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}
	
	
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	    	List<String> nameAndToken=new ArrayList<String>();
			nameAndToken=userDB.getNameAndToken();
			if (nameAndToken!=null && nameAndToken.size()==2)
			{
				String state=NetUilts.loginofPost(GlobalVariable.getRequest(),nameAndToken.get(0),id,getResources().getString(R.string.displacementLevel),nameAndToken.get(1));
				showResponse(state);
			}
			else
			{
				Message message=new Message();
				message.what=1;
				toastHandler.sendMessage(message);
			}
	    }
	};

	private Handler toastHandler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 1:
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.unlogin),Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv || id==R.id.backLl) {
            ((Activity)DisplacementMeasuredUnitQueryWaitActivity.this).finish();
			}
		}
    };  
	
    
    
}
