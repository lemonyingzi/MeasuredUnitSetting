package com.example.bluetooth.combinedchart;

import com.github.mikephil.charting.components.YAxis;

import android.util.Log;

public class MyYAxis extends YAxis {
	 public MyYAxis() {
	        super();
	    }

	    public MyYAxis(AxisDependency position) {
	        super(position);
	    }

	    @Override
	    public String getFormattedLabel(int index) {
	        if (index < 0 || index >= mEntries.length)
	            return "";
	        else {
	            float max=mEntries[0];
	            for (int i=1;i<mEntries.length;i++){
	                Log.d("test", "getFormattedLabel: "+mEntries[i]);
	                if (mEntries[i]>max){
	                    max=mEntries[i];
	                }
	            }
	            return getValueFormatter().getFormattedValue(mEntries[index] /max*100, this);
	        }
	    }
}
