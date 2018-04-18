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
	 * ͼƬ��Դid
	 */
	private int[] imgIdArray ;

	/**
	 * װImageView����
	 */
	private ImageView[] mImageViews;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		helpViewPage=(ViewPager) findViewById(R.id.helpImageViewPager);
		//////////////����ϵͳ���Ի�ȡͼƬ////////////////////////
		//��ȡϵͳ���ԣ����Ϊ���ģ�������Ϊ���ģ����Ϊ����������Ϊ������Ϊ�����������ΪӢ��
        Locale curLocale = getResources().getConfiguration().locale;   
        Resources resources = getResources();  // ���res��Դ����    
        Configuration config = resources.getConfiguration();  // ������ö���    
        DisplayMetrics dm = resources.getDisplayMetrics();  // �����Ļ��������Ҫ�Ƿֱ��ʣ����صȡ�    
        resources.updateConfiguration(config, dm);  
        //ͨ��Locale��equals�������жϳ���ǰ���Ի���  
        if (curLocale.equals(Locale.SIMPLIFIED_CHINESE)) {    //����
        	imgIdArray = new int[]{R.drawable.ch01, R.drawable.ch02, R.drawable.ch03, R.drawable.ch04,
    				R.drawable.ch05,R.drawable.ch06, R.drawable.ch07, R.drawable.ch08};
        } else if(curLocale.equals(Locale.JAPAN)){    
            //����
        	imgIdArray = new int[]{R.drawable.en02, R.drawable.en02, R.drawable.en02, R.drawable.en02,
    				R.drawable.en02,R.drawable.en02, R.drawable.en02, R.drawable.en02};
        }
        else//����ȥ��ΪӢ��
        {
        	imgIdArray = new int[]{R.drawable.en01, R.drawable.en02, R.drawable.en03, R.drawable.en04,
    				R.drawable.en05,R.drawable.en06, R.drawable.en07, R.drawable.en08};
        }
		
		
		//��ͼƬװ�ص�������
		mImageViews = new ImageView[imgIdArray.length];
		for(int i=0; i<mImageViews.length; i++){
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(imgIdArray[i]);
			mImageViews[i] = imageView;


		}
		
		  //-----��ʼ��PagerAdapter-----
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
