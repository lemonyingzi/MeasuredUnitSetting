package com.measuredunitsetting.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

public class PublicMethod {
	
	
	  public int getHeadPosition(String data)
	    {
	  		if (data.length()>14) {//ͷβ����������С����
		    	for(int i=0;i<data.length();i++)
		    	{
	    			if (data.substring(i,i+6).equalsIgnoreCase("534A4B")) {
	        			return i;
	    			}
		    	} 
	  		}
	    	return -1;
	    }
	    
	    public int getTailPosition(int headPosition, String data)
	    {
	 		if (data.length()>headPosition+6+8) {//ͷβ����������С����
		    	for(int i=headPosition;i<data.length();i++)
		    	{
		    		if (data.length()<i+8) {
						return -1;
					}
					if (data.substring(i,i+8).equalsIgnoreCase("4A435A58")) {
		    			return i;
					}
		    	} 
	 		}
	    	return -1;    
	    }
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
	    /**
	     * �ж��ַ����Ƿ�Ϊ����
	     * @param str
	     * @return
	     */
	    public boolean isNumeric(String str){
	        Pattern pattern = Pattern.compile("[0-9]*");
	        return pattern.matcher(str).matches();   
	    }
	    
	    public String bytesToHexString(byte[] src){   
	        StringBuilder stringBuilder = new StringBuilder("");   
	        if (src == null || src.length <= 0) {   
	            return null;   
	        }   
	        for (int i = 0; i < src.length; i++) {   
	            int v = src[i] & 0xFF;   
	            String hv = Integer.toHexString(v);   
	            if (hv.length() < 2) {   
	                stringBuilder.append(0);   
	            }   
	            stringBuilder.append(hv);   
	        }   
	        return stringBuilder.toString();   
	    }  
	    
	    /**
	     * ��ȡ��һ�������
	     * @param measurePointNum
	     * @return
	     */
	    public String GetNextMeasurePointNum(String measurePointNum)
	    {
	    	String mpn=null;
	    	if (isNumeric(measurePointNum)) {
				int measurePointNumInt=Integer.parseInt(measurePointNum);
				measurePointNumInt++;
				mpn=String.valueOf(measurePointNumInt);
				while(mpn.length()<8)
					mpn="0"+mpn;
			}
			return mpn;
	    }
	    /**
	     * ��ȡ��һ�������
	     * @param measurePointNum
	     * @return
	     */
	    public String GetLastMeasurePointNum(String measurePointNum)
	    {
	    	String mpn=null;
	    	if (isNumeric(measurePointNum)) {
				int measurePointNumInt=Integer.parseInt(measurePointNum);
				measurePointNumInt--;
				mpn=String.valueOf(measurePointNumInt);
				while(mpn.length()<8)
					mpn="0"+mpn;
			}
			return mpn;
	    }
	    
	    /** 
	     * �ֽ�ת��Ϊ���� 
	     *  
	     * @param b �ֽڣ�����4���ֽڣ� 
	     * @param index ��ʼλ�� 
	     * @return 
	     */  
	    public float byte2float(byte[] b, int index) {    
	        int l;                                             
	        l = b[index + 0];                                  
	        l &= 0xff;                                         
	        l |= ((long) b[index + 1] << 8);                   
	        l &= 0xffff;                                       
	        l |= ((long) b[index + 2] << 16);                  
	        l &= 0xffffff;                                     
	        l |= ((long) b[index + 3] << 24);                  
	        return Float.intBitsToFloat(l);                    
	    }  
	    
	    @SuppressLint("NewApi")
		public String formatTimeEight(String time)
	    {
	    	   Date d = null;
	    	   SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	   try {
	    		d = sd.parse(time);
	    	   } catch (ParseException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	   }
	    	   long rightTime = (long) (d.getTime() + 8 * 60 * 60 * 1000); //�ѵ�ǰ�õ���ʱ����date.getTime()�ķ���д��ʱ�������ʽ���ټ���8Сʱ��Ӧ�ĺ�����
	    	   String newtime = sd.format(rightTime);//�ѵõ����µ�ʱ����ٴθ�ʽ����ʱ��ĸ�ʽ
	    	   return newtime;
	    }
}
