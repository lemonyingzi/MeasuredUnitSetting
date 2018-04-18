package com.measuredunitsetting.hydraulic.monitornetwork;

import java.util.ArrayList;
import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.db.HydraulicMeasuredUnitDB;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.list.MeasuredUnitAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HydraulicMonitorNetworkDetailActivity extends Activity{
	private final static String TAG=HydraulicMonitorNetworkDetailActivity.class.getSimpleName();
	//返回
	TextView backTv;
	LinearLayout backLl;
	//测量单元ListView
	ListView measureUnitLv;
	//工程名称
	TextView projectNameTv;
	//监测网编号
	TextView monitorNetworkNumberTv;
	//测量单元总数
	TextView measureUnitTotalNumberTv;
	//设置完成
	TextView measurementUnitHasBeenSettedTv;
	MeasuredUnitAdapter measuredUnitAdapter;
    private List<HydraulicMeasuredUnit> measureUnitList=new ArrayList<HydraulicMeasuredUnit>(); 
    HydraulicMeasuredUnitDB hydraulicMeasuredUnitDB;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hydraulicmonitornetworkdetail);
		
		int monitorNetworkId=getIntent().getIntExtra("monitorNetworkId", 0);
		String projectName=getIntent().getStringExtra("projectName");
		String monitorNetworkNumber=getIntent().getStringExtra("monitorNetworkNumber");
		int totalUnitNumber=getIntent().getIntExtra("totalUnitNumber", 0);
		//初始化
		backTv=(TextView)findViewById(R.id.backTv);
		backTv.setOnClickListener(bottomListener);
		backLl=(LinearLayout)findViewById(R.id.backLl);
		backLl.setOnClickListener(bottomListener);
		
		projectNameTv=(TextView)findViewById(R.id.projectNameTv);
		projectNameTv.setText(projectName);
		monitorNetworkNumberTv=(TextView)findViewById(R.id.monitorNetworkNumberTv);
		monitorNetworkNumberTv.setText(monitorNetworkNumber);
		measureUnitTotalNumberTv=(TextView)findViewById(R.id.measureUnitTotalNumberTv);
		measureUnitTotalNumberTv.setText(String.valueOf(totalUnitNumber));
		measurementUnitHasBeenSettedTv=(TextView)findViewById(R.id.measurementUnitHasBeenSettedTv);
		hydraulicMeasuredUnitDB=new HydraulicMeasuredUnitDB(getApplicationContext(),GlobalVariable.getDataBaseName(),null,7);	
		measureUnitList=hydraulicMeasuredUnitDB.getMeasureUnits(monitorNetworkId);
		if (measureUnitList!=null && measureUnitList.size()>=totalUnitNumber) {
			measurementUnitHasBeenSettedTv.setText(R.string.yes);
		}
		else 
		{
			measurementUnitHasBeenSettedTv.setText(R.string.no);
		}
		
		measuredUnitAdapter=new MeasuredUnitAdapter(HydraulicMonitorNetworkDetailActivity.this, R.layout.measureunit_item, measureUnitList);
	    View vhead=View.inflate(this, R.layout.measureunit_item_head, null);
	    measureUnitLv=(ListView) findViewById(R.id.measureUnitList);
	    measureUnitLv.addHeaderView(vhead);
	    measureUnitLv.setAdapter(measuredUnitAdapter);
	    
	    measureUnitLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
	    		if (position>0) {
	    		   	HydraulicMeasuredUnit monitorNetwork= measureUnitList.get(position-1);
	    		   	Intent intent=new Intent(HydraulicMonitorNetworkDetailActivity.this,HydraulicMonitorNetworkDetailActivity.class);
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
        	
            ((Activity)HydraulicMonitorNetworkDetailActivity.this).finish();
			}
		}
    };  
    
    
}
