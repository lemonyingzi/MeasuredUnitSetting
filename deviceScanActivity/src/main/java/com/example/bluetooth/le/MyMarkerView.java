package com.example.bluetooth.le;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.widget.TextView;

public class MyMarkerView extends MarkerView {

	  private TextView tvContent;

	    public MyMarkerView(Context context, int layoutResource) {
	        super(context, layoutResource);
	        tvContent=(TextView) findViewById(R.id.tvContent);
	    }
	    
	    // callbacks everytime the MarkerView is redrawn, can be used to update the
	    // content

		@Override
		public void refreshContent(Entry e, Highlight highlight) {
			// TODO Auto-generated method stub
		      if (e instanceof CandleEntry) {

		            CandleEntry ce = (CandleEntry) e;

		            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
		        } else {

//		            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
		        	tvContent.setText("" +e.getVal());
		        }
		}
		@Override
		public int getXOffset(float xpos) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int getYOffset(float ypos) {
			// TODO Auto-generated method stub
			return 0;
		}
}
