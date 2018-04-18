package com.measuredunitsetting;

import com.google.gson.Gson;
import com.measuredunitsetting.data.PublicMethod;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.entity.LoginResult;
import com.measuredunitsetting.global.GlobalVariable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{
	private final static String TAG=LoginActivity.class.getSimpleName().toString();
	TextView backTv;//返回
	TextView forgetPasswordTv;//忘记密码
	LinearLayout loginLl;//登录
	String connectState=null;
	String userName=null;
	String password=null;
	EditText userNameEt;
	EditText passwordEt;
	
	UserDB userDB=null;
	PublicMethod publicMethod;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		publicMethod=new PublicMethod();
		
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
	    backTv.setOnClickListener(bottomListener);
	    //忘记密码
	    forgetPasswordTv=(TextView)findViewById(R.id.forgetPasswordTv);
	    forgetPasswordTv.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View v) {
    		Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
			startActivity(intent);
    		}
        });
	    userNameEt=(EditText)findViewById(R.id.userNameEt);
	    passwordEt=(EditText)findViewById(R.id.passwordEt);
	    //数据库
	    userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(),null, 7);
	    
	    //登录
	    loginLl=(LinearLayout)findViewById(R.id.loginLl);
	    loginLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		//开启线程连接网络
					if (TextUtils.isEmpty(userNameEt.getText())) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.inputUserName), Toast.LENGTH_SHORT).show();
						return;
					}
					if (TextUtils.isEmpty(passwordEt.getText())) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.inputPassword), Toast.LENGTH_SHORT).show();
						return;
					}
					userName=userNameEt.getText().toString();
					password=passwordEt.getText().toString();
				new Thread(runnable).start();
	    		}
	        });
	    
	
	}
	

	 
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	        String state=NetUilts.loginofPost(GlobalVariable.getLogin(),userName, password,"");
	        showResponse(state);
	    }
	};

	
	private void showResponse(final String  response)
	{
		runOnUiThread(new Runnable(){

			@SuppressLint("NewApi")
			@Override
			public void run() {
		        Gson gson=new Gson();
		        LoginResult loginResult=gson.fromJson(response, LoginResult.class);
				if (loginResult!=null && loginResult.getType().equals(GlobalVariable.getLoginSuccess())) {
					//设置检测机构代码
					GlobalVariable.setExamOrganCode(loginResult.getExamOrganCode());
					//存入数据库
					int userId=userDB.getId(userName);
					if (userId!=0) {
						userDB.updateLoginFlag();
						userDB.update(userId,loginResult.getToken());
					}
					else
					{
						userDB.updateLoginFlag();
						userDB.insert(userName, password, loginResult);
						userId=userDB.getId(userName);
					}
					GlobalVariable.setUserId(userId);
					//跳转到主界面
					Intent intent=new Intent();
					setResult(RESULT_OK,intent);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
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
        	
            ((Activity)LoginActivity.this).finish();
			}
		}
    };  
    
    
    
    
}
