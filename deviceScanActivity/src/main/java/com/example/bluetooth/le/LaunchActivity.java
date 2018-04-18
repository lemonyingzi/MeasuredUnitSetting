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
        
        /************加载网页部分************************/
        WebView browser=(WebView)findViewById(R.id.Toweb);  
        browser.getSettings().setSupportZoom(true);  
        browser.getSettings().setJavaScriptEnabled(true);  
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);  
        browser.getSettings().setBuiltInZoomControls(true);//support zoom  
        browser.getSettings().setUseWideViewPort(true);// 这个很关键  
        browser.getSettings().setLoadWithOverviewMode(true);  
        
        
        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，  
        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象  
        browser.setWebViewClient(new WebViewClient() {  
            public boolean shouldOverrideUrlLoading(WebView view, String url)  
            {   

                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边  
//                view.loadUrl(url);  
                        return false;  
            }         
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
                super.onReceivedSslError(view, handler, error);
            }
            
           });         
        Handler x = new Handler();//定义一个handle对象  
        x.postDelayed(new splashhandler(), 3000);//设置3秒钟延迟执行splashhandler线程。其实你这里可以再新建一个线程去执行初始化工作，如判断SD,网络状态等  
        
        
        /***************设置APP语言****************************/
        //获取系统语言，如果为中文，则设置为中文，如果为日语则设置为日语，如果为其他语言则均为英语
        Locale curLocale = getResources().getConfiguration().locale;   
        Resources resources = getResources();  // 获得res资源对象    
        Configuration config = resources.getConfiguration();  // 获得设置对象    
        DisplayMetrics dm = resources.getDisplayMetrics();  // 获得屏幕参数：主要是分辨率，像素等。    
        //通过Locale的equals方法，判断出当前语言环境  
        if (Locale.SIMPLIFIED_CHINESE.equals(curLocale)) {    
//                config.locale=Locale.CHINA;
            browser.loadUrl("http://222.92.76.215:8768/sfpmsch.aspx");  

        } else if(Locale.JAPAN.equals(curLocale)){    
            //日文
        	config.locale=Locale.JAPAN;
        }
        else//其余去不为英文
        {
            browser.loadUrl("http://222.92.76.215:8768/sfpmsen.aspx");  

            config.locale = Locale.ENGLISH;  // 设置APP语言设置为英文   
        }
        
        resources.updateConfiguration(config, dm);

    }  
  
    class splashhandler implements Runnable{  
        public void run() {  
        	Log.d(TAG, "跳转");
            startActivity(new Intent(getApplication(),MainActivity.class));// 这个线程的作用3秒后就是进入到你的主界面  
            LaunchActivity.this.finish();// 把当前的LaunchActivity结束掉  
        }  
    }  
      
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
//        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
    
 
}
