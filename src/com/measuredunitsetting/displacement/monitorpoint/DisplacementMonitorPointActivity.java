package com.measuredunitsetting.displacement.monitorpoint;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.MonitorNetworkAdatper;
import com.measuredunitsetting.list.MonitorPointAdapter;

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

public class DisplacementMonitorPointActivity extends Activity{
	private final static String TAG=DisplacementMonitorPointActivity.class.getSimpleName();
	TextView backTv;

	MonitorPointAdapter monitorNetworkAdapter;
	ListView monitorNetworkLv;
    private List<DisplacementMonitorPoint> monitorNetworkList=new ArrayList<DisplacementMonitorPoint>(); 
    DisplacementMonitorPointDB displacementMonitorPointDB;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpoint);
		//����
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//��ʼ��
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	
		
	    View vhead=View.inflate(this, R.layout.projectnamemonitorpointnumber_item_head, null);
	    monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
	    monitorNetworkLv.addHeaderView(vhead);
		init();

	    monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    			DisplacementMonitorPoint monitorPoint= monitorNetworkList.get(position-1);
	    		   	Intent intent=new Intent(DisplacementMonitorPointActivity.this,DisplacementMonitorPointDetailActivity.class);
	    		   	intent.putExtra("monitorPointId", monitorPoint.getId());
	    		   	startActivity(intent);
				}
	    	}
	    });
	    
	    //listView����ɾ���¼�
	    monitorNetworkLv.setOnItemLongClickListener(new OnItemLongClickListener() {  
	    	@Override  
           public boolean onItemLongClick(AdapterView<?> parent, View view,  
                    final int position, long id) {  
	    		if (position>0) {
	    		       //����AlertDialog.Builder���󣬵������б����ʱ�򵯳�ȷ��ɾ���Ի���   
	    			final DisplacementMonitorPoint monitorNetwork=monitorNetworkList.get(position-1);
	    			
	                AlertDialog.Builder builder=new Builder(DisplacementMonitorPointActivity.this);  
	                builder.setMessage(getResources().getString(R.string.deleteFollowMonitorNetwork)+"\r\n"
	                +getResources().getString(R.string.projectNameOrProjectNumber)+" "+ getResources().getString(R.string.monitoringNetworkNumber)+"\r\n"
	                +monitorNetwork.getProjectName()+"                "+monitorNetwork.getMonitorPointNumber()+"\r\n"
	                +getResources().getString(R.string.UnrecoverableAfterDeletion));  
	                //���AlertDialog.Builder�����setPositiveButton()����   
	                builder.setPositiveButton(getResources().getString(R.string.cancel), new OnClickListener() {  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                   
	                    }  
	                });  
	                  
	                //���AlertDialog.Builder�����setNegativeButton()����   
	                builder.setNegativeButton(getResources().getString(R.string.delete), new OnClickListener() {  
	                    @Override  
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	displacementMonitorPointDB.deleteAccordingToId(monitorNetwork.getId(),GlobalVariable.getUserId());
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
		monitorNetworkList=displacementMonitorPointDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorPointAdapter(DisplacementMonitorPointActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
	    monitorNetworkLv.setAdapter(monitorNetworkAdapter);
	}
	
    /**
     * �������ؼ�
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//������������    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMonitorPointActivity.this).finish();
			}
		}
    };  
    

}
