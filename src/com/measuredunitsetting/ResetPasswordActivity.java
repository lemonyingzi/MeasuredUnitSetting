package com.measuredunitsetting;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.entity.RequestPhoneNumberResult;
import com.measuredunitsetting.entity.ResetPasswordResult;
import com.measuredunitsetting.global.GlobalVariable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.SMSSDK;

public class ResetPasswordActivity extends Activity {
	private final static String TAG = ResetPasswordActivity.class.getSimpleName();

	TextView backTv;
	LinearLayout confirmLl;
	EditText passwordEt,repeatPasswordEt;
	String password,newPassword;
	List<String> nameAndToken=new ArrayList<String>();
    UserDB userDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
		
		passwordEt=(EditText)findViewById(R.id.passwordEt);
		repeatPasswordEt=(EditText)findViewById(R.id.repeatPasswordEt);
		
	    userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(),null, 7);

		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
		
		confirmLl=(LinearLayout)findViewById(R.id.confirmLl);
		confirmLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		
	    	    if (TextUtils.isEmpty(passwordEt.getText()) || TextUtils.isEmpty(repeatPasswordEt.getText())) {
	    			Toast.makeText(getApplicationContext(), getResources().getString(R.string.passwordIsNull), Toast.LENGTH_SHORT).show();
	    			return;
	            }
	            
	            password = passwordEt.getText().toString();  
	            newPassword=repeatPasswordEt.getText().toString();
	            
	            if (password.length()<6 || newPassword.length()<6) {
	            	Toast.makeText(getApplicationContext(), getResources().getString(R.string.passwordLengthMustLangerThanSix), Toast.LENGTH_SHORT).show();
	    			return;
	    		}
	            if (!password.equals(newPassword)) {
	    			Toast.makeText(getApplicationContext(), getResources().getString(R.string.twoPasswordsAreDifferent), Toast.LENGTH_SHORT).show();
	    			return;
	            }
	        	//从数据库查询进行登录
	    		nameAndToken=userDB.getNameAndToken();
	    		//如果数据库中有用户名和密码，则用用户名和密码进行登录
	    		if (nameAndToken!=null && nameAndToken.size()==2) {
	    			new Thread(runnable).start();
	    		}
	    		}
	        });
	}
	//调用线程登录
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	        String state=NetUilts.loginofPost(GlobalVariable.getResetPassword(),nameAndToken.get(0),password,nameAndToken.get(1));
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
		        ResetPasswordResult loginResult=gson.fromJson(response, ResetPasswordResult.class);
				if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getResetSuccess())) {
					Intent intent=new Intent(ResetPasswordActivity.this,ResetPasswordSuccessActivity.class);
					startActivity(intent);
				}
				else if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getResetFail())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.resetFail), Toast.LENGTH_SHORT).show();

				}
				else if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getInvalidToken())) {
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.invalidToken), Toast.LENGTH_SHORT).show();
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
        	if (id==R.id.backTv) {
        	
            ((Activity)ResetPasswordActivity.this).finish();
			}
		}
    };  
}
