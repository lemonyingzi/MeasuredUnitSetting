package com.example.bluetooth.list;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager{
	private static String TAG=ChildViewPager.class.getSimpleName().toString();
	 /** ����ʱ���µĵ� **/
    PointF downP = new PointF();
    /** ����ʱ��ǰ�ĵ� **/
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
        //ÿ�ν���onTouch�¼�����¼��ǰ�İ��µ�����
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
            //��¼����ʱ�������
            //�мǲ����� downP = curP �������ڸı�curP��ʱ��downPҲ��ı�
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            Log.d(TAG, "downP.x:"+downP.x+",downP.y:"+downP.y);
            //�˾������Ϊ��֪ͨ���ĸ�ViewPager���ڽ��е��Ǳ��ؼ��Ĳ�������Ҫ���ҵĲ������и���
            getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            //�˾������Ϊ��֪ͨ���ĸ�ViewPager���ڽ��е��Ǳ��ؼ��Ĳ�������Ҫ���ҵĲ������и���
        	Log.d(TAG, "��Ҫ������");
            getParent().requestDisallowInterceptTouchEvent(true);
        }
 
        if(arg0.getAction() == MotionEvent.ACTION_UP){
            //��upʱ�ж�������ͺ������ֵ��
            //�����һ���㣬��ִ�е���¼����������Լ�д�ĵ���¼���������onclick
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
        super.onTouchEvent(arg0); //ע����䲻�� return super.onTouchEvent(arg0); ���򴥷�parent����
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
    
    // �������뼰���� �黹���ؼ�����  
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
                if(xDistance < yDistance) {  //���»������¼��������ؼ�
                    getParent().requestDisallowInterceptTouchEvent(false);  
                } else {  //���һ������¼������ӿؼ�
                	Log.d(TAG, "xDistance:"+xDistance);
                	Log.d(TAG,"�����ӿؼ�");
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
//                  if(xDistance < yDistance) {  //���»������¼��������ؼ�
//                      getParent().requestDisallowInterceptTouchEvent(false);  
//                  } else {  //���һ������¼������ӿؼ�
//                  	Log.d(TAG, "xDistance:"+xDistance);
//                  	Log.d(TAG,"�����ӿؼ�");
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
