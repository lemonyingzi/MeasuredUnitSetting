package com.example.bluetooth.list;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager{
	private static String TAG=ChildViewPager.class.getSimpleName().toString();
	 /** 触摸时按下的点 **/
    PointF downP = new PointF();
    /** 触摸时当前的点 **/
    PointF curP = new PointF();
 
    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
 
    public ChildViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        //每次进行onTouch事件都记录当前的按下的坐标
        if(getChildCount()<=1)
        {
          return super.onTouchEvent(arg0);
        }
        curP.x = arg0.getX();
        curP.y = arg0.getY();
        Log.d(TAG, "onTouchEvent:");
        Log.d(TAG, "curP.x:"+curP.x+",curP.y:"+curP.y);
        
        
        if(arg0.getAction() == MotionEvent.ACTION_DOWN)
        {
            //记录按下时候的坐标
            //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            Log.d(TAG, "downP.x:"+downP.x+",downP.y:"+downP.y);
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
        	Log.d(TAG, "不要打扰我");
            getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_UP){
            //在up时判断纵坐标和横坐标的值，
            //如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
        	Log.d(TAG, "up");
            getParent().requestDisallowInterceptTouchEvent(true);
//            if(downP.x==curP.x){
//                return true;
//            }
//            else
//            {
//            	
//            }
        }
        
        if (arg0.getAction() == MotionEvent.ACTION_CANCEL) {
        	Log.d(TAG, "cancel");
            getParent().requestDisallowInterceptTouchEvent(false);

		}
        super.onTouchEvent(arg0); //注意这句不能 return super.onTouchEvent(arg0); 否则触发parent滑动
        return true;
    }
    
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            {
//                getParent().requestDisallowInterceptTouchEvent(true);
//                Log.d(TAG, "down");
//            }
//                break;
//            case MotionEvent.ACTION_MOVE:
//            {
//            	getParent().requestDisallowInterceptTouchEvent(true);
//            	Log.d(TAG, "move");
//            }
//            	break;
//            case MotionEvent.ACTION_UP:
//            {
//            	getParent().requestDisallowInterceptTouchEvent(true);
//            	Log.d(TAG, "up");
//            }
//            	break;
//            case MotionEvent.ACTION_CANCEL:
//            {
//                getParent().requestDisallowInterceptTouchEvent(false);
//                Log.d(TAG, "cancel");
//            }
//                break;
//        }
//         super.dispatchTouchEvent(ev);
//         return true;
//    }
    
    // 滑动距离及坐标 归还父控件焦点  
    private float xDistance, yDistance, xLast, yLast;  
  
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        getParent().requestDisallowInterceptTouchEvent(true);  
        switch (ev.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                xDistance = yDistance = 0f;  
                xLast = ev.getX();  
                yLast = ev.getY();  
                break;  
            case MotionEvent.ACTION_MOVE:  
                final float curX = ev.getX();  
                final float curY = ev.getY();  
  
                xDistance += Math.abs(curX - xLast);  
                yDistance += Math.abs(curY - yLast);  
                xLast = curX;  
                yLast = curY;  
                if(xDistance < yDistance) {  //上下滑动，事件交给父控件
                    getParent().requestDisallowInterceptTouchEvent(false);  
                } else {  //左右滑动，事件交给子控件
                	Log.d(TAG, "xDistance:"+xDistance);
                	Log.d(TAG,"交给子控件");
                    getParent().requestDisallowInterceptTouchEvent(true);  
                }  
                break;  
            case MotionEvent.ACTION_UP:  
            case MotionEvent.ACTION_CANCEL:  
           
                break;  
        }  
        return super.dispatchTouchEvent(ev);
//        return true;
    }  
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev)
//    {
////    	  getParent().requestDisallowInterceptTouchEvent(true);  
//          switch (ev.getAction()) {  
//              case MotionEvent.ACTION_DOWN:  
//                  xDistance = yDistance = 0f;  
//                  xLast = ev.getX();  
//                  yLast = ev.getY();  
//                  break;  
//              case MotionEvent.ACTION_MOVE:  
//                  final float curX = ev.getX();  
//                  final float curY = ev.getY();  
//    
//                  xDistance += Math.abs(curX - xLast);  
//                  yDistance += Math.abs(curY - yLast);  
//                  xLast = curX;  
//                  yLast = curY;  
//                  if(xDistance < yDistance) {  //上下滑动，事件交给父控件
//                      getParent().requestDisallowInterceptTouchEvent(false);  
//                  } else {  //左右滑动，事件交给子控件
//                  	Log.d(TAG, "xDistance:"+xDistance);
//                  	Log.d(TAG,"交给子控件");
//                      getParent().requestDisallowInterceptTouchEvent(true);  
//                  }  
//                  break;  
//              case MotionEvent.ACTION_UP:  
//              case MotionEvent.ACTION_CANCEL:  
//                  break;  
//          }  
//          return super.dispatchTouchEvent(ev);  
//    }
}
