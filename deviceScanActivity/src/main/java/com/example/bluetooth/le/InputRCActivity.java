package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetooth.data.DataConvert;
import com.example.bluetooth.db.*;
import com.example.bluetooth.list.RC;
import com.example.bluetooth.list.RCAdatper;
import com.example.bluetooth.le.*;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class InputRCActivity extends Activity {
    private final static String TAG = InputRCActivity.class.getSimpleName();
    RcIridDB rcIridDB;
    RoundCounterDetailInfoDB roundCounterDetailInfoDB;
    DataConvert dataConvert;
    private List<RC> rcList=new ArrayList<RC>(); 
    ListView listView;
    RCAdatper rcAdatper;
    /**
     * �ײ��ĸ�textview
     */
    TextView iTv;//��
    TextView resultTv;//�ɼ�
    TextView rankingTv;//����
    TextView timerTv;//��ʱ��
    TextView backTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputrc);
        rcIridDB=new RcIridDB(getApplicationContext(),"TimerStore",null,7);//���ݿ��ʼ��
//        roundCounterDetailInfoDB=new RoundCounterDetailInfoDB(getApplicationContext(), "TimerStore",null, 7);
        dataConvert=new DataConvert();
        backTv=(TextView) findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        initRC();
        
        rcAdatper=new RCAdatper(InputRCActivity.this, R.layout.inputrc_item, rcList);
        View vhead=View.inflate(this, R.layout.inputrc_item_head, null);
        listView=(ListView) findViewById(R.id.rc_list);
        listView.addHeaderView(vhead);
        listView.setAdapter(rcAdatper);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        		if (position>0) {
        		   	RC rc=rcList.get(position-1);
                	Intent intent=new Intent(InputRCActivity.this,InputRCTextActivity.class);
                	intent.putExtra("iridint", rc.getIRIDInt());
                	intent.putExtra("irid", rc.getIRID());
                	startActivity(intent);
				}
        	}
        	
        });

      
    }
    
    /** 
     * ����Back�������¼�,����1: 
     * ע��: 
     * super.onBackPressed()���Զ�����finish()����,�ر� 
     * ��ǰActivity. 
     * ��Ҫ����Back����,ע�͸��д��뼴�� 
     */  
    @Override  
    public void onBackPressed() {  
        super.onBackPressed();  
        for(int i=0;i<rcList.size();i++)
        {
        	String iridInt=rcList.get(i).getIRIDInt();
        	String rc=rcList.get(i).getRc();
        	android.util.Log.d(TAG, "rc:"+rc);
        	rcIridDB.updateRC(rc, iridInt);         
        }
     }  
    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
        public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
    	       for(int i=0;i<rcList.size();i++)
    	        {
    	        	String iridInt=rcList.get(i).getIRIDInt();
    	        	String rc=rcList.get(i).getRc();
    	        	android.util.Log.d(TAG, "rc:"+rc);
    	        	rcIridDB.updateRC(rc, iridInt);         
    	        }   
        		((Activity)InputRCActivity.this).finish();
			}
		}
    };
    
    
    /**
     * ��ʼ������
     */
    private void initRC()
    {
    	Cursor mIRIDCursor=rcIridDB.select();
    	if (mIRIDCursor.moveToFirst()) {
			do {
				String id=mIRIDCursor.getString(mIRIDCursor.getColumnIndex("id"));
				String IRID=mIRIDCursor.getString(mIRIDCursor.getColumnIndex("irid"));
				String rccar=mIRIDCursor.getString(mIRIDCursor.getColumnIndex("rc"));
				android.util.Log.d(TAG, "irid:"+IRID+",rc:"+rccar);
				RC rc=new RC(IRID,rccar,IRID);
				rcList.add(rc);
			} while (mIRIDCursor.moveToNext());
		}
    	mIRIDCursor.close();
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	rcList.clear();
    	initRC();
    	rcAdatper.notifyDataSetChanged();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	rcList.clear();
    	initRC();
    }
}
