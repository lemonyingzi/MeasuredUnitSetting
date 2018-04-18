package com.example.bluetooth.download;

import java.io.File;

import com.example.bluetooth.data.GlobalVariable;
import com.example.bluetooth.le.IActivity;
import com.example.bluetooth.le.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class DownloadService extends Service {
	private final static String TAG = DownloadService.class.getSimpleName();

	boolean isDownloading=false;
	private DownloadTask downloadTask;
	private String downloadUrl;	
	
	private DownloadListener listener=new DownloadListener()
	{
		@Override
		public void onProgress(int progress)
		{
			getNotificationManager().notify(1,getNotification(getApplicationContext().getResources().getString(R.string.downloading),progress));
			if (!isDownloading) {
				Toast.makeText(getApplicationContext(), R.string.downloading, Toast.LENGTH_SHORT).show();
				isDownloading=true;
			}
		}

		@Override
		public void onSuccess() {
			downloadTask=null;
			stopForeground(true);
			getNotificationManager().notify(1,getNotification(getApplicationContext().getResources().getString(R.string.downloadSuccess),-1));
			Toast.makeText(DownloadService.this,R.string.downloadSuccess, Toast.LENGTH_SHORT).show();
			isDownloading=false;
			installAPK(GlobalVariable.getNewApkUri());
			
		}

		@Override
		public void onFailed() {
			downloadTask=null;
			stopForeground(true);
			getNotificationManager().notify(1,getNotification(getApplication().getResources().getString(R.string.noUpdate),-1));
			isDownloading=false;
			Toast.makeText(DownloadService.this, R.string.noUpdate, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPaused() {
			downloadTask=null;
			Toast.makeText(DownloadService.this, R.string.downloadPaused, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCanceled() {
			downloadTask=null;
			stopForeground(true);
			Toast.makeText(DownloadService.this,R.string.downloadCanceled, Toast.LENGTH_SHORT).show();			
		}	
	};
    /** 
     * 安装apk文件 
     */  
    private void installAPK(Uri apk) {  
         Intent intent = new Intent(Intent.ACTION_VIEW); 
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
         intent.setDataAndType(apk, "application/vnd.android.package-archive"); 
         startActivity(intent);
    }  
    

	private DownloadBinder mBinder=new DownloadBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class DownloadBinder extends android.os.Binder{
		public void startDownload(String url)
		{
			if (downloadTask==null) {
				downloadUrl=url;
				downloadTask=new DownloadTask(listener);
				downloadTask.execute(downloadUrl);
				startForeground(1, getNotification(getApplicationContext().getResources().getString(R.string.checking),-1));
				Toast.makeText(DownloadService.this, R.string.checking, Toast.LENGTH_SHORT).show();			
			}
		}
		
		public void pauseDownload()
		{
			if (downloadTask!=null) {
				downloadTask.pauseDownload();
			}
		}
		
		public void cancelDownload()
		{
			if (downloadTask!=null) {
				downloadTask.cancelDownload();
			}
			else
			{
				if (downloadUrl!=null) {
					String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
					String directory=android.os.Environment.getExternalStoragePublicDirectory
							(android.os.Environment.DIRECTORY_DOWNLOADS).getPath();
					File file=new File(directory+fileName);
					if (file.exists()) {
						file.delete();
					}
					getNotificationManager().cancel(1);
					stopForeground(true);
					Toast.makeText(DownloadService.this, R.string.downloadCanceled, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	private NotificationManager getNotificationManager()
	{
		return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	private android.app.Notification getNotification(String title,int progress)
	{
		Intent intent=new Intent(DownloadService.this,IActivity.class);
		PendingIntent pi=PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
		builder.setSmallIcon(android.R.drawable.ic_dialog_info);
		builder.setLargeIcon(android.graphics.BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info));
		builder.setContentIntent(pi);
		builder.setContentTitle(title);
		if (progress>0) {
			builder.setContentText(progress+"%");
			builder.setProgress(100, progress, false);
		}
		return builder.build();
	}
}
