package com.measuredunitsetting.displacement.test;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkActivity;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkDetailActivity;
import com.measuredunitsetting.list.MonitorNetworkAdatper;
import com.measuredunitsetting.list.MonitorPointAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DisplacementMonitorNetworkTestActivity extends Activity{
	
	TextView backTv;
	LinearLayout monitorNetworkProjectNameLl;
    private List<DisplacementMonitorPoint> monitorNetworkList=new ArrayList<DisplacementMonitorPoint>(); 
    DisplacementMonitorPointDB displacementMonitorPointDB;
	MonitorPointAdapter monitorNetworkAdapter;
	ListView monitorNetworkLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointtest);
		
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	

		//监测网名称
		monitorNetworkList=displacementMonitorPointDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorPointAdapter(DisplacementMonitorNetworkTestActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
	    View vhead=View.inflate(this, R.layout.projectnamemonitorpointnumber_item_head, null);
	    monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
	    monitorNetworkLv.setAdapter(monitorNetworkAdapter);
	    monitorNetworkLv.addHeaderView(vhead);
	    
	    monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    			DisplacementMonitorPoint monitorNetwork= monitorNetworkList.get(position-1);
	    		   	Intent intent=new Intent(DisplacementMonitorNetworkTestActivity.this,DisplacementMonitorNetworkTestOneActivity.class);
	    		   	intent.putExtra("monitorNetworkId", monitorNetwork.getId());
	    		   	startActivity(intent);
				}
	    	}
	    });
	}
	
    /**
     * 顶部和底部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
    	public void onClick(View v)
        {    
        	int id = v.getId();
        	if (id==R.id.backTv) {
        	
            ((Activity)DisplacementMonitorNetworkTestActivity.this).finish();
			}
		}
    };  
}
