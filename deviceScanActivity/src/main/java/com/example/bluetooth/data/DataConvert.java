package com.example.bluetooth.data;

import android.annotation.SuppressLint;
import android.util.Log;

public class DataConvert {
	private final String TAG="DataConvert";
    @SuppressLint("UseValueOf")
	public byte[] HexStringToByteArray(String s) 
    { 
        byte[] buffer = new byte[s.length() / 2]; 
        int m=0,n=0;
        for (int i = 0; i < s.length()/2; i++){
            m=i*2+1;
            n=m+1;
            buffer[i] = (byte)(Integer.decode("0x"+ s.substring(i*2, m) + s.substring(m,n)) & 0xFF);
        }
        return buffer;
    }
    
    public short byte2Short(byte[] z)
    {
    	if(z!=null && z.length!=0)
    	{
    		short r=(short)(((z[1] & 0x00FF) << 8) | (0x00FF & z[0]));
        	return r;
    	}
    	return 0;
    }
    //byte转换成int
    public int byte2int(byte[] res) {   
    // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
    	int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或   
    	| ((res[2] << 24) >>> 8) | (res[3] << 24);   
    	return targets;   
    }    
    
    
    /**
     * 字节转uint
     * @param src
     * @param offset
     * @return
     */
    public long byte2int(byte[] src, int offset)         
    {             
    	long value;             
    	value = (long)(((src[offset] & 0xFF) << 24)| ((src[offset + 1] & 0xFF) << 16) | ((src[offset + 2] & 0xFF) << 8)      
    			| (src[offset + 3] & 0xFF));             
    	return value;        
    }
    
    //byte转换成int
    public long byte2long(byte[] bb) {   
    // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
//    	long targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或   
//    	| ((res[2] << 24) >>> 8) | (res[3] << 24);   
//    	return targets;   
    	 return ((((long) bb[ 0] & 0xff) << 56) 
                 | (((long) bb[ 1] & 0xff) << 48) 
                 | (((long) bb[ 2] & 0xff) << 40) 
                 | (((long) bb[ 3] & 0xff) << 32) 
                 | (((long) bb[ 4] & 0xff) << 24) 
                 | (((long) bb[ 5] & 0xff) << 16) 
                 | (((long) bb[ 6] & 0xff) << 8) | (((long) bb[ 7] & 0xff) << 0)); 
    }    
    
    /**
     * 毫秒转换为时分秒
     * @param milseconds
     * @return
     */
    public String milsecondsToHHMMSS(int milseconds)
    {
//    	Log.d(TAG, "milseconds:"+milseconds);
    	
    	//将besttime转换成时分秒
		int hours=milseconds/3600000;//取小时
		int minutes=(milseconds-hours*3600000)/60000;//取分钟
		int seconds=(milseconds-hours*3600000-minutes*60000)/1000;
		int milliseconds=(milseconds-hours*3600000-minutes*60000-seconds*1000);
//		Log.d(TAG, "hours:"+hours);
//		Log.d(TAG, "minutes:"+minutes);
//		Log.d(TAG, "seconds:"+seconds);
//		Log.d(TAG, "milliseconds:"+milliseconds);
		String bestTimeStr="";
		if (hours!=0 ) {//时
			bestTimeStr=hours+":";
		}
		
		
		if (minutes!=0 ) {//分
			bestTimeStr=bestTimeStr+minutes+":";
		}
	
		
		
		if (seconds!=0) {//秒
			bestTimeStr=bestTimeStr+seconds+".";
		}
		else
		{
			bestTimeStr=bestTimeStr+"00"+".";
		}

		String milSecondStr="";
		if (milliseconds==0) {
			milSecondStr="000";
		}
		else if (milliseconds>0 && milliseconds<10) {
			milSecondStr="00"+milliseconds;
		}
		else if (milliseconds>=10 && milliseconds<100) {
			milSecondStr="0"+milliseconds;
		}
		else {
			milSecondStr=String.valueOf(milliseconds);
		}
		bestTimeStr=bestTimeStr+milSecondStr;

//		Log.d(TAG, bestTimeStr);
		return bestTimeStr;
    }

    /**
     * 获取IRID的十进制数字
     * @param IRID
     * @return
     */
	public String getIRIDInt(String iRID) {
		byte[] IRIDByte=HexStringToByteArray(iRID);
		byte[] IRIDByteTemp=new byte[8];
		IRIDByteTemp[0]=0x00;
		IRIDByteTemp[1]=0x00;
		IRIDByteTemp[2]=0x00;
		IRIDByteTemp[3]=0x00;
		IRIDByteTemp[4]=IRIDByte[0];
		IRIDByteTemp[5]=IRIDByte[1];
		IRIDByteTemp[6]=IRIDByte[2];
		IRIDByteTemp[7]=IRIDByte[3];
//		 final StringBuilder stringBuilder = new StringBuilder(IRIDByteTemp.length);
//         for(byte byteChar : IRIDByteTemp)
//            stringBuilder.append(String.format("%02X", byteChar));
//        Log.d(TAG, "IRIDByteTemp:"+stringBuilder);
			
		long IRIDInt=byte2long(IRIDByteTemp);
		String IRIDStr=String.valueOf(IRIDInt);
//		Log.d(TAG, "IRIDStr:"+IRIDStr);
		String iridResult=IRIDStr.substring(IRIDStr.length()-6,IRIDStr.length());
		return iridResult;
	}
}
