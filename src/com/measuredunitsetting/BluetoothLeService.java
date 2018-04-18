/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.measuredunitsetting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import com.measuredunitsetting.data.*;
import com.measuredunitsetting.entity.DisplacementMeasuredUnit;
import com.measuredunitsetting.entity.HydraulicMeasuredUnit;
import com.measuredunitsetting.global.GlobalVariable;
import com.measuredunitsetting.global.LogUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_UUID =
            "com.example.bluetooth.le.uuid_DATA";
    public final static String EXTRA_NAME =
            "com.example.bluetooth.le.name_DATA";
    public final static String EXTRA_PASSWORD =
            "com.example.bluetooth.le.password_DATA";
    

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_FUNCTION = "0000ffe1-0000-1000-8000-00805f9b34fb";
	
    

    public  String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }
    public  byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("���Ȳ���ż��");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // ��λһ�飬��ʾһ���ֽ�,��������ʾ��16�����ַ�������ԭ��һ�������ֽ�
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }
    
    
    
    
    
    byte[] WriteBytes = new byte[20];
	public void txxx(String g){
		g=""+g;
		WriteBytes= hex2byte(g.toString().getBytes());
		BluetoothGattCharacteristic gg;
		
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
		//byte t[]={51,1,2};
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
		//mBluetoothGatt.setCharacteristicNotification(gg, true);
		
		//gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		//mBluetoothGatt.setCharacteristicNotification(gg, true);
	}
	///�з���intת��Ϊ�޷���SHORT
	private byte[] unsigned_short_2byte(int length)
	{
		byte[] targets = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) ((length >>> offset) & 0xff);
		}
//		final StringBuilder stringBuilder = new StringBuilder(targets.length);
//	      for(byte byteChar : targets)
//              stringBuilder.append(String.format("%02X", byteChar));
		return targets;
	}
	//��intת��Ϊbyte
	private byte[] int2byte(int i)
	{
		 byte[] a = new byte[4];
	        a[0] = (byte) (0xff & i);
	        a[1] = (byte) ((0xff00 & i) >> 8);
	        a[2] = (byte) ((0xff0000 & i) >> 16);
	        a[3] = (byte) ((0xff000000 & i) >> 24);
		return a;
	}
	////////////////////////////////////////////////Һѹˮ׼///////////////////////////////
	//���Ͳ�ѯָ��
	public void t_data_query_hydraulic()
	{
		try
		{
			byte[] queryData=new byte[19];
			queryData[0]=(byte) 0x13;
			queryData[1]=(byte) 0x00;
			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x06;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
			LogUtil.i(TAG, "examOrganCode:"+GlobalVariable.getExamOrganCode());
			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x12;
			queryData[14]=0x04;
			//β
			queryData[15]=0x4a;
			queryData[16]=0x53;
			queryData[17]=0x4b;
			queryData[18]=0x46;

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "query:"+ou);

			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.d(TAG,ex.toString());
		}
	}
	//���Ͳ�ѯָ��
	public void t_data_query_queue_hydraulic()
	{
		try
		{
			byte[] queryData=new byte[19];
			queryData[0]=(byte) 0x13;
			queryData[1]=(byte) 0x00;
			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x06;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x12;
			queryData[14]=0x07;
			//β
			queryData[15]=0x4a;
			queryData[16]=0x53;
			queryData[17]=0x4b;
			queryData[18]=0x46;

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "query:"+ou);

			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.d(TAG,ex.toString());
		}

	}
	//���Ͳɼ�ָ��
	public void t_data_collect_hydraulic()
	{
		try
		{
			byte[] queryData=new byte[19];
			queryData[0]=(byte) 0x13;
			queryData[1]=(byte) 0x00;
			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x06;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x11;
			queryData[14]=0x05;
			//β
			queryData[15]=0x4a;
			queryData[16]=0x53;
			queryData[17]=0x4b;
			queryData[18]=0x46;

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "query:"+ou);

			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.i(TAG,ex.toString());
		}
	}
	/**
	 * �û��趨
	 * @param hydraulicMeasuredUnit
	 * @throws InterruptedException 
	 */
	public void t_data_usersetting_hydraulic(HydraulicMeasuredUnit hydraulicMeasuredUnit) throws InterruptedException
	{
		try
		{
			byte[] queryData=new byte[20];
			queryData[0]=(byte) 0x2a;
			queryData[1]=0x00;

			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x06;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();

			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x10;
			queryData[14]=0x02;
			//��Ŵ���
			byte[] serialNumCodeByte="0120".getBytes();
			queryData[15]=serialNumCodeByte[0];
			queryData[16]=serialNumCodeByte[1];
			queryData[17]=serialNumCodeByte[2];
			queryData[18]=serialNumCodeByte[3];

			byte[] serialNumData=unsigned_short_2byte(hydraulicMeasuredUnit.getSerialNumber());
//			byte[] serialNumData=unsigned_short_2byte(1);
			queryData[19]=serialNumData[0];


			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "user setting:"+ou);
			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);

			byte[] queryDataTwo=new byte[20];
			//�������

			//������ʹ���
			byte[] measureTypeCode="0111".getBytes();

			queryDataTwo[0]=serialNumData[1];

			queryDataTwo[1]=measureTypeCode[0];
			queryDataTwo[2]=measureTypeCode[1];
			queryDataTwo[3]=measureTypeCode[2];
			queryDataTwo[4]=measureTypeCode[3];
			//�����������
			if (hydraulicMeasuredUnit.getMeasureType()!=null && hydraulicMeasuredUnit.getMeasureType().equals(getResources().getString(R.string.benchmark)))
			{
				queryDataTwo[5]=(byte) 0xA0;
			}
			else if (hydraulicMeasuredUnit.getMeasureType()!=null && hydraulicMeasuredUnit.getMeasureType().equals(getResources().getString(R.string.commonMeasurePoint)))
			{
				queryDataTwo[5]=(byte) 0xB0;
			}
			//����Ŵ���
			byte[] measurePointNumByte="000B".getBytes();
			queryDataTwo[6]=measurePointNumByte[0];
			queryDataTwo[7]=measurePointNumByte[1];
			queryDataTwo[8]=measurePointNumByte[2];
			queryDataTwo[9]=measurePointNumByte[3];
			//���������


			byte[] measurePointNumData=hydraulicMeasuredUnit.getMeasurePointNumber().getBytes();
//		byte[] measurePointNumData="00000000".getBytes();

			queryDataTwo[10]=measurePointNumData[0];
			queryDataTwo[11]=measurePointNumData[1];
			queryDataTwo[12]=measurePointNumData[2];
			queryDataTwo[13]=measurePointNumData[3];
			queryDataTwo[14]=measurePointNumData[4];
			queryDataTwo[15]=measurePointNumData[5];
			queryDataTwo[16]=measurePointNumData[6];
			queryDataTwo[17]=measurePointNumData[7];

			queryDataTwo[18]=0x4a;
			queryDataTwo[19]=0x53;

			ou=publicMethod.bytesToHexString(queryDataTwo);

			//��ʱ
			Thread.sleep(6);


			LogUtil.i(TAG, "user setting:"+ou);
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryDataTwo);
			mBluetoothGatt.writeCharacteristic(gg);

			byte[] queryDataThree=new byte[2];

			//β
			queryDataThree[0]=0x4b;

//		queryDataThree[0]=(byte) 0xfe;
			queryDataThree[1]=0x46;


			//��ʱ
			Thread.sleep(6);
			ou=publicMethod.bytesToHexString(queryDataThree);
			LogUtil.i(TAG, "user setting:"+ou);
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryDataThree);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.d(TAG,ex.toString());
		}
	}
	
	/////////////////////////////////////////���ˮƽλ��///////////////////////////////////
	public void t_data_query_displacement()
	{
		try
		{
			byte[] queryData=new byte[19];
			queryData[0]=(byte) 0x13;
			queryData[1]=(byte) 0x00;
			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x04;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x12;
			queryData[14]=0x04;
			//β
			queryData[15]=0x4a;
			queryData[16]=0x53;
			queryData[17]=0x4b;
			queryData[18]=0x46;

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "query:"+ou);

			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.i(TAG,ex.toString());
		}
	}
	
	
	/**
	 * �û��趨
	 * @param
	 * @throws InterruptedException 
	 */
	public void t_data_usersetting_displacement(DisplacementMeasuredUnit displacementMeasuredUnit) throws InterruptedException
	{
		try
		{
			byte[] queryData=new byte[20];
			queryData[0]=(byte) 0x22;
			queryData[1]=0x00;

			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x04;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();

			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x10;
			queryData[14]=0x02;

			byte[] serialNumData=unsigned_short_2byte(displacementMeasuredUnit.getSerialNumber());
//			byte[] serialNumData=unsigned_short_2byte(1);
			queryData[15]=serialNumData[0];
			queryData[16]=serialNumData[1];

			//���
			float depth=displacementMeasuredUnit.getDepth();

			int depthInt=(int)(depth*10);
			int depthInt0=depthInt/100;
			int depthInt1=(depthInt-depthInt0*100)/10;
			int depthInt2=(depthInt-depthInt0*100-depthInt1*10);

			byte[] depthByte=new byte[3];
			depthByte[0]=(byte) depthInt0;
			depthByte[1]=(byte) depthInt1;
			depthByte[2]=(byte) depthInt2;

			queryData[17]=depthByte[0];
			queryData[18]=depthByte[1];
			queryData[19]=depthByte[2];

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "user setting:"+ou);
			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
			//��ʱ
			Thread.sleep(6);

			byte[] queryDataTwo=new byte[14];//20180417 edit /add iid
			String iid=displacementMeasuredUnit.getMeasureUnitId();
			byte[] iidByte=iid.getBytes();

			queryDataTwo[0]=iidByte[0];
			queryDataTwo[1]=iidByte[1];
			queryDataTwo[2]=iidByte[2];
			queryDataTwo[3]=iidByte[3];
			queryDataTwo[4]=iidByte[4];
			queryDataTwo[5]=iidByte[5];
			queryDataTwo[6]=iidByte[6];
			queryDataTwo[7]=iidByte[7];
			queryDataTwo[8]=iidByte[8];
			queryDataTwo[9]=iidByte[9];
			//β
			queryDataTwo[10]=0x4a;
			queryDataTwo[11]=0x53;
			queryDataTwo[12]=0x4b;
			queryDataTwo[13]=0x46;

			ou=publicMethod.bytesToHexString(queryDataTwo);
			LogUtil.i(TAG, "user setting:"+ou);
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryDataTwo);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.d(TAG,ex.toString());
		}


	}
	//���Ͳɼ�ָ��
	public void t_data_collect_displacement()
	{
		try
		{
			byte[] queryData=new byte[19];
			queryData[0]=(byte) 0x13;
			queryData[1]=(byte) 0x00;
			//ͷ
			queryData[2]=0x49;
			queryData[3]=0x42;
			queryData[4]=0x53;
			//�豸����
			queryData[5]=0x10;
			queryData[6]=0x04;
			//����������
			byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
			queryData[7]=examOrganCode[0];
			queryData[8]=examOrganCode[1];
			queryData[9]=examOrganCode[2];
			queryData[10]=examOrganCode[3];
			queryData[11]=examOrganCode[4];
			queryData[12]=examOrganCode[5];
			//ָ�����
			queryData[13]=0x11;
			queryData[14]=0x05;
			//β
			queryData[15]=0x4a;
			queryData[16]=0x53;
			queryData[17]=0x4b;
			queryData[18]=0x46;

			PublicMethod publicMethod=new PublicMethod();
			String ou=publicMethod.bytesToHexString(queryData);
			LogUtil.i(TAG, "query:"+ou);

			BluetoothGattCharacteristic gg;
			gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			//byte t[]={51,1,2};
			gg.setValue(queryData);
			mBluetoothGatt.writeCharacteristic(gg);
		}
		catch(Exception ex)
		{
			LogUtil.i(TAG,ex.toString());
		}

	}

	//���Ͳ�ѯָ��
		public void t_data_query_queue_displacement()
		{
			try
			{
				byte[] queryData=new byte[19];
				queryData[0]=(byte) 0x13;
				queryData[1]=(byte) 0x00;
				//ͷ
				queryData[2]=0x49;
				queryData[3]=0x42;
				queryData[4]=0x53;
				//�豸����
				queryData[5]=0x10;
				queryData[6]=0x04;
				//����������
				byte[] examOrganCode=GlobalVariable.getExamOrganCode().getBytes();
				queryData[7]=examOrganCode[0];
				queryData[8]=examOrganCode[1];
				queryData[9]=examOrganCode[2];
				queryData[10]=examOrganCode[3];
				queryData[11]=examOrganCode[4];
				queryData[12]=examOrganCode[5];
				//ָ�����
				queryData[13]=0x12;
				queryData[14]=0x07;
				//β
				queryData[15]=0x4a;
				queryData[16]=0x53;
				queryData[17]=0x4b;
				queryData[18]=0x46;

				PublicMethod publicMethod=new PublicMethod();
				String ou=publicMethod.bytesToHexString(queryData);
				LogUtil.i(TAG, "query:"+ou);

				BluetoothGattCharacteristic gg;
				gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
				//byte t[]={51,1,2};
				gg.setValue(queryData);
				mBluetoothGatt.writeCharacteristic(gg);
			}
			catch(Exception ex)
			{
				LogUtil.d(TAG,ex.toString());
			}

		}
	
	public void enable_noty()
	{
		BluetoothGattService service =mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
		BluetoothGattCharacteristic ale =service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
		boolean set = mBluetoothGatt.setCharacteristicNotification(ale, true);
		LogUtil.i(TAG," setnotification = " + set);
		BluetoothGattDescriptor dsc =ale.getDescriptor(UUID.fromString(  "00002902-0000-1000-8000-00805f9b34fb"));
		byte[]bytes = {0x01,0x00};
		dsc.setValue(bytes);
		mBluetoothGatt.writeDescriptor(dsc);
	}

    public void enable_JDY_ble( boolean p){
    	
    	try {
		if(p)
	    {
			BluetoothGattService service =mBluetoothGatt.getService(UUID.fromString(Service_uuid));
			BluetoothGattCharacteristic ale;// =service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
			switch( 0 )
			{
				case 0://0xFFE1 //͸��
				{
					ale =service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
				}break;
				case 1:// 0xFFE2 //iBeacon_UUID
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"));
				}break;
				case 2://0xFFE3 //iBeacon_Major
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb"));
				}break;
				case 3://0xFFE4 //iBeacon_Minor
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
				}break;
				case 4://0xFFE5 //�㲥���
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb"));
				}break;
				case 5://0xFFE6 //���빦��
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe6-0000-1000-8000-00805f9b34fb"));
				}break;
				case 6:// 0xFFE7 //�豸������
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe7-0000-1000-8000-00805f9b34fb"));
				}break;
				case 7:// 0xFFE8 //IO������ܹ���
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe8-0000-1000-8000-00805f9b34fb"));
				}break;
				case 8:// 0xFFE9 //PWM����
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb"));
				}break;
				case 9:// 0xFFEA //��λģ��
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffea-0000-1000-8000-00805f9b34fb"));
				}break;
				case 10:// 0xFFEB //���书��
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffeb-0000-1000-8000-00805f9b34fb"));
				}break;
				case 11:// 0xFFEC //RTC����
				{
					ale =service.getCharacteristic(UUID.fromString("0000ffec-0000-1000-8000-00805f9b34fb"));
				}break;
				default:
					ale =service.getCharacteristic(UUID.fromString(Characteristic_uuid_TX));
					break;
			} 
			boolean set = mBluetoothGatt.setCharacteristicNotification(ale, true);
			LogUtil.i(TAG," setnotification = " + set);
			BluetoothGattDescriptor dsc =ale.getDescriptor(UUID.fromString(  "00002902-0000-1000-8000-00805f9b34fb"));
			byte[]bytes = {0x01,0x00};
			dsc.setValue(bytes);
			boolean success =mBluetoothGatt.writeDescriptor(dsc);
			LogUtil.i(TAG, "writing enabledescriptor:" + success);
	    }
	    else
	    {
		   BluetoothGattService service =mBluetoothGatt.getService(UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455"));
		   BluetoothGattCharacteristic ale =service.getCharacteristic(UUID.fromString(Service_uuid));
		   boolean set = mBluetoothGatt.setCharacteristicNotification(ale, false);
		   LogUtil.i(TAG," setnotification = " + set);
		   BluetoothGattDescriptor dsc =ale.getDescriptor(UUID.fromString(Characteristic_uuid_TX));
		   byte[]bytes = {0x00, 0x00};
		   dsc.setValue(bytes);
		   boolean success =mBluetoothGatt.writeDescriptor(dsc);
		   LogUtil.i(TAG, "writing enabledescriptor:" + success);
	    }
        	
        	
//        	jdy=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
//        	mBluetoothGatt.setCharacteristicNotification(jdy, p);
		} catch (NumberFormatException e) {
			e.printStackTrace();

		}
    }
    public void read_uuid(  ){
    	String txt="AAE50111";
    	WriteBytes= hex2byte(txt.toString().getBytes());
    	
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    
    public Boolean set_uuid( String txt ){//uuid
    	
    	if( txt.length()==36 )
		{
			String v1="",v2="",v3="",v4="";
			v1=txt.substring(8,9);
			v2=txt.substring(13,14);
			v3=txt.substring(18,19);
			v4=txt.substring(23,24);
			if( v1.equals("-")&&v2.equals("-")&&v3.equals("-")&&v4.equals("-") )
			{
				txt=txt.replace("-","");
		    	txt="AAF1"+txt;
		    	WriteBytes= hex2byte(txt.toString().getBytes());
		    	
		    	BluetoothGattCharacteristic gg;
				gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
				//byte t[]={51,1,2};
				gg.setValue(WriteBytes);
				mBluetoothGatt.writeCharacteristic(gg);
				return true;
			}else{
				//Toast toast = Toast.makeText(DeviceControlActivity.this, "��ʾ��UUID�����ʽ����", Toast.LENGTH_SHORT); 
    			//toast.show(); 
				return false;
			}
		}else {
			//Toast toast = Toast.makeText(DeviceControlActivity.this, "��ʾ��UUID���벻��", Toast.LENGTH_SHORT); 
			//toast.show(); 
			return false;
		}
    	
    }
    public void set_func( String mayjor0,String minor0 ){//mayjor minor
    	
    	String mayjor="",minor="";
		String sss=mayjor0;
		int i = Integer.valueOf(sss).intValue();
		String vs=String.format("%02x", i);
		if( vs.length()==2)vs="00"+vs;
		else if( vs.length()==3)vs="0"+vs;
		
		mayjor=vs;
		
		sss=minor0;
		i = Integer.valueOf(sss).intValue();
		vs=String.format("%02x", i);
		if( vs.length()==2)vs="00"+vs;
		else if( vs.length()==3)vs="0"+vs;
		minor=vs;
    	
    	
    	String txt="AAF21AFF4C000215"+mayjor+minor+"CD00";
    	WriteBytes= hex2byte(txt.toString().getBytes());
    	
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		//byte t[]={51,1,2};
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    public void uuid_1001_send_data( String value )
    {
    	WriteBytes= hex2byte(value.toString().getBytes());
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));

		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
	
    public void set_dev_name( String name ){
    	int length=name.length();
    	String len=String.valueOf(length);
    	int ilen=len.length();
    	String he=String.format("%02X", length);
    	
    	name=bin2hex(name);
    	String txt="AAE4"+he+name;
    	WriteBytes= hex2byte(txt.toString().getBytes());
    	
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    
    public void out_io_set( String value )
    {
    	WriteBytes= hex2byte(value.toString().getBytes());
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    
    
    public void set_password( String value ){
    	String st1=bin2hex(value);
    	st1="AAE2"+st1;
    	WriteBytes= hex2byte(st1.toString().getBytes());
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    public void set_adv_time( int i )
    {
    	if( i==0 ){
			out_io_set("AA0900");
		}else if( i==0 ){
			out_io_set("AA0901");
		}else if( i==0 ){
			out_io_set("AA0902");
		}else if( i==0 ){
			out_io_set("AA0903");
		}else {
			out_io_set("AA0901");
		}
    }
    
    public void password_value( String value )
    {
    	//String txt="AAE2"+he+name;
    	//WriteBytes= hex2byte(value.toString().getBytes());
    	String txt="AAE2";
    	value=bin2hex(value);
    	txt=txt+value;
    	WriteBytes= hex2byte( txt.toString().getBytes() );
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    public void password_enable( boolean p )
    {
    	String g_pass="";
    	if(p){
    		g_pass="AAE101";
    	}else{
    		g_pass="AAE100";
    	}
    	WriteBytes= hex2byte(g_pass.toString().getBytes());
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    public void userkey( String key )
    {
    	String g_pass="AA20";
    	key=bin2hex(key);
    	g_pass+=key;
    	WriteBytes= hex2byte(g_pass.toString().getBytes());
    	BluetoothGattCharacteristic gg;
		gg=mBluetoothGatt.getService(UUID.fromString(Service_uuid)).getCharacteristic(UUID.fromString(Characteristic_uuid_FUNCTION));
		gg.setValue(WriteBytes);
		mBluetoothGatt.writeCharacteristic(gg);
    }
    
    
    public int get_connected_status( List<BluetoothGattService> gattServices )
    {
        final String LIST_NAME1 = "NAME";
        final String LIST_UUID1 = "UUID";
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        //mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        
        int count_char = 0;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
//            Log.d(TAG, "services uuid:"+uuid);
            currentServiceData.put(
                    LIST_NAME1, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID1, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            
            
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
//            BluetoothGattCharacteristic characteristic=gattService.getCharacteristic(StringCharacteristic_uuid_TX);
//            Log.d(TAG, "characteristics size:"+gattCharacteristics.size());
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
          
        
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
//                Log.d(TAG, "uuid:"+uuid);
                currentCharaData.put(
                        LIST_NAME1, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID1, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                count_char++;
            }
            //mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        return count_char;
    }
    
    
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            LogUtil.i(TAG, "bluetoothGattCallback");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                LogUtil.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                LogUtil.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                mBluetoothGatt.close();
                mBluetoothDeviceAddress=null;
                LogUtil.i(TAG, "Disconnected from GATT server.");

                GlobalVariable.setBluetoothIsConnected(false);
                broadcastUpdate(intentAction);
            }
            else
            {
            	LogUtil.i(TAG, "nesState:"+newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                LogUtil.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                LogUtil.i(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                LogUtil.i(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            LogUtil.i(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
        	//Log.i(TAG, "8");
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                   stringBuilder.append(String.format("%02X", byteChar));
                intent.putExtra(EXTRA_DATA,stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }
    /**
     * �½�LocalBinder�̳���Binder
     * @author Administrator
     *
     */
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
    	try
    	{
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (mBluetoothManager == null) {
                    LogUtil.i(TAG, "Unable to initialize BluetoothManager.");
                    return false;
                }
            }

            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                LogUtil.i(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }
    	}
    	catch(Exception ex)
    	{
    		mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    	}
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            LogUtil.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        LogUtil.i(TAG, "mBluetoothDeviceAddress:"+mBluetoothDeviceAddress);
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            LogUtil.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                LogUtil.i(TAG, "connect:"+"true");
                return true;
            } else {
            	LogUtil.i(TAG, "connect:"+"false");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtil.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        //����һ���µ�����
        if (mBluetoothGatt!=null) {
            mBluetoothGatt.disconnect();
            }
        
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        LogUtil.i(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        LogUtil.i(TAG,"bluetooth gatt disconnect");
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }
    public boolean isconnect() {
       
        
       return mBluetoothGatt.connect();
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
        	{
				LogUtil.i(TAG, "mBluetoothGatt is null");
				return null;
        	}

        return mBluetoothGatt.getServices();
    }
}
