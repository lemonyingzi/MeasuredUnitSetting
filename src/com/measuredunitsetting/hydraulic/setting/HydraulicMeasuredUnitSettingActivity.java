package com.measuredunitsetting.hydraulic.setting;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.MonitorNetworkAdatper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HydraulicMeasuredUnitSettingActivity extends Activity {
	private final static String TAG = HydraulicMeasuredUnitSettingActivity.class.getSimpleName();

	TextView backTv;
	LinearLayout newMonitoringNetworkLl;
	MonitorNetworkAdatper monitorNetworkAdapter;
	ListView monitorNetworkLv;
    private List<HydraulicMonitorNetwork> monitorNetworkList=new ArrayList<HydraulicMonitorNetwork>(); 
    HydraulicMonitorNetworkDB monitorNetworkDB;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmeasureunitsetting);
		//初始化
		monitorNetworkDB=new HydraulicMonitorNetworkDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);
		monitorNetworkList=monitorNetworkDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//新监测网
		newMonitoringNetworkLl=(LinearLayout)findViewById(R.id.newMonitoringNetworkLl);
		newMonitoringNetworkLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent=new Intent(HydraulicMeasuredUnitSettingActivity.this,HydraulicMeasuredUnitSettingTwoActivity.class);
				startActivity(intent);
	    		}
	        });
	    
		monitorNetworkAdapter=new MonitorNetworkAdatper(HydraulicMeasuredUnitSettingActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
        View vhead=View.inflate(this, R.layout.projectnamemonitornetworknumber_item_head, null);
        monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
        monitorNetworkLv.addHeaderView(vhead);
        monitorNetworkLv.setAdapter(monitorNetworkAdapter);
        
        monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        		if (position>0) {
        		   	HydraulicMonitorNetwork monitorNetwork= monitorNetworkList.get(position-1);
                	Intent intent=new Intent(HydraulicMeasuredUnitSettingActivity.this,HydraulicMeasuredUnitSettingThreeActivity.class);
                	intent.putExtra("projectName", monitorNetwork.getProjectName());
                	intent.putExtra("monitorNetworkNumber", monitorNetwork.getMonitorNetworkNumber());
                	intent.putExtra("totalUnitNumber", monitorNetwork.getTotalUnitNumber());
                	startActivity(intent);
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
        	
            ((Activity)HydraulicMeasuredUnitSettingActivity.this).finish();
			}
		}
    };  
    
	@Override
	protected void onRestart() {
		monitorNetworkList=monitorNetworkDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorNetworkAdatper(HydraulicMeasuredUnitSettingActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorNetworkList);
        monitorNetworkLv.setAdapter(monitorNetworkAdapter);

		super.onRestart();
	}
    
}
