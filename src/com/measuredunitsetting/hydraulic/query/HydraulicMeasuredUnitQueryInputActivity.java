package com.measuredunitsetting.hydraulic.query;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.UserDB;
import com.measuredunitsetting.global.GlobalVariable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HydraulicMeasuredUnitQueryInputActivity extends Activity{
	private final static String TAG=HydraulicMeasuredUnitQueryInputActivity.class.getSimpleName();
	
	TextView backTv;
	LinearLayout confrimLl;
	EditText inputIDEt;
	UserDB userDB=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasuredunitqueryinput);
	
		//���ݿ�
		userDB=new UserDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);


		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//����ID EditText
		inputIDEt=(EditText)findViewById(R.id.inputIDEt);
		//ȷ��
		confrimLl=(LinearLayout)findViewById(R.id.confirmLl);
		confrimLl.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0) {
						//�����߳���������
							if (TextUtils.isEmpty(inputIDEt.getText())) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.inputId), Toast.LENGTH_SHORT).show();
								return;
							}
							List<String> nameAndToken=userDB.getNameAndToken();
							if (nameAndToken!=null && nameAndToken.size()==2)
							{
								Intent intent=new Intent(HydraulicMeasuredUnitQueryInputActivity.this,HydraulicMeasuredUnitQueryWaitActivity.class);
								intent.putExtra("id", inputIDEt.getText().toString());
								startActivity(intent);
							}
							else
							{
								Toast.makeText(getApplicationContext(),getResources().getString(R.string.unlogin),Toast.LENGTH_SHORT).show();
							}
			    		}
				});
	}
	
	

	

    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        		((Activity)HydraulicMeasuredUnitQueryInputActivity.this).finish();
			}
		}
    };  
    

}
