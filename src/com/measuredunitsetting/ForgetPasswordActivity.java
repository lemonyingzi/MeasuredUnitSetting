package com.measuredunitsetting;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.global.LogUtil;
import com.measuredunitsetting.entity.RequestPhoneNumberResult;
import com.measuredunitsetting.global.GlobalVariable;
import com.mob.MobSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPasswordActivity extends Activity{
	private final static String TAG=ForgetPasswordActivity.class.getSimpleName();
	//返回
	TextView backTv;
	//重置密码
	LinearLayout resetPasswordLl;
    EventHandler eventHandler;  
    //获取验证码
    Button getVerifyCodeBt;
    //手机号码EditText
    EditText phoneNumberEt;
    String strPhoneNumber;
    //输入验证码
    EditText verifyCodeEt;
    UserDB userDB;
	List<String> nameAndToken=new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpassword);
		
		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
	    
	    getVerifyCodeBt=(Button)findViewById(R.id.getVerifyCodeBt);
		resetPasswordLl=(LinearLayout)findViewById(R.id.resetPasswordLl);
		verifyCodeEt=(EditText)findViewById(R.id.verifyCodeEt);
		phoneNumberEt=(EditText) findViewById(R.id.phoneNumberEt);

		MobSDK.init(getApplicationContext(), "225395d1ebcbc", "89704076c1288fc86d1fbec62829734f");
	    userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(),null, 7);

		//获取验证码按钮
		getVerifyCodeBt.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
			            if (android.text.TextUtils.isEmpty(phoneNumberEt.getText())) {
			                LogUtil.i(TAG, getResources().getString(R.string.incorrectPhoneNumber));
			                return;
						}
			            strPhoneNumber = phoneNumberEt.getText().toString();  

			        	//从数据库查询进行登录
			    		nameAndToken=userDB.getNameAndToken();
			    		//如果数据库中有用户名和密码，则用用户名和密码进行登录
			    		if (nameAndToken!=null && nameAndToken.size()==2) {
			    			new Thread(runnable).start();
			    		}
					}
			
				});  
		
		
		
		//提交
		resetPasswordLl.setOnClickListener(bottomListener);  
		resetPasswordLl.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View v) {
			    		try
			    		{
//			    	  		Intent intent=new Intent(ForgetPasswordActivity.this,ResetPasswordActivity.class);
//							startActivity(intent);
			    			if (TextUtils.isEmpty(phoneNumberEt.getText()) || TextUtils.isEmpty(verifyCodeEt.getText())) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.inputError), Toast.LENGTH_SHORT).show();
								return;
							}
				    		SMSSDK.submitVerificationCode("86", strPhoneNumber, verifyCodeEt.getText().toString());
			    		}
			    		catch (Exception e) {
							LogUtil.i(TAG, e.toString());
						}
		    		}
		        });
		
        eventHandler = new EventHandler() {  
            @Override  
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPasswordActivity.this,getResources().getString(R.string.authenticationSuccess),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgetPasswordActivity.this,ResetPasswordActivity.class);
                                startActivity(intent);
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPasswordActivity.this,getResources().getString(R.string.verificationCodeHasBeenSent),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    }
                }else{
                	runOnUiThread(new Runnable()
        			{
						@Override
						public void run() {
		                    Toast.makeText(ForgetPasswordActivity.this,getResources().getString(R.string.inputError),Toast.LENGTH_SHORT).show();
						}
        			});
                }
            } 
        };  
        SMSSDK.registerEventHandler(eventHandler);  
	}
	
	//调用线程登录
		Runnable runnable = new Runnable(){
		    @Override
		    public void run() {
		        String state=NetUilts.loginofPost(GlobalVariable.getRequestPhoneNumber(),nameAndToken.get(0),nameAndToken.get(1));
		        showResponse(state);
		    }
		};
		
		
		
		//显示结果
		private void showResponse(final String  response)
		{
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
			        Gson gson=new Gson();
			        RequestPhoneNumberResult loginResult=gson.fromJson(response, RequestPhoneNumberResult.class);
					if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getRequestSuccess())) {
						if (loginResult.getPhoneNumber().equals(strPhoneNumber)) {//服务器返回的手机号和输入的手机号相同
					         SMSSDK.getVerificationCode("86", strPhoneNumber);  
					            getVerifyCodeBt.setClickable(false);  
					            //开启线程去更新button的text  
					            new Thread() {  
					                @Override  
					                public void run() {  
					                    int totalTime = 60;  
					                    for (int i = 0; i < totalTime; i++) {  
					                        Message message = myHandler.obtainMessage(0x01);  
					                        message.arg1 = totalTime - i;  
					                        myHandler.sendMessage(message);  
					                        try {  
					                            sleep(1000);  
					                        } catch (InterruptedException e) {  
					                            e.printStackTrace();  
					                        }  
					                    }  
					                    myHandler.sendEmptyMessage(0x02);  
					                }  
					            }.start();  
						}
						else//服务器返回的手机号和输入的手机号不相同
						{
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.incorrectPhoneNumber), Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(),getResources().getString(R.string.requestFail), Toast.LENGTH_SHORT).show();
					}				
				}
			});
		}
	    
		
		
	    Handler myHandler = new Handler() {  
	        @Override  
	        public void handleMessage(Message msg) {  
	            switch (msg.what) {  
	                case 0x01:  
	                	getVerifyCodeBt.setText(getResources().getString(R.string.resend)+"("+msg.arg1+")");
	                    break;  
	                case 0x02:  
	                	getVerifyCodeBt.setText(getResources().getString(R.string.getVerificationCode));
	                	getVerifyCodeBt.setClickable(true);  
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
        	if (id==R.id.backTv) {
        	
            ((Activity)ForgetPasswordActivity.this).finish();
			}
		}
    };  
    


	protected void onDestroy() {
	   super.onDestroy();
	   SMSSDK.unregisterEventHandler(eventHandler);
	}

	
}
