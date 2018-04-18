package com.measuredunitsetting.displacement.setting;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.hydraulic.setting.HydraulicMeasuredUnitSettingActivity;
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

public class DisplacementMeasuredUnitSettingActivity extends Activity {
	private final static String TAG = DisplacementMeasuredUnitSettingActivity.class.getSimpleName();

	TextView backTv;
	LinearLayout newMonitoringNetworkLl;
	MonitorPointAdapter monitorNetworkAdapter;
	ListView monitorNetworkLv;
    private List<DisplacementMonitorPoint> monitorPointList=new ArrayList<DisplacementMonitorPoint>(); 
    DisplacementMonitorPointDB displacementMonitorPointDB;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmeasureunitsetting);
		//初始化
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);
		monitorPointList=displacementMonitorPointDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		//返回
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		//新监测网
		newMonitoringNetworkLl=(LinearLayout)findViewById(R.id.newMonitoringNetworkLl);
		newMonitoringNetworkLl.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent=new Intent(DisplacementMeasuredUnitSettingActivity.this,DisplacementMeasuredUnitSettingTwoActivity.class);
				startActivity(intent);
	    		}
	        });
	    
		monitorNetworkAdapter=new MonitorPointAdapter(DisplacementMeasuredUnitSettingActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorPointList);
        View vhead=View.inflate(this, R.layout.projectnamemonitorpointnumber_item_head, null);
        monitorNetworkLv=(ListView) findViewById(R.id.monitorNetworkList);
        monitorNetworkLv.addHeaderView(vhead);
        monitorNetworkLv.setAdapter(monitorNetworkAdapter);
        
        monitorNetworkLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        		if (position>0) {
        			DisplacementMonitorPoint monitorPoint= monitorPointList.get(position-1);
                	Intent intent=new Intent(DisplacementMeasuredUnitSettingActivity.this,DisplacementMeasuredUnitSettingThreeActivity.class);
                	intent.putExtra("projectName", monitorPoint.getProjectName());
                	intent.putExtra("monitoringPointNumber", monitorPoint.getMonitorPointNumber());
//                	intent.putExtra("monitorDepth", monitorPoint.getMonitorDepth());
//                	intent.putExtra("unitSpacing", monitorPoint.getUnitSpacing());
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
        	
            ((Activity)DisplacementMeasuredUnitSettingActivity.this).finish();
			}
		}
    };
	@Override
	protected void onRestart() {
		monitorPointList=displacementMonitorPointDB.getMonitorNetworkAccordingToUserId(GlobalVariable.getUserId());
		monitorNetworkAdapter=new MonitorPointAdapter(DisplacementMeasuredUnitSettingActivity.this, R.layout.projectnamemonitornetworknumber_item, monitorPointList);
		monitorNetworkLv.setAdapter(monitorNetworkAdapter);

		super.onRestart();
	}
    
}
