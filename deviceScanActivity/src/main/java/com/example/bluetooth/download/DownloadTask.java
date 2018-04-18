package com.example.bluetooth.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.example.bluetooth.data.GlobalVariable;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer>{
	private final static String TAG = DownloadTask.class.getSimpleName();

	public static final int TYPE_SUCCESS=0;
	public static final int TYPE_FAILED=1;
	public static final int TYPE_PAUSED=2;
	public static final int TYPE_CANCELED=3;
	
	private DownloadListener listener;
	private boolean isCanceled=false;
	private boolean isPaused=false;
	private int lastProgress;
	
	public DownloadTask(DownloadListener listener) {
		this.listener=listener;
	}
	
	
	@Override
	protected Integer doInBackground(String... params) {
		InputStream is=null;
		RandomAccessFile savedFile=null;
		File file=null;
		
		try
		{
			long downloadedLength=0;//记录已下载的文件长度
			String downloadUrl=params[0];
			String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
			String directory=android.os.Environment.getExternalStoragePublicDirectory
					(android.os.Environment.DIRECTORY_DOWNLOADS).getPath();
			file=new File(directory+fileName);
			GlobalVariable.setNewApkUri(Uri.fromFile(file));
			if (file.exists()) {//如果存在，则删除
				file.delete();
				downloadedLength=file.length();
				Log.d(TAG, "downloadedLength:"+downloadedLength);
			}
			
			long contentLength=getContentLength(downloadUrl);
			if (contentLength==0) {//说明已是最新版
				return TYPE_FAILED;
			}
//			else if (contentLength==downloadedLength) {
//				file.delete();
//				Log.d(TAG, "file delete");
//				downloadedLength=0;
//			}
//			
			OkHttpClient client=new OkHttpClient();
			Request request=new Request.Builder()
					.addHeader("RANGE", "bytes="+downloadedLength+"-")
					.url(downloadUrl)
					.build();
			Response response=client.newCall(request).execute();
			if (response!=null) {
				is=response.body().byteStream();
				savedFile=new RandomAccessFile(file, "rw");
				savedFile.seek(downloadedLength);
				byte[] b=new byte[1024];
				int total=0;
				int len;
				while((len=is.read(b))!=-1)
				{
					if (isCanceled) {
						return TYPE_CANCELED;
					}
					else if (isPaused) {
						return TYPE_PAUSED;
					}
					else {
						total+=len;
						savedFile.write(b,0,len);
						int progress=(int)((total+downloadedLength)*100/contentLength);
						publishProgress(progress);
						
					}
				}
				response.body().close();
				return TYPE_SUCCESS;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try
			{
				if (is!=null) {
					is.close();
				}
				if (savedFile!=null) {
					savedFile.close();
				}
				if (isCanceled && file!=null) {
					file.delete();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return TYPE_FAILED;
	}

	@Override
	protected void onProgressUpdate(Integer ...values)
	{
		int progress=values[0];
		if (progress>lastProgress) {
			listener.onProgress(progress);
			lastProgress=progress;
		}
	}
	
	@Override
	protected void onPostExecute(Integer status)
	{
		switch (status) {
		case TYPE_SUCCESS:
			listener.onSuccess();
			break;
		case TYPE_FAILED:
			listener.onFailed();
			break;
		case TYPE_PAUSED:
			listener.onPaused();
			break;
		case TYPE_CANCELED:
			listener.onCanceled();
		default:
			break;
		}
	}
	public void pauseDownload()
	{
		isPaused=true;
	}
	public void cancelDownload()
	{
		isCanceled=true;
	}
	private long getContentLength(String downloadUrl) throws java.io.IOException{
		OkHttpClient client=new OkHttpClient();
		Request request=new Request.Builder().url(downloadUrl).build();
		Response response=client.newCall(request).execute();
		if (response!=null && response.isSuccessful()) {
			long contentLength=response.body().contentLength();
			response.body().close();
			return contentLength;
		}
		return 0;
	}
}
