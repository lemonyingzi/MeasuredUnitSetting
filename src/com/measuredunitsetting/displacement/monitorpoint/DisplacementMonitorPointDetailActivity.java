package com.measuredunitsetting.displacement.monitorpoint;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.DisplacementMeasuredUnitDB;
import com.measuredunitsetting.db.DisplacementMonitorPointDB;
import com.measuredunitsetting.db.HydraulicMeasuredUnitDB;
import com.measuredunitsetting.db.HydraulicMonitorNetworkDB;
import com.measuredunitsetting.entity.DisplacementMeasuredUnit;
import com.measuredunitsetting.entity.DisplacementMonitorPoint;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import com.measuredunitsetting.entity.HydraulicMonitorNetwork;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.DisplacementMeasuredUnitAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DisplacementMonitorPointDetailActivity extends Activity{
	private final static String TAG=DisplacementMonitorPointDetailActivity.class.getSimpleName();
	//返回
	TextView backTv;
	LinearLayout backLl;
	//测量单元ListView
	ListView measureUnitLv;
	//工程名称
	TextView projectNameTv;
	//监测点编号
	TextView monitorPointNumberTv;
	//测量深度
	TextView monitorDepthTv;
	//测量单元间距
	TextView unitSpacingTv;
	//测量单元数量
	TextView measureUnitNumberTv;
	
	
	//设置完成
	TextView measurementUnitHasBeenSettedTv;
	//数据库
	DisplacementMonitorPointDB displacementMonitorPointDB;
	
	
	DisplacementMeasuredUnitAdapter measuredUnitAdapter;
	
	
    private List<DisplacementMeasuredUnit> measureUnitList=new ArrayList<DisplacementMeasuredUnit>(); 
    DisplacementMeasuredUnitDB displacementMeasuredUnitDB;
    
    
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_displacementmonitorpointdetail);
		
		int monitorPointId=getIntent().getIntExtra("monitorPointId", 0);
		
		displacementMonitorPointDB=new DisplacementMonitorPointDB(getApplicationContext(), GlobalVariable.getDataBaseName(), null, 7);
		
		DisplacementMonitorPoint displacementMonitorPoint=displacementMonitorPointDB.selectAccordingToMonitorPointId(monitorPointId,GlobalVariable.getUserId());
		
		//初始化
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		projectNameTv.setText(displacementMonitorPoint.getProjectName());
		
		monitorPointNumberTv=(TextView)findViewById(R.id.monitorPointNumberTv);
		monitorPointNumberTv.setText(displacementMonitorPoint.getMonitorPointNumber());
		
		monitorDepthTv=(TextView)findViewById(R.id.monitorDepthTv);
		monitorDepthTv.setText(String.valueOf(displacementMonitorPoint.getMonitorDepth()));		
		
		
		unitSpacingTv=(TextView)findViewById(R.id.unitSpacingTv);
		unitSpacingTv.setText(String.valueOf(displacementMonitorPoint.getUnitSpacing()));
		int measureUnitNumber=(int) (displacementMonitorPoint.getMonitorDepth()/displacementMonitorPoint.getUnitSpacing());
		measureUnitNumberTv=(TextView)findViewById(R.id.measureUnitNumberTv);
		measureUnitNumberTv.setText(String.valueOf(measureUnitNumber));
		
		measurementUnitHasBeenSettedTv=(TextView)findViewById(R.id.measurementUnitHasBeenSettedTv);
		displacementMeasuredUnitDB=new DisplacementMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	
		measureUnitList=displacementMeasuredUnitDB.getMeasureUnits(monitorPointId);
		
		
		if (measureUnitList!=null && measureUnitList.size()>=measureUnitNumber) {
			measurementUnitHasBeenSettedTv.setText(R.string.yes);
		}
		else 
		{
			measurementUnitHasBeenSettedTv.setText(R.string.no);
		}
		
		measuredUnitAdapter=new DisplacementMeasuredUnitAdapter(DisplacementMonitorPointDetailActivity.this, R.layout.serialnumberdepthid_item, measureUnitList);
	    View vhead=View.inflate(this, R.layout.serialnumberdepthid_item_head, null);
	    measureUnitLv=(ListView) findViewById(R.id.measureUnitList);
	    measureUnitLv.addHeaderView(vhead);
	    measureUnitLv.setAdapter(measuredUnitAdapter);
	    
	    measureUnitLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    		   	Intent intent=new Intent(DisplacementMonitorPointDetailActivity.this,DisplacementMonitorPointDetailActivity.class);
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
        	if (id==R.id.backTv || id==R.id.backLl) {
        	
            ((Activity)DisplacementMonitorPointDetailActivity.this).finish();
			}
		}
    };  
    
    
}
