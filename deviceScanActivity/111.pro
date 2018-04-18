#-injars DeviceScanActivity.jar
#-outjars DeviceScanActivity555.jar

-libraryjars 'C:\Program Files\Java\jdk1.8.0_111\jre\lib\rt.jar'
#-libraryjars libs\BaiduLBS_Android.jar
#-libraryjars jar\android-support-v4.jar
#-libraryjars jar\android-support-v7-appcompat.jar
#-libraryjars jar\android-support-v7-recyclerview.jar
#-libraryjars jar\arm64-v8a
#-libraryjars jar\armeabi
#-libraryjars jar\armeabi-v7a
#-libraryjars jar\glide-3.6.1.jar
#-libraryjars jar\mpandroidchartlibrary-2-1-6.jar
#-libraryjars jar\x86
#-libraryjars jar\x86_64
#-libraryjars jar\android.app.Notification

-dontusemixedcaseclassnames  #��ʹ�ô�Сд��ϣ���ֹ��ѹʱ��һЩ�����ִ�Сдϵͳ�г�����
-dontskipnonpubliclibraryclasses #�������ǹ������ļ�
-verbose  #����ʱ��¼��־
-dontoptimize  #���Ż���������ļ�  Ĭ������£��Ż���ʶ���رա�Dex����ϲ������ͨ���������Ż���ԤУ�鲽�����У������Լ������Щ�Ż���
-dontpreverify  #��ԤУ�� 
-keepattributes *Annotation*  #������ע��annotation

-dontwarn org.apache.**
-keep class org.apache.**{ *; }
-keep class com.baidu.**{ *; }

-keep public class * extends android.support.v4.app.Fragment  
-keep public class * extends android.app.Activity  
-keep public class * extends android.app.Application  
-keep public class * extends com.sun.xml.internal.ws.wsdl.writer.document.Service 
-keep public class * extends android.content.BroadcastReceiver  
-keep public class * extends android.content.ContentProvider  
-keep public class * extends android.app.Service

-ignorewarnings

-dontwarn com.example.bluetooth.db.**
-keep class com.example.bluetooth.db.** { *;}

-dontwarn com.example.bluetooth.le.**
-keep class com.example.bluetooth.le.** {*;}

-dontwarn com.example.bluetooth.list.**
-keep class com.example.bluetooth.list.** {*;}

-dontwarn com.example.bluetooth.result.**
-keep class com.example.bluetooth.result.**{*;}

-dontwarn com.example.bluetooth.set.**
-keep class com.example.bluetooth.set.**{*;}

-dontwarn com.example.bluetooth.track.**
-keep class com.example.bluetooth.track.**{*;}

-dontwarn com.example.bluetooth.data.**
-keep class com.example.bluetooth.data.**{*;}

-dontwarn com.github.mikephil.charting.**
-keep class com.github.mikephil.charting.**{*;}

#-dontwarn com.baidu.location.**
-keep class com.baidu.location.** {*;}
-keep class com.baidu.lbsapi.** {*;}
-keep class com.baidu.platform.** {*;}
-keep class com.baidu.android.bbalbs.**{*;}

#-dontwarn com.baidu.mapapi.model.**
-keep class com.baidu.mapapi.model.** {*;}

-dontwarn  android.support.v4.view.**
-keep class android.support.v4.view.** {*;}

#-dontwarn com.baidu.mapapi.map.**
-keep class com.baidu.mapapi.map.** {*;}

-dontwarn android.support.v4.content.**
-keep class android.support.v4.content.** {*;}

-dontwarn android.support.v4.widget.**
-keep class android.support.v4.widget.** {*;}

-dontwarn android.support.v7.widget.**
-keep class android.support.v7.widget.**  {*;}

-dontwarn  android.support.v4.app.**
-keep class android.support.v4.app.** {*;}

-keep class vi.com.gdi.bgl.android.**{*;}  


