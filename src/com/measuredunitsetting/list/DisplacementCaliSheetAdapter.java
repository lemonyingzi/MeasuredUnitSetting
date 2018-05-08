package com.measuredunitsetting.list;

import java.util.List;

import com.measuredunitsetting.R;
import com.measuredunitsetting.entity.DisplacementCaliSheet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DisplacementCaliSheetAdapter  extends ArrayAdapter<DisplacementCaliSheet> {
	private int resourceId;
	public DisplacementCaliSheetAdapter(Context context, int textViewResourceId, List<DisplacementCaliSheet> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
	{
		DisplacementCaliSheet result=getItem(position);
		View view=convertView;
		ViewHolder viewHolder;
		if (convertView==null) {
			view=LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.repetitionDiffTv=(TextView) view.findViewById(R.id.repetitionDiffTv);
			viewHolder.conclusionTv=(TextView) view.findViewById(R.id.conclusionTv);
			viewHolder.compareAngleTv=(TextView) view.findViewById(R.id.compareAngleTv);
			viewHolder.delayDiffTv=(TextView) view.findViewById(R.id.delayDiffTv);
			viewHolder.zaxisTv=(TextView) view.findViewById(R.id.zaxisTv);
			viewHolder.relativeDiffTv=(TextView)view.findViewById(R.id.relativeDiffTv);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.repetitionDiffTv.setText(result.getRepetitonDiff());
		viewHolder.conclusionTv.setText(result.getConclusion());
		viewHolder.compareAngleTv.setText(result.getCompareAngle());
		viewHolder.delayDiffTv.setText(result.getDelayDiff());
		viewHolder.zaxisTv.setText(result.getZaxis());
		viewHolder.relativeDiffTv.setText(result.getRelativeDiff());
		return view;
	}
	
    
    class ViewHolder
    {
    	
    	TextView repetitionDiffTv;
    	TextView conclusionTv;

    	TextView compareAngleTv;
    	TextView delayDiffTv;
    	TextView zaxisTv;
    	TextView relativeDiffTv;

    }
}