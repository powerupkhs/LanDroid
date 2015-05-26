package com.sogogi.landroid.networkmodule;

import static de.robv.android.xposed.XposedHelpers.*;

import java.io.File;
import java.util.Arrays;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class VirtualNetworkMod implements IXposedHookZygoteInit, IXposedHookLoadPackage {
	static String savedProcessName = "";
	public static int mode = 0;

	public void hackNetworkInfo(int paramInt, NetworkInfo paramNetworkInfo) {
		Log.e("msg", "hackNetworkInfo 들어옴");
		if (paramNetworkInfo == null)
			return;

		if (((paramNetworkInfo.getType() == 1) && (paramInt == 1)) || ((paramNetworkInfo.getType() == 0) && (paramInt == 2))) {
			try {
				if (!paramNetworkInfo.isAvailable()) {
					XposedHelpers.setBooleanField(paramNetworkInfo, "mIsAvailable", true);
				}

				if (!paramNetworkInfo.isConnected()) {
					XposedHelpers.setObjectField(paramNetworkInfo, "mState", NetworkInfo.State.CONNECTED);
				}

				if (paramNetworkInfo.getDetailedState() != NetworkInfo.DetailedState.CONNECTED) {
					XposedHelpers.setObjectField(paramNetworkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		XposedBridge.log("handleLoadPackage Loaded app: " + lpparam.packageName);

		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getActiveNetworkInfo", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
				int i = getMode(); // 임의로 값 지정
				Log.e("msg", "핸들로드패키지 겟핵모드 부름 : " + i);

				NetworkInfo localNetworkInfo2;

				if (i == 0)
					return;

				Object localObject = paramAnonymousMethodHookParam.getResult();
				NetworkInfo localNetworkInfo1 = null;

				if (localObject != null) {
					localNetworkInfo1 = (NetworkInfo) localObject;

					if ((localNetworkInfo1 != null) && (i == 1) && (localNetworkInfo1.getType() == 1)) // wifi
					{
						hackNetworkInfo(i, localNetworkInfo1);
					} else if ((localNetworkInfo1 != null) && (i == 2) && (localNetworkInfo1.getType() == 0)) // mobile
					{
						hackNetworkInfo(i, localNetworkInfo1);
					}

				} else if (i == 1) {
					localNetworkInfo2 = ((ConnectivityManager) paramAnonymousMethodHookParam.thisObject).getNetworkInfo(1);

					if (localNetworkInfo2 == null) {
						localNetworkInfo2 = Controller.newNetWorkInfo(1);
						Log.d("msg", "getActiveNetworkInfo : wifi networkInfo created");
					}

					hackNetworkInfo(i, localNetworkInfo2);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo2);
				} else if (i == 2) {
					localNetworkInfo2 = ((ConnectivityManager) paramAnonymousMethodHookParam.thisObject).getNetworkInfo(0);

					if (localNetworkInfo2 == null) {
						localNetworkInfo2 = Controller.newNetWorkInfo(0);
						Log.d("msg", "getActiveNetworkInfo : mobile networkInfo created");
					}

					hackNetworkInfo(i, localNetworkInfo2);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo2);
				}
			}
		});

		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getNetworkInfo", Integer.TYPE, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
				Log.e("msg", "핸들로드패키지 getNetworkInfo");

				int i = getMode();

				if (i == 0)
					return;

				Object localObject = paramAnonymousMethodHookParam.getResult();
				NetworkInfo localNetworkInfo = null;

				if (localObject != null)
					localNetworkInfo = (NetworkInfo) localObject;

				Integer localInteger = (Integer) paramAnonymousMethodHookParam.args[0];

				if ((localNetworkInfo == null) && (i == 1) && (localInteger.intValue() == 1)) {
					localNetworkInfo = Controller.newNetWorkInfo(1);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo);
					Log.d("msg", "핸들로드패키지 getNetworkInfo : wifi networkInfo created");
				} else if ((localNetworkInfo == null) && (i == 2) && (localInteger.intValue() == 0)) {
					localNetworkInfo = Controller.newNetWorkInfo(0);
					paramAnonymousMethodHookParam.setResult(localNetworkInfo);
					Log.d("msg", "핸들로드패키지 getNetworkInfo : mobile networkInfo created");
				}

				hackNetworkInfo(i, localNetworkInfo);
			}
		});

		findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getAllNetworkInfo", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
				Log.e("msg", "핸들로드패키지 getAllNetworkInfo");

				int i = getMode();

				if (i == 0)
					return;

				Object localObject = paramAnonymousMethodHookParam.getResult();

				if (localObject != null) {
					NetworkInfo[] arrayOfNetworkInfo1 = (NetworkInfo[]) localObject;

					int type = 0;
					int length = arrayOfNetworkInfo1.length;
					boolean existWifi = false;
					boolean existMobile = false;

					for (int n = 0; n < length; n++) {
						NetworkInfo localNetworkInfo1 = arrayOfNetworkInfo1[n];

						if (localNetworkInfo1.getType() == 1) {
							type = 1;
							existWifi = true;
						}
						if (localNetworkInfo1.getType() == 0) {
							type = 0;
							existMobile = true;
						}

						if ((i == 1) && (type == 1)) {
							hackNetworkInfo(i, localNetworkInfo1);

						} else if ((i == 2) && (type == 0)) {
							hackNetworkInfo(i, localNetworkInfo1);
						}
					}

					if ((i == 1) && !existWifi) {
						NetworkInfo localNetworkInfo3 = Controller.newNetWorkInfo(1);
						hackNetworkInfo(i, localNetworkInfo3);
						NetworkInfo[] arrayOfNetworkInfo3 = Arrays.copyOf(arrayOfNetworkInfo1, 1 + arrayOfNetworkInfo1.length);
						arrayOfNetworkInfo3[arrayOfNetworkInfo1.length] = localNetworkInfo3;
						paramAnonymousMethodHookParam.setResult(arrayOfNetworkInfo3);
						Log.d("msg", "getAllNetworkInfo : wifi networkInfo created and added");
					} else if ((i == 2) && !existMobile) {
						NetworkInfo localNetworkInfo2 = Controller.newNetWorkInfo(0);
						hackNetworkInfo(i, localNetworkInfo2);
						NetworkInfo[] arrayOfNetworkInfo2 = Arrays.copyOf(arrayOfNetworkInfo1, 1 + arrayOfNetworkInfo1.length);
						arrayOfNetworkInfo2[arrayOfNetworkInfo1.length] = localNetworkInfo2;
						paramAnonymousMethodHookParam.setResult(arrayOfNetworkInfo2);
						Log.d("msg", "getAllNetworkInfo : mobile networkInfo created and added");
					}
				}
			}
		});
	}

	public int getMode() {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/NetworkApply");

		if (file.exists()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {

	}
}