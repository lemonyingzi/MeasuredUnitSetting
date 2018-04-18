package com.measuredunitsetting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.measuredunitsetting.global.LogUtil;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetUilts {
	private final static String TAG=NetUilts.class.getSimpleName();
	 /* 
	    * 用post方式登录 
	    * @param username 
	    * @param password 
	    * @return 登录状态 
	    * */  
	    public static String  loginofPost(String type,String name,String password,String token){  
	        try {  
	            OkHttpClient clicent=new OkHttpClient();
	            RequestBody requestBody=new FormBody.Builder()
	            		.add("type", type)
	            		.add("name", name)
	            		.add("password", password)
	            		.add("token", token)
	            		.build();

	            Request request=new Request.Builder()
	            		.url("http://222.92.76.215:8769/AppServer/Login")
	            		.post(requestBody)
	            		.build();
	            Response response=clicent.newCall(request).execute();
	            String responseData=response.body().string();
	            LogUtil.i(TAG, "responseData:"+responseData);
	            return responseData;
	            
	        } catch (Exception e) {  
	        	LogUtil.e(TAG, e.toString());
				loginofPostIn(type,name,password,token);
	        }finally{
	        }
	        return null;  
	    }

	/**
	 * 用内网进行连接
	 * @param type
	 * @param name
	 * @param password
	 * @param token
	 * @return
	 */
	    public static String loginofPostIn(String type,String name,String password,String token)
		{
			try {
				OkHttpClient clicent=new OkHttpClient();
				RequestBody requestBody=new FormBody.Builder()
						.add("type", type)
						.add("name", name)
						.add("password", password)
						.add("token", token)
						.build();

				Request request=new Request.Builder()
						.url("http://192.168.9.31:8769/AppServer/Login")
						.post(requestBody)
						.build();
				Response response=clicent.newCall(request).execute();
				String responseData=response.body().string();
				LogUtil.i(TAG, "responseData:"+responseData);
				return responseData;

			} catch (Exception e) {
				LogUtil.e(TAG, e.toString());

			}finally{

			}
			return null;

		}




	    public static String loginofPost(String type,String name,String deviceId,String deviceType,String token)
	    {
	    	 try {  

		            OkHttpClient clicent=new OkHttpClient();
		            RequestBody requestBody=new FormBody.Builder()
		            		.add("type", type)
		            		.add("name", name)
		            		.add("deviceId", deviceId)
		            		.add("deviceType", deviceType)
		            		.add("token", token)
		            		.build();

		            Request request=new Request.Builder()
		            		.url("http:///222.92.76.215:8769/AppServer/Login")
		            		.post(requestBody)
		            		.build();
		            Response response=clicent.newCall(request).execute();
		            String responseData=response.body().string();
		            LogUtil.i(TAG, "responseData:"+responseData);
		            return responseData;
		            
		        } catch (Exception e) {  
		        	LogUtil.e(TAG, e.toString());
		        	loginofPostIn(type,name,deviceId,deviceType,token);
		        }finally{  
		           
		        }  
		        return null;
	    }

	public static String loginofPostIn(String type,String name,String deviceId,String deviceType,String token)
	{
		try {

			OkHttpClient clicent=new OkHttpClient();
			RequestBody requestBody=new FormBody.Builder()
					.add("type", type)
					.add("name", name)
					.add("deviceId", deviceId)
					.add("deviceType", deviceType)
					.add("token", token)
					.build();

			Request request=new Request.Builder()
					.url("http://192.168.9.31:8769/AppServer/Login")
					.post(requestBody)
					.build();
			Response response=clicent.newCall(request).execute();
			String responseData=response.body().string();
			LogUtil.i(TAG, "responseData:"+responseData);
			return responseData;

		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}finally{

		}
		return null;
	}

	    public static String loginofPost(String type,String name,String token)
	    {
	    	 try {  

		            OkHttpClient clicent=new OkHttpClient();
		            RequestBody requestBody=new FormBody.Builder()
		            		.add("type", type)
		            		.add("name", name)
		            		.add("token", token)
		            		.build();

		            Request request=new Request.Builder()
		            		.url("http:///222.92.76.215:8769/AppServer/Login")
		            		.post(requestBody)
		            		.build();
		            Response response=clicent.newCall(request).execute();
		            String responseData=response.body().string();
		            LogUtil.i(TAG, "responseData:"+responseData);
		            return responseData;
		            
		        } catch (Exception e) {  
		        	LogUtil.e(TAG, e.toString());
		        	loginofPostIn(type,name,token);
		        }finally{  
		           
		        }  
		        return null;  
	    }
	public static String loginofPostIn(String type,String name,String token)
	{
		try {

			OkHttpClient clicent=new OkHttpClient();
			RequestBody requestBody=new FormBody.Builder()
					.add("type", type)
					.add("name", name)
					.add("token", token)
					.build();

			Request request=new Request.Builder()
					.url("http:///192.168.9.31:8769/AppServer/Login")
					.post(requestBody)
					.build();
			Response response=clicent.newCall(request).execute();
			String responseData=response.body().string();
			LogUtil.i(TAG, "responseData:"+responseData);
			return responseData;

		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
		}finally{

		}
		return null;
	}
	    /* 
	     * 使用GET的方式登录 
	     * @param username 
	     * @param password 
	     * @return 登录状态 
	     * */  
	    public static String loginOfGet(String username,String password){  
	        HttpURLConnection conn=null;  
	        try {  
	            String data="username="+username+"&"+"password="+password;  
	            URL url=new URL("http://192.168.9.31:8769/AndroidServer/Servers?"+data);  
	            conn=(HttpURLConnection) url.openConnection();  
	            conn.setRequestMethod("GET");//设置请求方式  
	            conn.setConnectTimeout(10000);//设置连接超时时间  
	            conn.setReadTimeout(5000);//设置读取超时时间  
	            conn.connect();//开始连接  
	            int responseCode=conn.getResponseCode();//获取响应吗  
	            if(responseCode==200){  
	                //访问成功  
	                InputStream is=conn.getInputStream();//得到InputStream输入流  
	                String state=getstateFromInputstream(is);  
	                LogUtil.i("请求结果：",state);
	                return state;  
	            }else{  
	                //访问失败  
	                LogUtil.i("访问失败！","");
	            }  
	        } catch (Exception e) {  
	            LogUtil.i("访问异常！",e.toString());
	        }finally{  
	            if(conn!=null){//如果conn不等于空，则关闭连接  
	                conn.disconnect();  
	            }  
	        }  
	        return null;  
	  
	    }  
	  
	    private static String getstateFromInputstream(InputStream is) throws IOException {  
	        ByteArrayOutputStream baos=new ByteArrayOutputStream();//定义一个缓存流  
	        byte[] buffer=new byte[1024];//定义一个数组，用于读取is  
	        int len=-1;  
	        while((len =is.read(buffer)) != -1){//将字节写入缓存  
	            baos.write(buffer,0,len);  
	        }  
	        is.close();//关闭输入流  
	        String state =baos.toString();//将缓存流中的数据转换成字符串  
	        baos.close();  
	        return state;  
	    }  
}
