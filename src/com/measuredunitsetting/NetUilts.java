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
	    * ��post��ʽ��¼ 
	    * @param username 
	    * @param password 
	    * @return ��¼״̬ 
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
	 * ��������������
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
	     * ʹ��GET�ķ�ʽ��¼ 
	     * @param username 
	     * @param password 
	     * @return ��¼״̬ 
	     * */  
	    public static String loginOfGet(String username,String password){  
	        HttpURLConnection conn=null;  
	        try {  
	            String data="username="+username+"&"+"password="+password;  
	            URL url=new URL("http://192.168.9.31:8769/AndroidServer/Servers?"+data);  
	            conn=(HttpURLConnection) url.openConnection();  
	            conn.setRequestMethod("GET");//��������ʽ  
	            conn.setConnectTimeout(10000);//�������ӳ�ʱʱ��  
	            conn.setReadTimeout(5000);//���ö�ȡ��ʱʱ��  
	            conn.connect();//��ʼ����  
	            int responseCode=conn.getResponseCode();//��ȡ��Ӧ��  
	            if(responseCode==200){  
	                //���ʳɹ�  
	                InputStream is=conn.getInputStream();//�õ�InputStream������  
	                String state=getstateFromInputstream(is);  
	                LogUtil.i("��������",state);
	                return state;  
	            }else{  
	                //����ʧ��  
	                LogUtil.i("����ʧ�ܣ�","");
	            }  
	        } catch (Exception e) {  
	            LogUtil.i("�����쳣��",e.toString());
	        }finally{  
	            if(conn!=null){//���conn�����ڿգ���ر�����  
	                conn.disconnect();  
	            }  
	        }  
	        return null;  
	  
	    }  
	  
	    private static String getstateFromInputstream(InputStream is) throws IOException {  
	        ByteArrayOutputStream baos=new ByteArrayOutputStream();//����һ��������  
	        byte[] buffer=new byte[1024];//����һ�����飬���ڶ�ȡis  
	        int len=-1;  
	        while((len =is.read(buffer)) != -1){//���ֽ�д�뻺��  
	            baos.write(buffer,0,len);  
	        }  
	        is.close();//�ر�������  
	        String state =baos.toString();//���������е�����ת�����ַ���  
	        baos.close();  
	        return state;  
	    }  
}
