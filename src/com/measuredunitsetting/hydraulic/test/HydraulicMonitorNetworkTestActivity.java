package com.measuredunitsetting.hydraulic.test;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkActivity;
import com.measuredunitsetting.hydraulic.monitornetwork.HydraulicMonitorNetworkDetailActivity;
import com.measuredunitsetting.list.MonitorNetworkAdatper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HydraulicMonitorNetworkTestActivity extends Activity{
	
	TextView backTv;
	LinearLayout monitorNetworkProjectNameLl;
    private List<HydraulicMonitorNetwork> monitorNetworkList=new ArrayList<HydraulicMonitorNetwork>(); 
    HydraulicMonitorNetworkDB hydraulicMonitorNetworkDB;
	MonitorNetworkAdatper monitorNetworkAdapter;
	ListView monitorNetworkLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetwroktest);
		
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		hydraulicMonitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	

		//监测网名称
		monitorNetworkList=hydraulicMonitorNetworkDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorNetworkAdatper(HydraulicMonitorNetworkTestActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
	    View vhead=View.inflate(this, R.layout.projectnamemonitornetworknumber_item_head, null);
	    monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
	    monitorNetworkLv.setAdapter(monitorNetworkAdapter);
	    monitorNetworkLv.addHeaderView(vhead);
	    
	    monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    		   	HydraulicMonitorNetwork monitorNetwork= monitorNetworkList.get(position-1);
	    		   	Intent intent=new Intent(HydraulicMonitorNetworkTestActivity.this,HydraulicMonitorNetworkTestOneActivity.class);
	    		   	intent.putExtra("monitorNetworkId", monitorNetwork.getId());
	    		   	intent.putExtra("totalUnitNumber", monitorNetwork.getTotalUnitNumber());
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
        	
            ((Activity)HydraulicMonitorNetworkTestActivity.this).finish();
			}
		}
    };  
}
