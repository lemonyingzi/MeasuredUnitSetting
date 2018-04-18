package com.example.bluetooth.le;

import com.example.bluetooth.result.ResultActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RankingActivity extends Activity implements OnClickListener {
    LinearLayout iTv;//我
    LinearLayout resultTv;//成绩
    LinearLayout rankingTv;//排名
    LinearLayout timerTv;
    //////////////////////手势////////////////
	 private GestureDetector mGestureDetector;
	 private int verticalMinDistance = 180;
	 private int minVelocity         = 0;
    LinearLayout rankingLl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        
        iTv=(LinearLayout) findViewById(R.id.bottomI);
        iTv.setOnClickListener(bottomListener);
        resultTv=(LinearLayout) findViewById(R.id.bottomResults);
        resultTv.setOnClickListener(bottomListener);
        rankingTv=(LinearLayout) findViewById(R.id.bottomRanking);
        rankingTv.setOnClickListener(bottomListener);
        timerTv=(LinearLayout) findViewById(R.id.bottomTimer);
        timerTv.setOnClickListener(bottomListener);
        
//        rankingLl=(LinearLayout)findViewById(R.id.rankingLl);
//        rankingLl.setOnTouchListener(this);
//        rankingLl.setLongClickable(true);
//        mGestureDetector = new GestureDetector(RankingActivity.this);
    }
    
    
    //底部定时器 成绩 排名 我 跳转
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
			if (id == R.id.bottomI) 
			{
				Intent intent=new Intent(RankingActivity.this,IActivity.class);
				startActivity(intent);
			}
			else if(id==R.id.bottomTimer)
			{
				Intent intent=new Intent(RankingActivity.this,MainActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomResults) {
				Intent intent=new Intent(RankingActivity.this,ResultActivity.class);
				startActivity(intent);
			}
		}
    };
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//	      if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
//	            // 切换Activity,向左手势
//	    	  	 Intent intent = new Intent(RankingActivity.this, IActivity.class);
//	             startActivity(intent);
//	        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
//	            // 切换Activity，向右手势
//	        	 Intent intent = new Intent(RankingActivity.this, ResultActivity.class);
//	             startActivity(intent);
////	             finish();
////	             overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//	        }
//	    
//	        return true;
//	}
//
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		
//	}  
//    
//	@Override  
//	  public boolean dispatchTouchEvent(MotionEvent ev) {  
//	      if (mGestureDetector != null) {  
//	          if (mGestureDetector.onTouchEvent(ev))  
//	              //If the gestureDetector handles the event, a swipe has been executed and no more needs to be done.  
//	              return true;  
//	      }  
//	      return super.dispatchTouchEvent(ev);  
//	  }  
//	  
//	    
//	  @Override  
//	  public boolean onTouchEvent(MotionEvent event) {  
//	          return mGestureDetector.onTouchEvent(event);  
//	  }   
//    
}
