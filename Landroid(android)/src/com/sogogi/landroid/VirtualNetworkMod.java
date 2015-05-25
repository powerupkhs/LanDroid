package com.sogogi.landroid;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class VirtualNetworkMod implements IXposedHookZygoteInit, IXposedHookLoadPackage
{
	static String savedProcessName = "";
	public static int mode = 0;
	
	public void hackNetworkInfo(int paramInt, NetworkInfo paramNetworkInfo)
	{
		Log.e("msg", "핵네트워크인포 들어옴");
		if (paramNetworkInfo == null) return;

		if (((paramNetworkInfo.getType() == 1) && (paramInt == 1)) || ((paramNetworkInfo.getType() == 0) && (paramInt == 2)))
		{
			Log.e("msg", "핵네트워크인포 이프문 안에 들어옴");
			try
			{
				if (!paramNetworkInfo.isAvailable())
				{
					XposedHelpers.setBooleanField(paramNetworkInfo, "mIsAvailable", true);
					Log.e("msg", "핵네트워크인포 이프문 안에 이즈어베이러블");
				}
				if (!paramNetworkInfo.isConnected())
				{
					XposedHelpers.setObjectField(paramNetworkInfo, "mState", NetworkInfo.State.CONNECTED);
					Log.e("msg", "핵네트워크인포 이프문 안에 엠스테이트");
				}
				if (paramNetworkInfo.getDetailedState() != NetworkInfo.DetailedState.CONNECTED)
				{
					XposedHelpers.setObjectField(paramNetworkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
					Log.e("msg", "핵네트워크인포 이프문 안에 엠디테일스테이트");
				}
			}
			catch (Exception localException)
			{
				Log.e("msg", "   hackNetworkInfo error : " + localException.getMessage());
				Log.e("msg", "   hackNetworkInfo error : " + localException.getCause());
			}
		}
	}
	
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
	{		
		XposedBridge.log("!!! fuck !!! Loaded app: " + lpparam.packageName);

		Class localClass4 = XposedHelpers.findClass("android.net.ConnectivityManager", lpparam.classLoader);
		Class localClass1 = localClass4;

		//final MainActivity main = new MainActivity();
		
		
		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getActiveNetworkInfo" ,  new XC_MethodHook()
		{
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable
			{
				int i = getMode();  // 임의로 값 지정

				Log.e("msg", "핸들로드패키지 겟핵모드 부름 : " + i);

				NetworkInfo localNetworkInfo2;

				if (i == 0) return;

				Object localObject = paramAnonymousMethodHookParam.getResult();
				NetworkInfo localNetworkInfo1 = null;

				if (localObject != null)
				{
					localNetworkInfo1 = (NetworkInfo)localObject;

					if ((localNetworkInfo1 != null) && (i == 1) && (localNetworkInfo1.getType() == 1)) // wifi
					{
						Log.e("msg", "핸들로드패키지 첫번째 들어옴");
						hackNetworkInfo(i, localNetworkInfo1);
					}
					else if((localNetworkInfo1 != null) && (i == 2) && (localNetworkInfo1.getType() == 0)) // mobile
					{
						Log.e("msg", "핸들로드패키지 두번째 들어옴");
						hackNetworkInfo(i, localNetworkInfo1);
					}

				}					
				else if(i == 1)
				{

					Log.e("msg", "핸들로드패키지 1일때 들어옴");
					localNetworkInfo2 = ((ConnectivityManager)paramAnonymousMethodHookParam.thisObject).getNetworkInfo(1);
					Log.e("msg", "핸들로드패키지 1일때 들어옴, 네트워크 설정 전: " + localNetworkInfo2);

					if (localNetworkInfo2 == null)
					{
						localNetworkInfo2 = Controller.newNetWorkInfo(1);
						Log.d("msg", "getActiveNetworkInfo : wifi networkInfo created");
					}
					
					hackNetworkInfo(i, localNetworkInfo2);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo2);
					Log.e("msg", "핸들로드패키지 1일때 들어옴, 네트워크 설정 후!!!!!!!: " + localNetworkInfo2);
				}
				else if(i == 2)
				{
					Log.e("msg", "핸들로드패키지 2일때 들어옴");
					localNetworkInfo2 = ((ConnectivityManager)paramAnonymousMethodHookParam.thisObject).getNetworkInfo(0);
					if (localNetworkInfo2 == null)
					{
						localNetworkInfo2 = Controller.newNetWorkInfo(0);
						Log.d("msg", "getActiveNetworkInfo : mobile networkInfo created");
					}
					
					hackNetworkInfo(i, localNetworkInfo2);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo2);
				}
			}
		});	
		
		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getNetworkInfo" , Integer.TYPE , new XC_MethodHook()
		{
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable
			{
				Log.e("msg", "핸들로드패키지 getNetworkInfo");
				
				int i = getMode();
				
				if (i == 0)
					return;
								
				Object localObject = paramAnonymousMethodHookParam.getResult();
				NetworkInfo localNetworkInfo = null;
				
				if (localObject != null)
					localNetworkInfo = (NetworkInfo)localObject;
				
				Integer localInteger = (Integer)paramAnonymousMethodHookParam.args[0];
				
				if ((localNetworkInfo == null) && (i == 1) && (localInteger.intValue() == 1))
				{
					localNetworkInfo = Controller.newNetWorkInfo(1);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo);
					Log.d("msg", "핸들로드패키지 getNetworkInfo : wifi networkInfo created");
				}
				else if ((localNetworkInfo == null) && (i == 2) && (localInteger.intValue() == 0))
				{
					localNetworkInfo = Controller.newNetWorkInfo(0);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo);
					Log.d("msg", "핸들로드패키지 getNetworkInfo : mobile networkInfo created");
				}
				
				hackNetworkInfo(i, localNetworkInfo);
			}
		});         
             
		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getAllNetworkInfo" , new XC_MethodHook()
		{
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable
			{
				Log.e("msg", "핸들로드패키지 getAllNetworkInfo");

				int i = getMode();

				if (i == 0)
					return;

				Object localObject = paramAnonymousMethodHookParam.getResult();

				if (localObject != null)
				{
					NetworkInfo[] arrayOfNetworkInfo1 = (NetworkInfo[])localObject;
					
					int type = 0;
					int length = arrayOfNetworkInfo1.length;
					boolean existWifi = false;
					boolean existMobile = false;
					
					for (int n=0; n<length; n++)
					{
						NetworkInfo localNetworkInfo1 = arrayOfNetworkInfo1[n];
						
						if (localNetworkInfo1.getType() == 1)
						{
							type = 1;
							existWifi = true;
						}
						if (localNetworkInfo1.getType() == 0)
						{
							type = 0;
							existMobile = true;
						}
																		
						if ((i == 1) && (type == 1))
						{
							hackNetworkInfo(i, localNetworkInfo1);
							
						}
						else if ((i == 2) && (type == 0))
						{
							hackNetworkInfo(i, localNetworkInfo1);
						}
					}
					
					if ((i == 1) && !existWifi)
					{				
						NetworkInfo localNetworkInfo3 = Controller.newNetWorkInfo(1);
						hackNetworkInfo(i, localNetworkInfo3);
						NetworkInfo[] arrayOfNetworkInfo3 = (NetworkInfo[])Arrays.copyOf(arrayOfNetworkInfo1, 1 + arrayOfNetworkInfo1.length);
						arrayOfNetworkInfo3[arrayOfNetworkInfo1.length] = localNetworkInfo3;
						paramAnonymousMethodHookParam.setResult(arrayOfNetworkInfo3);
						Log.d("TT2", "getAllNetworkInfo : wifi networkInfo created and added");
					}
					else if ((i == 2) && !existMobile)
					{
						NetworkInfo localNetworkInfo2 = Controller.newNetWorkInfo(0);
						hackNetworkInfo(i, localNetworkInfo2);
						NetworkInfo[] arrayOfNetworkInfo2 = (NetworkInfo[])Arrays.copyOf(arrayOfNetworkInfo1, 1 + arrayOfNetworkInfo1.length);
						arrayOfNetworkInfo2[arrayOfNetworkInfo1.length] = localNetworkInfo2;
						paramAnonymousMethodHookParam.setResult(arrayOfNetworkInfo2);
						Log.d("TT2", "getAllNetworkInfo : mobile networkInfo created and added");
					}
				}
			}
		});
	}
	
	public int getMode()
	{
		String path = "/sdcard/NetworkApply";        
        File file = new File(path);
        
        if(file.exists())
        {
        	return 1;
        }
        else
        {
        	return 0;
        }
	}
	
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable 
	{
		XposedBridge.log("initZygote : modulePath : " + startupParam.modulePath) ;

		try
		{
			findAndHookMethod("com.android.server.am.ActivityManagerService", null, "systemReady", Runnable.class, new XC_MethodHook()
			{
				protected void beforeHookedMethod(final MethodHookParam param) throws Throwable 
				{
					Log.i("Xposed", "자이고트 비포 들어옴");
					
					final Runnable origCallback = (Runnable) param.args[0];
					
					param.args[0] = new Runnable()
					{
						public void run()
						{
							Log.i("Xposed", "자이고트 오버라이디 런 들어옴");
							
							origCallback.run();
							
							Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
							handleSystemServicesReady(mContext);
						}
					};
				}
			});
		}
		catch (Throwable t)
		{ 
			XposedBridge.log("error getting android.net.ConnectivityManager.getActiveNetworkInfo()");
			XposedBridge.log(t); 
		}           
	} 


	public void handleSystemServicesReady(Context context) 
	{
//		Log.i("Xposed", " handleSystemServicesReady");
//		
//		context.registerReceiver(new BroadcastReceiver() 
//		{
//			public void onReceive(Context context, Intent intent) 
//			{
//				Log.i("Xposed", "핸들시스템 온리시브 들어옴");
////				Log.i("Xposed", Controller.instance + " ------> BroadcastReceiver : HcActivateFakeWifi");
////				Controller.setFakeWifiActivated(1) ;         
//			}
//		}, new IntentFilter("HcActivateFakeWifi"),   // filter : Selects the Intent broadcasts to be received.
//		null,                                     // broadcastPermission : naming a permissions that a broadcaster must hold in order to send an Intent to you. If null, no permission is required.
//		null);                                    // scheduler : identifying the thread that will receive the Intent. If null, the main thread of the process will be used.
//
//		context.registerReceiver(new BroadcastReceiver() 
//		{
//			public void onReceive(Context context, Intent intent) 
//			{
//				Log.i("Xposed", Controller.instance + " ------> BroadcastReceiver : HcDesactivateFakeWifi");
//				Controller.setFakeWifiActivated(0) ;         
//			}
//		}, 
//		new IntentFilter("HcDesactivateFakeWifi"),   // filter : Selects the Intent broadcasts to be received.
//		null,                                        // broadcastPermission : naming a permissions that a broadcaster must hold in order to send an Intent to you. If null, no permission is required.
//		null);                                       // scheduler : identifying the thread that will receive the Intent. If null, the main thread of the process will be used.

	}
	
	
	
}
