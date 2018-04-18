package com.example.bluetooth.le;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LaunchActivity extends Activity {
	private final String TAG=LaunchActivity.class.getSimpleName();
	
    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_launcher);  
        
        /************������ҳ����************************/
        WebView browser=(WebView)findViewById(R.id.Toweb);  
        browser.getSettings().setSupportZoom(true);  
        browser.getSettings().setJavaScriptEnabled(true);  
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);  
        browser.getSettings().setBuiltInZoomControls(true);//support zoom  
        browser.getSettings().setUseWideViewPort(true);// ����ܹؼ�  
        browser.getSettings().setLoadWithOverviewMode(true);  
        
        
        // ���ҳ�������ӣ����ϣ��������Ӽ����ڵ�ǰbrowser����Ӧ��  
        // �������¿�Android��ϵͳbrowser����Ӧ�����ӣ����븲��webview��WebViewClient����  
        browser.setWebViewClient(new WebViewClient() {  
            public boolean shouldOverrideUrlLoading(WebView view, String url)  
            {   

                //  ��д�˷������������ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�  
//                view.loadUrl(url);  
                        return false;  
            }         
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // ����������վ��֤��
                super.onReceivedSslError(view, handler, error);
            }
            
           });         
        Handler x = new Handler();//����һ��handle����  
        x.postDelayed(new splashhandler(), 3000);//����3�����ӳ�ִ��splashhandler�̡߳���ʵ������������½�һ���߳�ȥִ�г�ʼ�����������ж�SD,����״̬��  
        
        
        /***************����APP����****************************/
        //��ȡϵͳ���ԣ����Ϊ���ģ�������Ϊ���ģ����Ϊ����������Ϊ������Ϊ�����������ΪӢ��
        Locale curLocale = getResources().getConfiguration().locale;   
        Resources resources = getResources();  // ���res��Դ����    
        Configuration config = resources.getConfiguration();  // ������ö���    
        DisplayMetrics dm = resources.getDisplayMetrics();  // �����Ļ��������Ҫ�Ƿֱ��ʣ����صȡ�    
        //ͨ��Locale��equals�������жϳ���ǰ���Ի���  
        if (Locale.SIMPLIFIED_CHINESE.equals(curLocale)) {    
//                config.locale=Locale.CHINA;
            browser.loadUrl("http://222.92.76.215:8768/sfpmsch.aspx");  

        } else if(Locale.JAPAN.equals(curLocale)){    
            //����
        	config.locale=Locale.JAPAN;
        }
        else//����ȥ��ΪӢ��
        {
            browser.loadUrl("http://222.92.76.215:8768/sfpmsen.aspx");  

            config.locale = Locale.ENGLISH;  // ����APP��������ΪӢ��   
        }
        
        resources.updateConfiguration(config, dm);

    }  
  
    class splashhandler implements Runnable{  
        public void run() {  
        	Log.d(TAG, "��ת");
            startActivity(new Intent(getApplication(),MainActivity.class));// ����̵߳�����3�����ǽ��뵽���������  
            LaunchActivity.this.finish();// �ѵ�ǰ��LaunchActivity������  
        }  
    }  
      
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
//        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
    
 
}
