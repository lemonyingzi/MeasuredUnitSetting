package com.measuredunitsetting.hydraulic.monitornetwork;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.MonitorNetworkAdatper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class HydraulicMonitorNetworkActivity extends Activity{
	private final static String TAG=HydraulicMonitorNetworkActivity.class.getSimpleName();
	TextView backTv;

	MonitorNetworkAdatper monitorNetworkAdapter;
	ListView monitorNetworkLv;
    private List<HydraulicMonitorNetwork> monitorNetworkList=new ArrayList<HydraulicMonitorNetwork>(); 
    HydraulicMonitorNetworkDB hydraulicMonitorNetworkDB;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetwork);
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//初始化
		hydraulicMonitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	
		
	    View vhead=View.inflate(this, R.layout.projectnamemonitornetworknumber_item_head, null);
	    monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
	    monitorNetworkLv.addHeaderView(vhead);
		init();

	    monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    		   	HydraulicMonitorNetwork monitorNetwork= monitorNetworkList.get(position-1);
	    		   	Intent intent=new Intent(HydraulicMonitorNetworkActivity.this,HydraulicMonitorNetworkDetailActivity.class);
	    		   	intent.putExtra("monitorNetworkId", monitorNetwork.getId());
	    		   	intent.putExtra("projectName", monitorNetwork.getProjectName());
	    		   	intent.putExtra("monitorNetworkNumber", monitorNetwork.getMonitorNetworkNumber());
	    		   	intent.putExtra("totalUnitNumber", monitorNetwork.getTotalUnitNumber());
	    		   	startActivity(intent);
				}
	    	}
	    });
	    
	    //listView长按删除事件
	    monitorNetworkLv.setOnItemLongClickListener(new OnItemLongClickListener() {  
	    	@Override  
           public boolean onItemLongClick(AdapterView<?> parent, View view,  
                    final int position, long id) {  
	    		if (position>0) {
	    		       //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框   
	    			final HydraulicMonitorNetwork monitorNetwork=monitorNetworkList.get(position-1);
	    			
	                AlertDialog.Builder builder=new Builder(HydraulicMonitorNetworkActivity.this);  
	                builder.setMessage(getResources().getString(R.string.deleteFollowMonitorNetwork)+"\r\n"
	                +getResources().getString(R.string.projectNameOrProjectNumber)+" "+ getResources().getString(R.string.monitoringNetworkNumber)+"\r\n"
	                +monitorNetwork.getProjectName()+"                "+monitorNetwork.getMonitorNetworkNumber()+"\r\n"
	                +getResources().getString(R.string.UnrecoverableAfterDeletion));  
	                //添加AlertDialog.Builder对象的setPositiveButton()方法   
	                builder.setPositiveButton(getResources().getString(R.string.cancel), new OnClickListener() {  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                   
	                    }  
	                });  
	                  
	                //添加AlertDialog.Builder对象的setNegativeButton()方法   
	                builder.setNegativeButton(getResources().getString(R.string.delete), new OnClickListener() {  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	hydraulicMonitorNetworkDB.deleteAccordingToId(monitorNetwork.getId(),GlobalVariable.getUserId());
	                		init();
	                    }  
	                });  
	                  
	                builder.create().show();  
				}
            	return true;
            }  
        });  
	}
	
	private void init()
	{
		monitorNetworkList=hydraulicMonitorNetworkDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorNetworkAdatper(HydraulicMonitorNetworkActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
	    monitorNetworkLv.setAdapter(monitorNetworkAdapter);
	}
	
    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)HydraulicMonitorNetworkActivity.this).finish();
			}
		}
    };  
    

}
