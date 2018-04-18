package com.example.bluetooth.le;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import com.example.bluetooth.db.DataBaseHelper;
import com.example.bluetooth.db.UserDB;
import com.example.bluetooth.download.DownloadService;
import com.example.bluetooth.result.ResultActivity;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IActivity extends Activity implements OnClickListener {
	private final static String TAG = IActivity.class.getSimpleName();
	public static final int TAKE_PHOTO=1;
	public static final int CHOOSE_PHOTO=2;
	
	private Uri imageUri;
	private ImageView imageView;
	DataBaseHelper dbhelper;
	private TextView nickNameView;
	private TextView idView;
	private TextView phoneNumberView;
	
	LinearLayout iTv;//我
	LinearLayout resultTv;//成绩
	LinearLayout rankingTv;//排名
	LinearLayout timerTv;
	LinearLayout myTimerLayout;
	LinearLayout myRCLayout;
	LinearLayout aboutLl;
	LinearLayout updateLl;
	LinearLayout helpLl;
    //////////////////////手势////////////////
	 private GestureDetector mGestureDetector;
	 private int verticalMinDistance = 180;
	 private int minVelocity         = 0;
	 RelativeLayout iRelativeLayout;
	
	private int max = 100;
	private int current = 0;
	private String speed = "1";
	
	private DownloadService.DownloadBinder downloadBinder;//下载实例
	
	private ServiceConnection connection=new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadBinder=(DownloadService.DownloadBinder) service;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_i);
		
//		iRelativeLayout=(RelativeLayout)findViewById(R.id.iRl);
//		iRelativeLayout.setOnTouchListener(this);
//		iRelativeLayout.setLongClickable(true);
//        mGestureDetector = new GestureDetector(IActivity.this);
		
		imageView=(ImageView) findViewById(R.id.head_image_view);
		nickNameView=(TextView)findViewById(R.id.nick_name_view);
		idView=(TextView)findViewById(R.id.ID_view);
		phoneNumberView=(TextView)findViewById(R.id.phone_view);
		helpLl=(LinearLayout) findViewById(R.id.helpLl);

		/**
		 * 我的计时器
		 */
		myTimerLayout=(LinearLayout)findViewById(R.id.my_timer_layout);
		/**
		 * 我的RC
		 */
		myRCLayout=(LinearLayout)findViewById(R.id.my_rc_layout);
		//底部
        iTv=(LinearLayout) findViewById(R.id.bottomI);
        iTv.setOnClickListener(bottomListener);
        resultTv=(LinearLayout) findViewById(R.id.bottomResults);
        resultTv.setOnClickListener(bottomListener);
        rankingTv=(LinearLayout) findViewById(R.id.bottomRanking);
        rankingTv.setOnClickListener(bottomListener);
        timerTv=(LinearLayout) findViewById(R.id.bottomTimer);
        timerTv.setOnClickListener(bottomListener);
        aboutLl=(LinearLayout) findViewById(R.id.aboutLl);
        aboutLl.setOnClickListener(new OnClickListener()
			{
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(IActivity.this,AboutActivity.class);
				startActivity(intent);
			}
	
		});
        //绑定服务
   
		Intent intent=new Intent(IActivity.this,DownloadService.class);
		startService(intent);
		bindService(intent, connection, BIND_AUTO_CREATE);


		if (ContextCompat.checkSelfPermission(IActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
			!=PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(IActivity.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}
		
        /////////检查更新//////////////////////////
        updateLl=(LinearLayout) findViewById(R.id.updateLl);
        updateLl.setOnClickListener(new OnClickListener()
    		{
				@Override
				public void onClick(View v) {
					String url="http://222.92.76.215:8768/ACCURATE Smart Timing System/ACCURATE Smart IrTimer105.apk";
					downloadBinder.startDownload(url);
				}
        	
        	});
        //////////////帮助///////////////////////////
        helpLl.setOnClickListener(new OnClickListener()
        
		{
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(IActivity.this,HelpActivity.class);
				startActivity(intent);
			}
	
		});
     
        dbhelper=new DataBaseHelper(getApplicationContext(),"TimerStore",null,7);//数据库初始化
		try
		{
			SQLiteDatabase db=dbhelper.getWritableDatabase();
			Cursor cursor=db.query("USER", null, null, null, null, null, null, null);
			if(cursor.moveToFirst())
			{
				do{
					String id=cursor.getString(cursor.getColumnIndex("id"));
					String name=cursor.getString(cursor.getColumnIndex("name"));
					String blename=cursor.getString(cursor.getColumnIndex("blename"));
					String address=cursor.getString(cursor.getColumnIndex("bleaddress"));
					String phoneNumber=cursor.getString(cursor.getColumnIndex("phonenumber"));
//					nickNameView.setText(name);
//					idView.setText(id);
//					phoneNumberView.setText(phoneNumber);
				}
				while(cursor.moveToNext());
			}
		}
		catch(Exception e)
		{
			Log.d(TAG, e.toString());
		}
		/**
		 * 点击imageview则弹出菜单
		 */
//		imageView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Log.d(TAG, "button click");
//				showPopupMenu(imageView); 
//			}
//		});
		/**
		 * 从数据库中读取头像，如果头像存在，则加载到imageview中
		 */
		UserDB userDB=new UserDB(getApplicationContext(), "TimerStore", null, 7);
		Bitmap bitmap=userDB.getBmp();
		if (bitmap!=null) {
			imageView.setImageBitmap(bitmap);
		}
		
		/**
		 * 点击“我的定时器”跳转到定时器绑定页面
		 */
        myTimerLayout.setOnClickListener(new View.OnClickListener() {
    	public void onClick(View v) {
	    		Intent intent=new Intent(IActivity.this,BindedAndUnbindedActivity.class);
				startActivity(intent);
    		}
        });
        /**
         * 点击“我的RC”跳转到修改RC页面
         */
        myRCLayout.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v)
        	{
        		Intent intent=new Intent(IActivity.this,InputRCActivity.class);
        		startActivity(intent);
        	}
        });
	}
	

	/**
	 * 弹出菜单
	 * @param view
	 */
	private void showPopupMenu(View view) { 
	    // View当前PopupMenu显示的相对View的位置 
	    PopupMenu popupMenu = new PopupMenu(this, view);  
	    // menu布局 
	    popupMenu.getMenuInflater().inflate(R.menu.takepicture, popupMenu.getMenu());  
	    // menu的item点击事件 
	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
	      @Override
	      public boolean onMenuItemClick(MenuItem item) { 
	        switch (item.getItemId()) {
			case R.id.take_picture://为拍照，则打开相机
				//启动相机程序
				Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
				if(hasSdcard())//判断是否有内存卡
				{
					Log.d(TAG, "sdcard exist");
					File outputImage=new File(getExternalCacheDir(), "output_image.jpg");
					try
					{
						if(outputImage.exists())
						{
							outputImage.delete();
						}
						outputImage.createNewFile();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					if(Build.VERSION.SDK_INT>=24)
					{
						imageUri=FileProvider.getUriForFile(IActivity.this, "com.example.bluetooth.le.fileprovider", outputImage);
						Log.d(TAG, "7.0以上系统："+String.valueOf(imageUri));
					}
					else
					{
						imageUri=Uri.fromFile(outputImage);
						Log.d(TAG, "7.0以下系统："+String.valueOf(imageUri));
					}
				}
				else {
					Log.d(TAG, "sdcard not exist");
					android.content.ContentValues values = new android.content.ContentValues();
					imageUri = getContentResolver().insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					Log.d(TAG, "imageUri:"+String.valueOf(imageUri));
				}
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,TAKE_PHOTO);
				break;
			case R.id.open_album://打开相册
				Log.d(TAG, "open album");
				if(ContextCompat.checkSelfPermission(IActivity.this, 
						Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
				{
					Log.d(TAG, "permission not granted");
					android.support.v4.app.ActivityCompat.requestPermissions(IActivity.this, new String[]{
							Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				}
				else
				{
					openAlbums();
				}
				break;
			default:
				break;
			}
	        return true; 
	      } 
	    });  
	     // PopupMenu关闭事件 
	    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() { 
	       @Override
	       public void onDismiss(PopupMenu menu) { 
	         //Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show(); 
	      } 
	     }); 
	   
	     popupMenu.show(); 
	   } 
	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 打开相册
	 */
	private void openAlbums()
	{
		Intent intent=new Intent("android.intent.action.GET_CONTENT");
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_PHOTO);
	}
	
   /**
    * 底部定时器 成绩 排名 我 跳转
    */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象    
        public void onClick(View v)
        {    
        	int id = v.getId();
			if(id==R.id.bottomTimer)
			{
				Intent intent=new Intent(IActivity.this,MainActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomRanking) {
				Intent intent=new Intent(IActivity.this,RankingActivity.class);
				startActivity(intent);
			}
			else if (id==R.id.bottomResults) {
				Intent intent=new Intent(IActivity.this,ResultActivity.class);
				startActivity(intent);
			}
		}
    };  
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    	switch (requestCode) {
		case 1:
			if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_DENIED) {
				openAlbums();
			}
			else {
				Log.d(TAG, "you denied the permission");
			}
			break;

		default:
			break;
		}
    }
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		switch(requestCode)
		{
			case TAKE_PHOTO:
				if(resultCode==RESULT_OK)
				{
					try
					{
						Log.d(TAG, "imageUri:"+String.valueOf(imageUri));
						Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
						Log.d(TAG, "压缩前的大小："+bitmap.getRowBytes()*bitmap.getHeight());
						Bitmap compressBitmap=zoomImage(bitmap, 15);
						Log.d(TAG, "压缩后的大小："+compressBitmap.getRowBytes()*compressBitmap.getHeight());
						imageView.setImageBitmap(compressBitmap);     
					}
					catch(Exception e)
					{
						Log.d(TAG,e.toString());
					}
				}
				else if (resultCode==RESULT_CANCELED) {
					Log.d(TAG, "operation canceled");
				}
				break;
			case CHOOSE_PHOTO:
				if(resultCode==RESULT_OK)
				{
					if(Build.VERSION.SDK_INT>=19)
					{
						//4.4及以上系统使用这个方法处理图片
						handleImageOnKitKat(data);
					}
					else
					{	//4.4以下系统使用这个方法处理图片
						handleImageBeforeKitKat(data);
					}
				}
				break;
				default:break;
			}
	}

	
	@TargetApi(19)
	private void handleImageOnKitKat(Intent data)
	{
		String imagePath=null;
		Uri uri=data.getData();
		//如果是document类型的uri,则通过document id处理
		if(DocumentsContract.isDocumentUri(this, uri))
		{
			String docId=DocumentsContract.getDocumentId(uri);
			if("com.android.providers.media.documents".equals(uri.getAuthority()))
			{
				String id=docId.split(":")[1];
				String selection=MediaStore.Images.Media._ID+"="+id;
				imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
			}
			else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				Uri contentUri=android.content.ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), 
						Long.valueOf(docId));
				imagePath=getImagePath(contentUri, null);				
			}
		}
		//如果是content类型的uri，则使用普通方式处理
		else if("content".equalsIgnoreCase(uri.getScheme()))
		{
			imagePath=getImagePath(uri, null);
		}
		//如果是file类型的uri，直接获取图片路径即可
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			imagePath=uri.getPath();
		}
		displayImage(imagePath);
	}
	
	
	private void handleImageBeforeKitKat(Intent data)
	{
		Uri uri=data.getData();
		String imagePath=getImagePath(uri,null);
		displayImage(imagePath);
	}
	/**
	 * 获取图片路径
	 * @param uri
	 * @param selection
	 * @return
	 */
	private String getImagePath(Uri uri,String selection)
	{
		String path=null;
		Cursor cursor=getContentResolver().query(uri, null, selection, null, null);
		if (cursor!=null) {
			if(cursor.moveToFirst())
			{
				path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}
	/**
	 * 显示图片
	 * @param imagePath
	 */
	private void displayImage(String imagePath)
	{
		if(imagePath!=null)
		{
			Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
			Bitmap compressBitmap=zoomImage(bitmap, 15);
			imageView.setImageBitmap(compressBitmap);
		}
		else
		{
			Log.d(TAG, "failed to get image");
		}
	}
	@Override 
	protected void onDestroy()
	{
		super.onDestroy();
		try
		{
			unbindService(connection);
		}
		catch (Exception e) {
			Log.d(TAG, "onDestory:"+e.toString());
		}
	}
	
	/**
	 * 缩放图片
	 * @param image
	 * @param size
	 * @return
	 */
	 private Bitmap zoomImage(Bitmap image,int size)
	 {
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
	        image.compress(Bitmap.CompressFormat.JPEG, 85, out);
	        float zoom = (float)Math.sqrt(size * 1024 / (float)out.toByteArray().length);

	        Matrix matrix = new Matrix();
	        matrix.setScale(zoom, zoom);

	        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

	        out.reset();
	        result.compress(Bitmap.CompressFormat.JPEG, 85, out);
	        while(out.toByteArray().length > size * 1024){
	            System.out.println(out.toByteArray().length);
	            matrix.setScale(0.9f, 0.9f);
	            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
	            out.reset();
	            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
	        } 
	        /**
	         * 存入数据库
	         */
			UserDB userDB=new UserDB(getApplicationContext(), "TimerStore", null, 7);
			userDB.updateImage(out.toByteArray());	        
	        Log.d(TAG, "update success");
	        ByteArrayInputStream isBm=new ByteArrayInputStream(out.toByteArray());
	        Bitmap bitmap=BitmapFactory.decodeStream(isBm,null,null);
	        return bitmap;
	 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
