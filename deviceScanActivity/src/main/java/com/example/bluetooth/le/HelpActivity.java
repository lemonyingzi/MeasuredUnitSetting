package com.example.bluetooth.le;

import java.util.Locale;

import com.example.bluetooth.data.ZoomImageView;
import com.example.bluetooth.result.ResultActivity;
import com.github.mikephil.charting.charts.CombinedChart;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpActivity extends Activity{
	
	ViewPager helpViewPage;
	/**
	 * 图片资源id
	 */
	private int[] imgIdArray ;

	/**
	 * 装ImageView数组
	 */
	private ImageView[] mImageViews;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		helpViewPage=(ViewPager) findViewById(R.id.helpImageViewPager);
		//////////////根据系统语言获取图片////////////////////////
		//获取系统语言，如果为中文，则设置为中文，如果为日语则设置为日语，如果为其他语言则均为英语
        Locale curLocale = getResources().getConfiguration().locale;   
        Resources resources = getResources();  // 获得res资源对象    
        Configuration config = resources.getConfiguration();  // 获得设置对象    
        DisplayMetrics dm = resources.getDisplayMetrics();  // 获得屏幕参数：主要是分辨率，像素等。    
        resources.updateConfiguration(config, dm);  
        //通过Locale的equals方法，判断出当前语言环境  
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {    //中文
        	imgIdArray = new int[]{R.drawable.ch01, R.drawable.ch02, R.drawable.ch03, R.drawable.ch04,
    				R.drawable.ch05,R.drawable.ch06, R.drawable.ch07, R.drawable.ch08};
        } else if(curLocale.equals(Locale.JAPAN)){    
            //日文
        	imgIdArray = new int[]{R.drawable.en02, R.drawable.en02, R.drawable.en02, R.drawable.en02,
    				R.drawable.en02,R.drawable.en02, R.drawable.en02, R.drawable.en02};
        }
        else//其余去不为英文
        {
        	imgIdArray = new int[]{R.drawable.en01, R.drawable.en02, R.drawable.en03, R.drawable.en04,
    				R.drawable.en05,R.drawable.en06, R.drawable.en07, R.drawable.en08};
        }
		
		
		//将图片装载到数组中
		mImageViews = new ImageView[imgIdArray.length];
		for(int i=0; i<mImageViews.length; i++){
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(imgIdArray[i]);
			mImageViews[i] = imageView;


		}
		
		  //-----初始化PagerAdapter-----
		helpViewPage.setAdapter(pagerAdapter);
		helpViewPage.setOnPageChangeListener(new OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int arg0) {
         
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override

            public void onPageSelected(int position) {
            }
        });
        
        
	}
	
    PagerAdapter pagerAdapter=new PagerAdapter(){
        @Override
        public int getCount() {
            return mImageViews.length;
        }
        @Override

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override

        public void destroyItem(ViewGroup container,int position,Object o){
        }
        @Override
        public Object instantiateItem(ViewGroup container,int position){
//        	ImageView chart=mImageViews[position];
//        	container.removeView(chart);
//            container.addView(chart);
//            return chart;
            
		     ZoomImageView imageView = new ZoomImageView(
                     getApplicationContext());
             imageView.setImageResource(imgIdArray[position]);
             container.addView(imageView);
             mImageViews[position] = imageView;
             return imageView;
        }};
        

}
